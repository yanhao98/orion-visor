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
package org.dromara.visor.framework.security.core.handler;

import cn.orionsec.kit.web.servlet.web.Servlets;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.ErrorCode;
import org.dromara.visor.framework.security.core.utils.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足处理器
 * <p>
 * {@code @PreAuthorize("@ss.has('xxx')") } 返回 false 会进入此处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/7/6 16:01
 */
@Slf4j
public class ForbiddenAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        log.warn("AccessDeniedHandlerImpl-handle-forbidden {} {}", SecurityUtils.getLoginUserId(), request.getRequestURI());
        Servlets.writeHttpWrapper(response, ErrorCode.FORBIDDEN.getWrapper());
    }

}
