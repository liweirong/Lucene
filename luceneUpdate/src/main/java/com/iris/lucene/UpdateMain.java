package com.iris.lucene;

import java.io.IOException;

/**
 * 先把索引文件删除，重新建索引
 */
public class UpdateMain {
    public static void main(String[] args) throws IOException {
//        Long start = System.currentTimeMillis();
//                LuceneUpdate.query("防统方");
//        Long end = System.currentTimeMillis();
//        System.out.println("关键字查询耗时" + (end - start));

//        LuceneUpdate.insert(audit, i);
//        System.out.println("开始修改");
//        for (int j = 0; j < 4; j++) {
//            LuceneUpdate.check(j);
//        }
//
//
//
//
//        String json = "{\"id\":\"2\",\"happenTime\":1542960803,\"mainUuid\":\"b3e33260-5b62-4275-acf4-58bfb7c92059\",\"guestUuid\":\"193d0efe-ca60-4585-952e-5bffa1a439f2\",\"toolUuid\":\"264cafcd-61d4-4f37-a4d9-869d65c0cdd0\",\"ruleUuid\":\"217a2598-ee1f-4dd0-9ea2-7a9e48ea5f9f\",\"protectObjectUuid\":\"e83e4483-2aba-4fb1-ba52-f25a0bc0ba47\",\"sqlTemplateId\":1,\"operTypeId\":1,\"logUser\":\"iris\",\"applicationAccount\":\"ac\",\"srcPort\":22,\"sessionId\":\"570db1d8-d838-4261-9c7d-9d26f20f7822\",\"dbName\":\"name\",\"tableName\":\"audit_record\",\"tableNum\":1,\"fileldName\":\"/\",\"operSentence\":\"select *　from auditRecord where id = 你的第1个名字,防统方\",\"operSenctenceLen\":49,\"sqlBindValue\":\"x=1\",\"rowNum\":1,\"sqlExecTime\":0.01,\"sqlResponse\":\"sadaasd\",\"returnContent\":\"213\",\"returnContentLen\":6,\"dealState\":1,\"riskLev\":0,\"extendA\":\"\",\"extendB\":\"\",\"extendC\":\"\"}\n";
//        AuditRecordWithBLOBs audit = new Gson().fromJson(json, AuditRecordWithBLOBs.class);
//        audit.setDealState(((byte) 3));
//        start = System.currentTimeMillis();
//        LuceneUpdate.update(audit);
//        end = System.currentTimeMillis();
//        System.out.println("更新耗时" + (end - start));
        System.out.println("删除前check：%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        LuceneUpdate.check();
        Long start = System.currentTimeMillis();
        LuceneDelete.delete();
        Long end = System.currentTimeMillis();
        System.out.println("删除数据耗时" + (end - start)+"ms         |||||||||||||||||||||||||||||||||");
        System.out.println("删除后check：%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        LuceneUpdate.check();

    }


}
