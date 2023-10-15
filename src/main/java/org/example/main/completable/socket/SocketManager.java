package org.example.main.completable.socket;

import org.example.main.completable.dto.CalculationParameters;
import org.example.main.completable.dto.FunctionOutput;

import java.util.concurrent.CompletableFuture;

/**
 * Manager for sockets
 * Expects to have main (server) socket and client sockets for F and G
 */
public interface SocketManager extends AutoCloseable {
    /**
     * starts server socket
     */
    void start();

    /**
     * accepts F and G sockets
     */
    void accept();

    /**
     * closes server socket
     */
    void close();

    /**
     *
     * @param params - F function input parameters
     * @return completable future of function result with given parameters
     */
    CompletableFuture<FunctionOutput> calculateF(CalculationParameters params);

    /**
     *
     * @param params - G function input parameters
     * @return completable future of function result with given parameters
     */
    CompletableFuture<FunctionOutput> calculateG(CalculationParameters params);

    /**
     * Cancels calculation of F
     */
    void cancelF();
    /**
     * Cancels calculation of G
     */
    void cancelG();
    /**
     * Closes F socket
     */
    void shutdownF();
    /**
     * Closes G socket
     */
    void shutdownG();
    /**
     * Gets F status
     */
    String statusF();
    /**
     * Gets G status
     */
    String statusG();

    /**
     * @return status of calculation of function F with input parameter {@param x}
     */
    String statusF(int x);
    /**
     * @return status of calculation of function G with input parameter {@param x}
     */
    String statusG(int x);
}
