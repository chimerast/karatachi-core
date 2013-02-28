package org.karatachi.wicket.auth;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.core.request.mapper.CryptoMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

public abstract class AuthenticatedWebApplication<R, A extends Annotation>
        extends WebApplication implements
        IUnauthorizedComponentInstantiationListener {

    private final WeakReference<Class<? extends AuthenticatedWebSession<R>>> webSessionClassRef;

    public AuthenticatedWebApplication() {
        webSessionClassRef =
                new WeakReference<Class<? extends AuthenticatedWebSession<R>>>(
                        getWebSessionClass());
    }

    @Override
    protected void internalInit() {
        super.internalInit();

        getSecuritySettings().setAuthorizationStrategy(
                new AuthorizationStrategy<R, A>(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(
                this);

        if (getConfigurationType() == RuntimeConfigurationType.DEPLOYMENT) {
            setRootRequestMapper(new CryptoMapper(getRootRequestMapper(), this));
        }
    }

    public final void onUnauthorizedInstantiation(final Component component) {
        if (component instanceof Page) {
            if (!AuthenticatedWebSession.<R> getSession().isSignedIn()) {
                restartResponseAtSignInPage();
            } else {
                onUnauthorizedPage((Page) component);
            }
        } else {
            throw new UnauthorizedInstantiationException(component.getClass());
        }
    }

    public void restartResponseAtSignInPage() {
        throw new RestartResponseAtInterceptPageException(getSignInPageClass());
    }

    @Override
    public final Session newSession(final Request request,
            final Response response) {
        try {
            return webSessionClassRef.get().getDeclaredConstructor(
                    Request.class).newInstance(request);
        } catch (Exception e) {
            throw new WicketRuntimeException(
                    "Unable to instantiate web session " + getWebSessionClass(),
                    e);
        }
    }

    protected abstract Class<? extends AuthenticatedWebSession<R>> getWebSessionClass();

    protected abstract Class<? extends WebPage> getSignInPageClass();

    protected abstract Class<A> getAnnotationClass();

    protected abstract boolean authorize(R role, A annotation);

    protected void onUnauthorizedPage(final Page page) {
        throw new UnauthorizedInstantiationException(page.getClass());
    }
}
