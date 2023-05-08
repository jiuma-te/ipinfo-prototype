package com.experiment.ipinfo.loader;

import java.io.IOException;


public interface StringParser<T> {
    T parse(String data) throws IOException;
}
