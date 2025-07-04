<template>
  <div class="transfer-container">
    <!-- 传输框 -->
    <a-transfer v-model="value"
                :data="data"
                :source-input-search-props="{ placeholder: '请输入主机名称/编码/IP' }"
                :target-input-search-props="{ placeholder: '请输入主机名称/编码/IP' }"
                :disabled="!group.key"
                show-search
                one-way>
      <!-- 主机列表 -->
      <template #source-title="{ countTotal, countSelected, checked, indeterminate, onSelectAllChange }">
        <!-- 左侧标题 -->
        <div class="source-title-container">
          <a-checkbox style="margin-right: 8px;"
                      :model-value="checked"
                      :indeterminate="indeterminate"
                      @change="onSelectAllChange" />
          <span>
            主机列表 {{ countSelected }} / {{ countTotal }}
          </span>
        </div>
      </template>
      <!-- 右侧标题 -->
      <template #target-title="{ countTotal, countSelected, onClear }">
        <div class="target-title-container">
          <span>已选择 <span class="span-blue">{{ countTotal }}</span> 个</span>
          <span class="pointer"
                @click="onClear"
                title="清空">
            <icon-delete />
          </span>
        </div>
      </template>
      <!-- 内容 -->
      <template #item="{ label }">
        <a-tooltip position="top"
                   :mini="true"
                   :content="label">
          <span v-html="renderLabel(label)" />
        </a-tooltip>
      </template>
    </a-transfer>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'hostTransfer'
  };
</script>

<script lang="ts" setup>
  import type { TransferItem } from '@arco-design/web-vue/es/transfer/interface';
  import type { TreeNodeData } from '@arco-design/web-vue';
  import { onMounted, ref, watch, computed, onActivated } from 'vue';
  import { useCacheStore } from '@/store';
  import { getHostGroupRelList } from '@/api/asset/host-group';

  const props = withDefaults(defineProps<Partial<{
    modelValue: Array<string>;
    group: TreeNodeData;
  }>>(), {
    group: () => {
      return {};
    },
  });

  const emits = defineEmits(['loading', 'update:modelValue']);

  const cacheStore = useCacheStore();

  const data = ref<Array<TransferItem>>([]);

  const value = computed<Array<string>>({
    get() {
      return props.modelValue as Array<string>;
    },
    set(e) {
      if (e) {
        emits('update:modelValue', e);
      } else {
        emits('update:modelValue', []);
      }
    }
  });

  // 渲染 label
  const renderLabel = (label: string) => {
    const last = label.lastIndexOf('-');
    const prefix = label.substring(0, last - 1);
    const address = label.substring(last + 2, label.length);
    return `${prefix} - <span class="span-blue">${address}</span>`;
  };

  // 查询组内数据
  watch(() => props.group?.key, async (groupId) => {
    if (groupId) {
      // 加载组内数据
      try {
        emits('loading', true);
        const { data } = await getHostGroupRelList(groupId as number);
        value.value = data.map(String);
      } catch (e) {
      } finally {
        emits('loading', false);
      }
    } else {
      // 重置
      value.value = [];
    }
  });

  // 加载主机列表
  const loadHosts = () => {
    cacheStore.loadHosts().then(hosts => {
      data.value = hosts.map(s => {
        return {
          value: String(s.id),
          label: `${s.name} - ${s.address}`,
          disabled: false
        };
      });
    });
  };

  // 初始化选项
  onMounted(loadHosts);
  onActivated(loadHosts);

</script>

<style lang="less" scoped>
  .transfer-container {
    width: 100%;
    height: 100%;
  }

  :deep(.arco-transfer) {
    height: 100%;

    .arco-transfer-view {
      width: 100%;
      height: 100%;
      user-select: none;
    }

    .arco-transfer-view-source {

      .arco-transfer-list-item .arco-checkbox {
        width: calc(100% - 24px);
        position: absolute;
      }
    }

    .arco-transfer-view-target {

      .arco-transfer-list-item-content {
        margin-left: 4px;
        position: absolute;
        width: calc(100% - 52px);
        display: inline-block;
      }

      .arco-transfer-list-item-remove-btn {
        margin-right: 8px;
      }
    }
  }

  .source-title-container {
    display: flex;
    align-items: center;
  }

  .target-title-container {
    display: flex;
    justify-content: space-between;
    align-items: center;

    svg {
      font-size: 16px;
    }
  }

</style>
