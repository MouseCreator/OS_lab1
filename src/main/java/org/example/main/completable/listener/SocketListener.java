package org.example.main.completable.listener;

import org.example.main.completable.CompletableProcessExecutor;
import org.example.main.completable.ProcessExecutor;
import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.socket.SocketManagerImpl;

public class SocketListener {

    private void listen() {
        SocketManagerImpl socketManager = new SocketManagerImpl();
        socketManager.handleClient();

        ProcessExecutor<Integer, Integer> processExecutor = new CompletableProcessExecutor<>();
        ProcessCreator creator = new ProcessCreatorImpl();
        Process processF = creator.startFProcess();
        processF.destroy();
    }

    private void cancelCalculation() {
        // Collect Status F
        // Cancel Future F
        // Destroy process F
    }

}
