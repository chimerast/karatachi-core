package org.karatachi.wicket.auth;

import java.lang.annotation.Annotation;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.request.CryptedUrlWebRequestCodingStrategy;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;

public abstract class AuthenticatedWebApplication<R, A extends Annotation>
        extends WebApplication implements
        IUnauthorizedComponentInstantiationListener {
    @Override
    protected void internalInit() {
        super.internalInit();

        getSecuritySettings().setAuthorizationStrategy(
                new AuthorizationStrategy<R, A>(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(
                this);
    }

    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor() {
        if (getConfigurationType().equals(Application.DEPLOYMENT)) {
            return new WebRequestCycleProcessor() {
                @Override
                protected IRequestCodingStrategy newRequestCodingStrategy() {
                    return new CryptedUrlWebRequestCodingStrategy(
                            new WebRequestCodingStrategy());
                }
            };
        } else {
            return super.newRequestCycleProcessor();
        }
    }

    public final void onUnauthorizedInstantiation(final Component component) {
        if (component instanceof Page) {
            if (!AuthenticatedWebSession.<R> getSession().isSignedIn()) {
                throw new RestartResponseAtInterceptPageException(
                        getSignInPageClass());
            } else {
                throw new UnauthorizedInstantiationException(
                        component.getClass());
            }
        } else {
            throw new UnauthorizedInstantiationException(component.getClass());
        }
    }

    @Override
    public final Session newSession(final Request request,
            final Response response) {
        try {
            return getWebSessionClass().getDeclaredConstructor(Request.class).newInstance(
                    request);
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
}
