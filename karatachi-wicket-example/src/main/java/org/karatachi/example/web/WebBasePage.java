package org.karatachi.example.web;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.karatachi.example.component.SourceViewPage;
import org.karatachi.wicket.system.PackageLinkList;

public abstract class WebBasePage extends WebPage implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    private final ResourceReference RESET_CSS = new CssResourceReference(
            WebBasePage.class, "reset-fonts-grids.css");
    private final ResourceReference DEFAULT_CSS = new CssResourceReference(
            WebBasePage.class, "default.css");

    public WebBasePage() {
        this(null);
    }

    public WebBasePage(PageParameters parameters) {
        super(parameters);

        PopupSettings popup = new PopupSettings(PopupSettings.SCROLLBARS);
        popup.setWidth(1000);
        popup.setHeight(600);
        add(new BookmarkablePageLink<Void>("source", SourceViewPage.class,
                new PageParameters().add("class", getClass().getName())).setPopupSettings(popup));

        add(new PackageLinkList("packages", "org.karatachi.example.web"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(RESET_CSS));
        response.render(CssHeaderItem.forReference(DEFAULT_CSS));
    }
}
