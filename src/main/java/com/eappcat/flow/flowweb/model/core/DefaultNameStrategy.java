package com.eappcat.flow.flowweb.model.core;

import cn.hutool.core.util.StrUtil;

public class DefaultNameStrategy implements NameStrategy {

    @Override
    public String toTableName(String entity) {
        return StrUtil.toCamelCase(entity);
    }

    @Override
    public String toModelName(String table) {
        return StrUtil.toUnderlineCase(table);
    }

    @Override
    public String toColumnName(String attribute) {
        return StrUtil.toCamelCase(attribute);
    }

    @Override
    public String toModelAttributeName(String column) {
        return StrUtil.toUnderlineCase(column);
    }
}
