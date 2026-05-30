const app = getApp();

Page({
  data: {
    resumeText: '',
    loading: false,
    result: null,
    showResult: false
  },

  onInput(e) {
    this.setData({ resumeText: e.detail.value });
  },

  pasteResume() {
    wx.getClipboardData({
      success: (res) => {
        this.setData({
          resumeText: (this.data.resumeText + res.data).trim()
        });
        wx.showToast({ title: '已粘贴', icon: 'success', duration: 1000 });
      },
      fail: () => {
        wx.showToast({ title: '请先复制简历内容', icon: 'none' });
      }
    });
  },

  clearResume() {
    this.setData({ resumeText: '', result: null, showResult: false });
  },

  analyzeResume() {
    const text = this.data.resumeText.trim();
    if (!text) {
      wx.showToast({ title: '请粘贴简历内容', icon: 'none' });
      return;
    }
    if (text.length < 20) {
      wx.showToast({ title: '简历内容太短，至少20个字', icon: 'none' });
      return;
    }

    this.setData({ loading: true, result: null, showResult: false });

    wx.request({
      url: `${app.globalData.baseUrl}/api/career/resume/analyze`,
      method: 'POST',
      header: { 'content-type': 'application/json' },
      data: { resume: text },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          this.setData({
            result: res.data.data,
            showResult: true
          });
        } else {
          wx.showToast({
            title: res.data.msg || '分析失败',
            icon: 'none',
            duration: 2000
          });
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络错误: ' + (err.errMsg || '请检查连接'),
          icon: 'none',
          duration: 2000
        });
      },
      complete: () => {
        this.setData({ loading: false });
      }
    });
  },

  goBack() {
    wx.navigateBack();
  },

  // 复制优化后的简历
  copyOptimized() {
    if (!this.data.result || !this.data.result.optimizedVersion) {
      wx.showToast({ title: '没有可复制的内容', icon: 'none' });
      return;
    }
    wx.setClipboardData({
      data: this.data.result.optimizedVersion,
      success: () => {
        wx.showToast({ title: '已复制优化版简历', icon: 'success' });
      }
    });
  }
});
