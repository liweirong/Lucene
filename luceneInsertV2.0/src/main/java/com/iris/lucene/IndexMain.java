package com.iris.lucene;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class IndexMain {
    private static final Logger log = Logger.getLogger(IndexMain.class);

    private static final String[] BASE_PATH = {"/data/luceneInfoDir/auditRecord1","/data/luceneInfoDir/auditRecord2","/data/luceneInfoDir/auditRecord3","/data/luceneInfoDir/auditRecord4"};
//    private static final int nThreads = Runtime.getRuntime().availableProcessors();
    private static int CORE_POOL_SIZE = BASE_PATH.length ;
    private static int MAX_POOL_SIZE = CORE_POOL_SIZE;
    private static int KEEP_ALIVE_TIME = 30 * 1000;
    private static ThreadPoolExecutor executor;
    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(CORE_POOL_SIZE);
    private static ThreadFactory factory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ThreadPool thread:" + integer.getAndIncrement());
        }
    };

    public static void main(String[] args) {
        System.out.println("CORE_POOL_SIZE:"+CORE_POOL_SIZE);
        //线程池
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                workQueue,
                factory);


        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);
        List<Callable<Map<String, String>>> tasks = new ArrayList<>();


        tasks.add(new IndexDataTask(BASE_PATH[0],executor));
        tasks.add(new IndexDataTask(BASE_PATH[1],executor));
        tasks.add(new IndexDataTask(BASE_PATH[2],executor));
        tasks.add(new IndexDataTask(BASE_PATH[3],executor));

        while (true) {
            try {
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("IndexMain main", e);
                break;
            }
        }

       /* executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                workQueue,
                factory);
        System.out.println(CORE_POOL_SIZE);
        List<Callable<Map<String, String>>> tasks = new ArrayList<>();
        tasks.add(new IndexDataTask(BASE_PATH_1));

        while (true) {
            try {
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("IndexMain main", e);
                break;
            }
        }*/

    }
}
