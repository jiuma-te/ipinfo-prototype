package com.experiment.ipinfo.loader;

import java.io.IOException;

@FunctionalInterface
public interface DataParser<T> {
    T parse(String data) throws IOException;
}
