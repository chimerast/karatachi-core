package org.karatachi.wicket.auto;

import org.apache.wicket.model.CompoundPropertyModel;

public class SelfResolveForm extends AutoResolveForm<SelfResolveForm> {
    private static final long serialVersionUID = 1L;

    public SelfResolveForm(String id) {
        this(id, null);
    }

    public SelfResolveForm(String id, String feedbackId) {
        super(id, feedbackId);
        setModel(new CompoundPropertyModel<SelfResolveForm>(this));
    }
}
