package org.karatachi.wicket.system;

import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.karatachi.classloader.PackageDir;

public class PackageMounter {
    public static void mount(String packageName) {
        WebApplication application = WebApplication.get();
        if (application != null) {
            loadClasses(application, packageName, "/");
        }
    }

    private static void loadClasses(WebApplication application,
            String packageName, String path) {

        PackageDir packageDir =
                new PackageDir(Thread.currentThread().getContextClassLoader(),
                        packageName);
        for (Class<? extends Page> clazz : packageDir.getClasses(WebPage.class)) {
            if ((clazz.getModifiers() & Modifier.ABSTRACT) == 0) {
                String pageName =
                        StringUtils.uncapitalize(clazz.getSimpleName());
                if (pageName.endsWith("Page")) {
                    pageName = pageName.substring(0, pageName.length() - 4);
                }
                application.mountPage(path + pageName, clazz);
            }
        }

        for (String child : packageDir.getPackageNames()) {
            loadClasses(application, packageName + "." + child, path + child
                    + "/");
        }
    }
}
