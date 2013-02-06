package org.karatachi.wicket.auto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.validator.PatternValidator;
import org.karatachi.wicket.form.behavior.CheckBoxLabel;
import org.karatachi.wicket.form.behavior.ErrorHighlighter;
import org.karatachi.wicket.form.behavior.Placeholder;
import org.karatachi.wicket.form.behavior.ValidationMessage;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class AutoResolveForm<T> extends Form<T> implements IComponentResolver,
        IHeaderContributor {
    private static final long serialVersionUID = 1L;

    public static interface IFormComponentCustomizer extends
            IVisitor<FormComponent<?>, Void>, Serializable {
    }

    public static interface IResolvedFormComponentCustomizer extends
            Serializable {
        public <T> void component(FormComponent<T> formComponent,
                MarkupContainer container, MarkupStream markupStream);
    }

    private boolean confirm;
    private Behavior requiredComponentBorder;
    private IFormComponentCustomizer formComponentCustomizer;
    private List<IResolvedFormComponentCustomizer> resolvedFormComponentCustomizers =
            new ArrayList<IResolvedFormComponentCustomizer>();
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

    public FeedbackPanel getFeedback() {
        return feedback;
    }

    public void setRequiredComponentBorder(Behavior requiredComponentBorder) {
        this.requiredComponentBorder = requiredComponentBorder;
    }

    public void setFormComponentCustomizer(
            IFormComponentCustomizer formComponentCustomizer) {
        this.formComponentCustomizer = formComponentCustomizer;
    }

    public void addResolvedFormComponentCustomizer(
            IResolvedFormComponentCustomizer resolvedFormComponentCustomizer) {
        this.resolvedFormComponentCustomizers.add(resolvedFormComponentCustomizer);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Component resolve(MarkupContainer container,
            MarkupStream markupStream, ComponentTag tag) {
        if (tag.isAutoComponentTag()) {
            return null;
        }

        String tagId = tag.getId();
        String tagName = tag.getName();
        String type = tag.getAttribute("type");

        String wicketType = null, wicketKey = null;
        if (tag.getAttributes().containsKey("wicket:type")) {
            String[] wicketTypeValue =
                    tag.getAttribute("wicket:type").split(":");
            wicketType = wicketTypeValue[0];
            if (wicketTypeValue.length > 1) {
                wicketKey = wicketTypeValue[1];
            }
        }

        FormComponentResolver resolver =
                FormComponentResolver.getResolver(wicketType, type);

        if ("input".equalsIgnoreCase(tagName)) {
            if (resolver == null) {
                return null;
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
                return resolver.createViewComponent(tagId);
            } else {
                return FormComponentResolver.getDefaultResolver().createViewComponent(
                        tagId);
            }
        }

        if (confirm) {
            return resolver.createViewComponent(tagId).setRenderBodyOnly(true);
        }

        FormComponent<?> formcomponent =
                resolver.createFormComponent(tag.getId());

        String label = tag.getAttribute("wicket:label");
        if (label == null) {
            label = tagId;
        }
        label = container.getString(label, null, label);
        formcomponent.setLabel(new Model<String>(label));

        if (formcomponent instanceof CheckBox) {
            formcomponent.add(new CheckBoxLabel(label));
        }

        String placeholder = tag.getAttribute("wicket:placeholder");
        if (placeholder != null) {
            formcomponent.add(new Placeholder(placeholder));
        }

        String required = tag.getAttribute("wicket:required");
        if (required != null) {
            formcomponent.setRequired(Boolean.parseBoolean(required));
            if (requiredComponentBorder != null) {
                formcomponent.add(requiredComponentBorder);
            }
        }

        String pattern = tag.getAttribute("wicket:validate");
        if (pattern != null) {
            pattern = container.getString(pattern, null, pattern);
            ((FormComponent<String>) formcomponent).add(new PatternValidator(
                    pattern));
        }

        for (IResolvedFormComponentCustomizer customizer : resolvedFormComponentCustomizers) {
            customizer.component(formcomponent, container, markupStream);
        }

        return formcomponent;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (formComponentCustomizer != null) {
            visitChildren(FormComponent.class, formComponentCustomizer);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery));
        response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery_placeholder));
        response.render(OnDomReadyHeaderItem.forScript("jQuery(':input[placeholder]').placeholder({ blankSubmit: true });"));
    }

    public static class ErrorMessageAppendingCustomizer implements
            IFormComponentCustomizer {
        private static final long serialVersionUID = 1L;

        private Set<FormComponent<?>> visited = new HashSet<FormComponent<?>>();

        @Override
        public void component(FormComponent<?> component, IVisit<Void> visit) {
            if (!visited.contains(component)) {
                visited.add(component);
                component.add(new ValidationMessage());
                component.add(new ErrorHighlighter());
            }
        }
    }
}
