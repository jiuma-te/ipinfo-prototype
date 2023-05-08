package com.experiment.ipinfo.service

import com.google.common.io.Resources
import spock.lang.Specification

class IPInfoServiceTest extends Specification {

    def 'search tree should be load from location'() {
        given:
        def location = Resources.getResource("ip-ranges.json").toString()
        System.out.println("location = " + location);
        def geoIpLocation = Resources.getResource("geo-ip-feed.csv").toString()
        IPInfoService service = new IPInfoService(location, geoIpLocation)
        when:
        service.init()
        then:
        def ipInfo = service.getIpInfo("3.5.140.7")
        ipInfo.ipPrefix == "3.5.140.0/22"
        ipInfo.region == "ap-northeast-2"
        // There are 3 entries in the file ["AMAZON", "S3", "EC2"]
        ipInfo.service != "AMAZON"
        ipInfo.networkBorderGroup == "ap-northeast-2"
    }
}
