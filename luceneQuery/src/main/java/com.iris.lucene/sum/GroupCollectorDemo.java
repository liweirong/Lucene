//package com.iris.lucene.sum;
//
//import org.apache.lucene.index.DocValues;
//import org.apache.lucene.index.IndexReader;
//
//import java.io.IOException;
//import java.util.Arrays;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.search.Collector;
//import org.apache.lucene.search.FieldCache;
//import org.apache.lucene.search.Scorer;
//
//public class GroupCollectorDemo  extends Collertor {
//
//
//    private GF gf = new GF();// 保存分组统计结果
//    private String[] fc;// fieldCache
//    private String f;// 统计字段
//    String spliter;
//    int length;
//
//    @Override
//    public void setScorer(Scorer scorer) throws IOException {
//    }
//
//    @Override
//    public void setNextReader(IndexReader reader, int docBase)
//            throws IOException {
//        //读取f的字段值，放入FieldCache中
//        //在这里把所有文档的docid和它的f属性的值放入缓存中，以便获取
//        fc = DocValues.
//        System.out.println("fc:"+Arrays.toString(fc));
//        /**
//         * 先执行setNextReader方法再执行collect方法，
//         *
//         * 打印结果：
//         * fc:[5611, 5611, 5611, 5611, 5611, 5611, 5611, 5611, 5611, 5611]
//         */
//    }
//
//    @Override
//    public void collect(int doc) throws IOException {
//        //因为doc是每个segment的文档编号，需要加上docBase才是总的文档编号
//        // 添加到GroupField中，由GroupField负责统计每个不同值的数目
//        System.out.println(doc+"##"+doc+"##");
//        gf.addValue(fc[doc]);
//        /**
//         * 打印结果：
//         *  0##5611
//         1##5611
//         2##5611
//         3##5611
//         5##5611
//         6##5611
//         9##5611
//         */
//    }
//
//    @Override
//    public boolean acceptsDocsOutOfOrder() {
//        return true;
//    }
//
//    public void setFc(String[] fc) {
//        this.fc = fc;
//    }
//
//    public GF getGroupField() {
//        return gf;
//    }
//
//    public void setSpliter(String spliter) {
//        this.spliter = spliter;
//    }
//
//    public void setLength(int length) {
//        this.length = length;
//    }
//
//    public void setF(String f) {
//        this.f = f;
//    }
//
//}
