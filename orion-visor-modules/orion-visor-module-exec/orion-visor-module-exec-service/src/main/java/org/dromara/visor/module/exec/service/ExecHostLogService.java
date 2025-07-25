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

import org.dromara.visor.module.exec.entity.vo.ExecHostLogVO;

import java.util.List;

/**
 * 批量执行主机日志 服务类
 *
 * @author Jiahang Li
 * @version 1.0.1
 * @since 2024-3-11 14:05
 */
public interface ExecHostLogService {

    /**
     * 查询批量执行主机日志
     *
     * @param id id
     * @return row
     */
    ExecHostLogVO getExecHostLog(Long id);

    /**
     * 查询全部批量执行主机日志
     *
     * @param logId logId
     * @return rows
     */
    List<ExecHostLogVO> getExecHostLogList(Long logId);

    /**
     * 删除批量执行主机日志
     *
     * @param logIdList logIdList
     * @return effect
     */
    Integer deleteExecHostLogByLogId(List<Long> logIdList);

    /**
     * 删除批量执行主机日志
     *
     * @param id id
     * @return effect
     */
    Integer deleteExecHostLogById(Long id);

}
