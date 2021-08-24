package com.eappcat.flow.flowweb.binding;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Json implements BindingObject {
    @Override
    public String getName() {
        return "json";
    }
    public String toJson(Object o){
        return JSONObject.toJSONString(o);
    }
    public Object parse(String object){
        return JSONObject.parse(object);
    }
}
