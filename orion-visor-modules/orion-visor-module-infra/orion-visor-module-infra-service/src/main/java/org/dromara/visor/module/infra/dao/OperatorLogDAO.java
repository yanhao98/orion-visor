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
package org.dromara.visor.module.infra.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.visor.framework.mybatis.core.mapper.IMapper;
import org.dromara.visor.module.infra.entity.domain.OperatorLogDO;
import org.dromara.visor.module.infra.entity.po.OperatorLogCountPO;

import java.util.Date;
import java.util.List;

/**
 * 操作日志 Mapper 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-10 17:08
 */
@Mapper
public interface OperatorLogDAO extends IMapper<OperatorLogDO> {

    /**
     * 通过 userId 删除
     *
     * @param userId userId
     * @return effect
     */
    default int deleteByUserId(Long userId) {
        LambdaQueryWrapper<OperatorLogDO> wrapper = this.wrapper()
                .eq(OperatorLogDO::getUserId, userId);
        return this.delete(wrapper);
    }

    /**
     * 通过 userId 删除
     *
     * @param userIdList userIdList
     * @return effect
     */
    default int deleteByUserIdList(List<Long> userIdList) {
        LambdaQueryWrapper<OperatorLogDO> wrapper = this.wrapper()
                .in(OperatorLogDO::getUserId, userIdList);
        return this.delete(wrapper);
    }

    /**
     * 查询操作日志类型结果数量
     *
     * @param type      type
     * @param startTime startTime
     * @param endTime   endTime
     * @return rows
     */
    List<OperatorLogCountPO> selectOperatorLogTypeResultCount(@Param("type") String type,
                                                              @Param("startTime") Date startTime,
                                                              @Param("endTime") Date endTime);

    /**
     * 查询操作日志数量
     *
     * @param userId    userId
     * @param startTime startTime
     * @param endTime   endTime
     * @return rows
     */
    List<OperatorLogCountPO> selectOperatorLogCount(@Param("userId") Long userId,
                                                    @Param("startTime") Date startTime,
                                                    @Param("endTime") Date endTime);

}
