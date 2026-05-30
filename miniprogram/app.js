App({
  globalData: {
    // 当前电脑局域网IP（手机和电脑连同一WiFi即可访问）
    baseUrl: 'http://10.18.128.57:8080',
    conversationId: ''
  },

  onLaunch() {
    const convId = wx.getStorageSync('conversationId');
    if (convId) {
      this.globalData.conversationId = convId;
    }
  },

  getConversationId() {
    if (!this.globalData.conversationId) {
      this.globalData.conversationId = this.generateId();
      wx.setStorageSync('conversationId', this.globalData.conversationId);
    }
    return this.globalData.conversationId;
  },

  newConversation() {
    this.globalData.conversationId = this.generateId();
    wx.setStorageSync('conversationId', this.globalData.conversationId);
  },

  generateId() {
    const now = Date.now();
    const rand = Math.random().toString(36).substring(2, 10);
    return `${now}-${rand}`;
  }
});
