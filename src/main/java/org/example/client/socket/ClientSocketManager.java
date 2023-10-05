package org.example.client.socket;

import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketManager {

    private final static String address = "127.0.0.1";
    private final static int port = 7777;
    private void sendData(String name, int status, int result, String details) {
        try {
            ProcessResponseDTO processRequestDTO = new ProcessResponseDTO(name, status, result, details);
            Socket sendSocket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(sendSocket.getOutputStream());

            output.writeObject("POST");
            output.writeObject(processRequestDTO);

            sendSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ValueTimeoutRecord receiveData() {
        try {
            Socket receiveSocket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(receiveSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(receiveSocket.getInputStream());

            output.writeObject("GET");
            ProcessRequestDTO processResponseDTO = (ProcessRequestDTO) input.readObject();

            receiveSocket.close();
            return new ValueTimeoutRecord(processResponseDTO.value(), processResponseDTO.timeout());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
