<template>
  <a-drawer v-model:visible="visible"
            title="终端连接日志详情"
            :width="442"
            :mask-closable="false"
            :unmount-on-close="true"
            ok-text="关闭"
            :hide-cancel="true"
            @cancel="handleClose">
    <a-descriptions class="detail-container"
                    size="large"
                    :label-style="{ display: 'flex', width: '90px' }"
                    :column="1">
      <!-- id -->
      <a-descriptions-item label="id">
        {{ record.id }}
      </a-descriptions-item>
      <!-- 连接用户 -->
      <a-descriptions-item label="连接用户">
        <span>({{ record.userId }}) {{ record.username }}</span>
      </a-descriptions-item>
      <!-- 连接主机 -->
      <a-descriptions-item label="连接主机">
        <span>({{ record.hostId }}) {{ record.hostName }}</span>
        <br>
        <span class="host-address text-copy"
              :title="record.hostAddress"
              @click="copy(record.hostAddress)">
          {{ record.hostAddress }}
        </span>
      </a-descriptions-item>
      <!-- 连接类型 -->
      <a-descriptions-item label="连接类型">
        <a-tag :color="getDictValue(connectTypeKey, record.type, 'color')">
          {{ getDictValue(connectTypeKey, record.type) }}
        </a-tag>
      </a-descriptions-item>
      <!-- 连接状态 -->
      <a-descriptions-item label="连接状态">
        <span :style="{ color: getDictValue(connectStatusKey, record.status, 'color') }">
          {{ getDictValue(connectStatusKey, record.status) }}
        </span>
      </a-descriptions-item>
      <!-- 留痕地址 -->
      <a-descriptions-item label="留痕地址">
        <span>{{ record.extra?.location }}</span>
        <br>
        <span class="connect-address text-copy"
              :title="record.extra?.address"
              @click="copy(record.extra?.address)">
          {{ record.extra?.address }}
        </span>
      </a-descriptions-item>
      <!-- userAgent -->
      <a-descriptions-item label="userAgent">
        {{ record.extra?.userAgent }}
      </a-descriptions-item>
      <!-- 错误信息 -->
      <a-descriptions-item v-if="record.extra?.errorMessage" label="错误信息">
        <span class="span-red">
          {{ record.extra?.errorMessage }}
        </span>
      </a-descriptions-item>
      <!-- 开始时间 -->
      <a-descriptions-item label="开始时间">
        {{ dateFormat(new Date(record.startTime)) }}
      </a-descriptions-item>
      <!-- 结束时间 -->
      <a-descriptions-item v-if="record.endTime" label="结束时间">
        {{ dateFormat(new Date(record.endTime)) }}
      </a-descriptions-item>
      <!-- traceId -->
      <a-descriptions-item label="traceId">
        <span class="text-copy" @click="copy(record.extra?.traceId)">
          {{ record.extra?.traceId }}
        </span>
      </a-descriptions-item>
      <!-- sessionId -->
      <a-descriptions-item label="sessionId">
        <span class="text-copy" @click="copy(record.sessionId)">
          {{ record.sessionId }}
        </span>
      </a-descriptions-item>
    </a-descriptions>
  </a-drawer>
</template>

<script lang="ts">
  export default {
    name: 'connectLogDetailDrawer'
  };
</script>

<script lang="ts" setup>
  import type { TerminalConnectLogQueryResponse } from '@/api/terminal/terminal-connect-log';
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import useVisible from '@/hooks/visible';
  import { connectStatusKey, connectTypeKey } from '../types/const';
  import { useDictStore } from '@/store';
  import { dateFormat } from '@/utils';
  import { copy } from '@/hooks/copy';

  const { getDictValue, toOptions } = useDictStore();
  const { visible, setVisible } = useVisible();
  const { loading, setLoading } = useLoading();

  const record = ref<TerminalConnectLogQueryResponse>({} as TerminalConnectLogQueryResponse);

  // 打开
  const open = (s: any) => {
    record.value = s;
    setVisible(true);
  };

  defineExpose({ open });

  // 关闭
  const handleClose = () => {
    handlerClear();
  };

  // 清空
  const handlerClear = () => {
    setLoading(false);
  };

</script>

<style lang="less" scoped>
  .detail-container {
    padding: 24px;
  }

  :deep(.arco-descriptions-item-value) {
    color: var(--color-text-1);
  }

  .host-address, .connect-address {
    margin-top: 4px;
    display: inline-block;
    color: rgb(var(--arcoblue-6));
  }

  :deep(.arco-collapse-item-content) {
    background: unset;
    padding: 0;
  }

  :deep(.arco-collapse-item-header) {
    border: none;
  }

</style>
