<template>
  <div class="layout-container" v-if="render">
    <!-- 列表-表格 -->
    <host-identity-table v-if="renderTable"
                         ref="table"
                         @open-add="() => modal.openAdd()"
                         @open-update="(e) => modal.openUpdate(e)"
                         @open-key-view="(e) => keyDrawer.openView(e) " />
    <!-- 列表-卡片 -->
    <host-identity-card-list v-else
                             ref="card"
                             @open-add="() => modal.openAdd()"
                             @open-update="(e) => modal.openUpdate(e)"
                             @open-key-view="(e) => keyDrawer.openView(e) " />
    <!-- 添加修改模态框 -->
    <host-identity-form-modal ref="modal"
                              @added="reload"
                              @updated="reload" />
    <!-- 主机密钥抽屉 -->
    <host-key-form-drawer ref="keyDrawer" />
  </div>
</template>

<script lang="ts">
  export default {
    name: 'hostIdentity'
  };
</script>

<script lang="ts" setup>
  import { ref, computed, onBeforeMount } from 'vue';
  import { useAppStore, useDictStore } from '@/store';
  import { dictKeys } from './types/const';
  import HostIdentityCardList from './components/host-identity-card-list.vue';
  import HostIdentityTable from './components/host-identity-table.vue';
  import HostIdentityFormModal from './components/host-identity-form-modal.vue';
  import HostKeyFormDrawer from '../host-key/components/host-key-form-drawer.vue';

  const render = ref();
  const table = ref();
  const card = ref();
  const modal = ref();
  const keyDrawer = ref();
  const appStore = useAppStore();

  const renderTable = computed(() => appStore.hostIdentityView === 'table');

  // 重新加载
  const reload = () => {
    if (renderTable.value) {
      table.value.reload();
    } else {
      card.value.reload();
    }
  };

  // 加载字典值
  onBeforeMount(async () => {
    const dictStore = useDictStore();
    await dictStore.loadKeys(dictKeys);
    render.value = true;
  });

</script>

<style lang="less" scoped>

</style>
