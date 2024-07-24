import type { ITerminalPanelManager, TerminalPanelTabItem } from '../types/define';
import TerminalTabManager from '../handler/terminal-tab-manager';

// 终端面板管理器实现
export default class TerminalPanelManager implements ITerminalPanelManager {

  public active: number;

  public panels: Array<TerminalTabManager<TerminalPanelTabItem>>;

  constructor() {
    this.active = 0;
    this.panels = [new TerminalTabManager()];
  }

  // 获取当前面板
  getCurrentPanel(): TerminalTabManager<TerminalPanelTabItem> {
    return this.panels[this.active];
  }

  // 设置当前面板
  setCurrentPanel(active: number): void {
    this.active = active;
  };

  // 获取面板
  getPanel(index: number): TerminalTabManager<TerminalPanelTabItem> {
    return this.panels[index];
  };

  // 移除面板
  removePanel(index: number) {
    this.panels[index].clear();
    this.active = index >= this.panels.length ? this.panels.length - 1 : index;
  };

  // 重置
  reset() {
    this.active = 0;
    if (this.panels) {
      for (let panel of this.panels) {
        panel.clear();
      }
    }
  };

}
