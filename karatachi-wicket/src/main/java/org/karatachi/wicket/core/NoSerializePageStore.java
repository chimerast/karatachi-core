package org.karatachi.wicket.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.pageStore.IPageStore;

public class NoSerializePageStore implements IPageStore {

    private Map<String, Map<Integer, IManageablePage>> cache =
            new HashMap<String, Map<Integer, IManageablePage>>();

    public void destroy() {
        cache = null;
    }

    public IManageablePage getPage(String sessionId, int pageId) {
        Map<Integer, IManageablePage> sessionCache =
                getSessionCache(sessionId, false);
        IManageablePage page = sessionCache.get(pageId);
        if (page == null) {
            throw new IllegalArgumentException(
                    "Found this session, but there is no page with id "
                            + pageId);
        }
        return page;
    }

    public void removePage(String sessionId, int pageId) {
        getSessionCache(sessionId, false).remove(pageId);
    }

    public void storePage(String sessionId, IManageablePage page) {
        getSessionCache(sessionId, true).put(page.getPageId(), page);
    }

    public void unbind(String sessionId) {
        cache.remove(sessionId);
    }

    public Serializable prepareForSerialization(String sessionId, Object page) {
        return null;
    }

    public Object restoreAfterSerialization(Serializable serializable) {
        return null;
    }

    public IManageablePage convertToPage(Object page) {
        return (IManageablePage) page;
    }

    private Map<Integer, IManageablePage> getSessionCache(String sessionId,
            boolean create) {
        Map<Integer, IManageablePage> sessionCache = cache.get(sessionId);
        if (sessionCache == null) {
            if (create) {
                sessionCache = new HashMap<Integer, IManageablePage>();
                cache.put(sessionId, sessionCache);
            } else {
                throw new IllegalArgumentException(
                        "There are no pages stored for session id " + sessionId);
            }
        }
        return sessionCache;
    }
}
