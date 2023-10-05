package org.example.client.socket;

import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketManager {

    private final static String address = "localhost";
    private final static int port = 7777;
    private void sendData(ProcessRequestDTO processRequestDTO) {
        try {
            Socket sendSocket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(sendSocket.getOutputStream());

            output.writeObject("POST");
            output.writeObject(processRequestDTO);

            sendSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ProcessResponseDTO receiveData() {
        try {
            Socket receiveSocket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(receiveSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(receiveSocket.getInputStream());

            output.writeObject("GET");
            ProcessResponseDTO processResponseDTO = (ProcessResponseDTO) input.readObject();

            receiveSocket.close();
            return processResponseDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
