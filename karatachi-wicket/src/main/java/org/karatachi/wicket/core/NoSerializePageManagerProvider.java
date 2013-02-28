package org.karatachi.wicket.core;

import org.apache.wicket.Application;
import org.apache.wicket.DefaultPageManagerProvider;
import org.apache.wicket.page.IPageManagerContext;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.pageStore.IPageStore;
import org.apache.wicket.pageStore.memory.HttpSessionDataStore;
import org.apache.wicket.pageStore.memory.PageNumberEvictionStrategy;

/**
 * デフォルト動作のSerializeを行わないPageManagerProvider。
 * 
 * <pre>
 * {@code
 * setPageManagerProvider(new NoSerializePageManagerProvider(this,
 *     getPageManagerContext()));
 * }
 * </pre>
 */
public class NoSerializePageManagerProvider extends DefaultPageManagerProvider {

    private final IPageManagerContext pageManagerContext;
    private final int pagesNumber;

    public NoSerializePageManagerProvider(Application application,
            IPageManagerContext pageManagerContext, int pagesNumber) {
        super(application);

        this.pageManagerContext = pageManagerContext;
        this.pagesNumber = pagesNumber;
    }

    @Override
    protected IDataStore newDataStore() {
        return new HttpSessionDataStore(pageManagerContext,
                new PageNumberEvictionStrategy(pagesNumber));
    }

    @Override
    protected IPageStore newPageStore(IDataStore dataStore) {
        return new NoSerializePageStore();
    }
}
