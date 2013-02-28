package org.karatachi.wicket.auto;

public abstract class ChoiceFormComponentResolver<T> extends
        FormComponentResolver {
    protected Class<T> elementType;

    public void setElementType(Class<T> elementType) {
        this.elementType = elementType;
    }
}
