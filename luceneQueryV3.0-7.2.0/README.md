这个Lucene版本的亮点包括：
**特定的查询实现现在可以选择退出缓存。**
TopFieldDocCollector现在可以在索引排序并且未请求总命中数时提前终止匹配的收集。
>>IndexWriter #flushNextBuffer对IndexWriter的内存使用情况进行了更细粒度的控制。
>>在IndexWriter中修复了文档记帐。
>>可以使用DoubleValuesSource.fromQuery（）在ValuesSource中公开查询分数。