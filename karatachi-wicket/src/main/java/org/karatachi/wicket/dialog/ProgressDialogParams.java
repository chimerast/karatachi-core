package org.karatachi.wicket.dialog;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProgressDialogParams implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    final String message;
    boolean result;
    boolean terminated;
    double progress;
    transient Thread thread;

    public ProgressDialogParams() {
        this(null);
    }

    public ProgressDialogParams(String message) {
        this.message = message;
    }

    public void onSuccess(AjaxRequestTarget target) {
    }

    public void onCancel(AjaxRequestTarget target) {
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) throws InterruptedException {
        this.progress = progress;
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    public final void run() {
        try {
            execute();
            result = true;
        } catch (InterruptedException e) {
            logger.debug("execution interrupted..");
        }
        terminated = true;
    }

    public abstract void execute() throws InterruptedException;
}
