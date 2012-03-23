package org.karatachi.wicket.listview;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.PropertyModel;

public class AjaxSimplePageableListViewNavigator extends AjaxPagingNavigator {
    private static final long serialVersionUID = 1L;

    public AjaxSimplePageableListViewNavigator(String id,
            PageableListView<?> pageableListView) {
        super(id, pageableListView);
        add(new Label("headline", new PropertyModel<String>(this,
                "headlineText")));
    }

    @SuppressWarnings("unchecked")
    public String getHeadlineText() {
        PageableListView pageable = (PageableListView) getPageable();
        int firstListItem =
                pageable.getCurrentPage() * pageable.getRowsPerPage();
        int endListItem = pageable.getList().size();
        if (firstListItem + pageable.getRowsPerPage() < endListItem) {
            endListItem = firstListItem + pageable.getRowsPerPage();
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
    protected void onBeforeRender() {
        setVisible(getPageable().getPageCount() > 1);
        super.onBeforeRender();
    }

    @Override
    protected boolean callOnBeforeRenderIfNotVisible() {
        return true;
    }
}
