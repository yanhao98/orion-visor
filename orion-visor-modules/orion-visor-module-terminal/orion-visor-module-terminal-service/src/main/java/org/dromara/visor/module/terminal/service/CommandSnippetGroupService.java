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
package org.dromara.visor.module.terminal.service;

import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetGroupCreateRequest;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetGroupDeleteRequest;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetGroupUpdateRequest;
import org.dromara.visor.module.terminal.entity.vo.CommandSnippetGroupVO;

import java.util.List;

/**
 * 命令片段分组 服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024-1-24 12:28
 */
public interface CommandSnippetGroupService {

    /**
     * 创建命令片段分组
     *
     * @param request request
     * @return id
     */
    Long createCommandSnippetGroup(CommandSnippetGroupCreateRequest request);

    /**
     * 更新命令片段分组
     *
     * @param request request
     * @return effect
     */
    Integer updateCommandSnippetGroupById(CommandSnippetGroupUpdateRequest request);

    /**
     * 查询全部命令片段分组
     *
     * @return rows
     */
    List<CommandSnippetGroupVO> getCommandSnippetGroupList();

    /**
     * 删除命令片段分组
     *
     * @param request request
     * @return effect
     */
    Integer deleteCommandSnippetGroup(CommandSnippetGroupDeleteRequest request);

    /**
     * 清理未使用的分组
     */
    void clearUnusedGroup();

}
