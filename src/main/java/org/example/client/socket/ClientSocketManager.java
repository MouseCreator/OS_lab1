package org.example.client.socket;

import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketManager implements ClientSocketIO {

    private final static String address = "127.0.0.1";
    private final static int port = 7777;
    @Override
    public void sendData(String name, int origin, int status, int result, String details) {

        try {
            ProcessResponseDTO processRequestDTO = new ProcessResponseDTO(name, origin, status, result, details);
            System.out.println("To send:" + processRequestDTO.value());
            Socket sendSocket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(sendSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(sendSocket.getInputStream());

            output.writeObject("POST");
            output.writeObject(processRequestDTO);

            System.out.println("Sent:" + processRequestDTO.value());

            sendSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ValueTimeoutRecord receiveData(String name) {
        try {
            Socket receiveSocket = new Socket(address, port);
            ObjectOutputStream output = new ObjectOutputStream(receiveSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(receiveSocket.getInputStream());
            System.out.println("Sent Get request");
            output.writeObject("GET");
            output.writeObject(name);
            ProcessRequestDTO processResponseDTO = (ProcessRequestDTO) input.readObject();
            System.out.println("Closed");
            receiveSocket.close();
            System.out.println("Received:" + processResponseDTO.value());
            return new ValueTimeoutRecord(processResponseDTO.value(), processResponseDTO.timeout(), processResponseDTO.limitAttempts());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
