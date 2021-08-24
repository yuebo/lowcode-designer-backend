package com.eappcat.flow.flowweb.model.core;

import java.util.List;

public interface EntityCrudService {
    default void build(Model t){};
    int insert(Entity t);
    int save(Entity t);
    int update(Entity t);
    int delete(EntityId pk);
    default void insert(List<Entity> list){
        list.stream().forEach(this::insert);
    }
    default void update(List<Entity> list){
        list.stream().forEach(this::update);
    }
    default void delete(List<EntityId> list){
        list.stream().forEach(this::delete);
    }
}
