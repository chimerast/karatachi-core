package org.karatachi.wicket.script;

import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

public class AjaxLibrariesReference {
    public static final JavaScriptResourceReference prototype =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "prototype/prototype-1.7.1.js");

    public static final JavaScriptResourceReference jquery =
            JQueryResourceReference.get();
    public static final JavaScriptResourceReference jquery_textselection =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.textselection-1.0.0.js");
    public static final JavaScriptResourceReference jquery_easing =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.easing-1.3.js");
    public static final JavaScriptResourceReference jquery_placeholder =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.placeholder-1.0.1.js");
    public static final JavaScriptResourceReference jquery_zclip =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.zclip-1.1.1.js");
    public static final JavaScriptResourceReference jquery_zclip_swf =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/ZeroClipboard.swf");
}
