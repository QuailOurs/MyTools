package com.bj58.sec.test.demo;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class EsClient {
    private static EsClient INSTANCE = new EsClient();

    private Client client;

    public static EsClient getInstance() {
        return INSTANCE;
    }

    public void init(String masterIp, Integer masterPort, String clusterName) {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true)
                .build();

        this.client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(masterIp, masterPort));
    }

    public void insert(String index , String type , String info) {
        this.client.prepareIndex(index, type)
                .setSource(info)
                .execute()
                .actionGet();
    }
}
