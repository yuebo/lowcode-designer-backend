package com.eappcat.flow.flowweb.model.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityId {
    private Model model;
    private String id;
}
