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
package org.dromara.visor.module.terminal.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终端连接日志拓展信息对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024-3-12 23:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TerminalConnectLogExtraDTO", description = "终端连接日志拓展信息对象")
public class TerminalConnectLogExtraDTO {

    @Schema(description = "hostId")
    private Long hostId;

    @Schema(description = "主机名称")
    private String hostName;

    @Schema(description = "连接类型")
    private String connectType;

    @Schema(description = "traceId")
    private String traceId;

    @Schema(description = "channel")
    private String channel;

    @Schema(description = "sessionId")
    private String sessionId;

    @Schema(description = "请求地址")
    private String address;

    @Schema(description = "请求位置")
    private String location;

    @Schema(description = "ua")
    private String userAgent;

    @Schema(description = "错误信息")
    private String errorMessage;

}
