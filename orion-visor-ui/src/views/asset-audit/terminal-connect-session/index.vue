<template>
  <div class="layout-container" v-if="render">
    <!-- 列表-表格 -->
    <connect-session-table />
  </div>
</template>

<script lang="ts">
  export default {
    name: 'terminalConnectSession'
  };
</script>

<script lang="ts" setup>
  import { ref, onBeforeMount } from 'vue';
  import { useDictStore } from '@/store';
  import { dictKeys } from './types/const';
  import ConnectSessionTable from './components/connect-session-table.vue';

  const render = ref(false);

  // 加载字典配置
  onBeforeMount(async () => {
    const dictStore = useDictStore();
    await dictStore.loadKeys(dictKeys);
    render.value = true;
  });

</script>

<style lang="less" scoped>

</style>
