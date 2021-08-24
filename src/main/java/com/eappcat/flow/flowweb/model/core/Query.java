package com.eappcat.flow.flowweb.model.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Query {
    private List<QueryCriteria> criterias=new ArrayList<>();

    public static Query newQuery(){
        return new Query();
    }
    public Query criterias(QueryCriteria ...criteria){
        criterias.addAll(Arrays.asList(criteria));
        return this;
    }
}
