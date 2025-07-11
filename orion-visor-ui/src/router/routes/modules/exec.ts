import type { AppRouteRecordRaw } from '../types';
import { DEFAULT_LAYOUT, FULL_LAYOUT } from '../base';

const EXEC: Array<AppRouteRecordRaw> = [
  {
    name: 'execModule',
    path: '/exec-module',
    component: DEFAULT_LAYOUT,
    children: [
      {
        name: 'execCommand',
        path: '/exec/command',
        component: () => import('@/views/exec/exec-command/index.vue'),
      },
      {
        name: 'execCommandLog',
        path: '/exec/command-log',
        component: () => import('@/views/exec/exec-command-log/index.vue'),
      },
      {
        name: 'execJob',
        path: '/exec/job',
        component: () => import('@/views/exec/exec-job/index.vue'),
      },
      {
        name: 'execJobLog',
        path: '/exec/job-log',
        component: () => import('@/views/exec/exec-job-log/index.vue'),
      },
      {
        name: 'batchUpload',
        path: '/exec/upload',
        component: () => import('@/views/exec/batch-upload/index.vue'),
      },
      {
        name: 'uploadTask',
        path: '/exec/upload-task',
        component: () => import('@/views/exec/upload-task/index.vue'),
      },
      {
        name: 'execTemplate',
        path: '/exec/template',
        component: () => import('@/views/exec/exec-template/index.vue'),
      },
    ],
  },
  {
    name: 'execFullModule',
    path: '/exec-full-module',
    component: FULL_LAYOUT,
    children: [
      {
        name: 'execJobLogView',
        path: '/exec/job-log/view',
        component: () => import('@/views/exec/exec-job-log-view/index.vue'),
      },
    ],
  }
];

export default EXEC;
