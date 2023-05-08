package com.experiment.ipinfo.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class DataLoader {
    protected final String location;

    public DataLoader(String location) {
        this.location = location;
    }

    protected abstract InputStream openStream() throws IOException;

    public <T> T load(StringParser<T> parser) throws IOException {
        String data= new String(openStream().readAllBytes(), StandardCharsets.UTF_8);
        return parser.parse(data);
    }
}
