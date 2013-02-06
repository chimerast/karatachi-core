package org.karatachi.wicket.listview;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.PropertyModel;

public class SimplePageableListViewNavigator extends PagingNavigator {
    private static final long serialVersionUID = 1L;

    public SimplePageableListViewNavigator(String id, IPageable pageable) {
        super(id, pageable);
        add(new Label("headline", new PropertyModel<String>(this,
                "headlineText")));
    }

    public String getHeadlineText() {
        PageableListView<?> pageable = (PageableListView<?>) getPageable();
        long firstListItem =
                pageable.getCurrentPage() * pageable.getItemsPerPage();
        long endListItem = pageable.getList().size();
        if (firstListItem + pageable.getItemsPerPage() < endListItem) {
            endListItem = firstListItem + pageable.getItemsPerPage();
        }

        if (getLocale().getLanguage().equals("ja")) {
            return String.format("%d個のうち %d番目から %d番目を表示しています。",
                    pageable.getList().size(), firstListItem + 1, endListItem);
        } else {
            return String.format("%d items found, displaying %d to %d.",
                    pageable.getList().size(), firstListItem + 1, endListItem);
        }
    }

    @Override
    protected void onConfigure() {
        setVisible(getPageable().getPageCount() > 1);
    }
}
