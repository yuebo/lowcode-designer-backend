package com.eappcat.flow.flowweb.binding;

import com.eappcat.flow.flowweb.model.ModelBuilder;
import com.eappcat.flow.flowweb.model.ModelLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelApi implements BindingObject{
    @Autowired
    private ModelLoaderService modelLoaderService;
    @Override
    public String getName() {
        return "models";
    }

    public ModelBuilder load(String id){
        return modelLoaderService.load(id);
    }
}
