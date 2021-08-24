package com.eappcat.flow.flowweb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eappcat.flow.flowweb.binding.BindingObject;
import com.eappcat.flow.flowweb.dao.FlowDefDao;
import com.eappcat.flow.flowweb.entity.FlowDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.script.*;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;
@Component
@Slf4j
public class FlowEngine {
    @Autowired
    private FlowDefDao flowDefDao;
    @Autowired
    private List<BindingObject> bindingObjects;
    private ScriptEngineManager factory = new ScriptEngineManager();
    @Transactional(rollbackFor = Exception.class)
    public String run(String flow) throws Exception{
        return this.run(flow,null,null,true);
    }
    @Transactional(rollbackFor = Exception.class)
    public String run(String flow,Consumer<Bindings> consumer,Consumer<ScriptEngine> callback,boolean writer) throws Exception {

        JSONObject jsonObject=JSONObject.parseObject(flow);

        JSONArray list = jsonObject.getJSONArray("nodeList");
        Map<String,JSONObject> cache = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            JSONObject step = list.getJSONObject(i);
            cache.put(step.getString("id"),step);
        }
        String start = jsonObject.getString("start");

        ScriptEngine engine = factory.getEngineByName("groovy");
        if (callback!=null){
            callback.accept(engine);
        }
        Bindings bindings = engine.createBindings();
        bindingObjects.stream().forEach(bindingObject -> {
            bindings.put(bindingObject.getName(),bindingObject);
        });
        if (consumer!=null){
            consumer.accept(bindings);
        }
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        if (writer){
            StringWriter stringWriter = new StringWriter();
            engine.getContext().setWriter(stringWriter);
            engine.getContext().setErrorWriter(stringWriter);
            runCode(engine,cache,jsonObject,start);
            return stringWriter.toString();
        }else {
            runCode(engine,cache,jsonObject,start);
            return "";
        }



    }
    @Transactional(rollbackFor = Exception.class)
    public void runCode(ScriptEngine engine, Map<String,JSONObject> cache,JSONObject data, String code) throws Exception {
        JSONObject step = cache.get(code);
        if (step==null){
            return;
        }
        String type = step.getString("type");
        String output = "";
        StringBuilder stringBuilder = new StringBuilder();
        switch (type){
            case "declare": {
                JSONArray jsonArray = step.getJSONArray("variables");
                for (int j = 0; j < jsonArray.size(); j++) {
                    String var = jsonArray.getString(j);
                    stringBuilder.append(String.format("def %s;\n", var));
                }

                output = findOutput(code, data);

                break;
            }
            case "set":{
                String var = step.getString("var");
                String value = step.getString("value");
                stringBuilder.append(String.format("%s = %s;\n", var,value));
                output = findOutput(code,data);
                break;
            }
            case "exec": {
                JSONArray args = step.getJSONArray("args");

                String method = step.getString("method");
                List<String> argsList = new ArrayList<>();
                for (int j = 0; j < args.size(); j++) {
                    String arg = args.getString(j);
                    argsList.add(arg);
                }
                stringBuilder.append(String.format("%s(%s);\n", method, String.join(",", argsList)));
                output = findOutput(code, data);
                break;
            }
            case "invoke": {
                final JSONArray args = step.getJSONArray("args");

                String script = step.getString("script");

                Optional<FlowDef> flowDef = this.flowDefDao.findById(script);
                if (flowDef.isPresent()) {
                    this.run(flowDef.get().getContent(), innerBindings -> {
                        try {
                            List<Object> argsResult = new ArrayList<>();
                            for (int j = 0; j < args.size(); j++) {
                                String arg = args.getString(j);
                                argsResult.add(engine.eval(arg));
                            }
                            innerBindings.putAll(engine.getBindings(ScriptContext.GLOBAL_SCOPE));
                            innerBindings.put("args", argsResult);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }, engineInner -> {
                        engineInner.getContext().setWriter(engine.getContext().getWriter());
                        engineInner.getContext().setErrorWriter(engine.getContext().getErrorWriter());
                    },false);
                }

                output = findOutput(code, data);
                break;
            }
            case "if": {
                String exp = step.getString("exp");
                Object result = engine.eval(exp);
                if (TRUE.equals(result)) {
                    output = findOutput(code, data, "true");
                } else {
                    output = findOutput(code, data, "false");
                }
                break;
            }
            default:
                break;

        }
        engine.eval(stringBuilder.toString());
        runCode(engine,cache,data,output);
    }

    private String findOutput(String code, JSONObject data) {
        JSONArray jsonArray = data.getJSONArray("lineList");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (code.equals(jsonObject.getString("from"))){
                return jsonObject.getString("to");
            }
        }
        return null;
    }
    private String findOutput(String code, JSONObject data,String label) {
        JSONArray jsonArray = data.getJSONArray("lineList");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (code.equals(jsonObject.getString("from"))&&label.equals(jsonObject.getString("label"))){
                return jsonObject.getString("to");
            }
        }
        return null;
    }
    @Transactional(rollbackFor = Exception.class)
    public void runMvc(String path, Consumer<Bindings> request)throws Exception {
        FlowDef flowDef = flowDefDao.findByPath(path);
        if (flowDef!=null){
            this.run(flowDef.getContent(),request,null,true);
        }
    }
}
