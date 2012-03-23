package org.karatachi.wicket.listview;

import java.util.AbstractList;
import java.util.List;

import org.apache.wicket.markup.html.list.PageableListView;

public abstract class LazyLoadingList<E> extends AbstractList<E> {
    private PageableListView<E> pageable;
    private boolean initialized;
    private int offset;
    private int limit;
    private int size;
    private List<E> cache;

    public LazyLoadingList(PageableListView<E> pageable) {
        this.pageable = pageable;
        this.initialized = false;
        this.size = count();
    }

    private void init() {
        this.offset = pageable.getCurrentPage() * pageable.getRowsPerPage();
        this.limit = pageable.getRowsPerPage();
        this.cache = load(offset, limit);
        this.initialized = true;
    }

    public abstract List<E> load(int offset, int limit);

    public abstract int count();

    @Override
    public final E get(int index) {
        if (!initialized) {
            init();
        }
        return cache.get(index - offset);
    }

    @Override
    public final int size() {
        return size;
    }
}
