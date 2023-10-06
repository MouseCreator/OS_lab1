package org.example.main;


import java.io.IOException;

public class ExecutionManagerImpl implements ExecutionManager {

    private final FunctionExecutor functionExecutor;

    public ExecutionManagerImpl(FunctionExecutor functionExecutor) {
        this.functionExecutor = functionExecutor;
    }

    public int execute(int x, long timeout) throws Exception {
        throw new UnsupportedOperationException();

    }

    @Override
    public void close() {
        try {
            functionExecutor.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try {
            functionExecutor.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
