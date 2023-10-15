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

public class LongTermSocketManager implements SocketManager {
    private ServerSocket serverSocket;
    private Socket FSocket;
    private Socket GSocket;

    private final SeparateLock FLock = new SeparateLock();
    private final SeparateLock GLock = new SeparateLock();
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

    private String getStatus(ObjectInputStream inputStream, ObjectOutputStream outputStream, BlockingQueue<FunctionOutput> queue, SeparateLock lock) {
        int signal = Signal.STATUS;
        try {
            CalculationParameters calculationParameters = new CalculationParameters(-1, 1000L, signal);
            provideData(outputStream, calculationParameters, lock.read());
            return receiveData(inputStream, calculationParameters, queue, lock.write()).details();
        } catch (IOException e) {
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
                                     CalculationParameters params, BlockingQueue<FunctionOutput> queue, SeparateLock lock) {
        try {
            provideData(outputStream, params, lock.read());
            return receiveData(inputStream, params, queue, lock.write());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void provideData(ObjectOutputStream outputStream, CalculationParameters params, Lock lock)
            throws IOException {
        int x = params.x();
        long timeout = params.timeout();
        int signal = params.signal();
        try {
            lock.lock();
            outputStream.writeObject(new FunctionInput(x, timeout, signal));
            outputStream.flush();
        } finally {
            lock.unlock();
        }

    }

    private FunctionOutput receiveData(ObjectInputStream inputStream, CalculationParameters parameters,
                                       BlockingQueue<FunctionOutput> queue, Lock lock) {
        return awaitResult(inputStream, parameters, lock, queue);
    }

    private final BlockingQueue<FunctionOutput> currentF = new LinkedBlockingQueue<>();
    private final BlockingQueue<FunctionOutput> currentG = new LinkedBlockingQueue<>();

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
    private FunctionOutput awaitResult(ObjectInputStream inputStream, CalculationParameters parameters, Lock lock, BlockingQueue<FunctionOutput> outputQueue) {
        try {
            while (true) {
                if (lock.tryLock()) {
                    Object obj = inputStream.readObject();
                    lock.unlock();
                    FunctionOutput receivedOutput = (FunctionOutput) obj;
                    if (isWaitingFor(parameters, receivedOutput)) {
                        return receivedOutput;
                    }
                    synchronized (outputQueue) {
                        outputQueue.add(receivedOutput);
                        outputQueue.notifyAll();
                    }
                } else {
                    synchronized (outputQueue) {
                        if (outputQueue.isEmpty()) {
                            outputQueue.wait();
                        }
                        if (isWaitingFor(parameters, outputQueue.peek())) {
                            return outputQueue.poll();
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            return new FunctionOutput("Main", parameters.x(), Status.INTERRUPT, 0, "Calculation was interrupted");
        } finally {
            lock.unlock();
        }
    }

    /**
     *
     * @param params - function input
     * @param output - return value of the calculation
     * @return true, if we can assume that output matches input
     */
    private boolean isWaitingFor(CalculationParameters params, FunctionOutput output) {
        return params.signal() == Signal.STATUS && output.processStatus() == Status.STATUS || output.value() == params.x();
    }

}
