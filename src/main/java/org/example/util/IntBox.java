package org.example.util;

public final class IntBox {
    public int data;

    public IntBox() {
    }

    public IntBox(int data) {
        this.data = data;
    }

    public int getAndIncrement() {
        return data++;
    }
}
