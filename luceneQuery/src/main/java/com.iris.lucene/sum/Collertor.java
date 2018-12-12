package com.iris.lucene.sum;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Scorer;

import java.io.IOException;

public abstract  class Collertor {

    //指定打分器
    public abstract void setScorer(Scorer scorer) throws IOException;

    //对目标结果进行收集，很重要！
    public abstract void collect(int doc) throws IOException;

    //一个索引可能会有多个子索引，这里相当于是对子索引的遍历操作
    public abstract void setNextReader(IndexReader reader, int docBase) throws IOException;

    //
    public abstract boolean acceptsDocsOutOfOrder();

}
