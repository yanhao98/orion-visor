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
package org.dromara.visor.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.utils.IpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 公共配置类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/6/20 10:34
 */
@Slf4j
@Configuration
public class CommonConfiguration {

    @Value("${orion.api.ip-headers}")
    private String[] ipHeaders;

    /**
     * 设置 IP 请求头
     */
    @PostConstruct
    public void setIpHeader() {
        IpUtils.setIpHeader(ipHeaders);
        log.info("IpUtils.setIpHeader {}", String.join(",", ipHeaders));
    }

}
