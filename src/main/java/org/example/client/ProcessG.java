package org.example.client;

import org.example.client.calculator.LongTermCalculator;
import org.example.client.computation.Computation;
import org.example.client.socket.LongTermClientSocketIO;
import org.example.client.socket.LongTermClientSocketManager;

public class ProcessG {
    public static void main(String[] args) {
        LongTermClientSocketIO clientSocketIO = new LongTermClientSocketManager();
        Computation computation = new Computation(10);
        LongTermCalculator commonCalculator = new LongTermCalculator(clientSocketIO, computation, "Process G");
        clientSocketIO.connect();
        commonCalculator.calculate();
        clientSocketIO.close();
    }
}
