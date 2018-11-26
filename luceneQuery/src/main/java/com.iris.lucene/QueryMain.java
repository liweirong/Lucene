package com.iris.lucene;

import java.util.Date;
import java.util.Map;

public class QueryMain {
    private static final String filePath1 = "/data/lucene/auditRecord1";
    private static final String filePath2 = "/data/lucene/auditRecord2";

    public static void main(String[] args) throws InterruptedException {
        LuceneQuery luceneQuery = new LuceneQuery();
        Integer start = 0;
        Integer last = 0;
        int min = 10;
        for (int i = 0; i < min; i++) {
            Map<String, Object> map = luceneQuery.getTotal();
            Integer total1 = (Integer) map.get("total1");
            Integer total2 = (Integer) map.get("total2");
            Integer total = (Integer) map.get("total");
            System.out.println(new Date() + "【" + filePath1 + "】总数：" + total1 + "||||【" + filePath2 + "】总数：" + total2 + "|||||||" + "两个文件夹总数：" + total);
            if (i == 0) {
                start = total;
            } else if (i == min - 1) {
                last = total;
            }
            Thread.sleep(5 * 1000);
        }
        int add = last - start;
        System.out.println(min + "分钟的采样周期内一共增长数据" + add + "条，sql/qps = " + (add / (min * 60)));
    }
}
