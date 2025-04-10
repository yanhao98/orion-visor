<template>
  <card-list v-model:searchValue="formModel.searchValue"
             search-input-placeholder="输入 id / 名称 / 用户名"
             :create-card-position="false"
             :loading="loading"
             :field-config="cardFieldConfig"
             :list="list"
             :pagination="pagination"
             :card-layout-cols="cardColLayout"
             :filter-count="filterCount"
             :add-permission="['asset:host-identity:create']"
             :query-order="queryOrder"
             :fields-hook="fieldsHook"
             @add="emits('openAdd')"
             @reset="reset"
             @search="fetchCardData"
             @page-change="fetchCardData">
    <!-- 左侧操作 -->
    <template #leftHandle>
      <!-- 角色授权 -->
      <a-button v-permission="['asset:host-identity:grant']"
                class="card-header-button"
                @click="router.push({ name: GrantRouteName, query: { key: GrantKey.HOST_IDENTITY_ROLE }})">
        角色授权
        <template #icon>
          <icon-user-group />
        </template>
      </a-button>
      <!-- 用户授权 -->
      <a-button v-permission="['asset:host-identity:grant']"
                class="card-header-button"
                @click="router.push({ name: GrantRouteName, query: { key: GrantKey.HOST_IDENTITY_USER }})">
        用户授权
        <template #icon>
          <icon-user />
        </template>
      </a-button>
    </template>
    <!-- 过滤条件 -->
    <template #filterContent>
      <a-form :model="formModel"
              class="card-filter-form"
              size="small"
              ref="formRef"
              label-align="right"
              :auto-label-width="true"
              @keyup.enter="() => fetchCardData()">
        <!-- id -->
        <a-form-item field="id" label="id">
          <a-input-number v-model="formModel.id"
                          placeholder="请输入id"
                          allow-clear
                          hide-button />
        </a-form-item>
        <!-- 名称 -->
        <a-form-item field="name" label="名称">
          <a-input v-model="formModel.name"
                   placeholder="请输入名称"
                   allow-clear />
        </a-form-item>
        <!-- 类型 -->
        <a-form-item field="type" label="类型">
          <a-select v-model="formModel.type"
                    placeholder="请选择类型"
                    :options="toOptions(identityTypeKey)"
                    allow-clear />
        </a-form-item>
        <!-- 用户名 -->
        <a-form-item field="username" label="用户名">
          <a-input v-model="formModel.username"
                   placeholder="请输入用户名"
                   allow-clear />
        </a-form-item>
        <!-- 密钥 -->
        <a-form-item field="keyId" label="密钥">
          <host-key-selector v-model="formModel.keyId" allow-clear />
        </a-form-item>
        <!-- 描述 -->
        <a-form-item field="description" label="描述">
          <a-input v-model="formModel.description"
                   placeholder="请输入描述"
                   allow-clear />
        </a-form-item>
      </a-form>
    </template>
    <!-- 标题 -->
    <template #title="{ record }">
      {{ record.name }}
    </template>
    <!-- 类型 -->
    <template #type="{ record }">
      <a-tag :color="getDictValue(identityTypeKey, record.type, 'color')">
        {{ getDictValue(identityTypeKey, record.type) }}
      </a-tag>
    </template>
    <!-- 用户名 -->
    <template #username="{ record }">
      <span class="span-blue text-copy" @click="copy(record.username)">
        {{ record.username }}
      </span>
    </template>
    <!-- 密钥名称 -->
    <template #keyId="{ record }">
      <!-- 有密钥 -->
      <template v-if="record.keyId && record.type === IdentityType.KEY">
        <!-- 可查看详情 -->
        <a-tooltip v-if="hasAnyPermission(['asset:host-key:detail', 'asset:host-key:update'])"
                   content="点击查看详情">
          <a-tag :checked="true"
                 checkable
                 @click="emits('openKeyView', { id: record.keyId })">
            {{ record.keyName }}
          </a-tag>
        </a-tooltip>
        <!-- 不可查看详情 -->
        <a-tag v-else>
          {{ record.keyName }}
        </a-tag>
      </template>
      <!-- 无密钥 -->
      <template v-else>
        <span>-</span>
      </template>
    </template>
    <!-- 描述 -->
    <template #description="{ record }">
      <span :title="record.description">
        {{ record.description || '-' }}
      </span>
    </template>
    <!-- 拓展操作 -->
    <template #extra="{ record }">
      <a-space>
        <!-- 更多操作 -->
        <a-dropdown trigger="hover" :popup-max-height="false">
          <icon-more class="card-extra-icon" />
          <template #content>
            <!-- 修改 -->
            <a-doption v-permission="['asset:host-identity:update']"
                       @click="emits('openUpdate', record)">
              <span class="more-doption normal">修改</span>
            </a-doption>
            <!-- 删除 -->
            <a-doption v-permission="['asset:host-identity:delete']"
                       class="span-red"
                       @click="deleteRow(record.id)">
              <span class="more-doption error">删除</span>
            </a-doption>
          </template>
        </a-dropdown>
      </a-space>
    </template>
  </card-list>
</template>

<script lang="ts">
  export default {
    name: 'hostIdentityCardList'
  };
</script>

<script lang="ts" setup>
  import type { HostIdentityQueryRequest, HostIdentityQueryResponse } from '@/api/asset/host-identity';
  import { useCardPagination, useCardColLayout, useCardFieldConfig } from '@/hooks/card';
  import { computed, reactive, ref, onMounted } from 'vue';
  import useLoading from '@/hooks/loading';
  import { objectTruthKeyCount, resetObject } from '@/utils';
  import fieldConfig from '../types/card.fields';
  import { deleteHostIdentity, getHostIdentityPage } from '@/api/asset/host-identity';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useRouter } from 'vue-router';
  import usePermission from '@/hooks/permission';
  import { useCacheStore, useDictStore } from '@/store';
  import { copy } from '@/hooks/copy';
  import { GrantKey, GrantRouteName } from '@/views/asset/grant/types/const';
  import { TableName, IdentityType, identityTypeKey } from '../types/const';
  import { useQueryOrder, ASC } from '@/hooks/query-order';
  import HostKeySelector from '@/components/asset/host-key/selector/index.vue';

  const emits = defineEmits(['openAdd', 'openUpdate', 'openKeyView']);

  const list = ref<HostIdentityQueryResponse[]>([]);

  const router = useRouter();
  const cacheStore = useCacheStore();
  const cardColLayout = useCardColLayout();
  const pagination = useCardPagination();
  const queryOrder = useQueryOrder(TableName, ASC);
  const { cardFieldConfig, fieldsHook } = useCardFieldConfig(TableName, fieldConfig);
  const { toOptions, getDictValue } = useDictStore();
  const { loading, setLoading } = useLoading();
  const { hasAnyPermission } = usePermission();

  const formRef = ref();
  const formModel = reactive<HostIdentityQueryRequest>({
    searchValue: undefined,
    id: undefined,
    type: undefined,
    name: undefined,
    username: undefined,
    keyId: undefined,
  });

  // 条件数量
  const filterCount = computed(() => {
    return objectTruthKeyCount(formModel, ['searchValue']);
  });

  // 删除当前行
  const deleteRow = (id: number) => {
    Modal.confirm({
      title: '删除前确认!',
      titleAlign: 'start',
      content: '确定要删除这条记录吗?',
      okText: '删除',
      onOk: async () => {
        try {
          setLoading(true);
          // 调用删除接口
          await deleteHostIdentity(id);
          Message.success('删除成功');
          // 重新加载
          reload();
        } catch (e) {
        } finally {
          setLoading(false);
        }
      }
    });
  };

  // 重新加载
  const reload = () => {
    // 重新加载数据
    fetchCardData();
    // 清空缓存
    cacheStore.reset('hostIdentities', 'authorizedHostIdentities');
  };

  defineExpose({ reload });

  // 重置条件
  const reset = () => {
    resetObject(formModel);
    fetchCardData();
  };

  // 加载数据
  const doFetchCardData = async (request: HostIdentityQueryRequest) => {
    try {
      setLoading(true);
      const { data } = await getHostIdentityPage(queryOrder.markOrderly(request));
      list.value = data.rows;
      pagination.total = data.total;
      pagination.current = request.page;
      pagination.pageSize = request.limit;
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };

  // 切换页码
  const fetchCardData = (page = 1, limit = pagination.pageSize, form = formModel) => {
    doFetchCardData({ page, limit, ...form });
  };

  onMounted(() => {
    fetchCardData();
  });

</script>

<style lang="less" scoped>

</style>
