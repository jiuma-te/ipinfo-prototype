package com.experiment.ipinfo.model;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;



@Data
public class GeoIPInfo {

    // 52.95.139.0/24,CH,CH-ZH,Zurich
    @CsvBindByPosition(position = 0)
    private String ipPrefix;
    @CsvBindByPosition(position = 1)
    private String countryCode;
    @CsvBindByPosition(position = 2)
    private String region;
    @CsvBindByPosition(position = 3)
    private String city;
}
