package com.eappcat.flow.flowweb.entity;

import lombok.Data;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "tbl_flow_def")
@Data
@EntityListeners(AuditingEntityListener.class)
public class FlowDef {
    @Id
    private String id;
    private String name;
    @Column(length = 8000)
    private String content;
    private String module;
    private String path;
    @LastModifiedBy
    private Date updateDate;
}
