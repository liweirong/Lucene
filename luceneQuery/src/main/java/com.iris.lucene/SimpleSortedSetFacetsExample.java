package com.iris.lucene;

import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MoSon on 2017/6/30.
 */
public class SimpleSortedSetFacetsExample {
    //RAMDirectory：内存驻留目录实现。 默认情况下，锁定实现是SingleInstanceLockFactory。
    private final Directory indexDir = new RAMDirectory();
    private final FacetsConfig config = new FacetsConfig();
    private static Analyzer analyzer = new IKAnalyzer6x(true);
    public SimpleSortedSetFacetsExample() {
    }

    private void index() throws IOException {
        ////初始化索引创建器
        //WhitespaceAnalyzer仅仅是去除空格，对字符没有lowcase化,不支持中文；并且不对生成的词汇单元进行其他的规范化处理。
        //openMode:创建索引模式：CREATE，覆盖模式； APPEND，追加模式
        //IndexWriter：创建并维护索引
        IndexWriter indexWriter = new IndexWriter(this.indexDir, (new IndexWriterConfig(analyzer)).setOpenMode(OpenMode.CREATE));
        //建立文档
        Document doc = new Document();
        // 创建Field对象，并放入doc对象中
        doc.add(new SortedSetDocValuesFacetField("Author", "Bob"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2010"));
        // 写入IndexWriter
        indexWriter.addDocument(this.config.build(doc));
        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Lisa"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2010"));
        indexWriter.addDocument(this.config.build(doc));
        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Lisa"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2012"));
        indexWriter.addDocument(this.config.build(doc));
        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Susan"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2012"));
        indexWriter.addDocument(this.config.build(doc));
        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Frank"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "1999"));
        indexWriter.addDocument(this.config.build(doc));
        indexWriter.close();
    }

    //查询并统计文档的信息
    private List<FacetResult> search() throws IOException {
        //基本都是一层包着一层封装
        //DirectoryReader是可以读取目录中的索引的CompositeReader的实现。
        DirectoryReader indexReader = DirectoryReader.open(this.indexDir);
        //通过一个IndexReader实现搜索。
        IndexSearcher searcher = new IndexSearcher(indexReader);
        DefaultSortedSetDocValuesReaderState state = new DefaultSortedSetDocValuesReaderState(indexReader);
        //收集命中后续刻面。 一旦你运行了一个搜索并收集命中，就可以实例化一个Facets子类来进行细分计数。 使用搜索实用程序方法执行“普通”搜索，但也会收集到Collector中。
        FacetsCollector fc = new FacetsCollector();
        //实用方法，搜索并收集所有的命中到提供的Collector。
        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);
        //计算所提供的匹配中的所有命中。
        SortedSetDocValuesFacetCounts facets = new SortedSetDocValuesFacetCounts(state, fc);
        ArrayList results = new ArrayList();
        //getTopChildren：返回指定路径下的顶级子标签。
        results.add(facets.getTopChildren(10, "Author", new String[0]));
        results.add(facets.getTopChildren(10, "Publish Year", new String[0]));
        indexReader.close();
        return results;
    }

    private FacetResult drillDown() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(this.indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        DefaultSortedSetDocValuesReaderState state = new DefaultSortedSetDocValuesReaderState(indexReader);
        DrillDownQuery q = new DrillDownQuery(this.config);
        //添加查询条件
        q.add("Publish Year", new String[]{"2012"});
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, q, 10, fc);
        SortedSetDocValuesFacetCounts facets = new SortedSetDocValuesFacetCounts(state, fc);
        //获取符合的作者
        FacetResult result = facets.getTopChildren(10, "Author", new String[0]);
        indexReader.close();
        return result;
    }

    public List<FacetResult> runSearch() throws IOException {
        this.index();
        return this.search();
    }

    public FacetResult runDrillDown() throws IOException {
        this.index();
        return this.drillDown();
    }

    public static void main(String[] args) throws Exception {
        SimpleSortedSetFacetsExample example = new SimpleSortedSetFacetsExample();
        System.out.println("Facet counting example:");
        System.out.println("-----------------------");
        List results = example.runSearch();
        System.out.println("Author: " + results.get(0));


//
//
//        System.out.println("Publish Year: " + results.get(0));
//        System.out.println("\n");
//
//
//        System.out.println("Facet drill-down example (Publish Year/2010):");
//        System.out.println("---------------------------------------------");
//        System.out.println("Author: " + example.runDrillDown());
    }
}
