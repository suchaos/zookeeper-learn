package com.suchaos.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * nodecache
 *
 * @author suchao
 * @date 2020/2/5
 */
public class NodeCacheSample {

    private static final String IP = "192.168.1.7";
    private static final String PORT = "2181";

    private static final String PATH = "/zk-curator";

    private static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(IP + ":" + PORT)
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(PATH, "init".getBytes());

        final NodeCache cache = new NodeCache(client, PATH, false);
        cache.start();
        cache.getListenable().addListener(() ->
                System.out.println("Node data update, new date: " + new String(cache.getCurrentData().getData())));
        client.setData().forPath(PATH, "u".getBytes());
        Thread.sleep(1000);
        client.delete().deletingChildrenIfNeeded().forPath(PATH);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
