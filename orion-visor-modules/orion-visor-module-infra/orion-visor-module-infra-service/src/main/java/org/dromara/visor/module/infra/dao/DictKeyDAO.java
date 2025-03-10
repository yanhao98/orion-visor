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
import org.dromara.visor.common.constant.Const;
import org.dromara.visor.framework.mybatis.core.mapper.IMapper;
import org.dromara.visor.module.infra.entity.domain.DictKeyDO;

/**
 * 字典配置项 Mapper 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-16 16:33
 */
@Mapper
public interface DictKeyDAO extends IMapper<DictKeyDO> {

    /**
     * 通过 key 查询
     *
     * @param key key
     * @return dictKey
     */
    default DictKeyDO selectByKey(String key) {
        LambdaQueryWrapper<DictKeyDO> wrapper = this.lambda()
                .eq(DictKeyDO::getKeyName, key)
                .last(Const.LIMIT_1);
        return this.selectOne(wrapper);
    }

}
