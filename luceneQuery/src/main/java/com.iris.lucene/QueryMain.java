package com.iris.lucene;

import com.google.gson.Gson;
import com.iris.lucene.model.AuditRecordLuceneNew;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QueryMain {
    public static void main(String[] args) throws InterruptedException {
        LuceneQuery luceneQuery = new LuceneQuery();
        try {
            Map<String, Object> map = luceneQuery.facetByFieldName("ruleName,riskLev",18);
//            Map<String, Object> map = luceneQuery.facetByFieldName("riskLev",18);
            System.out.println(new Gson().toJson(map));


            Map<String, Object> map1 = luceneQuery.groupByFieldName("ruleName",10);
            System.out.println(new Gson().toJson(map1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getTotals(luceneQuery);
    }

    private static Integer getTotals(LuceneQuery luceneQuery) {


        Map<String, Object> map = luceneQuery.getTotal();
        Integer total1 = (Integer) map.get("total1");
        Integer total2 = (Integer) map.get("total2");
        Integer total3 = (Integer) map.get("total3");
        Integer total4 = (Integer) map.get("total4");
        int total = (int) map.get("total");
        List<AuditRecordLuceneNew> resultList = (List<AuditRecordLuceneNew>) map.get("list");
        System.out.println(new Date() + "|【[" + 1 + "]：" + total1 + "】||【[" + 2 + "]：" + total2 + "】||【[" + 3 + "]：" + total3 + "】||【[" + 4 + "]：" + total4 + "】 " + "文件夹总数：" + total);
        resultList.forEach(x -> System.out.println(new Gson().toJson(x)));
        return total;
    }
}
