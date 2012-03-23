package org.karatachi.net.rsh;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.karatachi.net.shell.Command;
import org.karatachi.system.ClassPropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RshService implements Runnable, RejectedExecutionHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static int waitingQueueSize;
    public static int minThread;
    public static int maxThread;
    public static int threadKeepAliveTime;
    public static int waitForShutdown;
    static {
        ClassPropertyLoader loader = new ClassPropertyLoader();
        loader.loadIfNotContains(RshService.class.getResourceAsStream("/karatachi.properties"));
        loader.loadIfNotContains(RshService.class.getResourceAsStream("/karatachi-net.properties"));
        loader.setClassProperties(RshService.class);
    }

    private final ServerSocket serverSocket;
    private final Charset charset;
    private final ThreadPoolExecutor executor;
    private final Thread controlServiceThread;
    private final Map<String, Class<? extends Command>> commands;

    public RshService(ServerSocket serverSocket,
            Map<String, Class<? extends Command>> commands, Charset charset) {
        this.serverSocket = serverSocket;
        this.commands = commands;
        this.charset = charset;

        LinkedBlockingQueue<Runnable> queue =
                new LinkedBlockingQueue<Runnable>(waitingQueueSize);
        this.executor =
                new ThreadPoolExecutor(minThread, maxThread,
                        threadKeepAliveTime, TimeUnit.MILLISECONDS, queue,
                        new RshWorkerThreadFactory());
        this.executor.prestartCoreThread();
        this.executor.setRejectedExecutionHandler(this);

        this.controlServiceThread = new Thread(this, "Rsh");
        this.controlServiceThread.start();
    }

    @Override
    public final void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        RshShell task = (RshShell) r;
        try {
            task.sendMessage("503 Max connections");
        } catch (IOException ignore) {
        }
        task.close();
    }

    public final int getPort() {
        return serverSocket.getLocalPort();
    }

    protected boolean checkRemoteAddr(InetSocketAddress addr) {
        return true;
    }

    public final void run() {
        logger.info("Start service thread: port={}",
                serverSocket.getLocalPort());

        while (!Thread.interrupted() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                InetSocketAddress addr =
                        (InetSocketAddress) socket.getRemoteSocketAddress();

                if (checkRemoteAddr(addr)) {
                    executor.execute(new RshShell(socket, commands, charset));
                } else {
                    socket.close();
                    logger.info("Invalid access from {}", addr.getAddress());
                }
            } catch (InterruptedIOException ex) {
                break;
            } catch (SocketException ignore) {
            } catch (IOException e) {
                logger.error("I/O error on accepting connection", e);
                break;
            }
        }

        try {
            serverSocket.close();
        } catch (IOException ignore) {
        }

        logger.info("Stop service thread");
    }

    public final void stop() {
        try {
            serverSocket.close();
            executor.shutdown();
        } catch (IOException e) {
            logger.error("Error on closing socket", e);
        }
    }

    public final void join() throws InterruptedException {
        controlServiceThread.join();
        executor.awaitTermination(waitForShutdown, TimeUnit.MILLISECONDS);
    }
}

class RshWorkerThreadFactory implements ThreadFactory {
    private volatile int ThreadCounter = 1;

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "RshWorker-" + (ThreadCounter++));
    }
}
