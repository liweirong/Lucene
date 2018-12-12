package com.iris.lucene;

import com.google.gson.Gson;
import com.iris.lucene.model.AuditRecordLuceneNew;

import java.util.*;

public class QueryMain {
    public static void main(String[] args) throws InterruptedException {
        LuceneQuery luceneQuery = new LuceneQuery();
//        getTotals(luceneQuery);
//        Integer start = 0;
        int min = 10;
        int[] total = new int[min];
//        Long[] time  = new Long[min];
        long startTime;
        long endTime;
        for (int i = 0; i < min; i++) {
//            startTime = System.currentTimeMillis();
            total[i] = getTotals(luceneQuery);
//            endTime = System.currentTimeMillis();
//            time[i] = endTime - startTime;
//            System.out.println(map.get("total")+"条数据中检索到"+map.get("totalFind")+"耗时（ms）"+total[i]);
            Thread.sleep(60*1000);
        }
//        System.out.println("-----------------------------------");
//        System.out.println(Arrays.stream(total).map(x->x+x));
//        System.out.println("-----------------------------------");
//        Map<String, Object> map = luceneQuery.getTotal();
//        ArrayList<AuditRecordWithBLOBs> list = (ArrayList<AuditRecordWithBLOBs>) map.get("list");
//        list.forEach(audit-> System.out.println(new Gson().toJson(audit)));
        total[min-1] = getTotals(luceneQuery);
        System.out.println(new Gson().toJson(total));
        System.out.println(min + "分钟的采样周期内一共增长数据" + (total[min-1] - total[0]) + "条，sql/qps = " + Arrays.stream(total).average().orElse(Double.NaN) / min);
    }

    private static Integer getTotals(LuceneQuery luceneQuery) {
        Map<String, Object> map = luceneQuery.getTotal();
        Integer total1 = (Integer) map.get("total1");
        Integer total2 = (Integer) map.get("total2");
        Integer total3 = (Integer) map.get("total3");
        Integer total4 = (Integer) map.get("total4");
        Integer total = (Integer) map.get("total");
        List<AuditRecordLuceneNew> resultList = (List<AuditRecordLuceneNew>) map.get("list");
        System.out.println(new Date() + "|【[" + 1 + "]：" + total1 + "】||【[" + 2 + "]：" + total2 + "】||【[" + 3 + "]：" + total3 + "】||【[" + 4 + "]：" + total4 + "】 " + "文件夹总数：" + total);
        System.out.println(new Gson().toJson(resultList));
        return total;
    }
}
