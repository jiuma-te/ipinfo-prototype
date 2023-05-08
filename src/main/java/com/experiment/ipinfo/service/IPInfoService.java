package com.experiment.ipinfo.service;

import com.experiment.ipinfo.loader.DataLoader;
import com.experiment.ipinfo.loader.IPRangesParser;
import com.experiment.ipinfo.model.IPPrefix;
import com.experiment.ipinfo.model.IPRanges;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressTrie;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class IPInfoService {

    final private String location;

    private final AtomicReference<CloudInfoMap> cloudInfoMapRef = new AtomicReference<>(null);

    public IPInfoService(String location) {
        this.location = location;
    }

    @PostConstruct
    public void init() throws IOException {
        refresh();
    }

    public boolean cloudInfoInitialized() {
        return cloudInfoMapRef.get() != null;
    }

    public IPPrefix getIpInfo(String ipStr) {
        IPAddress ipaddr = new IPAddressString(ipStr).getAddress();
        CloudInfoMap cloudInfoMap = cloudInfoMapRef.get();
        IPv4AddressTrie trie = cloudInfoMap.ipv4Trie;
        IPv4Address addr = ipaddr.toIPv4();
        IPv4Address longestPrefixMatch = trie.longestPrefixMatch(addr);
        System.out.println("Longest prefix match for " + addr + " is " + longestPrefixMatch);
        Multimap<String, IPPrefix> multiMap = cloudInfoMap.ipv4Multimap;
        Collection<IPPrefix> cloudIPs = multiMap.get(longestPrefixMatch.toCanonicalString());
        return Iterables.getLast(cloudIPs, null);
    }

    // configure refresh every minute for test
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void refresh() throws IOException {
        log.info("AWS ip range refreshed at " + Instant.now());

        // throw IOException here
        IPRanges ipRanges = loadData(this.location);

        try {
            CloudInfoMap cloudInfoMap = buildInfoMap(ipRanges);

            // set up new cloud info map
            cloudInfoMapRef.set(cloudInfoMap);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error in building cloud info map", e);
            throw new RuntimeException("Failed to set up cloud info map", e);
        }
    }

    private CloudInfoMap buildInfoMap(IPRanges ipRanges) throws ExecutionException, InterruptedException {
        CloudInfoMap cloudInfoMap = new CloudInfoMap();

        CompletableFuture<Void> setTrie = CompletableFuture.runAsync(
            () -> cloudInfoMap.ipv4Trie = getIPv4AddressTrie(ipRanges)
        );

        CompletableFuture<Void> setMultiMap = CompletableFuture.runAsync(
            () -> cloudInfoMap.ipv4Multimap = getIPv4Multimap(ipRanges)
        );

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(setTrie, setMultiMap);
        allTasks.get();

        return cloudInfoMap;
    }

    private IPv4AddressTrie getIPv4AddressTrie(IPRanges ipRanges) {
        IPv4AddressTrie trie = new IPv4AddressTrie();
        trie.getRoot().setAdded(); // makes 0.0.0.0/0 an added node
        ipRanges.getIpPrefixes().forEach(ipPrefix ->
            trie.add(new IPAddressString(ipPrefix.getIpPrefix()).getAddress().toIPv4())
        );
        return trie;
    }

    private ArrayListMultimap<String, IPPrefix> getIPv4Multimap(IPRanges ipRanges) {
        ArrayListMultimap<String, IPPrefix> multiMap = ArrayListMultimap.create();
        // todo, ensure ip prefix is in canonical format
        ipRanges.getIpPrefixes().forEach(ipPrefix -> multiMap.put(ipPrefix.getIpPrefix(), ipPrefix));
        return multiMap;
    }

    private IPRanges loadData(String url) throws IOException {
        return new URLDataLoader(url).load(new IPRangesParser());
    }

    private static class CloudInfoMap {
        volatile IPv4AddressTrie ipv4Trie;
        volatile ArrayListMultimap<String, IPPrefix> ipv4Multimap;
    }
}

