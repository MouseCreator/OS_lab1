package org.example.main;

import org.example.memoization.MemoizationMap;

import java.io.IOException;
import java.util.Optional;

public class ExecutionManagerImpl implements ExecutionManager {

    private final MemoizationMap<Integer> memoizationMap;

    private final FunctionExecutor functionExecutor;

    public ExecutionManagerImpl(MemoizationMap<Integer> memoizationMap, FunctionExecutor functionExecutor) {
        this.memoizationMap = memoizationMap;
        this.functionExecutor = functionExecutor;
    }

    public int execute(int x, long timeout) throws Exception {
        try {
            Optional<Integer> memoizationOptional = memoizationMap.get(x);
            if (memoizationOptional.isPresent())
                return memoizationOptional.get();
            functionExecutor.start();
            int result = functionExecutor.run(x, timeout);
            memoizationMap.put(x, result);
            return result;
        } finally {
            functionExecutor.close();
        }
    }

    @Override
    public void close() {
        try {
            this.functionExecutor.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try {
            this.functionExecutor.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
