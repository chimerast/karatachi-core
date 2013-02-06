package org.karatachi.wicket.flex;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class FlexComponent extends WebMarkupContainer implements
        IHeaderContributor {
    private static final long serialVersionUID = 1L;

    public static final JavaScriptResourceReference SWFOBJECT_JS =
            new JavaScriptResourceReference(FlexComponent.class, "swfobject.js");
    public static final ResourceReference EXPRESS_INSTALL_SWF =
            new PackageResourceReference(FlexComponent.class,
                    "expressInstall.swf");

    private String src;

    private String width;
    private String height;

    public <T extends Map<String, String> & Serializable> FlexComponent(
            String id, ResourceReference reference) {
        this(id, reference, (IModel<? extends Map<String, String>>) null);
    }

    public <T extends Map<String, String> & Serializable> FlexComponent(
            String id, String src) {
        this(id, src, (IModel<? extends Map<String, String>>) null);
    }

    public <T extends Map<String, String> & Serializable> FlexComponent(
            String id, ResourceReference reference, T params) {
        this(id, reference, new Model<T>(params));
    }

    public <T extends Map<String, String> & Serializable> FlexComponent(
            String id, String src, T params) {
        this(id, src, new Model<T>(params));
    }

    public FlexComponent(String id, ResourceReference reference,
            IModel<? extends Map<String, String>> model) {
        super(id, model);
        commonInit(urlFor(reference, null).toString());
    }

    public FlexComponent(String id, String src,
            IModel<? extends Map<String, String>> model) {
        super(id, model);
        commonInit(src);
    }

    private void commonInit(String src) {
        this.src = src;
        setOutputMarkupId(true);
        add(new FlexAppender());
    }

    protected String getRequiredVersion() {
        return "10.0.0";
    }

    protected String getExpressInstallSwf() {
        return urlFor(EXPRESS_INSTALL_SWF, null).toString();
    }

    protected String getUnavailableMessage() {
        return "このコンテンツを表示するには Adobe FlashPlayer が必要です。"
                + "<a href=\"http://get.adobe.com/jp/flashplayer/\">ダウンロードはこちら</a>";
    }

    protected Map<String, String> getParameter() {
        Map<String, String> ret = new TreeMap<String, String>();
        ret.put("bgcolor", "#FFFFFF");
        return ret;
    }

    public String getSwfMarkupId() {
        return getMarkupId() + "_swf";
    }

    @Override
    protected final void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.setType(XmlTag.TagType.OPEN);
        tag.setName("div");

        width = tag.getAttribute("width");
        height = tag.getAttribute("height");

        if (width == null) {
            width = "100%";
        }
        if (height == null) {
            height = "100%";
        }

        tag.remove("width");
        tag.remove("height");
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream,
            ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, getUnavailableMessage());
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getModelObject() {
        return (Map<String, String>) getDefaultModelObject();
    }

    @Override
    public final void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(SWFOBJECT_JS));
    }

    private class FlexAppender extends Behavior {
        private static final long serialVersionUID = 1L;

        @Override
        public void afterRender(Component component) {
            Response response = component.getResponse();
            response.write("<script type=\"text/javascript\">");

            response.write("var flashvars = {};");
            if (getModelObject() != null) {
                for (Entry<String, String> entry : getModelObject().entrySet()) {
                    response.write(String.format("flashvars.%s = '%s';",
                            escape(entry.getKey()), escape(entry.getValue())));
                }
            }

            if (getParameter() != null) {
                response.write("var params = {};");
                for (Entry<String, String> param : getParameter().entrySet()) {
                    response.write(String.format("params.%s = '%s';",
                            param.getKey(), param.getValue()));
                }
            }

            response.write("var attributes = {};");
            response.write(String.format("attributes.id = '%s';",
                    getSwfMarkupId()));
            response.write("attributes.name = attributes.id;");

            response.write(String.format(
                    "swfobject.embedSWF('%s', '%s', '%s', '%s', '%s', %s, flashvars, params, attributes);",
                    src, getMarkupId(), width, height, getRequiredVersion(),
                    getExpressInstallSwf() != null ? "'"
                            + getExpressInstallSwf() + "'" : "false"));

            response.write("</script>");
        }
    }

    public static String escape(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
