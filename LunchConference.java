package com.thoughtwork.plan.access;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.thoughtwork.plan.threadpool.ThrealPool;
import com.thoughtwork.plan.worker.StartWorker;

/**
 * 应用程序的入口
 * 
 * @author shuiming
 * 
 */
public class LunchConference {
    private static ExecutorService service = ThrealPool.executorService;

    static Semaphore semaphore = new Semaphore(3);

    /**
     * 应用程序的入口
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            lunchApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 应用程序设置一些基础变量
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void lunchApplication() throws InterruptedException, ExecutionException {
        ClassLoader classLoader = LunchConference.class.getClassLoader();
        URL url = classLoader.getResource("com/thoughtwork/plan/config/log4j.xml");
        String logPath = System.getProperty("user.dir");
        System.setProperty("log.dir", logPath);
        DOMConfigurator.configure(url);
        Future<Object> oFuture = null;
        for (int i = 0; i < 3; i++) {
            semaphore.acquire();
            oFuture = service.submit(new StartWorker());
            if (((AtomicInteger) oFuture.get()).get() == 3) {
                semaphore.release();
            }
        }
        try {
            semaphore.acquire();
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
