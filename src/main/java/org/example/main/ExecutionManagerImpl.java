package org.example.main;

import org.example.memoization.MemoizationMap;

import java.util.Optional;

public class ExecutionManagerImpl implements ExecutionManager {

    private final MemoizationMap<Integer> memoizationMap;

    private final FunctionExecutor functionExecutor;

    public ExecutionManagerImpl(MemoizationMap<Integer> memoizationMap, FunctionExecutor functionExecutor) {
        this.memoizationMap = memoizationMap;
        this.functionExecutor = functionExecutor;
    }

    public int execute(int x, long timeout) throws Exception {
        Optional<Integer> memoizationOptional = memoizationMap.get(x);
        if (memoizationOptional.isPresent())
            return memoizationOptional.get();
        functionExecutor.start();
        int result = functionExecutor.run(x, timeout);
        functionExecutor.close();
        memoizationMap.put(x, result);
        return result;
    }
}
