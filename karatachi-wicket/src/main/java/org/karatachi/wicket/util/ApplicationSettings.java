package org.karatachi.wicket.util;

import org.apache.wicket.Application;

public class ApplicationSettings {
    private static Boolean isDevelopmentMode;

    public static boolean isDevelopment() {
        if (isDevelopmentMode != null) {
            return isDevelopmentMode;
        } else if (Application.exists()) {
            isDevelopmentMode =
                    Application.DEVELOPMENT.equalsIgnoreCase(Application.get().getConfigurationType());
            return isDevelopmentMode;
        } else {
            return true;
        }
    }
}
