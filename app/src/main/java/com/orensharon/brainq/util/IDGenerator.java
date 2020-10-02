package com.orensharon.brainq.util;

public class IDGenerator {

    private static int id=0;
    public static int generate() {
        return ++id;
    }
}
