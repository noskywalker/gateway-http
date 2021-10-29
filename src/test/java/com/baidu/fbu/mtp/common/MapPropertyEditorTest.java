package com.baidu.fbu.mtp.common;

import org.junit.Before;
import org.junit.Test;

public class MapPropertyEditorTest {
    
    private MapPropertyEditor mapPropertyEdity;
    
    @Before
    public void setUp() {
        mapPropertyEdity = new MapPropertyEditor();
    }
    
    @Test
    public void testSetAsTextEmpty() {
        mapPropertyEdity.setAsText("");
    }
    
    @Test
    public void testSetAsText() {
        mapPropertyEdity.setAsText("encash=encashStrategy,encashresult=encashresultStrategy");
    }
}
