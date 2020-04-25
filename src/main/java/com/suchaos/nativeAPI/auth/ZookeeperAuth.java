package com.suchaos.nativeAPI.auth;

import com.suchaos.nativeAPI.create.ZookeeperConstructor;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * auth 相关
 *
 * @author suchao
 * @date 2020/2/4
 */
public class ZookeeperAuth implements Watcher {

    private static final String IP = "192.168.1.7";
    private static final String PORT = "2181";

    private static final String PATH = "/zk-book-auth-test";

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper(IP + ":" + PORT, 5000,
                new ZookeeperConstructor());
        zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
        zooKeeper.create(PATH, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
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
