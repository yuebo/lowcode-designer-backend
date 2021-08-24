package com.eappcat.flow.flowweb.model.core;

import lombok.Data;
@Data
public class QueryCriteria {
    private ModelField field;
    private Operator operator;
    private Object value;
    public static QueryCriteria newCriteria(){
        return new QueryCriteria();
    }
    public QueryCriteria field(ModelField field){
        this.field = field;
        return this;
    }

    public QueryCriteria operator(Operator operator){
        this.operator = operator;
        return this;
    }

    public QueryCriteria value(Object value){
        this.value = value;
        return this;
    }
}
