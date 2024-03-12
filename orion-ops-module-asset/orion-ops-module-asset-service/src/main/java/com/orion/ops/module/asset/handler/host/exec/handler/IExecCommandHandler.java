package com.orion.ops.module.asset.handler.host.exec.handler;

import com.orion.lang.able.SafeCloseable;

/**
 * 命令执行器定义
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/3/12 11:31
 */
public interface IExecCommandHandler extends Runnable, SafeCloseable {

    /**
     * 写入
     *
     * @param msg msg
     */
    void write(String msg);

    /**
     * 中断执行
     */
    void interrupted();

}
