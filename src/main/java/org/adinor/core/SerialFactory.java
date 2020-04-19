package org.adinor.core;

public class SerialFactory {

    public String produce() {
        return "" + Thread.currentThread().getId() + System.nanoTime();
    }
}
