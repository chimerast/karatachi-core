package org.karatachi.wicket.locator;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

public class WebRootServletWebRequest extends ServletWebRequest {
    public WebRootServletWebRequest(HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
    }

    @Override
    public String getRelativePathPrefixToContextRoot() {
        return "";
    }
}
