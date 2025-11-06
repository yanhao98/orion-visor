package org.dromara.visor.module.infra.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.visor.common.entity.RequestIdentityModel;

/**
 * 登录失败信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2025/10/8 15:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginFailedDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 失败次数
     */
    private Integer failedCount;

    /**
     * 失效时间
     */
    private Long expireTime;

    /**
     * 原始登录留痕信息
     */
    private RequestIdentityModel origin;

}
