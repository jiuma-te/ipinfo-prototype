package com.experiment.ipinfo.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDataLoader extends DataLoader {
    public FileDataLoader(String location) {
        super(location);
    }

    @Override
    protected InputStream openStream() throws IOException {
        return new FileInputStream(location);
    }
}
