|入库方案|效果|
------------- | ------------- 
|两个文件夹                                                    | ✖ 不方便检索|
|Gson -> fastJson                                             | ✔ json转换加快| 
|ramWriter.addDocument(tmp); -> ramWriter.addDocuments(tmp);  | ✖ 卡住|
|添加iwc.setRAMBufferSizeMB(100);  26000/s                    | ✖ 效果不明显|
|轮询目录改为指定文件名                                          | - |
------------------------


索引文件的格式

|文件类型|存储含义|
| --------- | ------------- |
|Segments|索引块|
|Fnm|Field 的名称|
|Fdt|存储了所有设有 Store.YES 的 Field 的数据|
|Fdx|存储文档在 fdt 中的位置|

Cfs

复合式索引格式的索引文件
--------------------- 
作者：sos1437 
来源：CSDN 
原文：https://blog.csdn.net/sos1437/article/details/4425303 
版权声明：本文为博主原创文章，转载请附上博文链接！
````
Locing
   lucence内部使用文件来locking， 默认的locking文件放在java.io.tmpdir,
   可以通过-Dorg.apache.lucene.lockDir=xxx指定新的dir， 有write.lock commit.lock两个文件，lock文件用来防止并行操作index，
   如果并行操作， lucene会抛出异常，可以通过设置-DdisableLuceneLocks=true来禁止locking，这样做一般来说很危险，
   除非你有操作系 统或者物理级别的只读保证，比如把index文件刻盘到CDROM上。
```