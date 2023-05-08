package com.experiment.ipinfo.loader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.experiment.ipinfo.model.GeoIPInfo;
import com.opencsv.bean.CsvToBeanBuilder;



public class GeoIPFeedParser implements StringParser<List<GeoIPInfo>> {
    @Override public List<GeoIPInfo> parse(String data) throws IOException {
        try (Reader reader = new StringReader(data)) {
            List<GeoIPInfo> geoInfos = new CsvToBeanBuilder(reader)
                    .withType(GeoIPInfo.class).build().parse();
            return geoInfos;
        }
    }
}
