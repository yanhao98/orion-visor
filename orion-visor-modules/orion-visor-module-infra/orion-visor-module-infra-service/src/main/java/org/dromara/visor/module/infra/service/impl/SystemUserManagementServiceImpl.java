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

import cn.orionsec.kit.lang.utils.Objects1;
import cn.orionsec.kit.lang.utils.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.ErrorMessage;
import org.dromara.visor.common.entity.RequestIdentityModel;
import org.dromara.visor.common.utils.Assert;
import org.dromara.visor.common.utils.Requests;
import org.dromara.visor.framework.biz.operator.log.core.utils.OperatorLogs;
import org.dromara.visor.framework.redis.core.utils.RedisStrings;
import org.dromara.visor.framework.security.core.utils.SecurityUtils;
import org.dromara.visor.module.common.config.AppLoginConfig;
import org.dromara.visor.module.infra.dao.SystemUserDAO;
import org.dromara.visor.module.infra.define.cache.UserCacheKeyDefine;
import org.dromara.visor.module.infra.entity.domain.SystemUserDO;
import org.dromara.visor.module.infra.entity.dto.LoginFailedDTO;
import org.dromara.visor.module.infra.entity.dto.LoginTokenDTO;
import org.dromara.visor.module.infra.entity.request.user.UserSessionOfflineRequest;
import org.dromara.visor.module.infra.entity.request.user.UserUnlockRequest;
import org.dromara.visor.module.infra.entity.vo.UserLockedVO;
import org.dromara.visor.module.infra.entity.vo.UserSessionVO;
import org.dromara.visor.module.infra.enums.LoginTokenStatusEnum;
import org.dromara.visor.module.infra.service.SystemUserManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/11/22 18:17
 */
@Slf4j
@Service
public class SystemUserManagementServiceImpl implements SystemUserManagementService {

    @Resource
    private AppLoginConfig appLoginConfig;

    @Resource
    private SystemUserDAO systemUserDAO;

    @Override
    public Integer getUserSessionCount(Long userId) {
        // 扫描缓存
        Set<String> keys = RedisStrings.scanKeys(UserCacheKeyDefine.LOGIN_TOKEN.format(userId, "*"));
        return Lists.size(keys);
    }

    @Override
    public List<UserLockedVO> getLockedUserList() {
        // 扫描缓存
        Set<String> keys = RedisStrings.scanKeys(UserCacheKeyDefine.LOGIN_FAILED.format("*"));
        if (Lists.isEmpty(keys)) {
            return Lists.empty();
        }
        // 查询缓存
        List<LoginFailedDTO> loginFailedList = RedisStrings.getJsonList(keys, UserCacheKeyDefine.LOGIN_FAILED);
        if (Lists.isEmpty(loginFailedList)) {
            return Lists.empty();
        }
        // 返回
        return loginFailedList.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getFailedCount() >= appLoginConfig.getLoginFailedLockThreshold())
                .map(s -> {
                    RequestIdentityModel origin = s.getOrigin();
                    return UserLockedVO.builder()
                            .username(s.getUsername())
                            .expireTime(s.getExpireTime())
                            .address(origin.getAddress())
                            .location(origin.getLocation())
                            .userAgent(origin.getUserAgent())
                            .loginTime(new Date(origin.getTimestamp()))
                            .build();
                })
                .sorted(Comparator.comparing(UserLockedVO::getLoginTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void unlockLockedUser(UserUnlockRequest request) {
        RedisStrings.delete(UserCacheKeyDefine.LOGIN_FAILED.format(request.getUsername()));
    }

    @Override
    public List<UserSessionVO> getUsersSessionList() {
        // 扫描缓存
        Set<String> keys = RedisStrings.scanKeys(UserCacheKeyDefine.LOGIN_TOKEN.format("*", "*"));
        if (Lists.isEmpty(keys)) {
            return Lists.empty();
        }
        // 获取用户会话列表
        return this.getUserSessionList(keys);
    }

    @Override
    public List<UserSessionVO> getUserSessionList(Long userId) {
        // 扫描缓存
        Set<String> keys = RedisStrings.scanKeys(UserCacheKeyDefine.LOGIN_TOKEN.format(userId, "*"));
        if (Lists.isEmpty(keys)) {
            return Lists.empty();
        }
        // 获取用户会话列表
        return this.getUserSessionList(keys);
    }

    /**
     * 获取用户会话列表
     *
     * @param keys keys
     * @return rows
     */
    private List<UserSessionVO> getUserSessionList(Set<String> keys) {
        Long loginUserId = SecurityUtils.getLoginUserId();
        // 查询缓存
        List<LoginTokenDTO> tokens = RedisStrings.getJsonList(keys, UserCacheKeyDefine.LOGIN_TOKEN);
        if (Lists.isEmpty(tokens)) {
            return Lists.empty();
        }
        // 返回
        return tokens.stream()
                .filter(Objects::nonNull)
                .filter(s -> LoginTokenStatusEnum.OK.getStatus().equals(s.getStatus()))
                .map(s -> {
                    RequestIdentityModel origin = s.getOrigin();
                    return UserSessionVO.builder()
                            .id(s.getId())
                            .username(s.getUsername())
                            .current(Objects1.eq(loginUserId, s.getId()) && origin.getTimestamp().equals(SecurityUtils.getLoginTimestamp()))
                            .address(origin.getAddress())
                            .location(origin.getLocation())
                            .userAgent(origin.getUserAgent())
                            .loginTime(new Date(origin.getTimestamp()))
                            .build();
                })
                .sorted(Comparator.comparing(UserSessionVO::getCurrent).reversed()
                        .thenComparing(Comparator.comparing(UserSessionVO::getLoginTime).reversed()))
                .collect(Collectors.toList());
    }

    @Override
    public void offlineUserSession(UserSessionOfflineRequest request) {
        Long userId = Assert.notNull(request.getUserId());
        Long timestamp = request.getTimestamp();
        log.info("SystemUserManagementService offlineUserSession userId: {}, timestamp: {}", userId, timestamp);
        // 查询用户
        SystemUserDO user = systemUserDAO.selectById(userId);
        Assert.notNull(user, ErrorMessage.USER_ABSENT);
        // 添加日志参数
        OperatorLogs.add(OperatorLogs.USERNAME, user.getUsername());
        // 删除刷新缓存
        RedisStrings.delete(UserCacheKeyDefine.LOGIN_REFRESH.format(userId, request.getTimestamp()));
        // 查询并且覆盖 token
        String tokenKey = UserCacheKeyDefine.LOGIN_TOKEN.format(userId, timestamp);
        LoginTokenDTO tokenInfo = RedisStrings.getJson(tokenKey, UserCacheKeyDefine.LOGIN_TOKEN);
        if (tokenInfo != null) {
            tokenInfo.setStatus(LoginTokenStatusEnum.SESSION_OFFLINE.getStatus());
            // 设置留痕信息
            tokenInfo.setOverride(Requests.getIdentity());
            // 更新 token
            RedisStrings.setJson(tokenKey, UserCacheKeyDefine.LOGIN_TOKEN, tokenInfo);
        }
    }

}
