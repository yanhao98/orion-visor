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
package org.dromara.visor.common.utils;

import cn.orionsec.kit.ext.location.Region;
import cn.orionsec.kit.ext.location.region.LocationRegions;
import cn.orionsec.kit.lang.utils.net.IPs;
import org.dromara.visor.common.constant.Const;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ip 工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/7/14 16:26
 */
public class IpUtils {

    private static String[] IP_HEADER = new String[]{"X-Forwarded-For", "X-Real-IP"};

    private static final Map<String, String> CACHE = new HashMap<>();

    private IpUtils() {
    }

    /**
     * 获取请求地址
     *
     * @param request request
     * @return addr
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        } else {
            for (String remoteAddrHeader : IP_HEADER) {
                String addr = checkIpHeader(request.getHeader(remoteAddrHeader));
                if (addr != null) {
                    return addr;
                }
            }
            return checkIpHeader(request.getRemoteAddr());
        }
    }

    /**
     * 获取 ip 位置
     *
     * @param ip ip
     * @return ip 位置
     */
    public static String getLocation(String ip) {
        if (ip == null) {
            return Const.CN_UNKNOWN;
        }
        // 查询缓存
        return CACHE.computeIfAbsent(ip, IpUtils::queryLocation);
    }

    /**
     * 查询 ip 位置
     *
     * @param ip ip
     * @return ip 位置
     */
    private static String queryLocation(String ip) {
        if (ip == null) {
            return Const.CN_UNKNOWN;
        }
        Region region;
        try {
            region = LocationRegions.getRegion(ip, 3);
        } catch (Exception e) {
            return Const.CN_UNKNOWN;
        }
        if (region != null) {
            String net = region.getNet();
            String province = region.getProvince();
            if (net.equals(Const.CN_INTRANET_IP)) {
                return net;
            }
            if (province.equals(Const.CN_UNKNOWN)) {
                return province;
            }
            StringBuilder location = new StringBuilder()
                    .append(region.getCountry())
                    .append(Const.DASHED)
                    .append(province)
                    .append(Const.DASHED)
                    .append(region.getCity());
            location.append(" (").append(net).append(')');
            return location.toString();
        }
        return Const.CN_UNKNOWN;
    }

    /**
     * 检查 ip 请求头
     *
     * @param headerValue headerValue
     * @return header
     */
    private static String checkIpHeader(String headerValue) {
        if (headerValue == null) {
            return null;
        } else {
            headerValue = headerValue.split(",")[0];
            return IPs.checkIp(headerValue);
        }
    }

    public static void setIpHeader(String[] ipHeader) {
        IP_HEADER = ipHeader;
    }

}
