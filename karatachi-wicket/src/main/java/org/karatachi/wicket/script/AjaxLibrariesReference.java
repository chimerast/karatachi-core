package org.karatachi.wicket.script;

import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class AjaxLibrariesReference {
    public static final JavaScriptResourceReference jquery =
            new JavaScriptResourceReference(AjaxLibrariesReference.class,
                    "jquery/jquery-1.10.2.js");
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
    public static final PackageResourceReference jquery_zclip_swf =
            new PackageResourceReference(AjaxLibrariesReference.class,
                    "jquery/ZeroClipboard.swf");
}
