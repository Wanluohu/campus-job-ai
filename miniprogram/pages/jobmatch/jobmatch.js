const app = getApp();

Page({
  data: {
    major: '',
    skills: '',
    interests: '',
    city: '',
    expectedSalary: '',

    loading: false,
    showResult: false,
    recommendations: [],
    profile: {}
  },

  onMajorInput(e) { this.setData({ major: e.detail.value }); },
  onSkillsInput(e) { this.setData({ skills: e.detail.value }); },
  onInterestsInput(e) { this.setData({ interests: e.detail.value }); },
  onCityInput(e) { this.setData({ city: e.detail.value }); },
  onSalaryInput(e) { this.setData({ expectedSalary: e.detail.value }); },

  matchJobs() {
    const { major, skills, interests, city, expectedSalary } = this.data;

    if (!major.trim() && !skills.trim()) {
      wx.showToast({ title: '请至少填写专业或技能', icon: 'none' });
      return;
    }

    this.setData({ loading: true, showResult: false, recommendations: [] });

    wx.request({
      url: `${app.globalData.baseUrl}/api/career/job-match`,
      method: 'POST',
      header: { 'content-type': 'application/json' },
      data: {
        major: major.trim(),
        skills: skills.trim(),
        interests: interests.trim(),
        city: city.trim(),
        expectedSalary: expectedSalary.trim()
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          const data = res.data.data;
          this.setData({
            recommendations: data.recommendations || [],
            profile: data.profile || {},
            showResult: true
          });
          if (!data.recommendations || data.recommendations.length === 0) {
            wx.showToast({ title: '未找到匹配结果', icon: 'none' });
          }
        } else {
          wx.showToast({ title: res.data.msg || '匹配失败', icon: 'none' });
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

  goBack() {
    wx.navigateBack();
  }
});
