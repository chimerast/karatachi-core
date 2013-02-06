package org.karatachi.wicket.script;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class SyntaxHilighterLabel extends WebComponent implements
        IHeaderContributor {
    private static final long serialVersionUID = 1L;

    public static final ResourceReference syntaxhighlighter =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "syntaxhighlighter/shCore.js");
    public static final ResourceReference clipboard =
            new PackageResourceReference(AjaxLibrariesReference.class,
                    "syntaxhighlighter/clipboard.swf");

    private final String brush;
    private final String theme;

    public SyntaxHilighterLabel(String id, String brush, String label) {
        this(id, brush, "Default", new Model<String>(label));
    }

    public SyntaxHilighterLabel(String id, String brush, IModel<String> model) {
        this(id, brush, "Default", model);
    }

    public SyntaxHilighterLabel(String id, String brush, String theme,
            String label) {
        this(id, brush, theme, new Model<String>(label));
    }

    public SyntaxHilighterLabel(String id, String brush, String theme,
            IModel<String> model) {
        super(id, model);
        this.brush = brush;
        this.theme = theme;
    }

    @Override
    public void onComponentTagBody(final MarkupStream markupStream,
            final ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag,
                getDefaultModelObjectAsString());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.setName("pre");
        tag.put("class", "brush: " + brush.toLowerCase());
        tag.setType(XmlTag.TagType.OPEN);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(syntaxhighlighter));
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(AjaxLibrariesReference.class,
                        "syntaxhighlighter/shBrush" + brush + ".js"), brush));

        response.render(CssHeaderItem.forReference(new CssResourceReference(
                AjaxLibrariesReference.class, "syntaxhighlighter/shCore.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(
                AjaxLibrariesReference.class, "syntaxhighlighter/shTheme"
                        + theme + ".css")));

        response.render(JavaScriptHeaderItem.forScript(
                "SyntaxHighlighter.config.clipboardSwf='"
                        + urlFor(clipboard, null)
                        + "'; SyntaxHighlighter.all()", "syntaxhilighter-init"));
    }
}
