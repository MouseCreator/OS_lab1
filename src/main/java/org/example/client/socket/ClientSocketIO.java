package org.example.client.socket;


import org.example.main.completable.dto.FunctionInput;

/**
 * Socket manager on client side (for F and G)
 */
public interface ClientSocketIO {
    /**
     *
     * @return data from server
     */
    FunctionInput receiveData();

    /**
     * Sends data to server
     * @param name - process name
     * @param origin - x value (origin)
     * @param status - process status
     * @param result - result f(x)
     * @param details - calculation details message
     */
    void sendData(String name, int origin, int status, int result, String details);
}
