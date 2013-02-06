package org.karatachi.wicket.ssl;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Page;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

public class RequiredSSLWebRequestCycleProcessor extends
        WebRequestCycleProcessor {
    @Override
    public void respond(RequestCycle requestCycle) {
        IRequestTarget requestTarget = requestCycle.getRequestTarget();
        if (requestTarget != null) {
            WebRequest webRequest = (WebRequest) requestCycle.getRequest();
            WebResponse webResponse = (WebResponse) requestCycle.getResponse();
            HttpServletRequest httpServletRequest =
                    webRequest.getHttpServletRequest();

            Class<? extends Page> pageClass = null;
            if (requestTarget instanceof IPageRequestTarget) {
                IPageRequestTarget pageTarget =
                        (IPageRequestTarget) requestTarget;
                pageClass = pageTarget.getPage().getClass();
            } else if (requestTarget instanceof IBookmarkablePageRequestTarget) {
                IBookmarkablePageRequestTarget bookmarkableTarget =
                        (IBookmarkablePageRequestTarget) requestTarget;
                pageClass = bookmarkableTarget.getPageClass();
            }

            if (pageClass != null && !httpServletRequest.isSecure()
                    && pageClass.isAnnotationPresent(RequiredSSL.class)) {
                StringBuffer url = new StringBuffer("https://");
                url.append(httpServletRequest.getServerName());
                url.append(webRequest.getHttpServletRequest().getContextPath());
                url.append(webRequest.getServletPath());
                webResponse.redirect(url.toString());
            } else if (pageClass != null && httpServletRequest.isSecure()
                    && !pageClass.isAnnotationPresent(RequiredSSL.class)) {
                StringBuffer url = new StringBuffer("http://");
                url.append(httpServletRequest.getServerName());
                url.append(webRequest.getHttpServletRequest().getContextPath());
                url.append(webRequest.getServletPath());
                webResponse.redirect(url.toString());
            } else {
                requestTarget.respond(requestCycle);
            }
        }
    }
}
