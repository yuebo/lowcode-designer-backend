package com.eappcat.flow.flowweb.model.core;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private Map<ModelField,Object> values=new HashMap();
    private final Model model;

    public Entity(Model model){
        this.model = model;
    }
    public Object get(ModelField name){
        return this.values.get(name);
    }
    public void set(ModelField name,Object object){
        this.values.put(name,object);
    }

    public Model getModel() {
        return model;
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();
        for (ModelField modelField:values.keySet()){
            jsonObject.put(modelField.getName(),values.get(modelField));
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJsonObject().toJSONString();
    }
    public <T> T toJavaObject(Class<T> tClass){
        return toJsonObject().toJavaObject(tClass);
    }
}
