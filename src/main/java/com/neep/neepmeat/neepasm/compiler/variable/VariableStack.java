package com.neep.neepmeat.neepasm.compiler.variable;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.plc.PLC;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;

public class VariableStack implements IntStack, NbtSerialisable
{
//    private final ObjectArrayList<Variable<?>> entries = new ObjectArrayList<>();
    private final IntArrayList entries = new IntArrayList();

    private final PLC plc;
    private final int maxSize;

    public VariableStack(PLC plc, int maxSize)
    {
        this.plc = plc;
        this.maxSize = maxSize;
    }

    @Override
    public void push(int entry)
    {
        if (size() >= maxSize)
        {
            plc.raiseError(new PLC.Error("Variable stack overflow"));
            return;
        }

        entries.push(entry);
    }

    @Override
    public int popInt()
    {
        if (isEmpty())
        {
            plc.raiseError(new PLC.Error("Variable stack underflow"));
            return 0;
        }
        return entries.popInt();
    }

    @Override
    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    @Override
    public int topInt()
    {
        return entries.top();
    }

    @Override
    public int peekInt(int i)
    {
        return entries.peek(i);
    }

    public int size()
    {
        return entries.size();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
//        NbtList list = new NbtList();
//        for (int i : entries)
//        {
//            list.add(NbtInt.of(i));
//        }
//        nbt.put("entries", list);
        nbt.putIntArray("entries", entries);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
//        NbtList list = nbt.getList("entries", NbtElement.INT_TYPE);
//        for (int i = 0; i < list.size(); ++i)
//        {
//            entries.add(i, list.getInt(i));
//        }
        entries.clear();
        int[] ints = nbt.getIntArray("entries");
        entries.size(ints.length);
        entries.setElements(ints);
    }

    public void clear()
    {
        entries.clear();
    }
}
