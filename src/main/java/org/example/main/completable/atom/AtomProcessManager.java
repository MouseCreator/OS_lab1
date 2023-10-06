package org.example.main.completable.atom;

import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.socket.SocketManagerAtom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AtomProcessManager {
    private Process processF;
    private Process processG;
    private SocketManagerAtom socketManager;
    private final ProcessCreator processCreator = new ProcessCreatorImpl();
    public SocketManagerAtom start() {
        if (socketManager != null) {
            throw new IllegalStateException("Main process manager is already running!");
        }
        socketManager = new SocketManagerAtom();
        processF = processCreator.startFProcess();
        processG = processCreator.startGProcess();
        new Thread(()->{
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(processG.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return socketManager;
    }

    public void close() {
        if (socketManager != null) {
            socketManager.close();
        }
        if (processF != null) {
            processF.destroy();
        }
        if (processG != null) {
            processG.destroy();
        }
    }
}
