package org.karatachi.example.web.net;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;

public class DownloadDataDirectPage extends WebPage {
    private static final long serialVersionUID = 1L;

    @Override
    protected void onConfigure() {
        super.onConfigure();

        IRequestHandler handler = new IRequestHandler() {
            @Override
            public void respond(IRequestCycle requestCycle) {
                try {
                    WebResponse response =
                            (WebResponse) requestCycle.getResponse();
                    response.setContentType("application/pdf");
                    response.setAttachmentHeader("test.pdf");
                    response.write(IOUtils.toByteArray(getClass().getResourceAsStream(
                            "test.pdf")));
                } catch (IOException e) {
                    throw new WicketRuntimeException(e);
                }
            }

            @Override
            public void detach(IRequestCycle requestCycle) {
            }
        };

        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }
}
