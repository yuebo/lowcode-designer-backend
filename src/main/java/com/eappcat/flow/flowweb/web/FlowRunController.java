package com.eappcat.flow.flowweb.web;

import com.eappcat.flow.flowweb.service.FlowEngine;
import com.eappcat.flow.flowweb.service.FlowService;
import com.eappcat.flow.flowweb.vo.FlowVO;
import com.eappcat.flow.flowweb.vo.RequestVO;
import com.eappcat.flow.flowweb.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/flow/run")
@CrossOrigin
public class FlowRunController {
    @Autowired
    private FlowEngine flowEngine;

    @RequestMapping("/**")
    public ResponseVO response(@RequestBody(required = false) String body, @RequestParam Map<String,Object> params, HttpServletRequest request) throws Exception{
        RequestVO requestVO = createRequestVO(body, params, request);
        ResponseVO responseVO = new ResponseVO();
        flowEngine.runMvc(StringUtils.strip(request.getRequestURI(),request.getContextPath().concat("flow/run/")), bindings -> {
            bindings.put("request",requestVO);
            bindings.put("response",responseVO);
        });
        return responseVO;
    }
    private RequestVO createRequestVO(@RequestBody(required = false) String body, @RequestParam Map<String, Object> params, HttpServletRequest request) {
        RequestVO requestVO = new RequestVO();
        requestVO.setUrl(request.getRequestURI());
        requestVO.setParams(params);
        requestVO.setBody(body);
        return requestVO;
    }
}
