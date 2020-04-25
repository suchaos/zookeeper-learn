package com.suchaos.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 使用 curator
 *
 * @author suchao
 * @date 2020/2/4
 */
public class CreateSessionFluent {

    private static final String IP = "192.168.1.7";
    private static final String PORT = "2181";

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(IP + ":" + PORT)
                .retryPolicy(retryPolicy)
                .namespace("curator")
                .build();
        client.start();
    }
}
