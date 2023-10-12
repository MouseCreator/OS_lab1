package org.example.client.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final Thread[] threads;
    private final int size;
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    public ThreadPool(int size) {
        this.threads = new Thread[size];
        this.size = size;
    }

    public void submitTask(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        start();
    }

    private void start() {
        for (int i = 0; i < size; i++) {
            Thread thread = new Thread(new ThreadRunnable());
            threads[i] = thread;
            thread.start();
        }
    }

    public void interruptAll() {
        shutdown();
        start();
    }
    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class ThreadRunnable implements Runnable{
        @Override
        public void run() {
            Runnable task;
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = tasks.poll();
                }
                try {
                    task.run();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
