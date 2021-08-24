package com.eappcat.flow.flowweb.model.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Data
public class Queries {
    private Model model;
    private List<Query> queries=new ArrayList<>();

    public static Queries newQueries(){
        return new Queries();
    }
    public Queries queries(Query ... queries){
        this.queries.addAll(Arrays.asList(queries));
        return this;
    }
    public Queries model(Model model){
        this.model = model;
        return this;
    }
}
