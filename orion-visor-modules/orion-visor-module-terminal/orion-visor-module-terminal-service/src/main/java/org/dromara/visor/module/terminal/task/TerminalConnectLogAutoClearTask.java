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
package org.dromara.visor.module.terminal.task;

import cn.orionsec.kit.lang.utils.Booleans;
import cn.orionsec.kit.lang.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.Const;
import org.dromara.visor.common.utils.LockerUtils;
import org.dromara.visor.module.common.config.AppAutoClearConfig;
import org.dromara.visor.module.terminal.entity.request.terminal.TerminalConnectLogClearRequest;
import org.dromara.visor.module.terminal.enums.TerminalConnectStatusEnum;
import org.dromara.visor.module.terminal.service.TerminalConnectLogService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 终端连接日志自动清理
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/4/24 23:50
 */
@Slf4j
@Component
public class TerminalConnectLogAutoClearTask {

    private static final Integer BATCH_SIZE = Const.N_1000;

    private static final String LOCK_KEY = "clear:tcl:lock";

    @Resource
    private AppAutoClearConfig appAutoClearConfig;

    @Resource
    private TerminalConnectLogService terminalConnectLogService;

    /**
     * 清理
     */
    @Scheduled(cron = "0 10 3 * * ?")
    public void clear() {
        log.info("TerminalConnectLogAutoClearTask.clear start");
        // 检查是否开启
        if (!Booleans.isTrue(appAutoClearConfig.getTerminalLogEnabled())) {
            log.info("TerminalConnectLogAutoClearTask.clear disabled");
            return;
        }
        // 获取锁并执行
        LockerUtils.tryLock(LOCK_KEY, this::doClear);
        log.info("TerminalConnectLogAutoClearTask.clear finish");
    }

    /**
     * 执行清理
     */
    private void doClear() {
        // 删除的时间区间
        Date createLessEq = Dates.stream()
                .subDay(appAutoClearConfig.getTerminalLogKeepDays())
                .date();
        // 清理
        for (int i = 0; ; i++) {
            log.info("TerminalConnectLogAutoClearTask.doClear start batch: {}", i + 1);
            TerminalConnectLogClearRequest request = new TerminalConnectLogClearRequest();
            request.setCreateTimeLe(createLessEq);
            request.setStatusList(TerminalConnectStatusEnum.FINISH_STATUS_LIST);
            request.setLimit(BATCH_SIZE);
            Integer count = terminalConnectLogService.clearTerminalConnectLog(request);
            log.info("TerminalConnectLogAutoClearTask.doClear end batch: {}, count: {}", i + 1, count);
            // 最后一批
            if (count < BATCH_SIZE) {
                return;
            }
        }
    }

}
