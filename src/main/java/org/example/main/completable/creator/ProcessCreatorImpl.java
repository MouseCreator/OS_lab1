package org.example.main.completable.creator;

import java.io.IOException;

public class ProcessCreatorImpl implements ProcessCreator {

    private Process processF;
    private Process processG;
    @Override
    public Process startFProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        try {
            assert processF == null;
            processF = processBuilder.start();
            return processF;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Process startGProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar2/OS_lab1.jar");
        try {
            assert processG == null;
            processG = processBuilder.start();
            return processG;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (processF != null) {
            processF.destroy();
        }
        if (processG != null) {
            processG.destroy();
        }
    }
}
