const app = getApp();

Page({
  data: {},

  goToResume() {
    wx.navigateTo({
      url: '/pages/resume/resume'
    });
  },

  goToInterview() {
    wx.navigateTo({
      url: '/pages/interview/interview'
    });
  },

  goToJobMatch() {
    wx.navigateTo({
      url: '/pages/jobmatch/jobmatch'
    });
  }
});
