package com.suchaos.nativeAPI.read;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 使用同步 API 获取结点数据内容
 *
 * @author suchao
 * @date 2020/2/4
 */
public class ZookeeperGetDateSync implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        String path = "/zk-book";

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                new ZookeeperGetDateSync());
        latch.await();
        System.out.println(zooKeeper.getState());

        //zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zooKeeper.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(new String(zooKeeper.getData(path, true, stat)));
        System.out.println(stat.getCzxid() + ", " + stat.getMzxid() + ", " + stat.getVersion());

        zooKeeper.setData(path, "456".getBytes(), -1);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                latch.countDown();
            } else if (Event.EventType.NodeDataChanged == watchedEvent.getType()) {
                try {
                    System.out.println(new String(zooKeeper.getData(watchedEvent.getPath(), true, stat)));
                    System.out.println(stat.getCzxid() + ", " + stat.getMzxid() + ", " + stat.getVersion());
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
