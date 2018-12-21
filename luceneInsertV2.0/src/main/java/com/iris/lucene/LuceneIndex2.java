package com.iris.lucene;


import com.iris.lucene.ik.IKAnalyzer6x;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NRTCachingDirectory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneIndex2 extends BaseIndexNew {
    private static final Logger log = Logger.getLogger(LuceneIndex2.class);
    // 索引路径
    private static final String indexPath = "/data/lucene/auditRecord2";
    private static Directory dir = null;
    private static Analyzer analyzer;
    private static IndexWriter indexWriter = null;
    private static Charset charset = Charset.forName("utf-8");

    static {
        analyzer = new IKAnalyzer6x(true); // true:用最大词长分词  false:最细粒度切分 20000
        try {
            dir = MMapDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {
            log.error("打开索引目录异常{}", e);
        }
    }


    /**
     * @param filePath 文件位置
     */
    public static void bulkIndex(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();

        }
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length == 0) {
            try {
                Thread.sleep(3000);
                System.out.println("没有文件，睡3秒后继续");
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        indexWriter = getWriter();
        String record;
        List<String> list = new ArrayList<>(initialCapacity);
        for (int i = 0; i < listFiles.length; i++) {
            File fileItem = listFiles[i];
            try (
                    FileInputStream fileIs = new FileInputStream(fileItem);
                    InputStreamReader isReader = new InputStreamReader(fileIs, charset);
                    BufferedReader br = new BufferedReader(isReader)
            ) {
                while ((record = br.readLine()) != null) {
                    list.add(record);
                }

                insert(list, indexWriter);
            } catch (Throwable e) {
                System.out.println(e);
                try {
                    indexWriter.rollback();
                } catch (IOException e1) {
                    log.error("数据入库回滚失败", e);
                }
            } finally {
                list.clear();
                fileItem.delete();
            }
        }
        closeIndexWriter();
    }


    /**
     * 把对象进行索引
     *
     * @param list        对象集合
     * @param indexWriter indexWriter
     * @return 总数
     */
    public static void insert(List<String> list, IndexWriter indexWriter) {
        //NRTCachingDirectory是针对RAMDirectory封装的代理类，主要用在近实时搜索场景下，NRT就是near-real-time（近实时）的缩写；具体特点是在少量写入索引时，写索引稍微慢，读索引相对比较快。
        NRTCachingDirectory ramDir = new NRTCachingDirectory(dir,100,10);
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter ramWriter = null;
        try {
            ramWriter = new IndexWriter(ramDir, iwc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 添加索引进内存
        for (int i = 0; i < list.size(); i++) {
            Document doc = getDoc(list.get(i));
            try {
                if (doc != null) {
                    assert ramWriter != null;
                    ramWriter.addDocument(doc);
                }
            } catch (IOException e) {
                System.out.println("添加索引异常2" + e);
            }
        }
        // 一个文件加载后再存入磁盘
        try {
            indexWriter.addIndexes(ramDir);
            indexWriter.commit();
        } catch (IOException e) {
          log.error("存入磁盘异常2{}", e);
        } finally {
            try {
                if (ramWriter != null || ramWriter.isOpen()) {
                    ramWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取IndexWriter实例
     *
     * @return
     * @throws Exception
     */
    private static IndexWriter getWriter() {

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        /**
         *
         * 在索引算法确定的情况下，最为影响Lucene索引速度有三个参数－－IndexWriter中的 MergeFactor, MaxMergeDocs, RAMBufferSizeMB 。这些参数无非是控制内外存交换和索引合并频率，从而达到提高索引速度。当然这些参数的设置也得依照硬件条件灵活设置。
         * MaxMergeDocs
         * 该参数决定写入内存索引文档个数，到达该数目后就把该内存索引写入硬盘，生成一个新的索引segment文件。
         * 所以该参数也就是一个内存buffer，一般来说越大索引速度越快。
         * MaxBufferedDocs这个参数默认是disabled的，因为Lucene中还用另外一个参数（RAMBufferSizeMB）控制这个bufffer的索引文档个数。
         * 其实MaxBufferedDocs和RAMBufferSizeMB这两个参数是可以一起使用的，一起使用时只要有一个触发条件满足就写入硬盘，生成一个新的索引segment文件。
         *
         * RAMBufferSizeMB
         * 控制用于buffer索引文档的内存上限，如果buffer的索引文档个数到达该上限就写入硬盘。当然，一般来说也只越大索引速度越快。
         * 当我们对文档大小不太确定时，这个参数就相当有用，不至于outofmemory error.
         *
         * MergeFactor
         * 这个参数是用于子索引（Segment）合并的。
         * Lucene中索引总体上是这样进行，索引现写到内存，触发一定限制条件后写入硬盘，生成一个独立的子索引－lucene中叫Segment。一般来说这些子索引需要合并成一个索引，也就是optimize()，否则会影响检索速度，而且也可能导致open too many files。
         * MergeFactor 这个参数就是控制当硬盘中有多少个子索引segments，我们就需要现把这些索引合并冲一个稍微大些的索引了。
         * MergeFactor这个不能设置太大，特别是当MaxBufferedDocs比较小时（segment 越多），否则会导致open too many files错误，甚至导致虚拟机外面出错。
         *
         * Note: Lucene 中默认索引合并机制并不是两两合并，好像是多个segment 合并成最终的一个大索引，所以MergeFactor越大耗费内存越多，索引速度也会快些，但我的感觉太大譬如300，最后合并的时候还是很满。Batch indexing 应 MergeFactor>10

         */
        try {
            indexWriter = new IndexWriter(dir, iwc);
        } catch (IOException e) {
            log.error("获取IndexWriter实例异常2", e);
            try {
                Thread.sleep(2000L);
                System.out.println("后台正在入库，两秒后继续");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            indexWriter = getWriter();
        }
        System.out.println("创建indexWriter");
        return indexWriter;
    }

    private static void closeIndexWriter() {
        if (indexWriter != null) {
            System.out.println("关闭indexWriter");
            try {
                indexWriter.commit();
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
