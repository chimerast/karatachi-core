package org.karatachi.wicket.dialog;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class MessageDialogParams implements Serializable {
    private static final long serialVersionUID = 1L;

    final String message;
    boolean result;

    public MessageDialogParams() {
        this(null);
    }

    public MessageDialogParams(String message) {
        this.message = message;
    }

    public void onSuccess(AjaxRequestTarget target) {
    }

    public void onCancel(AjaxRequestTarget target) {
    }

    public void onClosing(AjaxRequestTarget target) {
    }

    public void onClosed(AjaxRequestTarget target) {
    }
}
