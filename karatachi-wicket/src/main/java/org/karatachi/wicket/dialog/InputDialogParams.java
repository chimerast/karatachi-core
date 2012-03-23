package org.karatachi.wicket.dialog;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public abstract class InputDialogParams implements Serializable {
    private static final long serialVersionUID = 1L;

    final String message;
    boolean result;
    String input;

    public InputDialogParams(String message) {
        this(message, "");
    }

    public InputDialogParams(String message, String input) {
        this.message = message;
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public abstract void onSuccess(AjaxRequestTarget target);

    public void onCancel(AjaxRequestTarget target) {
    }
}
