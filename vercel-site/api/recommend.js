// Vercel Serverless Function: AI 考证资料推荐
const https = require('https');

// 商品数据库
const PRODUCT_DB = [
  // 四级
  { name: "星火英语四级真题", subtitle: "备考2026年12月", price: 54.6, category: "cet4", feature: "真题详解+模拟卷", rating: 4.8, sold: 23000, link: "https://s.click.taobao.com/ZoCXwIl" },
  { name: "华研外语四级真题", subtitle: "备考2026年6月", price: 26.1, category: "cet4", feature: "专四专项训练全套", rating: 4.7, sold: 18000, link: "https://s.click.taobao.com/tIVxvIl" },
  { name: "闪过四级词汇速刷版", subtitle: "考前急救组合", price: 37.3, category: "cet4", feature: "重点词+真题闪过", rating: 4.9, sold: 56000, link: "https://s.click.taobao.com/oVSnvIl" },
  // 六级
  { name: "星火英语六级真题", subtitle: "备考2026年6月", price: 15.8, category: "cet6", feature: "十合一通关+听力专项", rating: 4.8, sold: 15000, link: "https://s.click.taobao.com/1UvWO9l" },
  { name: "华研外语六级真题", subtitle: "淘金式详解", price: 26.4, category: "cet6", feature: "真题+词汇+听力+写作", rating: 4.7, sold: 12000, link: "https://s.click.taobao.com/j04PO9l" },
  { name: "新东方六级词汇联想记忆法", subtitle: "俞敏洪绿宝书乱序版", price: 30.6, category: "cet6", feature: "词根+联想双效记忆", rating: 4.8, sold: 34000, link: "https://s.click.taobao.com/qlbXvIl" },
  // 考研
  { name: "肖秀荣考研政治1000题全家桶", subtitle: "2027考研必备", price: 32.1, category: "kaoyan", feature: "肖四肖八+精讲精练", rating: 4.9, sold: 89000, link: "https://s.click.taobao.com/VfjUvIl" },
  { name: "张宇考研数学强化36讲", subtitle: "基础30讲+1000题", price: 51.0, category: "kaoyan", feature: "高数+线代+概率论", rating: 4.8, sold: 45000, link: "https://s.click.taobao.com/68cQvIl" },
  { name: "红宝书考研英语词汇", subtitle: "2027考研英语必备", price: 59.9, category: "kaoyan", feature: "词汇+真题+长难句", rating: 4.7, sold: 62000, link: "https://s.click.taobao.com/Egh8O9l" },
  { name: "王道考研408计算机全套", subtitle: "2027版四门专业课", price: 25.0, category: "kaoyan", feature: "网课+教材+真题笔记", rating: 4.9, sold: 31000, link: "https://s.click.taobao.com/h6z5O9l" },
  // 教资
  { name: "中公教资综合素质+教育知识", subtitle: "中职教师证资格", price: 88.0, category: "jiaoshi", feature: "教材+真题+预测卷", rating: 4.8, sold: 41000, link: "https://s.click.taobao.com/BWSCvIl" },
  { name: "山香教资幼儿园保教知识", subtitle: "2026新版全4册", price: 16.1, category: "jiaoshi", feature: "综合素质+保教知识", rating: 4.7, sold: 28000, link: "https://s.click.taobao.com/htwwN9l" },
  // 计算机
  { name: "未来教育计算机二级MS Office", subtitle: "备考2026年9月", price: 15.8, category: "computer", feature: "上机题库+核心考点", rating: 4.8, sold: 76000, link: "https://s.click.taobao.com/mPUtN9l" },
  { name: "未来教育计算机二级Python", subtitle: "2026年考试", price: 19.9, category: "computer", feature: "题库+视频+模拟", rating: 4.6, sold: 18000, link: "https://s.click.taobao.com/DbdpN9l" },
  // 考公
  { name: "中公国家公务员一本通", subtitle: "2027国考备考", price: 40.0, category: "gongkao", feature: "笔试一本通+题库", rating: 4.8, sold: 55000, link: "https://s.click.taobao.com/lnFwuIl" },
  { name: "粉笔公考行测5000题", subtitle: "2027国省考通用", price: 38.2, category: "gongkao", feature: "决战行测五千题", rating: 4.9, sold: 43000, link: "https://s.click.taobao.com/pzTuuIl" }
];

const CATEGORIES = [
  { id: "cet4", name: "英语四级", icon: "📝", desc: "大学必过第一关", badge: "🔥 热门" },
  { id: "cet6", name: "英语六级", icon: "🎯", desc: "冲刺高分拿证书", badge: "💪 推荐" },
  { id: "kaoyan", name: "考研专区", icon: "📚", desc: "政治+英语+数学+专业课", badge: "🏆 最全" },
  { id: "jiaoshi", name: "教师资格证", icon: "👩‍🏫", desc: "综合素质+教育知识", badge: "✨ 刚需" },
  { id: "computer", name: "计算机等级", icon: "💻", desc: "二级Office/Python/C", badge: "📈 热门" },
  { id: "gongkao", name: "公务员考试", icon: "🏛", desc: "行测+申论+面试", badge: "🎖 高薪" }
];

function getExamName(id) {
  const map = { cet4: "英语四级", cet6: "英语六级", kaoyan: "考研", jiaoshi: "教师资格证", computer: "计算机等级考试", gongkao: "公务员考试" };
  return map[id] || id;
}

function getLevelText(l) {
  const map = { beginner: "基础薄弱", medium: "有一定基础", good: "基础较好" };
  return map[l] || "";
}

function getTimeText(t) {
  const map = { rush: "不足1个月（冲刺）", normal: "1-3个月", long: "3个月以上" };
  return map[t] || "";
}

function getStyleText(s) {
  const map = { video: "喜欢看视频学习", book: "喜欢看书刷题", class: "喜欢跟着老师系统学" };
  return map[s] || "";
}

function buildPrompt(answers) {
  return `你是一个考证资料推荐专家。根据以下用户信息，推荐最适合ta的考证资料：

目标考试：${getExamName(answers.exam)}
当前水平：${getLevelText(answers.level)}
备考时间：${getTimeText(answers.time)}
学习偏好：${getStyleText(answers.style)}
预算范围：${answers.budget || "不限"}

请严格按以下格式输出（不要输出其他内容）：
【用户画像】用1句话概括这位同学的情况
【核心推荐】推荐1本最重要的书，格式：书名|推荐理由（30字以内）
【辅助推荐】推荐1-2本搭配使用的书，格式同上
【学习计划】给一个3句话的简短学习计划
【提醒】1句鼓励的话

注意：推荐具体书名时，优先推荐市面上口碑最好的版本（如星火、华研、中公、肖秀荣、新东方等）。`;
}

async function callDeepSeek(prompt) {
  const apiKey = process.env.DEEPSEEK_API_KEY || 'sk-6480cf775d384a1b87d17ed808589ae1';
  const body = JSON.stringify({
    model: "deepseek-chat",
    max_tokens: 1024,
    temperature: 0.7,
    messages: [
      { role: "system", content: "你是一个专业的考证资料推荐顾问。请严格按照格式输出，推荐具体、市面上真实存在的书名，不要编造。" },
      { role: "user", content: prompt }
    ]
  });

  return new Promise((resolve, reject) => {
    const req = https.request({
      hostname: 'api.deepseek.com',
      path: '/v1/chat/completions',
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${apiKey}`,
        'Content-Type': 'application/json'
      },
      timeout: 60000
    }, res => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          const json = JSON.parse(data);
          resolve(json.choices[0].message.content);
        } catch (e) {
          reject(new Error('API parse error'));
        }
      });
    });
    req.on('error', reject);
    req.on('timeout', () => { req.destroy(); reject(new Error('timeout')); });
    req.write(body);
    req.end();
  });
}

function parseAIResponse(text) {
  const analysis = {};
  const lines = text.split('\n');
  for (const line of lines) {
    const trimmed = line.trim();
    if (trimmed.startsWith('【用户画像】')) analysis.profile = trimmed.replace('【用户画像】', '').trim();
    else if (trimmed.startsWith('【核心推荐】')) {
      const rec = trimmed.replace('【核心推荐】', '').trim();
      const parts = rec.split('|');
      analysis.mainBook = parts[0]?.trim() || rec;
      analysis.mainReason = parts[1]?.trim() || "根据你的情况精准推荐";
    } else if (trimmed.startsWith('【辅助推荐】')) {
      const rec = trimmed.replace('【辅助推荐】', '').trim();
      const parts = rec.split('|');
      analysis.subBook = parts[0]?.trim() || rec;
      analysis.subReason = parts[1]?.trim() || "配合主书使用效果更好";
    } else if (trimmed.startsWith('【学习计划】')) analysis.plan = trimmed.replace('【学习计划】', '').trim();
    else if (trimmed.startsWith('【提醒】')) analysis.encouragement = trimmed.replace('【提醒】', '').trim();
  }
  if (!analysis.profile) analysis.profile = "一位正在备考的同学，目标明确，需要专业的资料推荐";
  if (!analysis.mainBook) { analysis.mainBook = "经典备考资料"; analysis.mainReason = "口碑最好，适合大多数考生"; }
  if (!analysis.plan) analysis.plan = "每天坚持学习2小时，先打基础再刷真题，考前1个月集中冲刺";
  if (!analysis.encouragement) analysis.encouragement = "努力一定会有回报，加油！";
  return analysis;
}

function matchProducts(exam, analysis) {
  const results = [];
  const mainBook = analysis.mainBook || '';
  for (const p of PRODUCT_DB) {
    if (p.category === exam || mainBook.includes(p.name) || p.name.includes(mainBook)) {
      results.push(p);
    }
  }
  if (results.length === 0) {
    for (const p of PRODUCT_DB) {
      if (p.category === exam) results.push(p);
    }
  }
  return results.slice(0, 4);
}

module.exports = async (req, res) => {
  // CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') return res.status(200).end();
  if (req.method !== 'POST') return res.status(405).json({ code: 405, msg: 'Method not allowed' });

  const answers = req.body;
  if (!answers || !answers.exam) return res.status(400).json({ code: 400, msg: '请完成所有题目' });

  try {
    const prompt = buildPrompt(answers);
    const aiResponse = await callDeepSeek(prompt);
    const analysis = parseAIResponse(aiResponse);
    const products = matchProducts(answers.exam, analysis);

    return res.json({ code: 200, msg: '推荐完成', data: { success: true, analysis, products } });
  } catch (e) {
    // Fallback: rule-based recommendation
    const analysis = {
      profile: `一位正在准备${getExamName(answers.exam)}的同学，${getLevelText(answers.level)}，备考时间${getTimeText(answers.time)}`,
      mainBook: `${getExamName(answers.exam)}经典备考套装`,
      mainReason: "销量最高、口碑最好的版本",
      plan: "每天坚持学习2小时，先打基础再刷真题，考前1个月集中冲刺",
      encouragement: "选择了考证这条路，就坚持走下去！"
    };
    const products = PRODUCT_DB.filter(p => p.category === answers.exam).slice(0, 3);
    return res.json({ code: 200, msg: '推荐完成（备用引擎）', data: { success: true, analysis, products, fallback: true } });
  }
};
