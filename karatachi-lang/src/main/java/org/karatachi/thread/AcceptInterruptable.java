package org.karatachi.thread;

public interface AcceptInterruptable {
    public void setInterruptable(InterruptableOperation interruptable)
            throws InterruptedException;

    public void clearInterruptable();
}
