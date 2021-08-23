package com.eappcat.flow.flowweb.dao;

import com.eappcat.flow.flowweb.entity.FlowDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDefDao extends JpaRepository<FlowDef,String> {
    FlowDef findByPath(String path);
}
