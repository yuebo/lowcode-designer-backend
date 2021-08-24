package com.eappcat.flow.flowweb.model.core;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Slf4j
public class Model {
    private String name;
    private ModelType type;
    private List<ModelField> fields=new ArrayList<>();
    public void add(ModelField field){
        if (!this.contains(field.getName())){
            this.fields.add(field);
        }else {
            log.error("添加的字段已经存在，忽略此字段");
        }
    }

    public void add(ModelField ... modelFields){
        for (int i = 0; i < modelFields.length; i++) {
            this.add(modelFields[i]);
        }
    }

    public boolean contains(String name){
        return this.fields.stream().filter(modelField -> Objects.equals(modelField.getName(),name)).findFirst().isPresent();
    }
    public ModelField get(String name){
        return this.fields.stream().filter(modelField -> Objects.equals(modelField.getName(),name)).findFirst().get();
    }
    public Model fromJson(String json){
       return JSONObject.parseObject(json).toJavaObject(Model.class);
    }
    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
