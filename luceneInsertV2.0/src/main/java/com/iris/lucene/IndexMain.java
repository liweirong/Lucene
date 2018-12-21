package com.iris.lucene;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class IndexMain {
    private static final Logger log = Logger.getLogger(IndexMain.class);
    private static int CORE_POOL_SIZE = BaseIndexNew.BASE_PATH.length ;
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

        for (String str:BaseIndexNew.BASE_PATH) {
            tasks.add(new IndexDataTask(str));

        }
//        tasks.add(new IndexDataTask(BASE_PATH[1]));
//        tasks.add(new IndexDataTask(BASE_PATH[2]));
//        tasks.add(new IndexDataTask(BASE_PATH[3]));

        while (true) {
            try {
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("IndexMain main", e);
                break;
            }
        }
    }
}
