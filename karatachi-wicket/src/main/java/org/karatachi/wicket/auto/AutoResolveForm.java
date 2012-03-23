package org.karatachi.wicket.auto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.IComponentBorder;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;
import org.karatachi.wicket.form.behavior.ErrorHighlightBehavior;
import org.karatachi.wicket.form.behavior.ValidationMessageBehavior;

public class AutoResolveForm<T> extends Form<T> implements IComponentResolver {
    private static final long serialVersionUID = 1L;

    private boolean confirm;
    private IComponentBorder requiredComponentBorder;
    private IVisitor<FormComponent<?>> formVisitor;
    private FeedbackPanel feedback;

    public AutoResolveForm(String id) {
        this(id, null, (T) null);
    }

    public AutoResolveForm(String id, T object) {
        this(id, null, object);
    }

    public AutoResolveForm(String id, IModel<T> model) {
        this(id, null, model);
    }

    public AutoResolveForm(String id, String feedbackId) {
        this(id, feedbackId, (T) null);
    }

    public AutoResolveForm(String id, String feedbackId, T object) {
        super(id, new CompoundPropertyModel<T>(object));
        commonInit(feedbackId);
    }

    public AutoResolveForm(String id, String feedbackId, IModel<T> model) {
        super(id, new CompoundPropertyModel<T>(model));
        commonInit(feedbackId);
    }

    private void commonInit(String feedbackId) {
        this.confirm = false;
        this.formVisitor = new FormVisitor();
        if (feedbackId != null) {
            add(feedback = new ComponentFeedbackPanel(feedbackId, this));
        }
        init(false);
    }

    protected void init(boolean confirm) {
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        if (this.confirm != confirm) {
            this.confirm = confirm;
            removeAll();
            if (feedback != null) {
                add(feedback);
            }
            init(confirm);
        }
    }

    public void setRequiredComponentBorder(
            IComponentBorder requiredComponentBorder) {
        this.requiredComponentBorder = requiredComponentBorder;
    }

    @SuppressWarnings("unchecked")
    public boolean resolve(MarkupContainer container,
            MarkupStream markupStream, ComponentTag tag) {
        if (tag.isAutoComponentTag()) {
            return false;
        }

        String tagId = tag.getId();
        String tagName = tag.getName();
        String type = tag.getAttributes().getString("type");

        String wicketType = null, wicketKey = null;
        if (tag.getAttributes().containsKey("wicket:type")) {
            String[] wicketTypeValue =
                    tag.getAttributes().getString("wicket:type").split(":");
            wicketType = wicketTypeValue[0];
            if (wicketTypeValue.length > 1) {
                wicketKey = wicketTypeValue[1];
            }
        }

        FormComponentResolver resolver =
                FormComponentResolver.getResolver(wicketType, type);

        if ("input".equalsIgnoreCase(tagName)) {
            if (resolver == null) {
                return false;
            }
        } else if ("textarea".equalsIgnoreCase(tagName)) {
            if (resolver == null) {
                resolver = FormComponentResolver.getTextareaResolver();
            }
        } else if ("select".equalsIgnoreCase(tagName)) {
            if (!(resolver instanceof ChoiceFormComponentResolver)) {
                throw new WicketRuntimeException(
                        "Resolver type should be ChoiceComponentResolver with select tag. "
                                + tagId);
            }
            try {
                Class<?> clazz =
                        Class.forName(container.getString(wicketKey, null,
                                wicketKey));
                ((ChoiceFormComponentResolver) resolver).setElementType(clazz);
            } catch (Exception e) {
                throw new WicketRuntimeException("Class " + wicketKey
                        + " not found.");
            }
        } else {
            if (resolver != null) {
                return container.autoAdd(resolver.createViewComponent(tagId),
                        markupStream);
            } else {
                return container.autoAdd(
                        FormComponentResolver.getDefaultResolver().createViewComponent(
                                tagId), markupStream);
            }
        }

        if (confirm) {
            return container.autoAdd(
                    resolver.createViewComponent(tagId).setRenderBodyOnly(true),
                    markupStream);
        }

        FormComponent<?> formcomponent =
                resolver.createFormComponent(tag.getId());

        String label = tag.getAttributes().getString("wicket:label");
        if (label == null) {
            label = tagId;
        }
        label = container.getString(label, null, label);
        formcomponent.setLabel(new Model<String>(label));

        String required = tag.getAttributes().getString("wicket:required");
        if (required != null) {
            formcomponent.setRequired(Boolean.parseBoolean(required));
            if (requiredComponentBorder != null) {
                formcomponent.setComponentBorder(requiredComponentBorder);
            }
        }

        String pattern = tag.getAttributes().getString("wicket:validate");
        if (pattern != null) {
            pattern = container.getString(pattern, null, pattern);
            ((FormComponent<String>) formcomponent).add(new PatternValidator(
                    pattern));
        }

        return addForRender(formcomponent, container, markupStream);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        visitChildren(FormComponent.class, formVisitor);
    }

    public boolean addForRender(Component component, MarkupContainer container,
            MarkupStream markupStream) {
        container.internalAdd(component);
        component.prepareForRender();
        try {
            if (markupStream == null) {
                component.render();
            } else {
                component.render(markupStream);
            }
        } finally {
            component.afterRender();
        }
        return true;
    }

    private static class FormVisitor implements IVisitor<FormComponent<?>>,
            Serializable {
        private static final long serialVersionUID = 1L;

        private Set<FormComponent<?>> visited = new HashSet<FormComponent<?>>();

        public Object component(FormComponent<?> component) {
            if (!visited.contains(component)) {
                visited.add(component);
                component.add(new ValidationMessageBehavior());
                component.add(new ErrorHighlightBehavior());
            }
            return IVisitor.CONTINUE_TRAVERSAL;
        }
    }
}
