<template>
  <div v-if="render"
       class="host-terminal-layout"
       :class="{ 'terminal-full-layout': layoutState.fullscreen }">
    <!-- 头部区域 -->
    <header class="host-terminal-layout-header">
      <layout-header @fullscreen="toggleFullscreen" />
    </header>
    <!-- 主体区域 -->
    <main class="host-terminal-layout-main">
      <!-- 左侧操作栏 -->
      <div class="host-terminal-layout-left">
        <left-sidebar />
      </div>
      <!-- 内容区域 -->
      <main class="host-terminal-layout-content">
        <!-- 主机加载中骨架 -->
        <loading-skeleton v-if="loading" />
        <!-- 终端内容区域 -->
        <main-content v-else
                      @open-command-snippet="() => snippetRef.open()"
                      @open-path-bookmark="() => pathRef.open()"
                      @open-transfer-list="() => transferRef.open()"
                      @open-command-bar="openCommandBar"
                      @screenshot="screenshot" />
      </main>
      <!-- 右侧操作栏 -->
      <div class="host-terminal-layout-right">
        <right-sidebar @open-command-snippet="() => snippetRef.open()"
                       @open-path-bookmark="() => pathRef.open()"
                       @open-transfer-list="() => transferRef.open()"
                       @open-command-bar="openCommandBar"
                       @screenshot="screenshot" />
      </div>
    </main>
    <!-- 退出全屏 -->
    <a-button v-if="layoutState.fullscreen"
              class="exit-fullscreen"
              shape="circle"
              title="退出全屏"
              @click="toggleFullscreen">
      <icon-fullscreen-exit />
    </a-button>
    <!-- 命令片段列表抽屉 -->
    <command-snippet-drawer ref="snippetRef" @closed="setFocus" />
    <!-- 路径书签列表抽屉 -->
    <path-bookmark-drawer ref="pathRef" @closed="setFocus" />
    <!-- 传输列表 -->
    <transfer-drawer ref="transferRef" @closed="setFocus" />
  </div>
</template>

<script lang="ts">
  export default {
    name: 'terminal'
  };
</script>

<script lang="ts" setup>
  import type { ISshSession } from '@/views/terminal/interfaces';
  import { ref, onBeforeMount, onUnmounted, onMounted } from 'vue';
  import { dictKeys, TerminalSessionTypes, TerminalTabs } from './types/const';
  import { useCacheStore, useDictStore, useTerminalStore } from '@/store';
  import { useRoute } from 'vue-router';
  import { useFullscreen } from '@vueuse/core';
  import useLoading from '@/hooks/loading';
  import debug from '@/utils/env';
  import { Message } from '@arco-design/web-vue';
  import LayoutHeader from './components/layout/layout-header.vue';
  import LeftSidebar from './components/layout/left-sidebar.vue';
  import RightSidebar from './components/layout/right-sidebar.vue';
  import MainContent from './components/layout/main-content.vue';
  import LoadingSkeleton from './components/layout/loading-skeleton.vue';
  import TransferDrawer from './components/transfer/transfer-drawer.vue';
  import CommandSnippetDrawer from './components/command-snippet/command-snippet-drawer.vue';
  import PathBookmarkDrawer from './components/path-bookmark/path-bookmark-drawer.vue';
  import '@/assets/style/host-terminal-layout.less';
  import '@xterm/xterm/css/xterm.css';

  const {
    fetchPreference, getCurrentSession, getCurrentDomViewportHandler, openSession,
    layoutState, preference, loadHostList, hosts, tabManager, sessionManager
  } = useTerminalStore();
  const { loading, setLoading } = useLoading(true);
  const { enter: enterFull, exit: exitFull } = useFullscreen();
  const cacheStore = useCacheStore();
  const route = useRoute();

  const originTitle = document.title;
  const render = ref(false);
  const snippetRef = ref();
  const pathRef = ref();
  const transferRef = ref();

  // 终端截屏
  const screenshot = () => {
    const handler = getCurrentDomViewportHandler();
    if (handler) {
      // 截屏
      handler.screenshot?.();
      // 聚焦
      handler.focus?.();
    }
  };

  // 打开命令发送
  const openCommandBar = () => {
    const session = getCurrentSession<ISshSession>(TerminalSessionTypes.SSH.type, true);
    if (session) {
      layoutState.commandBar = !layoutState.commandBar;
    }
  };

  // 切换全屏
  const toggleFullscreen = () => {
    layoutState.fullscreen = !layoutState.fullscreen;
    if (layoutState.fullscreen) {
      // 进入全屏
      enterFull();
    } else {
      // 退出全屏
      exitFull();
    }
    // 自适应
    setTimeout(() => {
      sessionManager.dispatchFit();
    }, 200);
  };

  // 自动聚焦
  const setFocus = () => {
    getCurrentDomViewportHandler()?.focus?.();
  };

  // 关闭视口处理
  const handleBeforeUnload = (event: any) => {
    event.preventDefault();
    event.returnValue = confirm('系统可能不会保存您所做的更改');
  };

  // 打开默认打开页面
  onBeforeMount(() => {
    // 打开默认 tab
    let openTab;
    const tab = route.query.tab as string;
    if (tab) {
      openTab = Object.values(TerminalTabs).find(s => s.key === tab);
    }
    tabManager.openTab(openTab || TerminalTabs.NEW_CONNECTION);
  });

  // 加载用户终端偏好
  onBeforeMount(() => {
    // 加载偏好
    fetchPreference().then(() => {
      // 设置系统主题配色
      const dark = preference.sshTheme.dark;
      document.body.setAttribute('terminal-theme', dark ? 'dark' : 'light');
      render.value = true;
    });
  });

  // 加载字典值
  onBeforeMount(() => {
    useDictStore().loadKeys(dictKeys);
  });

  // 加载主机信息
  onMounted(async () => {
    try {
      // 加载主机
      await loadHostList();
      const connect = route.query.connect as string;
      if (connect) {
        // 默认连接主机
        const connectHostId = Number.parseInt(connect);
        const connectHost = hosts.hostList.find(s => s.id === connectHostId);
        // 打开连接
        if (connectHost) {
          const type = Object.values(TerminalSessionTypes).find(s => s.type === route.query.type) || TerminalSessionTypes.SSH;
          openSession(connectHost, type);
        } else {
          Message.error(`主机 ${connectHostId} 不存在/无权限`);
        }
      }
    } catch (e) {
    } finally {
      setLoading(false);
    }
  });

  // 加载处理
  onMounted(() => {
    // 默认标题
    document.title = TerminalTabs.NEW_CONNECTION.title;
    // 注册关闭视口事件
    if (!debug) {
      window.addEventListener('beforeunload', handleBeforeUnload);
    }
  });

  // 卸载处理
  onUnmounted(() => {
    // 去除 body style
    document.body.removeAttribute('terminal-theme');
    // 重置 title
    document.title = originTitle;
    // 移除关闭视口事件
    if (!debug) {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    }
  });

</script>

<style lang="less" scoped>
  .host-terminal-layout {
    width: 100%;
    height: 100vh;
    position: relative;
    color: var(--color-content-text-2);

    &.terminal-full-layout {
      .host-terminal-layout-header, .host-terminal-layout-left, .host-terminal-layout-right {
        display: none;
      }

      .host-terminal-layout-main {
        height: 100%;

        :deep(.host-terminal-layout-content) {
          width: 100%;
        }
      }
    }

    &-header {
      width: 100%;
      height: var(--header-height);
      background: var(--color-bg-header);
      position: relative;
    }

    &-main {
      width: 100%;
      height: calc(100% - var(--sidebar-width));
      position: relative;
      display: flex;
      justify-content: space-between;
    }

    &-left, &-right {
      width: var(--sidebar-width);
      height: 100%;
      background: var(--color-bg-sidebar);
      overflow: hidden;
    }

    &-left {
      border-right: 1px var(--color-bg-content) solid;
    }

    &-right {
      border-left: 1px var(--color-bg-content) solid;
    }

    &-content {
      width: calc(100% - var(--sidebar-width) * 2);
      height: 100%;
      background: var(--color-bg-content);
      overflow: auto;
    }

    .exit-fullscreen {
      position: absolute;
      right: 24px;
      bottom: 24px;
      z-index: 9999;
    }
  }

</style>
