<template>
  <!-- 表头 -->
  <div class="sftp-table-header">
    <!-- 左侧操作 -->
    <div class="sftp-table-header-left">
      <!-- 返回上级 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="返回上级">
        <a-button class="header-action-icon icon-button mr8"
                  @click="backParentPath">
          <icon-left />
        </a-button>
      </a-tooltip>
      <!-- 当前路径 -->
      <div class="sftp-path-container"
           @click="setPathEditable(true)">
        <!-- 路径输入框 -->
        <div v-if="pathEditable">
          <a-input v-model="pathInput"
                   ref="pathInputRef"
                   size="mini"
                   placeholder="文件夹路径"
                   allow-clear
                   @press-enter="doChangePath" />
        </div>
        <!-- 路径视图 -->
        <a-breadcrumb class="sftp-path-wrapper" v-else>
          <!-- 根目录 -->
          <a-breadcrumb-item class="sftp-path-unit"
                             @click.stop="loadFileList('/')">
            <icon-storage />
          </a-breadcrumb-item>
          <!-- 子目录 -->
          <a-breadcrumb-item class="sftp-path-unit"
                             v-for="path in analysisPaths"
                             @click.stop="loadFileList(path.path)">
            {{ path.name }}
          </a-breadcrumb-item>
        </a-breadcrumb>
      </div>
    </div>
    <!-- 已关闭-右侧操作 -->
    <div v-if="session?.state.connected === false && closeMessage !== undefined"
         class="sftp-table-header-right">
      <!-- 错误信息 -->
      <a-tag class="close-message"
             color="red"
             :title="closeMessage">
        已断开: {{ closeMessage }}
      </a-tag>
      <!-- 重连 -->
      <a-tooltip v-if="session?.state.connected === false && session?.state.canReconnect"
                 position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="重连">
        <a-button class="header-action-icon icon-button ml8"
                  @click="reConnect">
          <icon-refresh />
        </a-button>
      </a-tooltip>
    </div>
    <!-- 路径编辑模式-右侧操作 -->
    <a-space v-else-if="pathEditable" class="sftp-table-header-right">
      <!-- 进入 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="进入">
        <a-button class="header-action-icon icon-button"
                  @click="doChangePath">
          <icon-right />
        </a-button>
      </a-tooltip>
      <!-- 取消 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="取消">
        <a-button class="header-action-icon icon-button"
                  @click="setPathEditable(false)">
          <icon-stop />
        </a-button>
      </a-tooltip>
    </a-space>
    <!-- 非路径编辑模式-右侧操作 -->
    <a-space v-else class="sftp-table-header-right">
      <!-- 刷新 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="刷新">
        <a-button class="header-action-icon icon-button"
                  @click="loadFileList()">
          <icon-refresh />
        </a-button>
      </a-tooltip>
      <!-- 显示隐藏文件 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 :content="showHiddenFile ? '不显示隐藏文件' : '显示隐藏文件'">
        <a-button class="header-action-icon icon-button"
                  @click="toggleShowHiddenFile">
          <icon-eye-invisible v-if="showHiddenFile" />
          <icon-eye v-else />
        </a-button>
      </a-tooltip>
      <!-- 创建文件 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="创建文件">
        <a-button class="header-action-icon icon-button"
                  @click="createFile(true)">
          <icon-drive-file />
        </a-button>
      </a-tooltip>
      <!-- 创建文件夹 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="创建文件夹">
        <a-button class="header-action-icon icon-button"
                  @click="createFile(false)">
          <icon-folder-add />
        </a-button>
      </a-tooltip>
      <!-- 删除选中文件 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="删除选中文件">
        <a-button class="header-action-icon icon-button"
                  @click="deleteSelectFiles">
          <icon-delete />
        </a-button>
      </a-tooltip>
      <!-- 上传 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="上传">
        <a-button class="header-action-icon icon-button"
                  @click="openUpload">
          <icon-upload />
        </a-button>
      </a-tooltip>
      <!-- 下载 -->
      <a-tooltip position="top"
                 :mini="true"
                 :overlay-inverse="true"
                 :auto-fix-position="false"
                 content-class="terminal-tooltip-content"
                 arrow-class="terminal-tooltip-content"
                 content="下载">
        <a-button class="header-action-icon icon-button"
                  @click="downloadFile">
          <icon-download />
        </a-button>
      </a-tooltip>
    </a-space>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'sftpTableHeader'
  };
</script>

<script lang="ts" setup>
  import type { PathAnalysis } from '@/utils/file';
  import type { ISftpSession } from '@/views/terminal/interfaces';
  import { nextTick, ref, watch } from 'vue';
  import { getParentPath, getPathAnalysis } from '@/utils/file';
  import { useTerminalStore } from '@/store';

  const props = defineProps<{
    closeMessage?: string;
    currentPath: string;
    session?: ISftpSession;
    selectedFiles: Array<string>;
  }>();

  const emits = defineEmits(['loadFile', 'createFile', 'upload', 'download', 'deleteFile', 'setLoading']);

  const showHiddenFile = ref(false);
  const analysisPaths = ref<Array<PathAnalysis>>([]);
  const pathEditable = ref(false);
  const pathInput = ref('');
  const pathInputRef = ref();

  // 监听路径变化
  watch(() => props.currentPath, (path) => {
    if (path) {
      analysisPaths.value = getPathAnalysis(path);
    } else {
      analysisPaths.value = [];
    }
  });

  // 返回上级目录
  const backParentPath = () => {
    loadFileList(getParentPath(props.currentPath));
  };

  // 设置命令编辑模式
  const setPathEditable = (editable: boolean) => {
    // 检查是否断开
    if (editable && !props.session?.state.connected) {
      return;
    }
    pathEditable.value = editable;
    pathInput.value = editable ? props.currentPath : '';
    // 自动聚焦
    nextTick(() => {
      if (editable) {
        pathInputRef.value?.focus();
      }
    });
  };

  // 执行修改目录
  const doChangePath = () => {
    loadFileList(pathInput.value);
    setPathEditable(false);
  };

  // 加载文件列表
  const loadFileList = (path: string = props.currentPath) => {
    // 检查是否断开
    if (!props.session?.state.connected) {
      return;
    }
    emits('loadFile', path);
  };

  // 设置是否显示隐藏文件
  const toggleShowHiddenFile = () => {
    showHiddenFile.value = !showHiddenFile.value;
    // 设置显示状态并且刷新
    if (props.session) {
      props.session.setShowHiddenFile(showHiddenFile.value);
      loadFileList();
    }
  };

  // 创建文件
  const createFile = (isTouch: boolean) => {
    emits('createFile', isTouch);
  };

  // 删除选中文件
  const deleteSelectFiles = () => {
    emits('deleteFile', [...props.selectedFiles]);
  };

  // 上传文件
  const openUpload = () => {
    emits('upload');
  };

  // 下载文件
  const downloadFile = () => {
    emits('download', [...props.selectedFiles], true);
  };

  // 重新连接
  const reConnect = () => {
    if (props.session) {
      emits('setLoading', true);
      // 重新连接
      useTerminalStore().reOpenSession(props.session.sessionKey);
    }
  };

</script>

<style lang="less" scoped>
  @action-num: 7;
  @action-gap: 8px;
  @action-size: 26px;
  @actions-width: @action-num * (@action-size + @action-gap);

  .sftp-table-header {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;

    &-left, &-right {
      display: flex;
      align-items: center;
      height: 100%;
    }

    &-left {
      width: calc(100% - @actions-width);
    }

    &-right {
      width: @actions-width;
      justify-content: flex-end;
    }
  }

  .sftp-path-container {
    background: var(--color-fill-2);
    width: 100%;
    border-radius: 2px;
    overflow: hidden;

    :deep(.sftp-path-unit) {
      cursor: pointer;
      white-space: nowrap;
      font-size: 12px;

      &:hover {
        color: rgb(var(--arcoblue-6));
      }
    }

    .sftp-path-wrapper {
      padding: 0 6px 1px 6px;
    }
  }

  .close-message {
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    margin-left: 16px;
  }

  .header-action-icon {
    width: @action-size;
    height: @action-size;
  }
</style>
