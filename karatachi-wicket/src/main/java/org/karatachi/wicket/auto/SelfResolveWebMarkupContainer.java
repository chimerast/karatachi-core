package org.karatachi.wicket.auto;

public class SelfResolveWebMarkupContainer extends
        AutoResolveWebMarkupContainer<SelfResolveWebMarkupContainer> {
    private static final long serialVersionUID = 1L;

    public SelfResolveWebMarkupContainer(String id) {
        super(id);
        setModelObject(this);
    }
}
