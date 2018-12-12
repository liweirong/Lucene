package com.iris.lucene;

import lombok.Data;

import java.util.*;

/**
 * 用于保存分组统计后每个字段的分组结果
 */
@Data
public class GroupField {

    /**
     * 字段名
     */
    private String name;
    /**
     * 所有可能的分组字段值,排序按每个字段值的文档个数大小排序
     */
    private List<String> values = new ArrayList<>();
    /**
     * 保存字段值和文档个数的对应关系
     */
    private Map<String, Integer> countMap = new HashMap<>();


    /**
     * 用于商品对象list的构造
     *
     * @param value
     */

    public void addValue(String value) {
        if ((value == null) || "".equals(value)) return;
        // 对于多值的字段，支持按空格拆分
        final String[] temp = value.split(",");
//        for (String str: temp) {
//            if (this.countMap.get(str) == null) {
//                this.countMap.put(str, 1);
//                // 构造商品类型临时对象
//                final AuditRecordLucene auditRecordLucene = new AuditRecordLucene();
//
//                auditRecordLucene.setCategoryId(Integer.parseInt(temp[0]));
//                auditRecordLucene.setCategoryName(temp[1]);
//                auditRecordLucene.setParentId(Integer.parseInt(temp[2]));
//                auditRecordLucene.setSortIndex(temp[3]);
//                auditRecordLucene.setParentCategoryName(temp[4]);
//                // simpleCategory.setAdImag(temp[5]);
//                // simpleCategory.setParentAdImage(temp[6]);
//                this.values.add(str);
//            } else {
//                this.countMap.put(str, this.countMap.get(str) + 1);
//            }
//        }
        for (String str : temp) {
            if (countMap.get(str) == null) {
                countMap.put(str, 1);
                values.add(str);
            } else {
                countMap.put(str, countMap.get(str) + 1);
            }
        }
    }
     class ValueComparator implements Comparator<String> {
         public int compare(String value0, String value1) {
             if (countMap.get(value0) > countMap.get(value1)) {
                 return -1;
             } else if (countMap.get(value0) < countMap.get(value1)) {
                 return 1;
             }
             return 0;
         }
     }
}
