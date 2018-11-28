package com.iris.lucene;

import java.util.Date;
import java.util.Map;

public class QueryMain {
    public static void main(String[] args) throws InterruptedException {
        LuceneQuery luceneQuery = new LuceneQuery();
        Integer start = 0;
        int min = 10;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < min; i++) {
            Integer total = getTotals(luceneQuery);
            if (i == 0) {
                start = total;
            }
            Thread.sleep(60 * 1000);
        }
        Integer total = getTotals(luceneQuery);
        long endTime = System.currentTimeMillis();
        int add = total - start;
        System.out.println(endTime - startTime);
        System.out.println(add * 1000 / (endTime - startTime));
        System.out.println(min + "分钟的采样周期内一共增长数据" + add + "条，sql/qps = " + (add / (min * 60)));
    }

    private static Integer getTotals(LuceneQuery luceneQuery) {
        Map<String, Object> map = luceneQuery.getTotal();
        Integer total1 = (Integer) map.get("total1");
        Integer total2 = (Integer) map.get("total2");
        Integer total3 = (Integer) map.get("total3");
        Integer total4 = (Integer) map.get("total4");
        Integer total = (Integer) map.get("total");
        System.out.println(new Date() + "|【[" + 1 + "]：" + total1 + "】||【[" + 2 + "]：" + total2 +"】||【[" + 3 + "]：" + total3 +"】||【[" + 4 + "]：" + total4 + "】 " + "文件夹总数：" + total);
        return total;
    }
}
