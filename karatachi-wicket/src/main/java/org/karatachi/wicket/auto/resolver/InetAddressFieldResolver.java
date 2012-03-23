package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.wicket.auto.FormComponentResolver;
import org.karatachi.wicket.form.field.InetAddressField;
import org.karatachi.wicket.label.InetAddressLabel;

public class InetAddressFieldResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "ipaddress";
    }

    @Override
    public Component createViewComponent(String id) {
        return new InetAddressLabel(id);
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new InetAddressField(id);
    }
}
