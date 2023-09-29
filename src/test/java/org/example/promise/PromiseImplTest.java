package org.example.promise;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class PromiseImplTest {

    @Test
    void get() {
        PromiseImpl<Integer> promise = new PromiseImpl<>();
        promise.execute(() -> {
            Thread.sleep(1000);
            return 2;
        });

        try {
            assertEquals(2, promise.get());
        } catch (InterruptedException | ExecutionException e) {
            fail();
        }
    }

    @Test
    void getFailure() {
        PromiseImpl<Integer> promise = new PromiseImpl<>();
        promise.execute(() -> {
            throw new IllegalArgumentException("A");
        });
        assertThrows(ExecutionException.class, promise::get);
    }


    @Test
    void getTimed() {
        PromiseImpl<Integer> promise = new PromiseImpl<>();
        promise.execute(() -> {
            Thread.sleep(1000);
            return 2;
        });

        assertThrows(TimeoutException.class, ()-> promise.get(500));
    }

    @Test
    void onCompleteTest() {
        PromiseImpl<Integer> promise = new PromiseImpl<>();
        class IntV {
            private int a = 0;
            void setA(int a) {
                this.a = 4;
            }
            int getA() {
                return a;
            }
        }
        IntV v = new IntV();
        promise.onComplete(() -> v.a = 4);
        promise.execute(() -> 2);
        try {
            promise.get();
        } catch (InterruptedException | ExecutionException e) {
            fail();
        }
        assertEquals(4, v.getA());
    }
}