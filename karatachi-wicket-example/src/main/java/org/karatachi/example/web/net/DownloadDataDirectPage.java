package org.karatachi.example.web.net;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebResponse;

public class DownloadDataDirectPage extends WebPage {
    @Override
    public void onPageAttached() {
        getRequestCycle().setRequestTarget(new IRequestTarget() {

            @Override
            public void detach(RequestCycle requestCycle) {
            }

            @Override
            public void respond(RequestCycle requestCycle) {
                requestCycle.getResponse().setContentType("application/pdf");
                WebResponse response =
                        ((WebResponse) requestCycle.getResponse());
                response.setAttachmentHeader("test.pdf");
                requestCycle.getResponse().write(
                        getClass().getResourceAsStream("test.pdf"));
            }
        });
    }
}
