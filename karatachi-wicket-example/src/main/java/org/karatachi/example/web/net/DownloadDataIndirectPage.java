package org.karatachi.example.web.net;

import org.apache.wicket.core.util.resource.PackageResourceStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;

public class DownloadDataIndirectPage extends WebPage {
    private static final long serialVersionUID = 1L;

    @Override
    protected void onConfigure() {
        super.onConfigure();

        IRequestHandler handler = new IRequestHandler() {
            @Override
            public void respond(IRequestCycle requestCycle) {
                PackageResourceStream rs =
                        new PackageResourceStream(DownloadTestPage.class,
                                "test.pdf");
                requestCycle.scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        rs, "test.pdf"));
            }

            @Override
            public void detach(IRequestCycle requestCycle) {
            }
        };

        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }
}
