package org.karatachi.thread;

public interface InterruptableOperation {
    public void interrupt();

    public static final InterruptableOperation NULL_OPERATION = new InterruptableOperation() {
        @Override
        public void interrupt() {
        }
    };
}
