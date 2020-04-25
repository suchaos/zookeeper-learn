package com.suchaos.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * 使用 zkclient
 *
 * @author suchao
 * @date 2020/2/4
 */
public class CreateNode {

    private static final String IP = "127.0.0.1";
    private static final String PORT = "2181";

    private static final String PATH = "/zkclient";

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient(IP + ":" + PORT, 5000);
        System.out.println("Zookeeper session established");

        zkClient.subscribeChildChanges(PATH,
                (s, list) -> System.out.println(s + " 's child changed, currentChilds: " + list));

        Thread.sleep(100);
        zkClient.createPersistent(PATH, true);

        Thread.sleep(100);
        zkClient.createPersistent(PATH + "/c1");

        Thread.sleep(100);
        zkClient.createPersistent(PATH + "/c1/cc1");

        Thread.sleep(100);
        zkClient.createPersistent(PATH + "/c2");

        Thread.sleep(Integer.MAX_VALUE);
    }
}
