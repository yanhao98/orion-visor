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
package org.dromara.visor.module.terminal.service.impl;

import cn.orionsec.kit.lang.utils.Booleans;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.Const;
import org.dromara.visor.common.constant.ErrorMessage;
import org.dromara.visor.common.utils.Valid;
import org.dromara.visor.framework.security.core.utils.SecurityUtils;
import org.dromara.visor.module.infra.api.DataGroupApi;
import org.dromara.visor.module.infra.api.DataGroupUserApi;
import org.dromara.visor.module.infra.entity.dto.data.DataGroupCreateDTO;
import org.dromara.visor.module.infra.entity.dto.data.DataGroupDTO;
import org.dromara.visor.module.infra.entity.dto.data.DataGroupRenameDTO;
import org.dromara.visor.module.infra.enums.DataGroupTypeEnum;
import org.dromara.visor.module.terminal.convert.CommandSnippetGroupConvert;
import org.dromara.visor.module.terminal.dao.CommandSnippetDAO;
import org.dromara.visor.module.terminal.entity.domain.CommandSnippetDO;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetGroupCreateRequest;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetGroupDeleteRequest;
import org.dromara.visor.module.terminal.entity.request.snippet.CommandSnippetGroupUpdateRequest;
import org.dromara.visor.module.terminal.entity.vo.CommandSnippetGroupVO;
import org.dromara.visor.module.terminal.service.CommandSnippetGroupService;
import org.dromara.visor.module.terminal.service.CommandSnippetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 命令片段分组 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024-1-24 12:28
 */
@Slf4j
@Service
public class CommandSnippetGroupServiceImpl implements CommandSnippetGroupService {

    @Resource
    private DataGroupApi dataGroupApi;

    @Resource
    private DataGroupUserApi dataGroupUserApi;

    @Resource
    private CommandSnippetDAO commandSnippetDAO;

    @Resource
    private CommandSnippetService commandSnippetService;

    @Override
    public Long createCommandSnippetGroup(CommandSnippetGroupCreateRequest request) {
        Long userId = SecurityUtils.getLoginUserId();
        log.info("CommandSnippetGroupService-createCommandSnippetGroup request: {}", JSON.toJSONString(request));
        // 创建
        DataGroupCreateDTO create = CommandSnippetGroupConvert.MAPPER.to(request);
        create.setParentId(Const.ROOT_PARENT_ID);
        return dataGroupUserApi.createDataGroup(DataGroupTypeEnum.COMMAND_SNIPPET, userId, create);
    }

    @Override
    public Integer updateCommandSnippetGroupById(CommandSnippetGroupUpdateRequest request) {
        Long id = Valid.notNull(request.getId(), ErrorMessage.ID_MISSING);
        log.info("CommandSnippetGroupService-updateCommandSnippetGroupById id: {}, request: {}", id, JSON.toJSONString(request));
        // 重命名
        DataGroupRenameDTO rename = CommandSnippetGroupConvert.MAPPER.to(request);
        return dataGroupApi.renameDataGroup(rename);
    }

    @Override
    public List<CommandSnippetGroupVO> getCommandSnippetGroupList() {
        Long userId = SecurityUtils.getLoginUserId();
        // 查询分组
        return dataGroupUserApi.getDataGroupList(DataGroupTypeEnum.COMMAND_SNIPPET, userId)
                .stream()
                .sorted(Comparator.comparing(DataGroupDTO::getSort))
                .map(CommandSnippetGroupConvert.MAPPER::to)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteCommandSnippetGroup(CommandSnippetGroupDeleteRequest request) {
        Long userId = SecurityUtils.getLoginUserId();
        Long id = request.getId();
        log.info("CommandSnippetGroupService-deleteCommandSnippetGroupById id: {}", id);
        // 删除分组
        Integer effect = dataGroupApi.deleteDataGroupById(id);
        if (Booleans.isTrue(request.getDeleteItem())) {
            // 删除组内数据
            effect += commandSnippetService.deleteByGroupId(userId, id);
        } else {
            // 移动到根节点
            effect += commandSnippetService.setGroupNull(userId, id);
        }
        return effect;
    }

    @Override
    public void clearUnusedGroup() {
        // 查询全部 groupId
        Map<Long, List<Long>> userGroupMap = commandSnippetDAO.of()
                .createWrapper()
                .select(CommandSnippetDO::getUserId, CommandSnippetDO::getGroupId)
                .isNotNull(CommandSnippetDO::getGroupId)
                .groupBy(CommandSnippetDO::getUserId)
                .groupBy(CommandSnippetDO::getGroupId)
                .then()
                .stream()
                .collect(Collectors.groupingBy(CommandSnippetDO::getUserId,
                        Collectors.mapping(CommandSnippetDO::getGroupId, Collectors.toList())));
        userGroupMap.forEach((k, v) -> {
            // 查询用户分组
            List<DataGroupDTO> groups = dataGroupUserApi.getDataGroupList(DataGroupTypeEnum.COMMAND_SNIPPET, k);
            if (groups.isEmpty()) {
                return;
            }
            // 不存在的则移除
            List<Long> deleteGroupList = groups.stream()
                    .map(DataGroupDTO::getId)
                    .filter(s -> !v.contains(s))
                    .collect(Collectors.toList());
            if (deleteGroupList.isEmpty()) {
                return;
            }
            log.info("CommandSnippetGroupService.clearUnusedGroup userId: {}, deleteGroupList: {}", k, deleteGroupList);
            // 删除分组
            Integer effect = dataGroupUserApi.deleteDataGroupByIdList(DataGroupTypeEnum.COMMAND_SNIPPET, k, deleteGroupList);
            log.info("CommandSnippetGroupService.clearUnusedGroup userId: {}, effect: {}", k, effect);
        });
    }

}
