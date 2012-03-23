package org.karatachi.example.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.karatachi.wicket.system.PackageLinkList;

public abstract class WebBasePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public WebBasePage() {
        add(new Label("title", getPageTitle()));
        add(new Label("pageTitle", getPageTitle()));

        add(new PackageLinkList("packages", "org.karatachi.example.web"));
    }

    protected String getPageTitle() {
        return getClass().getSimpleName();
    }
}
