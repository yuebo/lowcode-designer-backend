package com.eappcat.flow.flowweb.model.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public interface EntityQueryService {
    Entity findOne(EntityId pk);
    List<Entity> findList(Queries query);
    Page<Entity> findPage(Queries query, Pageable pageable);
    default List<Entity> findList(List<EntityId> list){
        return list.stream().map(this::findOne).collect(Collectors.toList());
    }
}
