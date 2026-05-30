const app = getApp();

Page({
  data: {
    // 面试准备
    setupMode: true,
    jobTypes: ['后端开发', '前端开发', '产品经理', '数据分析', '市场营销', '运营', '人力资源', '金融', '通用岗位'],
    jobTypeIndex: 0,
    difficulty: '初级',
    difficulties: ['初级', '中级', '高级'],

    // 面试进行
    sessionId: '',
    messages: [],
    inputValue: '',
    loading: false,
    interviewComplete: false,
    scrollToView: '',

    // 面试总结
    evaluation: ''
  },

  // ========== 设置阶段 ==========

  onJobTypeChange(e) {
    this.setData({ jobTypeIndex: e.detail.value });
  },

  onDifficultyChange(e) {
    const idx = e.detail.value;
    this.setData({ difficulty: this.data.difficulties[idx] });
  },

  startInterview() {
    const jobType = this.data.jobTypes[this.data.jobTypeIndex];
    const difficulty = this.data.difficulty;

    this.setData({ loading: true, setupMode: false });

    wx.request({
      url: `${app.globalData.baseUrl}/api/career/interview/start`,
      method: 'POST',
      header: { 'content-type': 'application/json' },
      data: { jobType, difficulty },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          const data = res.data.data;
          const msg = {
            id: Date.now(),
            role: 'interviewer',
            content: data.question
          };
          this.setData({
            sessionId: data.sessionId,
            messages: [msg],
            scrollToView: `msg-${msg.id}`
          });
        } else {
          wx.showToast({ title: res.data.msg || '启动失败', icon: 'none' });
          this.setData({ setupMode: true });
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络错误: ' + err.errMsg, icon: 'none' });
        this.setData({ setupMode: true });
      },
      complete: () => {
        this.setData({ loading: false });
      }
    });
  },

  // ========== 面试阶段 ==========

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  sendAnswer() {
    const answer = this.data.inputValue.trim();
    if (!answer || this.data.loading || this.data.interviewComplete) return;

    const userMsg = {
      id: Date.now(),
      role: 'user',
      content: answer
    };

    const messages = [...this.data.messages, userMsg];
    this.setData({
      messages,
      inputValue: '',
      loading: true,
      scrollToView: `msg-${userMsg.id}`
    });

    wx.request({
      url: `${app.globalData.baseUrl}/api/career/interview/answer`,
      method: 'POST',
      header: { 'content-type': 'application/json' },
      data: {
        sessionId: this.data.sessionId,
        answer
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          const data = res.data.data;
          const replyMsg = {
            id: Date.now() + 1,
            role: 'interviewer',
            content: data.isComplete ? data.overallEvaluation : data.feedback
          };

          const updated = [...this.data.messages, replyMsg];
          this.setData({
            messages: updated,
            interviewComplete: data.isComplete,
            evaluation: data.isComplete ? data.overallEvaluation : '',
            scrollToView: `msg-${replyMsg.id}`
          });
        } else {
          wx.showToast({ title: res.data.msg || '处理失败', icon: 'none' });
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络错误: ' + err.errMsg, icon: 'none' });
      },
      complete: () => {
        this.setData({ loading: false });
      }
    });
  },

  // ========== 通用 ==========

  scrollToBottom() {
    const msgs = this.data.messages;
    if (msgs.length > 0) {
      const last = msgs[msgs.length - 1];
      this.setData({ scrollToView: `msg-${last.id}` });
    }
  },

  restartInterview() {
    this.setData({
      setupMode: true,
      sessionId: '',
      messages: [],
      inputValue: '',
      loading: false,
      interviewComplete: false,
      evaluation: ''
    });
  },

  goBack() {
    wx.navigateBack();
  }
});
