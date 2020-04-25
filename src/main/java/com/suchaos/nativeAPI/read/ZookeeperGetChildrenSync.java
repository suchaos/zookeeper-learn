package com.suchaos.nativeAPI.read;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 使用同步 API 获取子节点列表
 *
 * @author suchao
 * @date 2020/2/4
 */
public class ZookeeperGetChildrenSync implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        String path = "/zk-book";

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                new ZookeeperGetChildrenSync());
        latch.await();
        System.out.println(zooKeeper.getState());

        zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zooKeeper.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        List<String> children = zooKeeper.getChildren(path, true);
        children.forEach(System.out::println);

        System.out.println("------------------");

        zooKeeper.create(path + "/c2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                latch.countDown();
            } else if (Event.EventType.NodeChildrenChanged == watchedEvent.getType()) {
                try {
                    System.out.println("Reget child: " + zooKeeper.getChildren(watchedEvent.getPath(), true));
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
