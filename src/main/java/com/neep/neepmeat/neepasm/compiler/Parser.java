package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Maps;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedFunctionCallInstruction;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * It's the worst lexer/parser ever!
 * We don't need all those silly things like operator precedence, nested scopes blah blah blah. It's all bloat.
 * Abstract symbol tree? What's that?
 */
public class Parser
{
    private final Map<String, InstructionProvider> instructionMap = Maps.newHashMap();

    private ParsedSource parsedSource;

    public Parser()
    {
        Instructions.REGISTRY.forEach(provider ->
        {
            instructionMap.put(provider.getParseName().toLowerCase(), provider);
        });
    }

    public ParsedSource parse(String source) throws NeepASM.ProgramBuildException
    {
        parsedSource = new ParsedSource();

        int line1 = 0;
        TokenView view = new TokenView(source);

        try
        {
            while (!view.eof())
            {
                parseLine(view);
                view.nextLine();
                line1++;
            }

            // Expand the macros (which seem to have turned into functions) at the end with their labels.
            for (var func : parsedSource.functions())
            {
                func.expand(parsedSource);
            }
        }
        catch (NeepASM.ParseException e)
        {
            throw new NeepASM.ProgramBuildException(line1, view.pos(), e.getMessage());
        }
        return parsedSource;
    }

    private void parseLine(TokenView view) throws NeepASM.ParseException
    {
        if (view.peekThing() == '%')
        {
            view.next();
            String id = view.nextIdentifier();
            if (id.equals("func"))
            {
                parseFunction(view);
                return;
            }
            throw new NeepASM.ParseException("unexpected directive '" + id + "'");
        }

        String token;
        char follow;
        try (var entry = view.save())
        {
            token = view.nextIdentifier();
            follow = view.nextThing();
            if (follow == ':')
            {
                parsedSource.label(new Label(token, parsedSource.size()));
                view.fastForward();
                if (!view.lineEnded() && !isComment(view))
                {
                    throw new NeepASM.ParseException("unexpected token after '" + token + "'");
                }

                entry.commit();
            }
        }

        ParsedInstruction instruction = parseInstruction(view);
        if (instruction != null)
            parsedSource.instruction(instruction);
    }

    private void parseFunction(TokenView view) throws NeepASM.ParseException
    {
        String name = view.nextIdentifier();
        if (name.isEmpty())
            throw new NeepASM.ParseException("");

        if (!view.lineEnded() && !isComment(view))
            throw new NeepASM.ParseException("unexpected token");

        view.nextLine();

        ParsedFunction function = new ParsedFunction(name);
        view.fastForward();
        while (view.peek() != '%')
        {
            parseFunctionLine(function, view);
            view.nextLine();

            if (view.eof())
                throw new NeepASM.ParseException("reached end of file while parsing function '" + name + "'");
        }
        parsedSource.function(function);
    }

    private void parseFunctionLine(ParsedFunction function, TokenView view) throws NeepASM.ParseException
    {
        String token;
        char follow;
        try (var entry = view.save())
        {
            token = view.nextIdentifier();
            follow = view.nextThing();
            if (follow == ':')
            {
                function.label(new Label(token, function.size()));
                view.fastForward();
                if (!view.lineEnded() && !isComment(view))
                {
                    throw new NeepASM.ParseException("unexpected token after '" + token + "'");
                }

                entry.commit();
            }
        }

        ParsedInstruction instruction = parseInstruction(view);
        if (instruction != null)
            function.instruction(instruction);
    }

    @Nullable
    private ParsedInstruction parseInstruction(TokenView view) throws NeepASM.ParseException
    {
        view.fastForward();
        if (view.lineEnded())
            return null;

        String id = view.nextIdentifier();

        InstructionProvider provider = readInstruction(id);
        if (provider != null)
        {
            InstructionParser parser = provider.getParser();
            return parser.parse(view, parsedSource, this);
        }

        ParsedFunction function = parsedSource.findFunction(id);
        if (function != null)
            return new ParsedFunctionCallInstruction(id);

        throw new NeepASM.ParseException("unrecognised operation '" + id + "'");
    }

    @Nullable
    private InstructionProvider readInstruction(String id)
    {
        return instructionMap.get(id.toLowerCase());
    }

    @Nullable
    public Argument parseArgument(TokenView view) throws NeepASM.ParseException
    {
        try (var entry = view.save())
        {
            if (view.nextThing() == '@')
            {
                b1: if (view.nextThing() == '(')
                {
                    int x, y, z;
                    Direction direction = Direction.UP;

                    if (isDigit(view.peekThing()))
                        x = view.nextInteger();
                    else break b1;

                    if (isDigit(view.peekThing()))
                        y = view.nextInteger();
                    else break b1;

                    if (isDigit(view.peekThing()))
                        z = view.nextInteger();
                    else break b1;

                    // Direction is optional
                    if (Character.isAlphabetic(view.peekThing()))
                    {
                        direction = parseDirection(view);
                        if (direction == null)
                            direction = Direction.UP; // Unrecognised text will throw an exception
                    }

                    if (view.nextThing() == ')')
                    {
                        entry.commit();
                        return new Argument(new BlockPos(x, y, z), direction);
                    }
                }
                throw new NeepASM.ParseException("malformed argument\n Arguments should be of the form '@(<x> <y> <z> <direction>)");
            }
        }
        return null;
    }

    /**
     * @return Direction or null if none found.
     * @throws NeepASM.ParseException if direction name is invalid
     */
    @Nullable
    private Direction parseDirection(TokenView view) throws NeepASM.ParseException
    {
        String name = view.nextIdentifier();
        if (!name.isEmpty())
        {
            Direction direction = directionByName(name);
            if (direction == null)
            {
                throw new NeepASM.ParseException("no such direction '" + name + "'\n Directions are specified with the full name or first letter (north or n)");
            }
            return direction;
        }
        return null;
    }

    /**
     * @param name Name of the direction (full name or first letter)
     * @return Direction or null if the name is invalid
     */
    @Nullable
    private Direction directionByName(String name)
    {
        name = name.toLowerCase();
        Direction d = Direction.byName(name);
        if (d == null)
        {
            d = switch (name)
            {
                case "n" -> Direction.NORTH;
                case "e" -> Direction.EAST;
                case "s" -> Direction.SOUTH;
                case "w" -> Direction.WEST;
                case "u" -> Direction.UP;
                case "d" -> Direction.DOWN;
                default -> null;
            };
        }
        return d;
    }

    @Nullable
    public KeyValue parseKV(TokenView view) throws NeepASM.ParseException
    {
        try (var entry = view.save())
        {
            String key = view.nextIdentifier();
            if (!key.isEmpty())
            {
                if (view.nextThing() == '=')
                {
                    String val = view.nextIdentifier();
                    if (!val.isEmpty())
                    {
                        entry.commit();
                        return new KeyValue(key, val);
                    }
                }
                throw new NeepASM.ParseException("malformed key-value pair");
            }
        }
        return null;
    }

    private boolean isDigit(char c)
    {
        return c == '-' || Character.isDigit(c);
    }

    public boolean isComment(TokenView view)
    {
        try (var ignored = view.save())
        {
            return view.nextThing() == '#';
        }
    }
}