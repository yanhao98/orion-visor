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
package org.dromara.visor.module.infra.controller;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.validator.group.Page;
import org.dromara.visor.framework.biz.operator.log.core.annotation.OperatorLog;
import org.dromara.visor.framework.log.core.annotation.IgnoreLog;
import org.dromara.visor.framework.log.core.enums.IgnoreLogMode;
import org.dromara.visor.framework.web.core.annotation.DemoDisableApi;
import org.dromara.visor.framework.web.core.annotation.RestWrapper;
import org.dromara.visor.module.infra.define.operator.TagOperatorType;
import org.dromara.visor.module.infra.entity.request.tag.TagCreateRequest;
import org.dromara.visor.module.infra.entity.request.tag.TagQueryRequest;
import org.dromara.visor.module.infra.entity.request.tag.TagUpdateRequest;
import org.dromara.visor.module.infra.entity.vo.TagVO;
import org.dromara.visor.module.infra.service.TagService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据标签 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-5 11:58
 */
@Tag(name = "infra - 数据标签服务")
@Slf4j
@Validated
@RestWrapper
@RestController
@RequestMapping("/infra/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @DemoDisableApi
    @OperatorLog(TagOperatorType.CREATE)
    @PostMapping("/create")
    @Operation(summary = "创建标签")
    @PreAuthorize("@ss.hasPermission('infra:tag:create')")
    public Long createTag(@Validated @RequestBody TagCreateRequest request) {
        return tagService.createTag(request);
    }

    @DemoDisableApi
    @OperatorLog(TagOperatorType.UPDATE)
    @PutMapping("/update")
    @Operation(summary = "修改标签")
    @PreAuthorize("@ss.hasPermission('infra:tag:update')")
    public Integer updateTag(@Validated @RequestBody TagUpdateRequest request) {
        return tagService.updateTag(request);
    }

    @IgnoreLog(IgnoreLogMode.RET)
    @PostMapping("/query")
    @Operation(summary = "分页查询标签")
    @PreAuthorize("@ss.hasPermission('infra:tag:query')")
    public DataGrid<TagVO> getTagPage(@Validated(Page.class) @RequestBody TagQueryRequest request) {
        return tagService.getTagPage(request);
    }

    @IgnoreLog(IgnoreLogMode.RET)
    @GetMapping("/list")
    @Operation(summary = "查询标签")
    @Parameter(name = "type", description = "type", required = true)
    public List<TagVO> getTagList(@RequestParam("type") String type) {
        return tagService.getTagList(type);
    }

    @DemoDisableApi
    @OperatorLog(TagOperatorType.DELETE)
    @DeleteMapping("/delete")
    @Operation(summary = "通过 id 删除标签")
    @Parameter(name = "id", description = "id", required = true)
    @PreAuthorize("@ss.hasPermission('infra:tag:delete')")
    public Integer deleteTag(@RequestParam("id") Long id) {
        return tagService.deleteTagById(id);
    }

}
