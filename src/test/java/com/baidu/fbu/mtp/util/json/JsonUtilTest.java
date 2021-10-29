package com.baidu.fbu.mtp.util.json;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilTest {
    
    @Test
    public void testGetObjectMapperInstance() {
        ObjectMapper mapper = JsonUtil.getObjectMapperInstance();
        System.out.println(mapper.toString());
    }
    
    @Test
    public void testIsNullJsonNode() {
        boolean b = JsonUtil.isNullJsonNode(null);
        System.out.println(b);
    }
    
    @Test
    public void testToJson() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "baidu");
        map.put("product", "umoney");
        String result = JsonUtil.toJson(map);
        System.out.println(result);
    }
    
    @Test(expected = RuntimeException.class)
    public void testToBeanException() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "baidu");
        map.put("product", "umoney");
        String json = JsonUtil.toJson(map);
        String result = JsonUtil.toBean(json, String.class);
        System.out.println(result.toString());
    }
    
    @Test
    public void testToBean() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "baidu");
        map.put("product", "umoney");
        String json = JsonUtil.toJson(map);
        Map<String, String> result = JsonUtil.toBean(json, Map.class);
        System.out.println(result.toString());
    }
    
    @Test(expected = RuntimeException.class)
    public void testToBeanTypeException() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "baidu");
        map.put("product", "umoney");
        String json = JsonUtil.toJson(map);
        TypeReference<String> type = new TypeReference<String>() {
        };
        Object result = JsonUtil.toBean(json, type);
        System.out.println(result.toString());
    }
    
    @Test
    public void testToBeanType() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "baidu");
        map.put("product", "umoney");
        String json = JsonUtil.toJson(map);
        TypeReference<Map<String, String>> type = new TypeReference<Map<String, String>>() {
        };
        Object result = JsonUtil.toBean(json, type);
        System.out.println(result.toString());
    }
}
