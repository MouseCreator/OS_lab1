package org.example.client;

import org.example.client.calculator.CalculatorAtom;
import org.example.client.socket.AtomClientSocketIO;
import org.example.client.socket.ClientSocketAtomManager;
import org.example.function.GFunction;

public class ProcessG {
    public static void main(String[] args) {
        AtomClientSocketIO clientSocketIO = new ClientSocketAtomManager();
        CalculatorAtom commonCalculator = new CalculatorAtom(clientSocketIO);
        clientSocketIO.connect();
        while (!Thread.interrupted()) {
            System.out.println("G starts!");
            commonCalculator.calculate(new GFunction(), "Process G");
        }
        clientSocketIO.close();
    }
}
