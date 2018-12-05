package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import com.iris.lucene.model.AuditRecordWithBLOBs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneQuery {

    private static final String[] filePath = {"/data/lucene/auditRecord1", "/data/lucene/auditRecord2", "/data/lucene/auditRecord3", "/data/lucene/auditRecord4"};
    private Directory dir1 = null;
    private Directory dir2 = null;
    private Directory dir3 = null;
    private Directory dir4 = null;
    private static Analyzer analyzer = new IKAnalyzer6x(true);
    /**
     * 表字段开始
     */

    private static final BooleanClause.Occur MUST = BooleanClause.Occur.MUST;
    private static final BooleanClause.Occur MUST_NOT = BooleanClause.Occur.MUST_NOT;


    public Map<String, Object> getTotal() {

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        /**
         * lucene 支持模糊查询，语义查询，短语查询，组合查询等
         * TermQuery BooleanQuery RangeQuery WildCardQuery
         */
//        // id
//        String[] ids = {"1"};
//        Query idQuery = new TermQuery(new Term(id, ids[0]));
//        booleanQuery.add(idQuery, MUST);


        // 操作语句
        Query opQuery;
//        String keyWord = "科技";
        String keyWord = "你的";
        String[] keyWords = keyWord.split("&");// 多个关键字用& 隔开取交集,其他情况是并集
        int size = keyWords.length;
        BooleanClause.Occur[] occurs = new BooleanClause.Occur[size];
        String[] fields = new String[size];
        for (int i = 0; i < size; i++) {
            occurs[i] = MUST;
            fields[i] = operSentence;
        }
        try {
            opQuery = MultiFieldQueryParser.parse(keyWords, fields, occurs, analyzer);
            booleanQuery.add(opQuery, MUST);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int total = 0;
        int totalFind = 0;
        IndexSearcher indexSearcher = null;
        try {
            dir1 = FSDirectory.open(Paths.get(filePath[0]));
            IndexReader reader1 = DirectoryReader.open(dir1);
//            total1 = reader1.maxDoc();


            dir2 = FSDirectory.open(Paths.get(filePath[1]));
            IndexReader reader2 = DirectoryReader.open(dir2);
//            total2 = reader2.maxDoc();

            dir3 = FSDirectory.open(Paths.get(filePath[2]));
            IndexReader reader3 = DirectoryReader.open(dir3);
//            total3 = reader3.maxDoc();

            dir4 = FSDirectory.open(Paths.get(filePath[3]));
            IndexReader reader4 = DirectoryReader.open(dir4);
//            total4 = reader4.maxDoc();

            MultiReader multiReader = new MultiReader(reader1, reader2, reader3, reader4);
            total = multiReader.maxDoc();//所有文档数
            System.out.println("audit_record中所有文档数:" + total);
            indexSearcher = new IndexSearcher(multiReader);
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }


        TopDocs hits;

        try {
            assert indexSearcher != null;
            totalFind = indexSearcher.count(booleanQuery.build());
            // 查询数据， 结束页面自前的数据都会查询到，但是只取本页的数据
            System.out.println("audit_record中total:" + totalFind);

            Sort sort = new Sort(new SortField("happenTime", SortField.Type.LONG, true)); // 时间降序排序
            hits = indexSearcher.search(booleanQuery.build(), 20, sort);
        } catch (IOException e) {
            System.out.println("检索异常" + e);
            return null;
        }

        // 得到得分文档数组
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        List<AuditRecordWithBLOBs> resultList = new ArrayList<>(scoreDocs.length);
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document doc = null;
            try {
                // 取得对应的文档对象
                doc = indexSearcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuditRecordWithBLOBs auditRecord = new AuditRecordWithBLOBs();
            auditRecord.setId(doc.get(id));
            auditRecord.setHappenTime(Long.valueOf(doc.get(happenTime)));
            auditRecord.setMainUuid(doc.get(mainUuid));
            auditRecord.setSrcPort(Integer.valueOf(doc.get(srcPort)));
            auditRecord.setSessionId(doc.get(sessionId));
            auditRecord.setOperTypeId(Byte.valueOf(doc.get(operTypeId)));
            auditRecord.setDbName(doc.get(dbName));
            auditRecord.setTableName(doc.get(tableName));
            auditRecord.setTableNum(Integer.valueOf(doc.get(tableNum)));
            auditRecord.setFileldName(doc.get(fileldName));
            auditRecord.setOperSenctenceLen(Integer.valueOf(doc.get(operSenctenceLen)));
            auditRecord.setOperSentence(doc.get(operSentence));
            auditRecord.setRowNum(Integer.valueOf(doc.get(rowNum)));
            auditRecord.setSqlExecTime(Double.valueOf(doc.get(sqlExecTime)));
            auditRecord.setSqlResponse(doc.get(sqlResponse));
            auditRecord.setReturnContent(doc.get(returnContent));
            auditRecord.setReturnContentLen(Integer.valueOf(doc.get(returnContentLen)));
            auditRecord.setDealState(Byte.valueOf(doc.get(dealState)));
            auditRecord.setProtectObjectUuid(doc.get(protectObjectUuid));
            auditRecord.setRuleUuid(doc.get(ruleUuid));
            auditRecord.setRiskLev(Byte.valueOf(doc.get(riskLev)));
            auditRecord.setExtendA(doc.get(extendA));
            auditRecord.setExtendB(doc.get(extendB));
            auditRecord.setExtendC(doc.get(extendC));
            resultList.add(auditRecord);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("totalFind", totalFind);
        map.put("total", total);
        map.put("list", resultList);

        return map;
    }

    /**
     * 表字段开始
     */
    public static final String id = "id";
    public static final String riskLev = "riskLev";
    public static final String operSentence = "operSentence";
    public static final String happenTime = "happenTime";
    public static final String mainUuid = "mainUuid";
    public static final String guestUuid = "guestUuid";
    public static final String toolUuid = "toolUuid";
    public static final String ruleUuid = "ruleUuid";
    public static final String protectObjectUuid = "protectObjectUuid";
    public static final String sqlTemplateId = "sqlTemplateId";
    public static final String operTypeId = "operTypeId";
    public static final String logUser = "logUser";
    public static final String applicationAccount = "applicationAccount";
    public static final String srcPort = "srcPort";
    public static final String sessionId = "sessionId";
    public static final String dbName = "dbName";
    public static final String tableName = "tableName";
    public static final String tableNum = "tableNum";
    public static final String fileldName = "fileldName";
    public static final String operSenctenceLen = "operSenctenceLen";
    public static final String sqlBindValue = "sqlBindValue";
    public static final String rowNum = "rowNum";
    public static final String sqlExecTime = "sqlExecTime";
    public static final String sqlResponse = "sqlResponse";
    public static final String returnContent = "returnContent";
    public static final String returnContentLen = "returnContentLen";
    public static final String dealState = "dealState";
    public static final String extendA = "extendA";
    public static final String extendB = "extendB";
    public static final String extendC = "extendC";


}
