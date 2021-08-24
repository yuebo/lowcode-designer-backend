package com.eappcat.flow.flowweb.model.core;

/**
 * Created by yuebo on 2018/6/21.
 * @author
 */
public interface NameStrategy {
    /**
     * 转换table名称
     * @param table
     * @return
     */
    String toTableName(String entity);
    /**
     * 转换entity名称
     * @param column
     * @return
     */
    String toModelName(String table);

    /**
     * @param attribute
     * @return
     */
    String toColumnName(String attribute);

    /**
     * @param column
     * @return
     */
    String toModelAttributeName(String column);

}
