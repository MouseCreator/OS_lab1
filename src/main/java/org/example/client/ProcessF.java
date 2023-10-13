package org.example.client;

import org.example.client.calculator.LongTermCalculator;
import org.example.client.computation.Computation;
import org.example.client.socket.LongTermClientSocketIO;
import org.example.client.socket.LongTermClientSocketManager;

public class ProcessF {
    public static void main(String[] args) {
        LongTermClientSocketIO clientSocketIO = new LongTermClientSocketManager();
        Computation computation = new Computation(15);
        LongTermCalculator calculator = new LongTermCalculator(clientSocketIO, computation, "Process F");
        System.out.println("F starts!");
        clientSocketIO.connect();
        calculator.calculate();
        clientSocketIO.close();
        System.out.println("F closes!");
    }


}
