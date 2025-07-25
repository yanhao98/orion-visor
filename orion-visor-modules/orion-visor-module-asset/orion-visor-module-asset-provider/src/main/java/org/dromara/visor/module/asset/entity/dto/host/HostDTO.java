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
package org.dromara.visor.module.asset.entity.dto.host;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 主机 业务对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-11 14:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HostDTO", description = "主机 业务对象")
public class HostDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "主机类型")
    private String types;

    @Schema(description = "系统类型")
    private String osType;

    @Schema(description = "系统架构")
    private String archType;

    @Schema(description = "主机名称")
    private String name;

    @Schema(description = "主机编码")
    private String code;

    @Schema(description = "主机地址")
    private String address;

    @Schema(description = "主机端口")
    private Integer port;

    @Schema(description = "主机状态")
    private String status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "是否收藏")
    private Boolean favorite;

    @Schema(description = "分组 id")
    private Set<Long> groupIdList;

    @Schema(description = "别名")
    private String alias;

    @Schema(description = "颜色")
    private String color;

    /**
     * 转为 base
     *
     * @return base
     */
    public HostBaseDTO toBase() {
        return HostBaseDTO.builder()
                .id(this.id)
                .types(this.types)
                .name(this.name)
                .code(this.code)
                .address(this.address)
                .port(this.port)
                .build();
    }

}
