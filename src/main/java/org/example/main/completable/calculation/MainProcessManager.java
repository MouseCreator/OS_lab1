package org.example.main.completable.calculation;

import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.socket.SocketManager;
import org.example.main.completable.socket.SocketManagerImpl;


public class MainProcessManager implements AutoCloseable {
    private Process processF;
    private Process processG;
    private SocketManager socketManager;
    private final ProcessCreator processCreator = new ProcessCreatorImpl();
    public SocketManager start() {
        if (socketManager != null) {
            throw new IllegalStateException("Main process manager is already running!");
        }
        socketManager = new SocketManagerImpl();
        processF = processCreator.startFProcess();
        processG = processCreator.startGProcess();
        return socketManager;
    }

    public void close() {
        if (processF != null) {
            processF.destroy();
        }
        if (processG != null) {
            processG.destroy();
        }
        if (socketManager != null) {
            socketManager.close();
        }
    }
}
