package com.eappcat.flow.flowweb.model;

import com.eappcat.flow.flowweb.model.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
@Component
public class InMemoryModelLoaderService implements ModelLoaderService {
    private Map<String,Model> models = new HashMap<>();
    @Autowired
    private EntityService entityService;

    @PostConstruct
    void initModels(){
        Model model = new Model();
        model.setName("tbl_message");
        ModelField id = ModelField.builder().name("id").type(FieldType.STRING).build();
        ModelField key = ModelField.builder().name("key").type(FieldType.STRING).build();
        ModelField value = ModelField.builder().name("value").type(FieldType.STRING).build();
        model.add(id,key,value);
        this.models.put(model.getName(),model);
        entityService.build(model);
    }

    @Override
    public ModelBuilder load(String id) {
        return ModelBuilder.builder().model(models.get(id)).entityService(entityService).nameStrategy(new DefaultNameStrategy()).build();
    }
}
