package com.suchaos.zkclient;

import org.I0Itec.zkclient.ZkClient;

/**
 * 使用 zkclient
 *
 * @author suchao
 * @date 2020/2/4
 */
public class CreateSession {

    private static final String IP = "192.168.1.7";
    private static final String PORT = "2181";

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient(IP + ":" + PORT, 5000);
        System.out.println("Zookeeper session established");
    }
}
