package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import com.iris.lucene.model.QueryModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
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
    private static final String id = "id";
    private static final String operSentence = "operSentence";
not
    private static final BooleanClause.Occur MUST = BooleanClause.Occur.MUST;
    private static final BooleanClause.Occur MUST_NOT = BooleanClause.Occur.MUST_NOT;


    public Map<String, Object> getTotal() {

//        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
//
//        /**
//         * lucene 支持模糊查询，语义查询，短语查询，组合查询等
//         * TermQuery BooleanQuery RangeQuery WildCardQuery
//         */
//        // id
//        String[] ids = {"1"};
//        Query idQuery = new TermQuery(new Term(id, ids[0]));
//        booleanQuery.add(idQuery, MUST);
//
//
//        // 操作语句
//        Query opQuery;
//        String keyWord = "你的";
//        String[] keyWords = keyWord.split("&");// 多个关键字用& 隔开取交集,其他情况是并集
//        int size = keyWords.length;
//        BooleanClause.Occur[] occurs = new BooleanClause.Occur[size];
//        String[] fields = new String[size];
//        for (int i = 0; i < size; i++) {
//            occurs[i] = MUST;
//            fields[i] = operSentence;
//        }
//        try {
//            opQuery = MultiFieldQueryParser.parse(keyWords, fields, occurs, analyzer);
//            booleanQuery.add(opQuery, MUST);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        int total1 = 0;
        int total2 = 0;
        int total3 = 0;
        int total4 = 0;
        int total = 0;

        try {
            dir1 = FSDirectory.open(Paths.get(filePath[0]));
            IndexReader reader1 = DirectoryReader.open(dir1);
            total1 = reader1.maxDoc();
//            IndexSearcher is1 = new IndexSearcher(reader1);
//            total1 = is1.count(booleanQuery.build());

            dir2 = FSDirectory.open(Paths.get(filePath[1]));
            IndexReader reader2 = DirectoryReader.open(dir2);
            total2 = reader2.maxDoc();

            dir3 = FSDirectory.open(Paths.get(filePath[2]));
            IndexReader reader3 = DirectoryReader.open(dir3);
            total3 = reader3.maxDoc();

            dir4 = FSDirectory.open(Paths.get(filePath[3]));
            IndexReader reader4 = DirectoryReader.open(dir4);
            total4 = reader4.maxDoc();

            MultiReader multiReader = new MultiReader(reader1, reader2, reader3, reader4);
            total = multiReader.maxDoc();//所有文档数
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total1", total1);
        map.put("total2", total2);
        map.put("total3", total3);
        map.put("total4", total4);
        map.put("total", total);
        return map;
    }
    /**
     * @param queryModel 检索条件
     * @return 结果
     */
    public Map<String, Object> listByCondition(QueryModel queryModel) {

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        /**
         * lucene 支持模糊查询，语义查询，短语查询，组合查询等
         * TermQuery BooleanQuery RangeQuery WildCardQuery
         */
        // id
        String[] ids = queryModel.getId();
        if (ids != null && ids.length > 0) {
            Query idQuery = getQueryFromArray(ids, id);
            booleanQuery.add(idQuery, getOccursByBool(queryModel.getEqualId()));
        }


        // 操作语句
        Query opQuery = null;
        Highlighter highlighter = null;
        String keyWord = queryModel.getKeyWord();
        if (!StringUtils.isEmpty(keyWord)) {
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
                booleanQuery.add(opQuery, getOccursByBool(queryModel.getEqualKeyWord()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            /**
             * 这里可以根据自己的需要来自定义查找关键字高亮时的样式。
             */
            QueryScorer scorer = new QueryScorer(opQuery);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
            highlighter = new Highlighter(simpleHTMLFormatter, scorer);
            highlighter.setTextFragmenter(fragmenter);
        }


        // 风险等级
        Byte[] riskLevs = queryModel.getRiskLev();
        if (riskLevs != null && riskLevs.length > 0) {
            Query riskLevQuery = getQueryFromArray(riskLevs, riskLev);
            booleanQuery.add(riskLevQuery, MUST);
        }


        // 时间
        if (queryModel.getStartTime() != null && queryModel.getEndTime() != null) {
            booleanQuery.add(LongPoint.newRangeQuery(happenTime, queryModel.getStartTime(), queryModel.getEndTime()), MUST);
        }



//        private static final String mainUuid = "mainUuid";
//        private static final String guestUuid = "guestUuid";
//        private static final String toolUuid = "toolUuid";
//        private static final String ruleUuid = "ruleUuid";

//        private static final String protectObjectUuid = "protectObjectUuid";
//        private static final String sqlTemplateId = "sqlTemplateId";
//        private static final String operTypeId = "operTypeId";
        if (queryModel.getOperTypeId() != null) {
            Integer[] operTypeIds = queryModel.getOperTypeId();
            Query operTypeIdQuery = getQueryFromArray(operTypeIds, operTypeId);
            booleanQuery.add(operTypeIdQuery, MUST);
        }

//        private static final String logUser = "logUser";
//        private static final String applicationAccount = "applicationAccount";
//        private static final String srcPort = "srcPort";
//        private static final String sessionId = "sessionId";
//        private static final String dbName = "dbName";
//        private static final String tableName = "tableName";
//        private static final String tableNum = "tableNum";
//        private static final String fileldName = "fileldName";
//        private static final String operSenctenceLen = "operSenctenceLen";
//        private static final String rowNum = "rowNum";
//        private static final String sqlExecTime = "sqlExecTime";
//        private static final String sqlResponse = "sqlResponse";
//        private static final String returnContent= "returnContent";
//        private static final String returnContentLen = "returnContentLen";
        // dealState
        if (queryModel.getDealState() != null) {
            TermQuery dealQuery = new TermQuery(new Term(dealState, String.valueOf(queryModel.getDealState())));
            booleanQuery.add(dealQuery, MUST);
        }
//        private static final String extendA = "extendA";
//        private static final String extendB = "extendB";
//        private static final String extendC = "extendC";


        // 大于等于
        // NumericRangeQuery.newIntRange("families.children.age", 5, Integer.MAX_VALUE, true, true)
        MultiReader multiReader = getMultiReader();
        IndexSearcher indexSearcher = new IndexSearcher(multiReader);
        TopDocs hits = null;
        int total = 0;
        try {

            total = indexSearcher.count(booleanQuery.build());
            // 查询数据， 结束页面自前的数据都会查询到，但是只取本页的数据
            Integer start = queryModel.getStart();
            Integer size = queryModel.getSize();
            System.out.println("audit_record中total:" + total + "|start:" + start + "|size:" + size);

            Sort sort = new Sort(new SortField(happenTime, SortField.Type.LONG, true)); // 时间降序排序
            if (total < size || start - 1 <= 0) {
                // 第一页或只有一页
                hits = indexSearcher.search(booleanQuery.build(), size, sort);
            } else {
                if (total > start) {
                    hits = indexSearcher.search(booleanQuery.build(), start, sort);
                    //获取到上一页最后一条
                    ScoreDoc preScore = hits.scoreDocs[start - 1];
                    //查询最后一条后的数据的一页数据
                    hits = indexSearcher.searchAfter(preScore, booleanQuery.build(), size, sort);
                }
                // 翻页过度
            }
        } catch (IOException e) {
            log.error("检索异常", e);
        }

        List<AuditRecordWithBLOBs> resultList = getListBySearchResult(indexSearcher, hits, highlighter);
        Map<String, Object> map = new HashMap<>();
        map.put("list", resultList);
        map.put("total", total);
        map.put("page", queryModel.getPage());
        map.put("size", queryModel.getSize());

        return map;
    }

    /**
     * 多个文件获取reader
     * @return MultiReader
     */
    private MultiReader getMultiReader() {
        List<IndexReader> reader = new ArrayList<>();
        MultiReader multiReader = null;
        for (int i = 0; i < 4; i++) {
            try {
                dir[i] = FSDirectory.open(Paths.get(filePath[i]));
                IndexReader indexReader = DirectoryReader.open(dir[i]);
                reader.add(indexReader);
            } catch (IOException e) {
                log.error(filePath[i] + "文件夹异常");
                e.printStackTrace();
            }
        }
        if (reader.size() == 0) {
            try {
                return new MultiReader();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        IndexReader[] ir = new IndexReader[reader.size()];
        for (int i = 0; i < reader.size(); i++) {
            System.out.println(reader.get(i));
            ir[i] = reader.get(i);
        }

        try {
            multiReader = new MultiReader(ir);
        } catch (IOException e) {
            log.error("获取多个文件夹一起查询reader有误", e);
        }
        return multiReader;
    }

    /**
     * 多个条件 - 取并集
     *
     * @param objects
     * @param field
     * @return
     */
    private Query getQueryFromArray(Object[] objects, String field) {
        Query query = null;
        int size = objects.length;
        BooleanClause.Occur[] occurs = new BooleanClause.Occur[size];
        String[] fields = new String[size];
        for (int i = 0; i < size; i++) {
            occurs[i] = SHOULD;
            fields[i] = field;
        }
        try {
            query = MultiFieldQueryParser.parse(MyStringUtils.getStringArrayFromArray(objects), fields, occurs, analyzer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return query;
    }

    private BooleanClause.Occur getOccursByBool(Boolean bool) {
        if (bool) {
            return MUST;
        } else {
            return MUST_NOT;
        }
    }

}
