package org.karatachi.wicket.dialog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 裏でThreadを起動するダイアログのためのパラメータ。
 * リクエスト間でオブジェクトを共有する必要があるため意図的にSerializableを外してある。
 * このクラスを使用する際は、Application.init()でPageManagerProviderに
 * {@link org.karatachi.wicket.core.NoSerializePageManagerProvider}
 * を設定し、シリアライズを行わないようにする必要がある。
 * 
 * @see org.karatachi.wicket.core.NoSerializePageManagerProvider
 */
public abstract class ProgressDialogParams implements Runnable {

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
        System.out.println(this);
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
