package com.eappcat.flow.flowweb.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RequestVO {
    private String url;
    private Map<String,Object> params=new HashMap<>();
    private String body;
}
