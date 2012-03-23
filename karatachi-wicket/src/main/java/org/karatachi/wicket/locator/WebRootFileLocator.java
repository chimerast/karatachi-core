package org.karatachi.wicket.locator;

import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.locator.ResourceStreamLocator;

public class WebRootFileLocator extends ResourceStreamLocator {
    private String pagesPath;

    public WebRootFileLocator(String pagesPath) {
        this.pagesPath = pagesPath;
    }

    @Override
    public IResourceStream locate(Class<?> clazz, String path) {
        if (path.indexOf(pagesPath, 0) != -1) {
            IResourceStream located =
                    super.locate(clazz, path.substring(pagesPath.length() + 1));
            if (located != null) {
                return located;
            }
        }
        return super.locate(clazz, path);
    }
}
