package org.example.client;

import org.example.client.calculator.LongTermCalculator;
import org.example.client.computation.Computation;
import org.example.client.socket.ClientSocketMock;
import org.example.client.socket.LongTermClientSocketIO;

public class ProcessG {
    public static void main(String[] args) {
        LongTermClientSocketIO clientSocketIO = new ClientSocketMock();
        Computation computation = new Computation(10);
        LongTermCalculator commonCalculator = new LongTermCalculator(clientSocketIO, computation, "Process G");
        System.out.println("G starts!");
        clientSocketIO.connect();
        commonCalculator.calculate();
        clientSocketIO.close();
        System.out.println("G closes!");
    }
}
