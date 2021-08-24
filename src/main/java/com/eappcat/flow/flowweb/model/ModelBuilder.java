package com.eappcat.flow.flowweb.model;

import cn.hutool.core.util.IdUtil;
import com.eappcat.flow.flowweb.model.core.*;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public class ModelBuilder {
    private Model model;
    private EntityService entityService;
    private NameStrategy nameStrategy;

    public Entity fromValues(Map<String,Object> data){
        Entity entity =new Entity(model);
        for (String field:data.keySet()){
            entity.set(model.get(nameStrategy.toColumnName(field)),data.get(field));
        }
        return entity;
    }

    public Map<String,Object> toValues(Entity entity){
        Map<String,Object> map = new HashMap<>();
        for (ModelField field:entity.getModel().getFields()){
            map.put(nameStrategy.toModelAttributeName(field.getName()),entity.get(field));
        }
        return map;
    }

    public Queries toQueries(List<List<Object>> queries){
        Queries result = Queries.newQueries();
        for (List<Object> values: queries){
            Object[] arrays = values.toArray(new Object[]{});
            if (arrays.length<=2){
                throw new IllegalArgumentException("参数数量不符合");
            }
            result.queries(Query.newQuery().criterias(QueryCriteria.newCriteria().field(this.model.get(nameStrategy.toColumnName(arrays[0].toString()))).operator(Operator.valueOf(arrays[1].toString())).value(arrays[2])));
        }
        return result.model(model);
    }

    public int save(Map<String,Object> data){
        Entity entity = fromValues(data);
        int result = 0;
        if (entity.get(this.model.get("id"))==null){
            entity.set(this.model.get("id"),newId());
            result = entityService.insert(entity);
        }else {
            result= entityService.update(entity);
        }
        data.put("id",entity.get(model.get("id")));
        return result;
    }
    public String newId(){
        return String.valueOf(IdUtil.getSnowflake().nextId());
    }

    public int delete(String id){
        return entityService.delete(EntityId.builder().id(id).model(model).build());
    }

    public Page<Map<String,Object>> findPage(List<List<Object>> queries,List<Integer> page){
        Queries q  = toQueries(queries);
        if (page.size()<2){
            throw new IllegalArgumentException("参数数量不符合");
        }
        Page<Entity> entities = entityService.findPage(q, PageRequest.of(page.get(0),page.get(1)));
        return new PageImpl<>(entities.stream().map(entity -> toValues(entity)).collect(Collectors.toList()),entities.getPageable(),entities.getTotalElements());
    }

    public List<Map<String,Object>> findAll(List<List<Object>> queries){
        Queries q  = toQueries(queries);
        List<Entity> entities = entityService.findList(q);
        return entities.stream().map(entity -> toValues(entity)).collect(Collectors.toList());
    }

    public Map<String,Object> findById(String id){
       return toValues(entityService.findOne(EntityId.builder().id(id).model(model).build()));
    }
}
