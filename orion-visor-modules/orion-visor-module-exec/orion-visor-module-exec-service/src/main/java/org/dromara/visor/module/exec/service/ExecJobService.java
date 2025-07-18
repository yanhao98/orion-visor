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
package org.dromara.visor.module.exec.service;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.visor.module.exec.entity.domain.ExecJobDO;
import org.dromara.visor.module.exec.entity.request.exec.*;
import org.dromara.visor.module.exec.entity.vo.ExecJobVO;

import java.util.List;

/**
 * 计划任务 服务类
 *
 * @author Jiahang Li
 * @version 1.0.3
 * @since 2024-3-28 12:03
 */
public interface ExecJobService {

    /**
     * 创建计划任务
     *
     * @param request request
     * @return id
     */
    Long createExecJob(ExecJobCreateRequest request);

    /**
     * 更新计划任务
     *
     * @param request request
     * @return effect
     */
    Integer updateExecJobById(ExecJobUpdateRequest request);

    /**
     * 更新计划任务状态
     *
     * @param request request
     * @return effect
     */
    Integer updateExecJobStatus(ExecJobUpdateStatusRequest request);

    /**
     * 修改执行用户
     *
     * @param request request
     * @return effect
     */
    Integer updateExecJobExecUser(ExecJobUpdateExecUserRequest request);

    /**
     * 查询计划任务
     *
     * @param id id
     * @return row
     */
    ExecJobVO getExecJobById(Long id);

    /**
     * 查询全部计划任务
     *
     * @return rows
     */
    List<ExecJobVO> getExecJobList();

    /**
     * 分页查询计划任务
     *
     * @param request request
     * @return rows
     */
    DataGrid<ExecJobVO> getExecJobPage(ExecJobQueryRequest request);

    /**
     * 查询计划任务数量
     *
     * @param request request
     * @return count
     */
    Long getExecJobCount(ExecJobQueryRequest request);

    /**
     * 获取下一个执行序列
     *
     * @param id id
     * @return seq
     */
    Integer getNextExecSeq(Long id);

    /**
     * 删除计划任务
     *
     * @param id id
     * @return effect
     */
    Integer deleteExecJobById(Long id);

    /**
     * 批量删除计划任务
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteExecJobByIdList(List<Long> idList);

    /**
     * 手动触发任务
     *
     * @param id id
     */
    void manualTriggerExecJob(Long id);

    /**
     * 触发任务
     *
     * @param request request
     * @param job     job
     */
    void triggerExecJob(ExecJobTriggerRequest request, ExecJobDO job);

    /**
     * 构建查询 wrapper
     *
     * @param request request
     * @return wrapper
     */
    LambdaQueryWrapper<ExecJobDO> buildQueryWrapper(ExecJobQueryRequest request);

}
