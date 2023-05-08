package com.experiment.ipinfo.service;

import com.experiment.ipinfo.loader.GeoIPFeedParser;
import com.experiment.ipinfo.loader.IPRangesParser;
import com.experiment.ipinfo.model.GeoIPInfo;
import com.experiment.ipinfo.model.IPv4Prefix;
import com.experiment.ipinfo.model.IPRanges;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressTrie;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;



@Slf4j
public class IPInfoService {
    // Hard-code the AS16509 Amazon AWS autonomous system number and name

    final private String ipRangesLocation;
    final private String geoIPFeedLocation;

    private final AtomicReference<CloudInfoMap> cloudInfoMapRef = new AtomicReference<>(null);

    public IPInfoService(String ipRangesLocation,
                         String geoIPFeedLocation) {
        this.ipRangesLocation = ipRangesLocation;
        this.geoIPFeedLocation = geoIPFeedLocation;
    }

    @PostConstruct
    public void init() throws IOException {
        refresh();
    }

    public boolean cloudInfoInitialized() {
        CloudInfoMap cachedCloudInfo = cloudInfoMapRef.get();

        return cachedCloudInfo != null &&
               cachedCloudInfo.ipv4Trie != null &&
               cachedCloudInfo.ipv4Multimap != null &&
               cachedCloudInfo.ipv4GeoInfoMap != null;
    }

    public IPv4Prefix getIpInfo(String ipStr) {
        IPAddress ipaddr = new IPAddressString(ipStr).getAddress();
        CloudInfoMap cloudInfoMap = cloudInfoMapRef.get();
        IPv4AddressTrie trie = cloudInfoMap.ipv4Trie;
        IPv4Address addr = ipaddr.toIPv4();
        IPv4Address longestPrefixMatch = trie.longestPrefixMatch(addr);
        System.out.println("Longest prefix match for " + addr + " is " + longestPrefixMatch);
        Multimap<String, IPv4Prefix> multiMap = cloudInfoMap.ipv4Multimap;
        Collection<IPv4Prefix> cloudIPs = multiMap.get(longestPrefixMatch.toCanonicalString());
        return Iterables.getLast(cloudIPs, null);
    }

    // configure refresh every minute for test
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void refresh() throws IOException {
        log.info("AWS ip range refreshed at " + Instant.now());

        // throw IOException here
        IPRanges ipRanges = loadIPRanges(this.ipRangesLocation);
        List<GeoIPInfo> geoIPInfos = loadGeoIPFeed(this.geoIPFeedLocation);

        try {
            CloudInfoMap cloudInfoMap = buildInfoMap(ipRanges, geoIPInfos);

            // set up new cloud info map
            cloudInfoMapRef.set(cloudInfoMap);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error in building cloud info map", e);
            throw new RuntimeException("Failed to set up cloud info map", e);
        }
    }

    private CloudInfoMap buildInfoMap(IPRanges ipRanges, List<GeoIPInfo> geoIpFeed) throws ExecutionException, InterruptedException {
        CloudInfoMap cloudInfoMap = new CloudInfoMap();

        CompletableFuture<Void> setTrie = CompletableFuture.runAsync(
            () -> cloudInfoMap.ipv4Trie = getIPv4AddressTrie(ipRanges)
        );

        CompletableFuture<Void> setMultiMap = CompletableFuture.runAsync(
            () -> cloudInfoMap.ipv4Multimap = getIPv4Multimap(ipRanges)
        );

        CompletableFuture<Void> setGeoIpMap = CompletableFuture.runAsync(
                () -> cloudInfoMap.ipv4GeoInfoMap = getIPv4GeoInfoMap(geoIpFeed)
        );

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(setTrie, setMultiMap, setGeoIpMap);
        allTasks.get();

        return cloudInfoMap;
    }

    private Map<String, GeoIPInfo> getIPv4GeoInfoMap(List<GeoIPInfo> geoIpInfos) {
        return geoIpInfos.stream()
                .filter(e ->  new IPAddressString(e.getIpPrefix()).isIPv4Mapped())
                .collect(Collectors.toMap(GeoIPInfo::getIpPrefix, Function.identity()));
    }


    private IPv4AddressTrie getIPv4AddressTrie(IPRanges ipRanges) {
        IPv4AddressTrie trie = new IPv4AddressTrie();
        trie.getRoot().setAdded(); // makes 0.0.0.0/0 an added node
        ipRanges.getIPv4Prefixes().forEach(IPv4Prefix ->
            trie.add(new IPAddressString(IPv4Prefix.getIpPrefix()).getAddress().toIPv4())
        );
        return trie;
    }

    private ArrayListMultimap<String, IPv4Prefix> getIPv4Multimap(IPRanges ipRanges) {
        ArrayListMultimap<String, IPv4Prefix> multiMap = ArrayListMultimap.create();
        // todo, ensure ip prefix is in canonical format
        ipRanges.getIPv4Prefixes().forEach(
                IPv4Prefix -> multiMap.put(IPv4Prefix.getIpPrefix(), IPv4Prefix));
        return multiMap;
    }

    private IPRanges loadIPRanges(String url) throws IOException {
        return new URLDataLoader(url).load(new IPRangesParser());
    }

    private List<GeoIPInfo> loadGeoIPFeed(String url) throws IOException {
        return new URLDataLoader(url).load(new GeoIPFeedParser());
    }

    private static class CloudInfoMap {
        volatile IPv4AddressTrie ipv4Trie;
        volatile ArrayListMultimap<String, IPv4Prefix> ipv4Multimap;
        volatile Map<String, GeoIPInfo> ipv4GeoInfoMap;
    }
}

