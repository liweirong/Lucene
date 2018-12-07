# Lucene
lucene企业级实战-解决mysql查询慢的轻量级框架（全文检索）

#lucene简介
        Lucene最初由鼎鼎大名Doug Cutting开发，2000年开源，现在也是开源全文检索方案的不二选择，
    它的特点概述起来就是：全Java实现、开源、高性能、功能完整、易拓展，功能完整体现在对分词的支持、各种查询方式（前缀、模糊、正则等）、打分高亮、列式存储（DocValues）等等。   
    而且Lucene虽已发展10余年，但仍保持着一个活跃的开发度，以适应着日益增长的数据分析需求，最新的6.0版本里引入block k-d trees，全面提升了数字类型和地理位置信息的检索性能，
    另基于Lucene的Solr和ElasticSearch分布式检索分析系统也发展地如火如荼，ElasticSearch也在我们项目中有所应用。

#File Structure
````
lucene
    |--- luceneInsert       数据插入模块
    |--- luceneInsertV2.0   数据插入模块
    |--- luceneQuery        性能检测模块
````
#性能
v1.0 性能分析：CentOS7.4、4核8G 3万条/秒<br>
![v1.0](images/insert_v1.0.jpg)

#lucene detail
索引文件格式
```
首先索引里都存了些什么呢？一个索引包含一个documents的序列，一个document是一个fields的序列，一个field是一个有名的terms序列，一个term是一个比特序列。

根据 Summary of File Extensions 的说明，目前Lucene 6.0中存在的索引格式如下
|Name|	Extension	|Brief Description
Segments File	segments_N	Stores information about a commit point
Lock File	write.lock	The Write lock prevents multiple IndexWriters from writing to the same file
Segment Info	.si	Stores metadata about a segment
Compound File	.cfs, .cfe	An optional “virtual” file consisting of all the other index files for systems that frequently run out of file handles
Fields	.fnm	Stores information about the fields
Field Index	.fdx	Contains pointers to field data
Field Data	.fdt	The stored fields for documents
Term Dictionary	.tim	The term dictionary, stores term info
Term Index	.tip	The index into the Term Dictionary
Frequencies	.doc	Contains the list of docs which contain each term along with frequency
Positions	.pos	Stores position information about where a term occurs in the index
Payloads	.pay	Stores additional per-position metadata information such as character offsets and user payloads
Norms	.nvd, .nvm	Encodes length and boost factors for docs and fields
Per-Document Values	.dvd, .dvm	Encodes additional scoring factors or other per-document information
Term Vector Index	.tvx	Stores offset into the document data file
Term Vector Documents	.tvd	Contains information about each document that has term vectors
Term Vector Fields	.tvf	The field level info about term vectors
Live Documents	.liv	Info about what files are live
Point values	.dii, .dim	Holds indexed points, if any
在Lucene索引结构中，既保存了正向信息，也保存了反向信息。

正向信息存储在：段(segments_N)->field(.fnm/.fdx/.fdt)->term(.tvx/.tvd/.tvf)

反向信息存储在：词典(.tim)->倒排表(.doc/.pos)
````
