package org.karatachi.example.web.net;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.PackageResourceStream;

public class DownloadDataIndirectPage extends WebPage {
    @Override
    public void onPageAttached() {
        getRequestCycle().setRequestTarget(new IRequestTarget() {

            @Override
            public void detach(RequestCycle requestCycle) {
            }

            @Override
            public void respond(RequestCycle requestCycle) {
                PackageResourceStream rs =
                        new PackageResourceStream(DownloadTestPage.class,
                                "test.pdf");
                requestCycle.setRequestTarget(new ResourceStreamRequestTarget(
                        rs, "test.pdf"));
            }
        });
    }
}
