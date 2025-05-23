package com.dsi.hackathon.exception;

import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@NoArgsConstructor
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public <T>DataNotFoundException(Class<T> tClass, Object id) {
        super("Could not find %s with id: %s".formatted(tClass.getSimpleName(), id));
    }

    public static <T> Supplier<DataNotFoundException> supplier(Class<T> tClass, Object id) {
        return () -> new DataNotFoundException(tClass, id);
    }
}
