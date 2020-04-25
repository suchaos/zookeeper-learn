package com.suchaos.nativeAPI.create;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 创建一个基本的 Zookeeper 会话实例
 *
 * @author suchao
 * @date 2020/2/4
 */
public class ZookeeperConstructor implements Watcher {

    private static final String IP = "192.168.1.7";
    private static final String PORT = "2181";

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper(IP + ":" + PORT, 5000,
                new ZookeeperConstructor());
        latch.await();
        System.out.println(zooKeeper.getState());

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }
}
