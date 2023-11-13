package com.orion.ops.module.asset.controller;

import com.orion.ops.framework.biz.operator.log.core.annotation.OperatorLog;
import com.orion.ops.framework.log.core.annotation.IgnoreLog;
import com.orion.ops.framework.log.core.enums.IgnoreLogMode;
import com.orion.ops.framework.web.core.annotation.RestWrapper;
import com.orion.ops.module.asset.convert.HostGroupConvert;
import com.orion.ops.module.asset.define.operator.HostGroupOperatorType;
import com.orion.ops.module.asset.entity.vo.HostGroupTreeVO;
import com.orion.ops.module.asset.service.HostService;
import com.orion.ops.module.infra.api.DataGroupApi;
import com.orion.ops.module.infra.entity.dto.data.DataGroupCreateDTO;
import com.orion.ops.module.infra.entity.dto.data.DataGroupDTO;
import com.orion.ops.module.infra.entity.dto.data.DataGroupMoveDTO;
import com.orion.ops.module.infra.entity.dto.data.DataGroupRenameDTO;
import com.orion.ops.module.infra.enums.DataGroupTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 主机分组 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-11-09 16:16
 */
@Tag(name = "asset - 主机分组服务")
@Slf4j
@Validated
@RestWrapper
@RestController
@RequestMapping("/asset/host-group")
@SuppressWarnings({"ELValidationInJSP", "SpringElInspection"})
public class HostGroupController {

    @Resource
    private HostService hostService;

    @Resource
    private DataGroupApi dataGroupApi;

    @OperatorLog(HostGroupOperatorType.CREATE)
    @PostMapping("/create")
    @Operation(summary = "创建主机分组")
    @PreAuthorize("@ss.hasPermission('asset:host-group:create')")
    public Long createHostGroup(@Validated @RequestBody DataGroupCreateDTO request) {
        return dataGroupApi.createDataGroup(DataGroupTypeEnum.HOST, request);
    }

    @IgnoreLog(IgnoreLogMode.RET)
    @GetMapping("/tree")
    @Operation(summary = "查询主机分组")
    @PreAuthorize("@ss.hasPermission('asset:host-group:query')")
    public List<HostGroupTreeVO> queryHostGroup() {
        List<DataGroupDTO> rows = dataGroupApi.getDataGroupTree(DataGroupTypeEnum.HOST);
        return HostGroupConvert.MAPPER.toList(rows);
    }

    @OperatorLog(HostGroupOperatorType.RENAME)
    @PutMapping("/rename")
    @Operation(summary = "修改名称")
    @PreAuthorize("@ss.hasPermission('asset:host-group:update')")
    public Integer updateHostGroupName(@Validated @RequestBody DataGroupRenameDTO request) {
        return dataGroupApi.renameDataGroup(request);
    }

    @OperatorLog(HostGroupOperatorType.MOVE)
    @PutMapping("/move")
    @Operation(summary = "移动位置")
    @PreAuthorize("@ss.hasPermission('asset:host-group:update')")
    public Integer moveHostGroup(@Validated @RequestBody DataGroupMoveDTO request) {
        return dataGroupApi.moveDataGroup(request);
    }

    @OperatorLog(HostGroupOperatorType.DELETE)
    @DeleteMapping("/delete")
    @Operation(summary = "删除主机分组")
    @PreAuthorize("@ss.hasPermission('asset:host-group:delete')")
    public Integer deleteHostGroup(@RequestParam("id") Long id) {
        return dataGroupApi.deleteDataGroupById(id);
    }


    // TODO 关联操作配置权限
    // TODO 关联操作配置操作日志类型
    // TODO 关联 增 删 查
    // TODO 聚合查询关联

}

