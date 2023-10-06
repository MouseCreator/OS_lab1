package org.example.client.socket;

import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketAtomManager implements AtomClientSocketIO{
    private final static String address = "127.0.0.1";
    private final static int port = 7777;
    private Socket socket;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    public void connect() {
        try {
            socket = new Socket(address, port);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ValueTimeoutRecord receiveData(String name) {
        try {
            ProcessRequestDTO processResponseDTO = (ProcessRequestDTO) inputStream.readObject();
            return new ValueTimeoutRecord(processResponseDTO.value(), processResponseDTO.timeout(), processResponseDTO.limitAttempts());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendData(String name, int origin, int status, int result, String details) {
        try {
            ProcessResponseDTO response = new ProcessResponseDTO(name, origin, status, result, details);
            outputStream.writeObject(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
