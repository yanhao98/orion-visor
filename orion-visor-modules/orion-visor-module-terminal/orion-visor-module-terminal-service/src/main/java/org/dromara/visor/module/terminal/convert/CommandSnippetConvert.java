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
package org.dromara.visor.module.terminal.convert;

import org.dromara.visor.module.terminal.entity.domain.CommandSnippetDO;
import org.dromara.visor.module.terminal.entity.dto.CommandSnippetCacheDTO;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetCreateRequest;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetUpdateRequest;
import org.dromara.visor.module.terminal.entity.vo.CommandSnippetVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 命令片段 内部对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024-1-22 15:28
 */
@Mapper
public interface CommandSnippetConvert {

    CommandSnippetConvert MAPPER = Mappers.getMapper(CommandSnippetConvert.class);

    CommandSnippetDO to(CommandSnippetCreateRequest request);

    CommandSnippetDO to(CommandSnippetUpdateRequest request);

    CommandSnippetVO to(CommandSnippetDO domain);

    List<CommandSnippetVO> to(List<CommandSnippetDO> list);

    CommandSnippetVO to(CommandSnippetCacheDTO cache);

    CommandSnippetCacheDTO toCache(CommandSnippetDO domain);

}
