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
package org.dromara.visor.module.terminal.handler.transfer.model;

import cn.orionsec.kit.net.host.SessionStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.visor.common.session.config.SshConnectConfig;

/**
 * 终端连接信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/7/12 23:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalConnection {

    /**
     * 连接信息
     */
    private SshConnectConfig connectConfig;

    /**
     * sessionStore
     */
    private SessionStore sessionStore;

}
