package com.suchaos.sample;

/**
 * A simple example program to use DataMonitor to start and
 * stop executables based on a znode. The program watches the
 * specified znode and saves the data that corresponds to the
 * znode in the filesystem. It also starts the specified program
 * with the specified arguments when the znode exists and kills
 * the program if the znode goes away.
 */

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;
import java.util.Arrays;

public class Executor
        implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    String znode;
    DataMonitor dm;
    ZooKeeper zk;
    String filename;
    String[] exec;
    //Process child;
    String child;

    public Executor(String hostPort, String znode, String filename,
                    String[] exec) throws KeeperException, IOException {
        this.filename = filename;
        this.exec = exec;
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            System.err
                    .println("USAGE: Executor hostPort znode filename program [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = args[1];
        String filename = args[2];
        String[] exec = new String[args.length - 3];
        System.arraycopy(args, 3, exec, 0, exec.length);
        try {
            new Executor(hostPort, znode, filename, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************************************
     * We do process any events ourselves, we just need to forward them on.
     *
     */
    @Override
    public void process(WatchedEvent event) {
        dm.process(event);
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    static class StreamWriter extends Thread {
        OutputStream os;

        InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        @Override
        public void run() {
            byte[] b = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
            } catch (IOException ignored) {
            }

        }
    }

    @Override
    public void exists(byte[] data) {
        if (data == null) {
            if (child != null) {
                System.out.println("Killing process");
//                child.destroy();
//                try {
//                    child.waitFor();
//                } catch (InterruptedException ignored) {
//                }
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
//                child.destroy();
//                try {
//                    child.waitFor();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(filename);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Starting child");
//                child = Runtime.getRuntime().exec(exec);
//                new StreamWriter(child.getInputStream(), System.out);
//                new StreamWriter(child.getErrorStream(), System.err);
            child = Arrays.toString(exec);
            new StreamWriter(new ByteArrayInputStream(child.getBytes()), System.out);
            new StreamWriter(new ByteArrayInputStream(child.getBytes()), System.err);
        }
    }
}
