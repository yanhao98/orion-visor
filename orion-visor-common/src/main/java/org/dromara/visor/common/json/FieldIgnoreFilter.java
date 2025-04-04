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
package org.dromara.visor.common.json;

import cn.orionsec.kit.lang.utils.collect.Lists;
import com.alibaba.fastjson.serializer.PropertyFilter;

import java.util.List;

/**
 * 字段忽略过滤器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/10/12 11:21
 */
public class FieldIgnoreFilter implements PropertyFilter {

    private final List<String> ignoreFields;

    public FieldIgnoreFilter(List<String> ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    @Override
    public boolean apply(Object object, String name, Object value) {
        return Lists.isEmpty(ignoreFields) || !ignoreFields.contains(name);
    }

}
