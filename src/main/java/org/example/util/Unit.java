package org.example.util;

public class Unit {
    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final Unit instance = new Unit();

    private Unit() {
    }

    public static Unit getInstance() {
        return instance;
    }
}
