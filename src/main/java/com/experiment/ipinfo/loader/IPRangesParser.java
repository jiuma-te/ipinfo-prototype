package com.experiment.ipinfo.loader;

import com.experiment.ipinfo.model.IPRanges;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class IPRangesParser implements StringParser<IPRanges> {
    @Override
    public IPRanges parse(String data) throws IOException {
        return new ObjectMapper().readValue(data, IPRanges.class);
    }
}
