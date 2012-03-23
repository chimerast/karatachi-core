package org.karatachi.example.component;

import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.wicket.auto.AutoResolvePageableListView;
import org.karatachi.wicket.listview.LazyLoadingList;
import org.seasar.dao.pager.DefaultPagerCondition;
import org.seasar.dao.pager.PagerCondition;
import org.seasar.wicket.S2WicketFilter;

public abstract class S2PagerListView<E> extends AutoResolvePageableListView<E> {
    private static final long serialVersionUID = 1L;

    public S2PagerListView(String id, int rowsPerPage) {
        super(id, new AbstractReadOnlyModel<List<E>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<E> getObject() {
                throw new IllegalStateException();
            }
        }, rowsPerPage);
        S2WicketFilter filter;
        setModel(new LoadableDetachableModel<List<E>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected final List<E> load() {
                return new LazyLoadingList<E>(S2PagerListView.this) {
                    @Override
                    public List<E> load(int offset, int limit) {
                        PagerCondition condition = newCondition();
                        condition.setOffset(offset);
                        condition.setLimit(limit);
                        return S2PagerListView.this.select(condition);
                    }

                    @Override
                    public int count() {
                        return S2PagerListView.this.count();
                    }
                };
            }
        });
    }

    protected abstract List<E> select(PagerCondition condition);

    protected abstract int count();

    protected PagerCondition newCondition() {
        return new DefaultPagerCondition();
    }
}
