package org.karatachi.wicket.auth;

import java.lang.annotation.Annotation;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;

public class AuthorizationStrategy<R, A extends Annotation> implements
        IAuthorizationStrategy {
    private AuthenticatedWebApplication<R, A> application;

    public AuthorizationStrategy(AuthenticatedWebApplication<R, A> application) {
        this.application = application;
    }

    public <T extends Component> boolean isInstantiationAuthorized(
            final Class<T> componentClass) {
        boolean authorized = true;

        Package componentPackage = componentClass.getPackage();
        if (componentPackage != null) {
            A packageAnnotation =
                    componentPackage.getAnnotation(application.getAnnotationClass());
            if (packageAnnotation != null) {
                authorized =
                        application.authorize(
                                AuthenticatedWebSession.<R> getSession().getRole(),
                                packageAnnotation);
            }
        }

        A classAnnotation =
                componentClass.getAnnotation(application.getAnnotationClass());
        if (classAnnotation != null) {
            authorized =
                    application.authorize(
                            AuthenticatedWebSession.<R> getSession().getRole(),
                            classAnnotation);
        }

        return authorized;
    }

    public boolean isActionAuthorized(Component component, Action action) {
        return true;
    }
}
