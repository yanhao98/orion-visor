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
package org.dromara.visor.framework.web.configuration.config;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.net.IPs;
import lombok.Data;
import org.dromara.visor.common.constant.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * api 配置属性
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2025/12/8 14:00
 */
@Data
@ConfigurationProperties("orion.api")
public class OrionApiConfig {

    private static final String URL_TEMPLATE = "http://{}:{}{}";

    /**
     * 公共 api 前缀
     */
    private String prefix;

    /**
     * 服务端主机地址
     */
    private String host;

    /**
     * 服务端口
     */
    @Value("${server.port}")
    private Integer port;

    /**
     * 服务端 url
     */
    private String url;

    public String getHost() {
        if (Const.IP_0000.equalsIgnoreCase(host)) {
            // 本机
            return IPs.IP;
        } else {
            return host;
        }
    }

    public String getUrl() {
        if (!Strings.isBlank(url)) {
            return url;
        }
        // 构建
        return Strings.format(URL_TEMPLATE, this.getHost(), port, prefix);
    }

}
