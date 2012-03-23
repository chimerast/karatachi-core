package org.karatachi.wicket.script;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class SyntaxHilighterLabel extends WebComponent implements
        IHeaderContributor {
    private static final long serialVersionUID = 1L;

    public static final JavascriptResourceReference syntaxhighlighter =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "syntaxhighlighter/shCore.js");
    public static final ResourceReference clipboard =
            new ResourceReference(AjaxLibrariesReference.class,
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
    protected void onComponentTagBody(final MarkupStream markupStream,
            final ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag,
                getDefaultModelObjectAsString());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.setName("pre");
        tag.put("class", "brush: " + brush.toLowerCase());
        tag.setType(XmlTag.OPEN);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavascriptReference(syntaxhighlighter);
        response.renderJavascriptReference(new JavascriptResourceReference(
                AjaxLibrariesReference.class, "syntaxhighlighter/shBrush"
                        + brush + ".js"), brush);

        response.renderCSSReference(new ResourceReference(
                AjaxLibrariesReference.class, "syntaxhighlighter/shCore.css"));
        response.renderCSSReference(new ResourceReference(
                AjaxLibrariesReference.class, "syntaxhighlighter/shTheme"
                        + theme + ".css"));

        response.renderJavascript("SyntaxHighlighter.config.clipboardSwf='"
                + urlFor(clipboard) + "'; SyntaxHighlighter.all()",
                "syntaxhilighter-init");
    }
}
