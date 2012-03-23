package org.karatachi.wicket.script;

import org.apache.wicket.markup.html.resources.JavascriptResourceReference;

public class AjaxLibrariesReference {
    public static final JavascriptResourceReference prototype =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "prototype/prototype.js");
    public static final JavascriptResourceReference scriptaculous =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "prototype/scriptaculous.js");

    public static final JavascriptResourceReference jquery =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery-1.7.1.js");
    public static final JavascriptResourceReference jquery_textselection =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.textselection-1.0.0.js");
    public static final JavascriptResourceReference jquery_easing =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.easing-1.3.js");
    public static final JavascriptResourceReference jquery_placeholder =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.placeholder-1.0.1.js");
    public static final JavascriptResourceReference jquery_zclip =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery.zclip-1.0.0.js");
    public static final JavascriptResourceReference jquery_zclip_swf =
            new JavascriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/ZeroClipboard.swf");
}
