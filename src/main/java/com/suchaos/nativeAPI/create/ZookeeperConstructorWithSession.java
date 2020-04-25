package com.suchaos.nativeAPI.create;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 创建一个基本的 Zookeeper 会话实例，复用 sessionId 和 sessionPasswd
 *
 * @author suchao
 * @date 2020/2/4
 */
public class ZookeeperConstructorWithSession implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper("192.168.1.7:2181", 5000,
                new ZookeeperConstructorWithSession());
        latch.await();
        System.out.println(zooKeeper.getState());
        long sessionId = zooKeeper.getSessionId();
        byte[] sessionPasswd = zooKeeper.getSessionPasswd();

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                new ZookeeperConstructorWithSession(), 1L, "test".getBytes());

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                new ZookeeperConstructorWithSession(), sessionId, sessionPasswd);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }
}
