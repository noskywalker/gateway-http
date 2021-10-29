package com.baidu.fbu.mtp.common;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ListPropertyEditorTest {
    
    private ListPropertyEditor listPropertyEditor;
    
    @Before
    public void setUp() {
        listPropertyEditor = new ListPropertyEditor();
    }
    
    @Test
    public void testSetAsTextEmpty() {
        listPropertyEditor.setAsText("");
    }
    
    @Test
    public void testSetAsText() {
        listPropertyEditor.setAsText("180.149.143.26,180.149.143.27,180.149.143.153");
    }
    
    @Test
    public void testListPropertyEditorWithCollectionType() {
        listPropertyEditor = new ListPropertyEditor(ArrayList.class);
    }
}
