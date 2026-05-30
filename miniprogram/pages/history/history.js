const app = getApp();

Page({
  data: {
    conversations: []
  },

  onShow() {
    this.loadConversations();
  },

  loadConversations() {
    const allConvs = this.getAllConversations();
    const list = Object.keys(allConvs).map(id => {
      const msgs = allConvs[id];
      const title = msgs.length > 0
        ? (msgs[0].content || '').substring(0, 30) + (msgs[0].content.length > 30 ? '...' : '')
        : '新对话';
      const firstMsg = msgs[0];
      const time = firstMsg
        ? this.formatTime(firstMsg.id)
        : '';
      return { id, title, messageCount: msgs.length, time };
    });
    list.sort((a, b) => {
      const aTime = a.time ? new Date(parseInt(a.id.split('-')[0])) : 0;
      const bTime = b.time ? new Date(parseInt(b.id.split('-')[0])) : 0;
      return bTime - aTime;
    });
    this.setData({ conversations: list });
  },

  getAllConversations() {
    try {
      return wx.getStorageSync('all_conversations') || {};
    } catch (e) {
      return {};
    }
  },

  formatTime(timestamp) {
    const d = new Date(timestamp);
    const pad = n => n < 10 ? '0' + n : n;
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  },

  openConversation(e) {
    const id = e.currentTarget.dataset.id;
    app.globalData.conversationId = id;
    wx.setStorageSync('conversationId', id);
    const allConvs = this.getAllConversations();
    if (allConvs[id]) {
      wx.setStorageSync('chat_messages', allConvs[id]);
    }
    wx.navigateBack();
  },

  deleteConversation(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '删除对话',
      content: '确定要删除这条对话记录吗？',
      success: (res) => {
        if (res.confirm) {
          const allConvs = this.getAllConversations();
          delete allConvs[id];
          wx.setStorageSync('all_conversations', allConvs);
          if (app.globalData.conversationId === id) {
            app.newConversation();
            wx.removeStorageSync('chat_messages');
          }
          this.loadConversations();
        }
      }
    });
  },

  goBack() {
    wx.navigateBack();
  }
});
