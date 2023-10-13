package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.dto.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LongTermSocketManager implements SocketManager {
    private ServerSocket serverSocket;
    private Socket FSocket;
    private Socket GSocket;
    private ObjectOutputStream outputStreamF;
    private ObjectInputStream inputStreamF;
    private ObjectOutputStream outputStreamG;
    private ObjectInputStream inputStreamG;

    private void initStreams() throws IOException {
        outputStreamF = new ObjectOutputStream(FSocket.getOutputStream());
        inputStreamF = new ObjectInputStream(FSocket.getInputStream());
        outputStreamG = new ObjectOutputStream(GSocket.getOutputStream());
        inputStreamG = new ObjectInputStream(GSocket.getInputStream());
    }

    public void start() {
        try {
            if (serverSocket != null) {
                throw new IllegalStateException("Server Socket is already open");
            }
            serverSocket = new ServerSocket(7777);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void accept() {
        try {
            FSocket = serverSocket.accept();
            GSocket = serverSocket.accept();
            initStreams();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public CompletableFuture<FunctionOutput> calculateF(CalculationParameters params) {
        return CompletableFuture.supplyAsync(()->runFuture(inputStreamF, outputStreamF, params, currentF, FLock));
    }

    public CompletableFuture<FunctionOutput> calculateG(CalculationParameters params) {
        return CompletableFuture.supplyAsync(()->runFuture(inputStreamG, outputStreamG, params, currentG, GLock));
    }

    @Override
    public void cancelF() {
        cancel(outputStreamF);
    }
    @Override
    public void cancelG() {
        cancel(outputStreamG);
    }


    private void cancel(ObjectOutputStream outputStream) {
        int signal = Signal.RESTART;
        try {
            outputStream.writeObject(new FunctionInput(0, 1L, signal));
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdownF() {
        shutdown(outputStreamF);
    }

    @Override
    public void shutdownG() {
        shutdown(outputStreamG);
    }

    @Override
    public String statusF() {
        return getStatus(inputStreamF, outputStreamF, currentF, FLock);
    }

    @Override
    public String statusG() {
        return getStatus(inputStreamG, outputStreamG, currentG, GLock);
    }

    private String getStatus(ObjectInputStream inputStream, ObjectOutputStream outputStream, BlockingQueue<FunctionOutput> queue, Lock lock) {
        int signal = Signal.STATUS;
        try {
            outputStream.writeObject(new FunctionInput(-1, 1000L, signal));
            outputStream.flush();
            lock.lock();
            Object obj = inputStream.readObject();
            lock.unlock();
            FunctionOutput result = (FunctionOutput) obj;
            return result.details();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void shutdown(ObjectOutputStream outputStream) {
        int signal = Signal.SHUTDOWN;
        try {
            outputStream.writeObject(new FunctionInput(0, 1L, signal));
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FunctionOutput runFuture(ObjectInputStream inputStream, ObjectOutputStream outputStream,
                                     CalculationParameters params, BlockingQueue<FunctionOutput> queue, Lock lock) {
        try {
            provideData(outputStream, params, lock);
            return receiveData(inputStream, params.x(), queue, lock);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final Lock FLock = new ReentrantLock();
    private final Lock GLock = new ReentrantLock();

    private void provideData(ObjectOutputStream outputStream, CalculationParameters params, Lock lock)
            throws IOException {
        int x = params.x();
        long timeout = params.timeout();
        int signal = params.signal();
        outputStream.writeObject(new FunctionInput(x, timeout, signal));
        outputStream.flush();
    }

    private FunctionOutput receiveData(ObjectInputStream inputStream, int waitsFor,
                                       BlockingQueue<FunctionOutput> queue, Lock lock) throws IOException, ClassNotFoundException {
        lock.lock();
        Object obj = inputStream.readObject();
        lock.unlock();
        FunctionOutput result = (FunctionOutput) obj;
        synchronized (queue) {
            queue.add(result);
            queue.notifyAll();
        }
        return getFunctionOutput(waitsFor, queue);
    }

    private final BlockingQueue<FunctionOutput> currentF = new LinkedBlockingQueue<>();
    private final BlockingQueue<FunctionOutput> currentG = new LinkedBlockingQueue<>();

    private FunctionOutput getFunctionOutput(int waitsFor, final BlockingQueue<FunctionOutput> outputQueue) {
        synchronized (outputQueue) {
            while (outputQueue.isEmpty() || outputQueue.peek().origin() != waitsFor) {
                try {
                    outputQueue.wait();
                } catch (InterruptedException e) {
                    return new FunctionOutput("Main", waitsFor, Status.INTERRUPT, 0, "Calculation was interrupted");
                }
            }
            return outputQueue.poll();
        }
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        close(FSocket);
        close(GSocket);
    }

    private void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
