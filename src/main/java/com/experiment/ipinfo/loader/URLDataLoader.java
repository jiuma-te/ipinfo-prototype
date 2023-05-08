package com.experiment.ipinfo.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLDataLoader extends DataLoader {
    public URLDataLoader(String location) {
        super(location);
    }

    @Override
    protected InputStream getDataStream() throws IOException {
        return new URL(location).openStream();
    }
}
