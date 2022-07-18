package org.scut.v1;

import org.junit.Test;
import org.scut.v1.util.LevelDBUtil;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Tests {



    @Test
    public void test(){
        LevelDBUtil levelDBUtil=new LevelDBUtil();
        levelDBUtil.put("nihao","sdfsd");
        System.out.println(levelDBUtil.get("nihao"));
        levelDBUtil.closeDB();
    }
}
