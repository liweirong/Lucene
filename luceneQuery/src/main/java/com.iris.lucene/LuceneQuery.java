package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import com.iris.lucene.model.AuditRecordLuceneNew;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * lucene本身不支持分组统计，不过可以使用fieldCache来实现分组统计功能，而且也有很好的性能。
 */
public class LuceneQuery extends BaseIndexNew {

    private static final String[] filePath = {"/data/lucene/auditRecord1", "/data/lucene/auditRecord2", "/data/lucene/auditRecord3", "/data/lucene/auditRecord4"};
    private Directory dir = null;
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

    private final FacetsConfig config = new FacetsConfig();

 /*   public static void main(String[] args) {
        LuceneQuery luceneQuery = new LuceneQuery();
        luceneQuery.getTotal();
    }*/

    public Map<String, Object> getTotal() {

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        /**
         * lucene 支持模糊查询，语义查询，短语查询，组合查询等
         * TermQuery BooleanQuery RangeQuery WildCardQuery
         */
        // id
       /* String[] ids = {"1"};
        Query idQuery = new TermQuery(new Term(id, ids[0]));
        booleanQuery.add(idQuery, MUST);*/


        // 操作语句
        Query opQuery;
//        String keyWord = "科技";
        String keyWord = "防统方";
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
        int total1 = 0;
        int total2 = 0;
        int total3 = 0;
        int total4 = 0;
        int totalFind = 0;
        IndexSearcher indexSearcher = null;
        MultiReader multiReader = null;

        List<IndexReader> reader = new ArrayList<>();

        try {
            dir1 = FSDirectory.open(Paths.get(filePath[0]));
            IndexReader reader1 = DirectoryReader.open(dir1);
            reader.add(reader1);
            total1 = reader1.maxDoc();
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }
        try {
            dir2 = FSDirectory.open(Paths.get(filePath[1]));
            IndexReader reader2 = DirectoryReader.open(dir2);
            reader.add(reader2);
            total2 = reader2.maxDoc();
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }
        try {
            dir3 = FSDirectory.open(Paths.get(filePath[2]));
            IndexReader reader3 = DirectoryReader.open(dir3);
            reader.add(reader3);
            total3 = reader3.maxDoc();
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }
        try {
            dir4 = FSDirectory.open(Paths.get(filePath[3]));
            IndexReader reader4 = DirectoryReader.open(dir4);
            reader.add(reader4);
            total4 = reader4.maxDoc();
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }
        int size1 = reader.size();
        IndexReader[] is = new IndexReader[size1];
        for (int i = 0; i < size1; i++) {
            is[i] = reader.get(i);
        }


        try {
            multiReader = new MultiReader(is);
            total = multiReader.maxDoc();//所有文档数
//            System.out.println("audit_record中所有文档数:" + total);
            indexSearcher = new IndexSearcher(multiReader);
        } catch (IOException e) {
            System.out.println("检索异常" + e);
        }

        /**
         * ------------------------------------------------------------------------------------
         **/
       /* DrillDownQuery q = new DrillDownQuery(this.config);
        //添加查询条件
        q.add(keyWord, "防统方");
        FacetsCollector fc = new FacetsCollector();
        try {
            DefaultSortedSetDocValuesReaderState state = new DefaultSortedSetDocValuesReaderState(multiReader);
            FacetsCollector.search(indexSearcher, q, 10, fc);
            SortedSetDocValuesFacetCounts facets = new SortedSetDocValuesFacetCounts(state, fc);
            FacetResult result = facets.getTopChildren(Integer.MAX_VALUE,"Author");
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        TopDocs hits;

        try {
            assert indexSearcher != null;
            totalFind = indexSearcher.count(booleanQuery.build());
            // 查询数据， 结束页面自前的数据都会查询到，但是只取本页的数据
            System.out.println("audit_record中满足条件的total:" + totalFind);

            Sort sort = new Sort(new SortField(happenTime, SortField.Type.LONG, true)); // 时间降序排序

            hits = indexSearcher.search(booleanQuery.build(), 3, sort);
        } catch (IOException e) {
            System.out.println("检索异常" + e);
            return null;
        }

        // 得到得分文档数组
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        List<AuditRecordLuceneNew> resultList = new ArrayList<>(scoreDocs.length);
        for (
                ScoreDoc scoreDoc : scoreDocs)

        {
            Document doc = null;
            try {
                // 取得对应的文档对象
                doc = indexSearcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuditRecordLuceneNew auditRecord = new AuditRecordLuceneNew();
            auditRecord.setId(Long.valueOf(doc.get(id)));
            auditRecord.setHappenTime(Long.valueOf(doc.get(happenTime)));
            auditRecord.setRiskLev(Integer.valueOf(doc.get(riskLev)));
            auditRecord.setOperSentence(operSentence);
            resultList.add(auditRecord);
        }
        /**
         * ------------------------------------------------------------------------------------
         **/

        Map<String, Object> map = new HashMap<>();
        map.put("totalFind", totalFind);
        map.put("total", total);
        map.put("total1", total1);
        map.put("total2", total2);
        map.put("total3", total3);
        map.put("total4", total4);
        map.put("list", resultList);

        return map;
    }

    // 切面索引存放处
    static final String taxoPath = "/data/lucene/taxo";

    public Map<String, Object> facetByFieldName(String fieldName, Integer topN) throws IOException {
        dir1 = FSDirectory.open(Paths.get(filePath[0]));
        IndexReader reader1 = DirectoryReader.open(dir1);
        IndexSearcher searcher = new IndexSearcher(reader1);

        Directory taxoDir = MMapDirectory.open(Paths.get(taxoPath));
        DirectoryTaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        FacetsCollector fc = new FacetsCollector();
//        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);
        Sort sort = new Sort(SortField.FIELD_SCORE);//指定分组排序规则 TODO 暂时未找到如何分组


        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(new TermQuery(new Term(ruleName, "tongyong")), MUST);
        booleanQuery.add(LongPoint.newRangeQuery(happenTime,0L,1545377400L),MUST);
//        booleanQuery.add(new TermQuery(new Term(riskLev, "0")), BooleanClause.Occur.MUST_NOT);
        searcher.search(booleanQuery.build(), fc);//  按条件统计
//        searcher.search(new MatchAllDocsQuery(), fc);// 无条件，统计所有


        Facets facets = new FastTaxonomyFacetCounts(taxoReader, new FacetsConfig(), fc);
        String[] names = fieldName.split(",");
        Map<String, Object> map = new HashMap<>();
        for (String name : names) {
            Map<String, Object> subMap = new HashMap<>(16);
            FacetResult facetResult = facets.getTopChildren(topN, name);
            if (facetResult != null) {
                if (facetResult.childCount < topN) {
                    topN = facetResult.childCount;
                }
                for (int i = 0; i < topN; i++) {
                    // 在此排序
                    subMap.putIfAbsent(facetResult.labelValues[i].label, facetResult.labelValues[i].value);
                }
            }
            map.putIfAbsent(name, subMap);
        }

        taxoReader.close();
        return map;
    }

}
