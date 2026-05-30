const app = getApp();

Page({
  data: {
    messages: [],
    inputValue: '',
    loading: false,
    scrollToView: ''
  },

  onLoad() {
    this.loadLocalMessages();
  },

  loadLocalMessages() {
    try {
      const msgs = wx.getStorageSync('chat_messages') || [];
      this.setData({ messages: msgs });
      if (msgs.length > 0) {
        this.scrollToBottom();
      }
    } catch (e) {
      // ignore
    }
  },

  saveLocalMessages() {
    try {
      const msgs = this.data.messages;
      wx.setStorageSync('chat_messages', msgs);
      const allConvs = wx.getStorageSync('all_conversations') || {};
      allConvs[app.getConversationId()] = msgs;
      wx.setStorageSync('all_conversations', allConvs);
    } catch (e) {
      // ignore
    }
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  sendMessage() {
    const text = this.data.inputValue.trim();
    if (!text || this.data.loading) return;

    const conversationId = app.getConversationId();
    const userMsg = {
      id: Date.now(),
      role: 'user',
      content: text
    };

    const messages = [...this.data.messages, userMsg];
    this.setData({
      messages,
      inputValue: '',
      loading: true
    });
    this.saveLocalMessages();
    this.scrollToBottom();

    wx.request({
      url: `${app.globalData.baseUrl}/api/chat/send`,
      method: 'POST',
      header: { 'content-type': 'application/json' },
      data: {
        message: text,
        conversationId
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          const reply = res.data.data;
          const assistantMsg = {
            id: Date.now() + 1,
            role: reply.role || 'assistant',
            content: reply.reply
          };
          const updated = [...this.data.messages, assistantMsg];
          this.setData({ messages: updated });
          this.saveLocalMessages();
        } else {
          this.addError(res.data.msg || '请求失败');
        }
      },
      fail: (err) => {
        this.addError('网络错误: ' + (err.errMsg || '请检查连接'));
      },
      complete: () => {
        this.setData({ loading: false });
        this.scrollToBottom();
      }
    });
  },

  addError(msg) {
    const errorMsg = {
      id: Date.now() + 1,
      role: 'error',
      content: msg
    };
    const updated = [...this.data.messages, errorMsg];
    this.setData({ messages: updated });
    this.saveLocalMessages();
  },

  scrollToBottom() {
    const msgs = this.data.messages;
    if (msgs.length > 0) {
      const last = msgs[msgs.length - 1];
      this.setData({ scrollToView: `msg-${last.id}` });
    }
  },

  newChat() {
    app.newConversation();
    this.setData({ messages: [] });
    wx.removeStorageSync('chat_messages');
    wx.setStorageSync('conversationId', app.globalData.conversationId);
  },

  goToHistory() {
    wx.navigateTo({
      url: '/pages/history/history'
    });
  }
});
