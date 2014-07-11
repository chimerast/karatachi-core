package org.karatachi.wicket.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.pageStore.IPageStore;

public class NoSerializePageStore implements IPageStore {
    private final int cacheSize;

    private ConcurrentHashMap<String, Map<Integer, IManageablePage>> cache;

    public NoSerializePageStore(int cacheSize) {
        this.cacheSize = cacheSize;
        this.cache =
                new ConcurrentHashMap<String, Map<Integer, IManageablePage>>();
    }

    @Override
    public void destroy() {
        cache = null;
    }

    @Override
    public IManageablePage getPage(String sessionId, int pageId) {
        Map<Integer, IManageablePage> sessionCache =
                getSessionCache(sessionId, false);
        if (sessionCache == null) {
            return null;
        }

        synchronized (sessionCache) {
            return sessionCache.get(pageId);
        }
    }

    @Override
    public void removePage(String sessionId, int pageId) {
        Map<Integer, IManageablePage> sessionCache =
                getSessionCache(sessionId, false);
        if (sessionCache == null) {
            return;
        }

        synchronized (sessionCache) {
            sessionCache.remove(pageId);
        }
    }

    @Override
    public void storePage(String sessionId, IManageablePage page) {
        Map<Integer, IManageablePage> sessionCache =
                getSessionCache(sessionId, true);
        if (sessionCache == null) {
            return;
        }

        synchronized (sessionCache) {
            sessionCache.put(page.getPageId(), page);
        }
    }

    @Override
    public void unbind(String sessionId) {
        cache.remove(sessionId);
    }

    @Override
    public Serializable prepareForSerialization(String sessionId, Object page) {
        return null;
    }

    @Override
    public Object restoreAfterSerialization(Serializable serializable) {
        return null;
    }

    @Override
    public IManageablePage convertToPage(Object page) {
        return (IManageablePage) page;
    }

    private Map<Integer, IManageablePage> getSessionCache(String sessionId,
            boolean create) {
        Map<Integer, IManageablePage> sessionCache = cache.get(sessionId);
        if (sessionCache == null) {
            if (create) {
                HashMap<Integer, IManageablePage> newCache = newSessionCache();
                sessionCache = cache.putIfAbsent(sessionId, newCache);
                if (sessionCache == null) {
                    sessionCache = newCache;
                }
            }
        }
        return sessionCache;
    }

    private LinkedHashMap<Integer, IManageablePage> newSessionCache() {
        // LRU Cache
        return new LinkedHashMap<Integer, IManageablePage>(cacheSize, 0.75f,
                true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(
                    Map.Entry<Integer, IManageablePage> eldest) {
                return size() > cacheSize;
            }
        };
    }
}
