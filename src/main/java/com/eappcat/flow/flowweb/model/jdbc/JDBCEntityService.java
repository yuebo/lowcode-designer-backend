package com.eappcat.flow.flowweb.model.jdbc;

import com.eappcat.flow.flowweb.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JDBCEntityService implements EntityService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SpelExpressionParser parser =new SpelExpressionParser();

    @Override
    public Entity findOne(EntityId pk) {
        List<String> fields = toFields(pk.getModel());
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity",quotTable(pk.getModel().getName()));
        context.setVariable("fields",String.join(",",fields));
        String template = "select #{#fields} from #{#entity} where `id` = ?";
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        String sql = expression.getValue(context).toString();
        log.info("sql: {}",sql);
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql,new Object[]{pk.getId()});
        if (result.size() == 0){
            return null;
        }
        Entity entity = new Entity(pk.getModel());

        result.stream().findFirst().ifPresent(data->{
            for(String key: data.keySet()){
                entity.set(pk.getModel().get(key.toLowerCase()),data.get(key));
            }
        });

        return entity;
    }

    private List<String> toFields(Model model, String ... exclude) {
        return model.getFields().stream()
                .filter(modelField -> !Arrays.asList(exclude).contains(modelField.getName()))
                .map(modelField-> quotField(modelField.getName())).collect(Collectors.toList());
    }

    public int save(Entity t) {
        Object id = t.get(t.getModel().get("id"));

        if (id==null){
            return this.insert(t);
        }else {
            return this.update(t);
        }
    }
    private String quotField(String field){
        return "`"+field+"`";
    }

    private String quotTable(String field){
        return "`"+field+"`";
    }

    public int update(Entity t) {
        List<String> fields = toFields(t.getModel());
        List<Object> values = toValues(t,"id");
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity",quotTable(t.getModel().getName()));
        context.setVariable("fields",String.join(",",fields.stream().filter(field->!field.equalsIgnoreCase(quotField("id"))).map(field->field.concat("=?")).collect(Collectors.toList())));
        String template = "update #{#entity} set #{#fields} where `id` = ?";
        values.add(t.get(t.getModel().get("id")));
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        String sql = expression.getValue(context).toString();
        log.info("sql: {}",sql);
        return jdbcTemplate.update(sql,values.toArray(new Object[]{}));

    }

    private List<Object> toValues(Entity t,String ... exclude) {
        return t.getModel().getFields().stream().filter(modelField -> !Arrays.asList(exclude).contains(modelField.getName())).map(modelField -> t.get(modelField)).collect(Collectors.toList());
    }

    public int insert(Entity t){
        List<String> fields = t.get(t.getModel().get("id"))==null?toFields(t.getModel(),"id"):toFields(t.getModel());
        List<Object> values = t.get(t.getModel().get("id"))==null?toValues(t,"id"):toValues(t);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity",quotTable(t.getModel().getName()));
        context.setVariable("fields",String.join(",",fields));
        context.setVariable("values",String.join(",",fields.stream().map(data-> "?").collect(Collectors.toList())));
        String template = "insert into #{#entity} (#{#fields}) values (#{#values})";
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        String sql = expression.getValue(context).toString();
        log.info("sql: {}",sql);
        return jdbcTemplate.update(sql,values.toArray(new Object[]{}));
    }

    @Override
    public int delete(EntityId pk) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity",quotTable(pk.getModel().getName()));
        String template = "delete from #{#entity} where `id` = ?";
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        String sql = expression.getValue(context).toString();
        log.info("sql: {}",sql);
        return jdbcTemplate.update(sql,new Object[]{pk.getId()});

    }


    @Override
    public List<Entity> findList(Queries query) {
        List<String> fields = toFields(query.getModel());
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity",quotTable(query.getModel().getName()));
        List<Object> values = new ArrayList<>();
        context.setVariable("fields",String.join(",",fields));
        StringBuilder stringBuilder = new StringBuilder();
        build(query,stringBuilder,values);
        String template = "select #{#fields} from #{#entity}";
        if (query.getQueries().size()>0){
            template = template + " where #{#query}";
            context.setVariable("query", stringBuilder.toString());

        }
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        String sql = expression.getValue(context).toString();
        log.info("sql: {}",sql);
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql,values.toArray(new Object[]{}));
        if (result.size() == 0){
            return new ArrayList<>();
        }

        return result.stream().map(data->{
            Entity entity = new Entity(query.getModel());

            for(String key: data.keySet()){
                entity.set(query.getModel().get(key.toLowerCase()),data.get(key));
            }
            return entity;
        }).collect(Collectors.toList());
    }

    private void build(Queries queries,StringBuilder stringBuilder, List<Object> values) {
        List<String> outerConditions = new ArrayList<>();
        for (Query query: queries.getQueries()){
            List<String> conditions = new ArrayList<>();
            for (QueryCriteria criteria: query.getCriterias()){
                String field = quotField(criteria.getField().getName());
                switch (criteria.getOperator()){
                    case eq:
                        conditions.add(field+" = ?");
                        values.add(criteria.getValue());
                        break;
                    case gt:
                        conditions.add(field+" > ?");
                        values.add(criteria.getValue());
                        break;
                    case gte:
                        conditions.add(field+" >= ?");
                        values.add(criteria.getValue());
                        break;
                    case lt:
                        conditions.add(field+" < ?");
                        values.add(criteria.getValue());
                        break;
                    case lte:
                        conditions.add(field+" <= ?");
                        values.add(criteria.getValue());
                        break;
                    case ne:
                        conditions.add(field+" <> ?");
                        values.add(criteria.getValue());
                        break;
                    case ends:
                        conditions.add(field+" like contact('%',?)");
                        values.add(criteria.getValue());
                        break;
                    case starts:
                        conditions.add(field+" like contact(?,'%')");
                        values.add(criteria.getValue());
                        break;
                    case contains:
                        conditions.add(field+" like contact('%',?,'%')");
                        values.add(criteria.getValue());
                        break;
                    case between:
                        conditions.add(field+" between ? and ? ");
                        List value = (List)criteria.getValue();
                        values.add(value.get(0));
                        values.add(value.get(1));
                        break;
                    case isnull:
                        conditions.add(field+" is null");
                        break;
                    case notnull:
                        conditions.add(field+" is not null");
                        break;
                    case in:
                        List<?> list = (List)criteria.getValue();
                        List<String> holders= list.stream().map(data-> "?").collect(Collectors.toList());
                        String placeholder =String.join(",",holders);
                        conditions.add(field+" in ("+placeholder+")");
                        values.addAll(list);
                        break;

                    default:
                        break;

                }

            }
            if (conditions.size()>0){
                outerConditions.add(String.join(" and ", conditions));
            }
        }
        if (outerConditions.size()>0){
            stringBuilder.append("(").append(String.join(") or (",outerConditions)).append(")");
        }
    }

    @Override
    public Page<Entity> findPage(Queries query, Pageable pageable) {
        List<Object> values = new ArrayList<>();

        String countSql = preparePageQuery(query, pageable, values,true);
        log.info("count sql: {}",countSql);

        long total = ((Number)jdbcTemplate.queryForList(countSql,values.toArray(new Object[]{})).get(0).get("cnt")).longValue();

        if (total == 0){
            return Page.empty();
        }
        values.clear();
        String sql = preparePageQuery(query, pageable, values,false);
        log.info("sql: {}",sql);

        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql,values.toArray(new Object[]{}));

        List<Entity> test = result.stream().map(data->{
            Entity entity = new Entity(query.getModel());

            for(String key: data.keySet()){
                entity.set(query.getModel().get(key.toLowerCase()),data.get(key));
            }
            return entity;
        }).collect(Collectors.toList());

        return new PageImpl<>(test,pageable,total);
    }

    private String preparePageQuery(Queries query, Pageable pageable, List<Object> values,boolean isCount) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        if (!isCount){
            List<String> fields = toFields(query.getModel());
            context.setVariable("fields",String.join(",",fields));
        }
        context.setVariable("entity",quotTable(query.getModel().getName()));
        StringBuilder stringBuilder = new StringBuilder();
        build(query,stringBuilder,values);

        String template = isCount?"select count(*) cnt from #{#entity}":"select #{#fields} from #{#entity}";
        if (query.getQueries().size()>0){
            template = template + " where #{#query}";
            context.setVariable("query", stringBuilder.toString());
            if (!isCount){
                template += " limit #{#limit}";
                context.setVariable("limit", String.join(",",String.format("%d,%d",pageable.getOffset(),pageable.getPageSize())));
            }
        }

        if (!isCount&&pageable.getSort()!=null){
            List<String> orderList = new ArrayList<>();
            while (pageable.getSort().iterator().hasNext()){
               Sort.Order order = pageable.getSort().iterator().next();
               orderList.add(String.format("%s %s",quotField(order.getProperty()),order.getDirection().isAscending()?"asc":"desc"));
            }
            if (orderList.size()>0){
                template+= " order by #{#order}";
                context.setVariable("order", String.join(",",orderList));
            }

        }
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        return expression.getValue(context).toString();
    }


    public void deleteByQuery(Queries query){
        List<Object> values = new ArrayList<>();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity",quotTable(query.getModel().getName()));
        StringBuilder stringBuilder = new StringBuilder();
        build(query,stringBuilder,values);
        String template = "delete from #{#entity}";
        if (query.getQueries().size()>0){
            template = template + " where #{#query}";
            context.setVariable("query", stringBuilder.toString());
        }
        Expression expression = parser.parseExpression(template,new TemplateParserContext());
        String sql = expression.getValue(context).toString();
        log.info("sql: {}",sql);

        this.jdbcTemplate.update(sql,values.toArray(new Object[]{}));

    }
    @Override
    public void delete(List<EntityId> list) {
        Queries queries = convertToQueries(list);
        this.deleteByQuery(queries);
    }

    @Override
    public List<Entity> findList(List<EntityId> list) {
        Queries queries = convertToQueries(list);
        return this.findList(queries);
    }

    private Queries convertToQueries(List<EntityId> list) {
        EntityId id = list.iterator().next();
        Model model = id.getModel();
        return Queries.newQueries().model(id.getModel()).queries(
                Query.newQuery()
                        .criterias(QueryCriteria.newCriteria()
                                .field(model.get("id"))
                                .value(list.stream().map(entityId -> entityId.getId()).collect(Collectors.toList()))));
    }

    private String fieldType(FieldType type){
        switch (type){
            case NUMBER:
                return "number(10,2)";
            case INTEGER:
                return "integer";
            case DATE:
                return "datetime";
            case BINNARY:
                return "blob";
            default:
                return "varchar(255)";
        }
    }

    @Override
    public void build(Model model) {
       String sql = String.format("create table if not exists %s (%s)", quotTable(model.getName()),
               String.join(",",model.getFields().stream().map(f-> {
                   return String.format("%s %s",quotField(f.getName()),fieldType(f.getType()));
               }).collect(Collectors.toList())));

       jdbcTemplate.execute(sql);
    }
}
