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
package org.dromara.visor.module.infra.service.impl;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Objects1;
import cn.orionsec.kit.lang.utils.collect.Lists;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.Const;
import org.dromara.visor.common.constant.ErrorMessage;
import org.dromara.visor.common.utils.Assert;
import org.dromara.visor.framework.biz.operator.log.core.utils.OperatorLogs;
import org.dromara.visor.framework.mybatis.core.query.Conditions;
import org.dromara.visor.framework.redis.core.utils.RedisLists;
import org.dromara.visor.framework.redis.core.utils.barrier.CacheBarriers;
import org.dromara.visor.module.infra.convert.TagConvert;
import org.dromara.visor.module.infra.dao.TagDAO;
import org.dromara.visor.module.infra.dao.TagRelDAO;
import org.dromara.visor.module.infra.define.cache.TagCacheKeyDefine;
import org.dromara.visor.module.infra.entity.domain.TagDO;
import org.dromara.visor.module.infra.entity.dto.TagCacheDTO;
import org.dromara.visor.module.infra.entity.po.TagRelCountPO;
import org.dromara.visor.module.infra.entity.request.tag.TagCreateRequest;
import org.dromara.visor.module.infra.entity.request.tag.TagQueryRequest;
import org.dromara.visor.module.infra.entity.request.tag.TagUpdateRequest;
import org.dromara.visor.module.infra.entity.vo.TagVO;
import org.dromara.visor.module.infra.service.TagRelService;
import org.dromara.visor.module.infra.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据标签 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-5 11:58
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagDAO tagDAO;

    @Resource
    private TagRelDAO tagRelDAO;

    @Resource
    private TagRelService tagRelService;

    @Override
    public Long createTag(TagCreateRequest request) {
        // 转换
        TagDO record = TagConvert.MAPPER.to(request);
        // 查询 tag 是否存在
        String type = record.getType();
        LambdaQueryWrapper<TagDO> wrapper = tagDAO.wrapper()
                .eq(TagDO::getName, record.getName())
                .eq(TagDO::getType, type);
        TagDO checkTag = tagDAO.of(wrapper).getOne();
        if (checkTag != null) {
            return checkTag.getId();
        }
        // 插入
        int effect = tagDAO.insert(record);
        log.info("TagService-createTag effect: {}, record: {}", effect, JSON.toJSONString(record));
        // 设置缓存
        String cacheKey = TagCacheKeyDefine.TAG_NAME.format(type);
        TagCacheDTO cache = TagConvert.MAPPER.toCache(record);
        RedisLists.pushJson(cacheKey, cache);
        RedisLists.setExpire(cacheKey, TagCacheKeyDefine.TAG_NAME);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateTag(TagUpdateRequest request) {
        Long id = request.getId();
        log.info("TagService-updateTag id: {}, request: {}", id, JSON.toJSONString(request));
        // 查询标签是否存在
        TagDO record = tagDAO.selectById(id);
        Assert.notNull(record, ErrorMessage.DATA_ABSENT);
        // 转换
        TagDO updateRecord = TagConvert.MAPPER.to(request);
        // 检查数据是否存在
        this.checkTagPresent(updateRecord);
        // 更新
        int effect = tagDAO.updateById(updateRecord);
        log.info("HostProxyService-updateHostProxyById effect: {}", effect);
        // 删除缓存
        RedisLists.delete(TagCacheKeyDefine.TAG_NAME.format(record.getType()));
        return effect;
    }

    @Override
    public List<TagVO> getTagList(String type) {
        // 查询缓存
        String cacheKey = TagCacheKeyDefine.TAG_NAME.format(type);
        List<TagCacheDTO> list = RedisLists.rangeJson(cacheKey, TagCacheKeyDefine.TAG_NAME);
        if (list.isEmpty()) {
            // 为空则需要查询缓存
            LambdaQueryWrapper<TagDO> wrapper = Conditions.eq(TagDO::getType, type);
            list = tagDAO.of(wrapper).list(TagConvert.MAPPER::toCache);
            // 设置屏障 防止穿透
            CacheBarriers.checkBarrier(list, TagCacheDTO::new);
            // 设置到缓存
            RedisLists.pushAllJson(cacheKey, TagCacheKeyDefine.TAG_NAME, list);
        }
        // 删除屏障
        CacheBarriers.removeBarrier(list);
        // 转换
        return list.stream()
                .map(TagConvert.MAPPER::to)
                .collect(Collectors.toList());
    }

    @Override
    public DataGrid<TagVO> getTagPage(TagQueryRequest request) {
        // 查询标签
        DataGrid<TagVO> dataGrid = tagDAO.of()
                .createValidateWrapper()
                .eq(TagDO::getType, request.getType())
                .like(TagDO::getName, request.getName())
                .then()
                .page(request)
                .order(request, TagDO::getId)
                .dataGrid(TagConvert.MAPPER::to);
        // 查询数量
        if (!dataGrid.isEmpty()) {
            List<Long> tagIdList = dataGrid.stream()
                    .map(TagVO::getId)
                    .collect(Collectors.toList());
            // 查询数量
            Map<Long, Integer> tagRelCountMap = tagRelDAO.selectTagRelCount(tagIdList)
                    .stream()
                    .collect(Collectors.toMap(TagRelCountPO::getTagId,
                            s -> Objects1.def(s.getCount(), Const.N_0)));
            // 设置数量
            for (TagVO tag : dataGrid) {
                Integer count = tagRelCountMap.getOrDefault(tag.getId(), Const.N_0);
                tag.setRelCount(count);
            }
        }
        return dataGrid;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteTagById(Long id) {
        return this.deleteTagByIdList(Lists.singleton(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteTagByIdList(List<Long> idList) {
        List<TagDO> tagList = tagDAO.selectBatchIds(idList);
        if (tagList.isEmpty()) {
            return Const.N_0;
        }
        // 设置日志参数
        String name = tagList.stream()
                .map(TagDO::getName)
                .collect(Collectors.joining(Const.COMMA));
        OperatorLogs.add(OperatorLogs.NAME, name);
        // 删除标签
        int effect = tagDAO.deleteBatchIds(idList);
        // 删除关联
        tagRelService.deleteTagIdList(idList);
        log.info("TagService-deleteTagByIdList idList: {}, effect: {}", idList, effect);
        // 删除缓存
        List<String> deleteKeys = tagList.stream()
                .map(TagDO::getType)
                .distinct()
                .map(TagCacheKeyDefine.TAG_NAME::format)
                .collect(Collectors.toList());
        RedisLists.delete(deleteKeys);
        return effect;
    }

    @Override
    public void clearUnusedTag(Integer days) {
        // 查询
        List<TagDO> tagList = tagDAO.selectUnusedTag(days);
        if (tagList.isEmpty()) {
            log.info("TagService.clearUnusedTag isEmpty");
            return;
        }
        // 删除数据
        List<Long> tagIdList = tagList.stream()
                .map(TagDO::getId)
                .collect(Collectors.toList());
        int effect = tagDAO.deleteBatchIds(tagIdList);
        log.info("TagService.clearUnusedTag deleted count: {}, deleted: {}, tags: {}", tagIdList.size(), effect, tagIdList);
        // 删除缓存
        List<String> cacheKeys = tagList.stream()
                .map(TagDO::getType)
                .distinct()
                .map(TagCacheKeyDefine.TAG_NAME::format)
                .collect(Collectors.toList());
        RedisLists.delete(cacheKeys);
    }

    /**
     * 检查数据是否存在
     *
     * @param domain domain
     */
    private void checkTagPresent(TagDO domain) {
        // 构造条件
        LambdaQueryWrapper<TagDO> wrapper = tagDAO.wrapper()
                // 更新时忽略当前记录
                .ne(TagDO::getId, domain.getId())
                // 用其他字段做重复校验
                .eq(TagDO::getType, domain.getType())
                .eq(TagDO::getName, domain.getName());
        // 检查是否存在
        boolean present = tagDAO.of(wrapper).present();
        Assert.isFalse(present, ErrorMessage.NAME_PRESENT);
    }

}
