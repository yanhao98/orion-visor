/*
 * Copyright (c) 2023 - present Dromara, All rights reserved.
 *
 *   https://visor.dromara.org
 *   https://visor.dromara.org.cn
 *   https://visor.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.visor.module.infra.service.impl;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Strings;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.module.infra.convert.HistoryValueConvert;
import org.dromara.visor.module.infra.dao.HistoryValueDAO;
import org.dromara.visor.module.infra.entity.domain.HistoryValueDO;
import org.dromara.visor.module.infra.entity.request.history.HistoryValueCreateRequest;
import org.dromara.visor.module.infra.entity.request.history.HistoryValueQueryRequest;
import org.dromara.visor.module.infra.entity.vo.HistoryValueVO;
import org.dromara.visor.module.infra.service.HistoryValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 历史归档 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-16 16:33
 */
@Slf4j
@Service
public class HistoryValueServiceImpl implements HistoryValueService {

    @Resource
    private HistoryValueDAO historyValueDAO;

    @Override
    public Long createHistoryValue(HistoryValueCreateRequest request) {
        log.info("HistoryValueService-createHistoryValue request: {}", JSON.toJSONString(request));
        // 转换
        HistoryValueDO record = HistoryValueConvert.MAPPER.to(request);
        // 插入
        int effect = historyValueDAO.insert(record);
        Long id = record.getId();
        log.info("HistoryValueService-createHistoryValue id: {}, effect: {}", id, effect);
        return id;
    }

    @Override
    public DataGrid<HistoryValueVO> getHistoryValuePage(HistoryValueQueryRequest request) {
        // 条件
        LambdaQueryWrapper<HistoryValueDO> wrapper = this.buildQueryWrapper(request)
                .orderByDesc(HistoryValueDO::getId);
        // 查询
        return historyValueDAO.of()
                .wrapper(wrapper)
                .page(request)
                .dataGrid(HistoryValueConvert.MAPPER::to);
    }

    @Override
    public HistoryValueDO getHistoryById(Long id) {
        return historyValueDAO.selectById(id);
    }

    @Override
    public HistoryValueDO getHistoryByRelId(Long id, Long relId, String type) {
        return historyValueDAO.of()
                .createWrapper()
                .eq(HistoryValueDO::getId, id)
                .eq(HistoryValueDO::getRelId, relId)
                .eq(HistoryValueDO::getType, type)
                .then()
                .getOne();
    }

    @Override
    public Integer deleteByRelId(String type, Long relId) {
        log.info("HistoryValueService-deleteByRelId type: {}, relId: {}", type, relId);
        int effect = historyValueDAO.deleteByRelId(type, relId);
        log.info("HistoryValueService-deleteByRelId type: {}, effect: {}", type, effect);
        return effect;
    }

    @Override
    public Integer deleteByRelIdList(String type, List<Long> relIdList) {
        log.info("HistoryValueService-deleteByRelIdList type: {}, relIdList: {}", type, relIdList);
        int effect = historyValueDAO.deleteByRelIdList(type, relIdList);
        log.info("HistoryValueService-deleteByRelIdList type: {}, effect: {}", type, effect);
        return effect;
    }

    /**
     * 构建查询 wrapper
     *
     * @param request request
     * @return wrapper
     */
    private LambdaQueryWrapper<HistoryValueDO> buildQueryWrapper(HistoryValueQueryRequest request) {
        String searchValue = request.getSearchValue();
        return historyValueDAO.wrapper()
                .eq(HistoryValueDO::getRelId, request.getRelId())
                .eq(HistoryValueDO::getType, request.getType())
                .and(Strings.isNotEmpty(searchValue), c -> c
                        .like(HistoryValueDO::getBeforeValue, searchValue).or()
                        .like(HistoryValueDO::getAfterValue, searchValue)
                );
    }

}
