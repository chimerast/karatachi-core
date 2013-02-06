package org.karatachi.wicket.util;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;

public class ApplicationSettings {
    private static Boolean isDevelopmentMode;

    public static boolean isDevelopment() {
        if (isDevelopmentMode != null) {
            return isDevelopmentMode;
        } else if (Application.exists()) {
            isDevelopmentMode =
                    Application.get().getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT;
            return isDevelopmentMode;
        } else {
            return true;
        }
    }
}
