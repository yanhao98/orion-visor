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
package org.dromara.visor.module.terminal.handler.terminal.session;

import cn.orionsec.kit.lang.utils.Booleans;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;
import org.dromara.visor.common.constant.AppConst;
import org.dromara.visor.module.common.config.GuacdConfig;
import org.dromara.visor.module.terminal.enums.DriveMountModeEnum;
import org.dromara.visor.module.terminal.handler.guacd.GuacdTunnel;
import org.dromara.visor.module.terminal.handler.guacd.IGuacdTunnel;
import org.dromara.visor.module.terminal.handler.guacd.constant.GuacdConst;
import org.dromara.visor.module.terminal.handler.guacd.constant.GuacdProtocol;
import org.dromara.visor.module.terminal.handler.terminal.model.TerminalChannelExtra;
import org.dromara.visor.module.terminal.handler.terminal.model.TerminalChannelProps;
import org.dromara.visor.module.terminal.handler.terminal.model.config.TerminalSessionRdpConfig;
import org.dromara.visor.module.terminal.handler.terminal.sender.IGuacdTerminalSender;

/**
 * rdp 会话
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2025/3/30 17:44
 */
@Slf4j
public class RdpSession extends AbstractGuacdSession<TerminalSessionRdpConfig> implements IRdpSession {

    private final GuacdConfig guacdConfig;

    public RdpSession(TerminalChannelProps props,
                      IGuacdTerminalSender sender,
                      TerminalSessionRdpConfig config,
                      GuacdConfig guacdConfig) {
        super(props, sender, config);
        this.guacdConfig = guacdConfig;
    }

    @Override
    protected IGuacdTunnel createTunnel() {
        return new GuacdTunnel(GuacdProtocol.RDP, sessionId, guacdConfig.getHost(), guacdConfig.getPort());
    }

    @Override
    protected void setTunnelParams() {
        super.setTunnelParams();
        // 设置额外参数
        TerminalChannelExtra extra = props.getExtra();
        // 音频输入会导致无法连接先写死
        extra.setEnableAudioInput(false);
        // dpi
        tunnel.dpi(config.getDpi());
        // 时区
        tunnel.timezone(config.getTimezone());
        // 忽略证书
        tunnel.setParameter(GuacdConst.IGNORE_CERT, true);
        // 域
        tunnel.setParameter(GuacdConst.DOMAIN, Strings.def(config.getDomain(), (String) null));
        // 系统布局
        tunnel.setParameter(GuacdConst.SERVER_LAYOUT, config.getKeyboardLayout());
        // 剪切板规范
        tunnel.setParameter(GuacdConst.NORMALIZE_CLIPBOARD, config.getClipboardNormalize());
        // 修改大小方法
        tunnel.setParameter(GuacdConst.RESIZE_METHOD, Booleans.isTrue(config.getVersionGt81()) ? GuacdConst.RESIZE_METHOD_DISPLAY_UPDATE : GuacdConst.RESIZE_METHOD_RECONNECT);
        // 显示设置
        tunnel.setParameter(GuacdConst.COLOR_DEPTH, extra.getColorDepth());
        tunnel.setParameter(GuacdConst.FORCE_LOSSLESS, extra.getForceLossless());
        // 偏好设置
        tunnel.setParameter(GuacdConst.ENABLE_WALLPAPER, extra.getEnableWallpaper());
        tunnel.setParameter(GuacdConst.ENABLE_THEMING, extra.getEnableTheming());
        tunnel.setParameter(GuacdConst.ENABLE_FONT_SMOOTHING, extra.getEnableFontSmoothing());
        tunnel.setParameter(GuacdConst.ENABLE_FULL_WINDOW_DRAG, extra.getEnableFullWindowDrag());
        tunnel.setParameter(GuacdConst.ENABLE_DESKTOP_COMPOSITION, extra.getEnableDesktopComposition());
        tunnel.setParameter(GuacdConst.ENABLE_MENU_ANIMATIONS, extra.getEnableMenuAnimations());
        tunnel.setParameter(GuacdConst.DISABLE_BITMAP_CACHING, extra.getDisableBitmapCaching());
        tunnel.setParameter(GuacdConst.DISABLE_OFFSCREEN_CACHING, extra.getDisableOffscreenCaching());
        tunnel.setParameter(GuacdConst.DISABLE_GLYPH_CACHING, extra.getDisableGlyphCaching());
        tunnel.setParameter(GuacdConst.DISABLE_GFX, extra.getDisableGfx());
        // 音频
        tunnel.setAudioMimeTypes(GuacdConst.AUDIO_MIMETYPES);
        tunnel.setParameter(GuacdConst.ENABLE_AUDIO_INPUT, extra.getEnableAudioInput());
        tunnel.setParameter(GuacdConst.DISABLE_AUDIO_OUTPUT, Booleans.isFalse(extra.getEnableAudioOutput()));
        // 驱动
        tunnel.setParameter(GuacdConst.CLIENT_NAME, AppConst.APP_NAME);
        tunnel.setParameter(GuacdConst.ENABLE_DRIVE, true);
        tunnel.setParameter(GuacdConst.CREATE_DRIVE_PATH, true);
        tunnel.setParameter(GuacdConst.DRIVE_NAME, GuacdConst.DRIVE_DRIVE_NAME);
        // 父文件夹必须存在 否则会报错 所以不能分层
        String driveMountPath = DriveMountModeEnum.of(extra.getDriveMountMode())
                .getDriveMountPath(props.getUserId(), props.getHostId(), props.getId());
        tunnel.setParameter(GuacdConst.DRIVE_PATH, Files1.getPath(guacdConfig.getDrivePath() + "/" + driveMountPath));
        // 初始化程序
        String initialProgram = config.getInitialProgram();
        if (!Strings.isBlank(initialProgram)) {
            tunnel.setParameter(GuacdConst.INITIAL_PROGRAM, initialProgram);
        }
        // 预连接
        String preConnectionId = config.getPreConnectionId();
        if (!Strings.isBlank(preConnectionId)) {
            tunnel.setParameter(GuacdConst.SECURITY, GuacdConst.SECURITY_VMCONNECT);
            tunnel.setParameter(GuacdConst.PRE_CONNECTION_ID, preConnectionId);
            tunnel.setParameter(GuacdConst.PRE_CONNECTION_BLOB, config.getPreConnectionBlob());
        }
        // RemoteApp
        String remoteApp = config.getRemoteApp();
        if (!Strings.isBlank(remoteApp)) {
            tunnel.setParameter(GuacdConst.REMOTE_APP, remoteApp);
            tunnel.setParameter(GuacdConst.REMOTE_APP_DIR, Strings.def(config.getRemoteAppDir(), (String) null));
            tunnel.setParameter(GuacdConst.REMOTE_APP_ARGS, Strings.def(config.getRemoteAppArgs(), (String) null));
        }
    }

    @Override
    protected boolean isLowBandwidthMode() {
        return Booleans.isTrue(config.getLowBandwidthMode());
    }

}
