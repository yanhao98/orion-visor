import type { ClearRequest, DataGrid, OrderDirection, Pagination } from '@/types/global';
import type { TableData } from '@arco-design/web-vue';
import axios from 'axios';
import qs from 'query-string';

/**
 * 终端连接日志查询请求
 */
export interface TerminalConnectLogQueryRequest extends Pagination, OrderDirection {
  id?: number;
  userId?: number;
  hostId?: number;
  hostAddress?: string;
  type?: string;
  sessionId?: string;
  status?: string;
  startTimeRange?: string[];
}

/**
 * 终端连接日志清理请求
 */
export interface TerminalConnectLogClearRequest extends TerminalConnectLogQueryRequest, ClearRequest {
}

/**
 * 终端连接日志查询响应
 */
export interface TerminalConnectLogQueryResponse extends TableData {
  id: number;
  userId: number;
  username: number;
  hostId: number;
  hostName: string;
  hostAddress: string;
  type: string;
  sessionId: string;
  status: string;
  startTime: number;
  endTime: number;
  extra: TerminalConnectLogExtra;
}

/**
 * 终端连接日志拓展对象
 */
export interface TerminalConnectLogExtra {
  traceId: string;
  channel: string;
  channelId: string;
  sessionId: string;
  address: string;
  location: string;
  userAgent: string;
  errorMessage: string;
}

/**
 * 分页查询终端连接日志
 */
export function getTerminalConnectLogPage(request: TerminalConnectLogQueryRequest) {
  return axios.post<DataGrid<TerminalConnectLogQueryResponse>>('/terminal/terminal-connect-log/query', request);
}

/**
 * 查询全部终端连接会话
 */
export function getTerminalConnectSessions(request: TerminalConnectLogQueryRequest) {
  return axios.post<Array<TerminalConnectLogQueryResponse>>('/terminal/terminal-connect-log/sessions', request);
}

/**
 * 查询用户最近连接的主机
 */
export function getLatestConnectHostId(type: string, limit: number) {
  return axios.post<Array<number>>('/terminal/terminal-connect-log/latest-connect', {
    type,
    limit
  });
}

/**
 * 删除终端连接日志
 */
export function deleteTerminalConnectLog(idList: Array<number>) {
  return axios.delete('/terminal/terminal-connect-log/delete', {
    params: { idList },
    paramsSerializer: params => {
      return qs.stringify(params, { arrayFormat: 'comma' });
    }
  });
}

/**
 * 查询终端连接日志数量
 */
export function getTerminalConnectLogCount(request: TerminalConnectLogQueryRequest) {
  return axios.post<number>('/terminal/terminal-connect-log/count', request);
}

/**
 * 清空终端连接日志
 */
export function clearTerminalConnectLog(request: TerminalConnectLogClearRequest) {
  return axios.post<number>('/terminal/terminal-connect-log/clear', request, {
    timeout: 60000,
  });
}

/**
 * 强制断开终端连接
 */
export function hostForceOffline(request: TerminalConnectLogQueryRequest) {
  return axios.put('/terminal/terminal-connect-log/force-offline', request);
}
