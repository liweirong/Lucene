package com.iris.lucene;

import com.google.gson.Gson;
import com.iris.lucene.model.AuditRecordWithBLOBs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class QueryMain {
    public static void main(String[] args)  {
        LuceneQuery luceneQuery = new LuceneQuery();
//        Integer start = 0;
        int min = 10;
        Long[] total  = new Long[min];
        for (int i = 0; i < min; i++) {
            long startTime = System.currentTimeMillis();
            Map<String, Object> map = luceneQuery.getTotal();
            long endTime = System.currentTimeMillis();
            total[i] = endTime - startTime;
            System.out.println(map.get("total")+"条数据中检索到"+map.get("totalFind")+"耗时（ms）"+total[i]);
        }
        System.out.println("-----------------------------------");
        System.out.println(new Gson().toJson(total));
        System.out.println(Arrays.stream(total).count()/min);
        System.out.println("-----------------------------------");
        Map<String, Object> map = luceneQuery.getTotal();
        ArrayList<AuditRecordWithBLOBs> list = (ArrayList<AuditRecordWithBLOBs>) map.get("list");
        list.forEach(audit-> System.out.println(new Gson().toJson(audit)));

//        System.out.println(min + "分钟的采样周期内一共增长数据" + add + "条，sql/qps = " + (add / (min * 60)));
    }

//    private static Integer getTotals(LuceneQuery luceneQuery) {
//        Map<String, Object> map = luceneQuery.getTotal();
//        Integer total1 = (Integer) map.get("total1");
//        Integer total2 = (Integer) map.get("total2");
//        Integer total3 = (Integer) map.get("total3");
//        Integer total4 = (Integer) map.get("total4");
//        Integer total = (Integer) map.get("total");
//        System.out.println(new Date() + "|【[" + 1 + "]：" + total1 + "】||【[" + 2 + "]：" + total2 +"】||【[" + 3 + "]：" + total3 +"】||【[" + 4 + "]：" + total4 + "】 " + "文件夹总数：" + total);
//        return total;
//    }
}
