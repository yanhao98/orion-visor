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
package org.dromara.visor.module.terminal.handler.terminal.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * sftp 文件响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/2/6 13:57
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SftpFileVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 绝对路径
     */
    private String path;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 文件大小(byte)
     */
    private Long size;

    /**
     * 属性
     */
    private String attr;

    /**
     * 是否为目录
     */
    private Boolean isDir;

    /**
     * 10进制表现的8进制权限
     */
    private Integer permission;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 组id
     */
    private Integer gid;

    /**
     * 更新时间
     */
    private Date modifyTime;

    /**
     * 是否可预览
     */
    private Boolean canPreview;

}
