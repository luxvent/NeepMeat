package com.neep.neepmeat.guide;

import com.neep.neepmeat.client.screen.tablet.GuideArticlePane;
import com.neep.neepmeat.client.screen.tablet.IGuideScreen;
import com.neep.neepmeat.guide.article.Article;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface GuideNode
{
    List<GuideNode> getChildren();
    void addChild(GuideNode node);
    String getId();
    Identifier getIcon();
    Text getText();
    void visitScreen(IGuideScreen screen);


    GuideNode BACK = new GuideNode()
    {
        @Override
        public List<GuideNode> getChildren()
        {
            return Collections.emptyList();
        }

        @Override
        public void addChild(GuideNode node) {}

        @Override
        public String getId()
        {
            return "";
        }

        @Override
        public Identifier getIcon()
        {
            return new Identifier("minecraft:empty");
        }

        @Override
        public Text getText()
        {
            return Text.of(" \u2190");
        }

        @Override
        public void visitScreen(IGuideScreen screen)
        {
            screen.pop();
        }
    };

    abstract class GuideNodeImpl implements GuideNode
    {
        private final String id;
        private final Identifier icon;
        private final Text text;

        public GuideNodeImpl(String id, Identifier icon, Text text)
        {
            this.id = id;
            this.icon = icon;
            this.text = text;
        }

        @Override
        public String getId()
        {
            return id;
        }

        @Override
        public Identifier getIcon()
        {
            return icon;
        }

        @Override
        public Text getText()
        {
            return text;
        }

        @Override
        public boolean equals(Object other)
        {
            if (other instanceof GuideNode node)
            {
                return Objects.equals(node.getId(), getId());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return id.hashCode();
        }
    }

    class MenuNode extends GuideNodeImpl
    {
        private final List<GuideNode> children;

        public MenuNode(String id, Identifier icon, Text text, List<GuideNode> children)
        {
            super(id, icon, text);
            this.children = children;
        }

        @Override
        public List<GuideNode> getChildren()
        {
            return children;
        }

        @Override
        public void addChild(GuideNode node)
        {
            children.add(node);
        }

        @Override
        public void visitScreen(IGuideScreen screen)
        {
            screen.push(this);
        }
    }

    class ArticleNode extends GuideNodeImpl
    {
        public ArticleNode(String id, Identifier icon, Text text)
        {
            super(id, icon, text);
        }

        @Override
        public List<GuideNode> getChildren()
        {
            return Collections.emptyList();
        }

        @Override
        public void addChild(GuideNode node)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void visitScreen(IGuideScreen screen)
        {
            Article article = GuideReloadListener.getInstance().getArticle(getId());
            screen.setRightPane(new GuideArticlePane(screen, article));
        }
    }
}
