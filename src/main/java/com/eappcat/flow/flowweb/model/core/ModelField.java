package com.eappcat.flow.flowweb.model.core;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class ModelField {
    private String name;
    private FieldType type;
}
