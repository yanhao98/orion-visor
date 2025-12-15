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
package org.dromara.visor.module.infra.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.visor.common.entity.RequestIdentityModel;

/**
 * 登录失败信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2025/10/8 15:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailedDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 失败次数
     */
    private Integer failedCount;

    /**
     * 失效时间
     */
    private Long expireTime;

    /**
     * 原始登录留痕信息
     */
    private RequestIdentityModel origin;

}
