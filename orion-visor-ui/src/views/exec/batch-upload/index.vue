<template>
  <div class="layout-container upload-container">
    <!-- 上传面板 -->
    <upload-panel ref="panel" />
  </div>
</template>

<script lang="ts">
  export default {
    name: 'batchUpload'
  };
</script>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { useDictStore } from '@/store';
  import { dictKeys } from './types/const';
  import { useRoute } from 'vue-router';
  import UploadPanel from './components/upload-panel.vue';

  const route = useRoute();

  const panel = ref();

  // 加载字典值
  onMounted(async () => {
    const dictStore = useDictStore();
    await dictStore.loadKeys(dictKeys);
  });

  // 跳转日志
  onMounted(async () => {
    const idParam = route.query.id as string;
    const keyParam = route.query.key as string;
    if (idParam) {
      await panel.value?.openLog(Number.parseInt(idParam));
    } else if (keyParam) {
      await panel.value?.openLog(Number.parseInt(keyParam));
    }
  });

</script>

<style lang="less" scoped>

  .upload-container {
    width: 100%;
    height: calc(100vh - 92px);
  }

</style>
