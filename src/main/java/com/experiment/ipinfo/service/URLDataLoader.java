package com.experiment.ipinfo.service;

import com.experiment.ipinfo.loader.DataLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLDataLoader extends DataLoader {

    public URLDataLoader(String location) {
        super(location);
    }

    protected InputStream openStream() throws IOException {
        return new URL(location).openStream();
    }
}
