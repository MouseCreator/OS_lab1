package org.example.client.calculator;

import org.example.client.socket.*;
import org.example.function.Function;
import org.example.main.completable.dto.Status;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CalculatorAtom implements CommonCalculator{

    private final AtomClientSocketIO clientSocketIO;

    public CalculatorAtom(AtomClientSocketIO clientSocketIO) {
        this.clientSocketIO = clientSocketIO;
    }

    public void calculate(Function<Integer, Integer> function, String name) {
        ValueTimeoutRecord valueTimeoutRecord = clientSocketIO.receiveData(name);
        int x = valueTimeoutRecord.x();
        long timeout = valueTimeoutRecord.timeoutMillis();
        int limit = valueTimeoutRecord.limit();
        Executor executor = new Executor(x, limit, function);
        try {
            Optional<Optional<Integer>> result = executor.execute().get(timeout, TimeUnit.MILLISECONDS);
            System.out.println("G done!");
            if (result.isEmpty()) {
                clientSocketIO.sendData(name, x, Status.FATAL_ERROR, 0,
                        "Calculation finished with error");
                return;
            }
            if (result.get().isEmpty()) {
                clientSocketIO.sendData(name, x, Status.LIGHT_ERROR_LIMIT, 0,
                        "Calculation finished with light error");
                return;
            }
            int fx = result.get().get();
            System.out.println("Case result");
            clientSocketIO.sendData(name, x, Status.SUCCESS, fx, "Calculation success!");
        } catch (ExecutionException e) {
            clientSocketIO.sendData(name, x, Status.FATAL_ERROR, 0,
                    "Calculation finished with execution error");
        } catch (TimeoutException e) {
            clientSocketIO.sendData(name, x, Status.TIMEOUT, 0,
                    "Execution timeout. Total light errors: " + executor.getLightErrors());
        } catch (InterruptedException e) {
            clientSocketIO.sendData(name, x, Status.INTERRUPT, 0, "Calculation was interrupted");
        }

    }

}
