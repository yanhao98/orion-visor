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
package org.dromara.visor.module.infra.handler.upload.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传操作类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/5/7 18:01
 */
@Getter
@AllArgsConstructor
public enum FileUploadOperatorType {

    /**
     * 开始上传
     */
    START("start"),

    /**
     * 上传完成
     */
    FINISH("finish"),

    /**
     * 上传失败
     */
    ERROR("error"),

    ;

    private final String type;

    public static FileUploadOperatorType of(String type) {
        if (type == null) {
            return null;
        }
        for (FileUploadOperatorType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
