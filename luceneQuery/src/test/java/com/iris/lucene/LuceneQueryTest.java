package com.iris.lucene;

import org.junit.Test;

public class LuceneQueryTest {

    @Test
    public void getTotal() {
        Integer[] i = {5654, 5434, 6011, 5930, 5916, 5999, 5991, 5827, 5764, 5871,
                5579, 4905, 5791, 5651, 5658, 5644, 5655, 5753, 5646, 5754,
                6271, 5252, 5780, 5839, 5742, 5661, 5852, 5799, 5658, 5676};
        int sum = 0;
        for (Integer integer : i) {
            sum += integer;
        }
        System.out.println(sum / 30); // 5732

        // 去除第一次查询耗时
        Integer[] j = {/*913,*/ 365, 464, 317, 347, 320, 333, 319, 332, 298,
                /*939,*/ 456, 462, 347, 361, 343, 350, 359, 357, 319,
                /*963,*/ 374, 390, 315, 412, 333, 366, 326, 297, 297
        };
        int sum2 = 0;
        for (Integer integer : j) {
            sum2 += integer;
        }
        System.out.println(sum2 / 27); // 354

    }
}
