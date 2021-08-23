package com.eappcat.flow.flowweb.service;

import cn.hutool.core.util.IdUtil;
import com.eappcat.flow.flowweb.dao.FlowDefDao;
import com.eappcat.flow.flowweb.entity.FlowDef;
import com.eappcat.flow.flowweb.vo.FlowVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class FlowService {
    @Autowired
    private FlowDefDao flowDefDao;

    public void save(FlowVO flowVO) {
        if (StringUtils.isEmpty(flowVO.getId())){
            flowVO.setId(IdUtil.fastSimpleUUID());
        }
        Optional<FlowDef> db = flowDefDao.findById(flowVO.getId());
        FlowDef d = db.orElseGet(FlowDef::new);
        BeanUtils.copyProperties(flowVO, d);
        d.setUpdateDate(new Date());
        flowDefDao.save(d);
    }

    public FlowVO load(String id) {
        FlowVO flowVO= new FlowVO();
        Optional<FlowDef> db = flowDefDao.findById(id);
        FlowDef d = db.orElseGet(FlowDef::new);
        BeanUtils.copyProperties(d, flowVO);
        return flowVO;
    }
}
