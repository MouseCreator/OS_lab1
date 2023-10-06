package org.example.client;

import org.example.client.calculator.CalculatorAtom;
import org.example.client.socket.AtomClientSocketIO;
import org.example.client.socket.ClientSocketAtomManager;
import org.example.function.FFunction;

public class ProcessF {
    public static void main(String[] args) {
        AtomClientSocketIO clientSocketIO = new ClientSocketAtomManager();
        CalculatorAtom calcAtom = new CalculatorAtom(clientSocketIO);
        clientSocketIO.connect();
        while (!Thread.interrupted()) {
            System.out.println("F starts!");
            calcAtom.calculate(new FFunction(), "Process F");
        }
        clientSocketIO.close();
    }


}
