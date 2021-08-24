package com.eappcat.flow.flowweb.web;

import com.eappcat.flow.flowweb.service.FlowEngine;
import com.eappcat.flow.flowweb.service.FlowService;
import com.eappcat.flow.flowweb.vo.FlowVO;
import com.eappcat.flow.flowweb.vo.RequestVO;
import com.eappcat.flow.flowweb.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/flow")
@CrossOrigin
public class FlowDefController {
    @Autowired
    private FlowService flowService;
    @Autowired
    private FlowEngine flowEngine;
    @PostMapping("save")
    public ResponseVO<FlowVO> save(@RequestBody FlowVO flowVO){
        flowService.save(flowVO);
        return ResponseVO.of(flowVO);
    }
    @GetMapping("load")
    public ResponseVO<FlowVO> load(@RequestParam("id") String id){
        return ResponseVO.of(flowService.load(id));
    }
    @PostMapping("run")
    public ResponseVO<String> run(@RequestBody FlowVO flowVO) throws Exception{
        String output = flowEngine.run(flowVO.getContent(),bindings -> {
            bindings.put("request",new RequestVO());
            bindings.put("response",new ResponseVO<>());
        },null,true);
        return ResponseVO.of(output);
    }


}
