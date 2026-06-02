// Vercel Serverless Function: 获取所有分类
const CATEGORIES = [
  { id: "cet4", name: "英语四级", icon: "📝", desc: "大学必过第一关", badge: "🔥 热门" },
  { id: "cet6", name: "英语六级", icon: "🎯", desc: "冲刺高分拿证书", badge: "💪 推荐" },
  { id: "kaoyan", name: "考研专区", icon: "📚", desc: "政治+英语+数学+专业课", badge: "🏆 最全" },
  { id: "jiaoshi", name: "教师资格证", icon: "👩‍🏫", desc: "综合素质+教育知识", badge: "✨ 刚需" },
  { id: "computer", name: "计算机等级", icon: "💻", desc: "二级Office/Python/C", badge: "📈 热门" },
  { id: "gongkao", name: "公务员考试", icon: "🏛", desc: "行测+申论+面试", badge: "🎖 高薪" }
];

module.exports = (req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.json({ code: 200, msg: 'success', data: CATEGORIES });
};
