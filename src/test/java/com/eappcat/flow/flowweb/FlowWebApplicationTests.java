package com.eappcat.flow.flowweb;

import com.eappcat.flow.flowweb.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;

@SpringBootTest
@Slf4j
class FlowWebApplicationTests {
    @Autowired
    private EntityService entityService;

    @Test
    void contextLoads() {
        Model model = new Model();
        model.setName("tbl_message");
        ModelField id = ModelField.builder().name("id").type(FieldType.STRING).build();
        ModelField key = ModelField.builder().name("key").type(FieldType.STRING).build();
        ModelField value = ModelField.builder().name("value").type(FieldType.STRING).build();
        model.add(id,key,value);

        entityService.build(model);

        Entity entity = new Entity(model);
        entity.set(id,"1");
        entity.set(key,"test");
        entity.set(value,"value");
        entityService.insert(entity);

        Entity entity2=entityService.findOne(EntityId.builder().id("1").model(model).build());
        log.info("data : {}", entity2);

        Page<Entity> entityPage = entityService.findPage(Queries.newQueries().model(model)
                        .queries(Query.newQuery().criterias(QueryCriteria.newCriteria().field(id).operator(Operator.in).value(Arrays.asList(1,2)))),
                PageRequest.of(0,10));
        log.info("data : {}", entityPage);

    }

}
