// Vercel Serverless Function: 获取某分类的商品
const PRODUCT_DB = [
  { name: "星火英语四级真题", subtitle: "备考2026年12月", price: 54.6, category: "cet4", feature: "真题详解+模拟卷", rating: 4.8, sold: 23000, link: "https://s.click.taobao.com/ZoCXwIl" },
  { name: "华研外语四级真题", subtitle: "备考2026年6月", price: 26.1, category: "cet4", feature: "专四专项训练全套", rating: 4.7, sold: 18000, link: "https://s.click.taobao.com/tIVxvIl" },
  { name: "闪过四级词汇速刷版", subtitle: "考前急救组合", price: 37.3, category: "cet4", feature: "重点词+真题闪过", rating: 4.9, sold: 56000, link: "https://s.click.taobao.com/oVSnvIl" },
  { name: "星火英语六级真题", subtitle: "备考2026年6月", price: 15.8, category: "cet6", feature: "十合一通关+听力专项", rating: 4.8, sold: 15000, link: "https://s.click.taobao.com/1UvWO9l" },
  { name: "华研外语六级真题", subtitle: "淘金式详解", price: 26.4, category: "cet6", feature: "真题+词汇+听力+写作", rating: 4.7, sold: 12000, link: "https://s.click.taobao.com/j04PO9l" },
  { name: "新东方六级词汇联想记忆法", subtitle: "俞敏洪绿宝书乱序版", price: 30.6, category: "cet6", feature: "词根+联想双效记忆", rating: 4.8, sold: 34000, link: "https://s.click.taobao.com/qlbXvIl" },
  { name: "肖秀荣考研政治1000题全家桶", subtitle: "2027考研必备", price: 32.1, category: "kaoyan", feature: "肖四肖八+精讲精练", rating: 4.9, sold: 89000, link: "https://s.click.taobao.com/VfjUvIl" },
  { name: "张宇考研数学强化36讲", subtitle: "基础30讲+1000题", price: 51.0, category: "kaoyan", feature: "高数+线代+概率论", rating: 4.8, sold: 45000, link: "https://s.click.taobao.com/68cQvIl" },
  { name: "红宝书考研英语词汇", subtitle: "2027考研英语必备", price: 59.9, category: "kaoyan", feature: "词汇+真题+长难句", rating: 4.7, sold: 62000, link: "https://s.click.taobao.com/Egh8O9l" },
  { name: "王道考研408计算机全套", subtitle: "2027版四门专业课", price: 25.0, category: "kaoyan", feature: "网课+教材+真题笔记", rating: 4.9, sold: 31000, link: "https://s.click.taobao.com/h6z5O9l" },
  { name: "中公教资综合素质+教育知识", subtitle: "中职教师证资格", price: 88.0, category: "jiaoshi", feature: "教材+真题+预测卷", rating: 4.8, sold: 41000, link: "https://s.click.taobao.com/BWSCvIl" },
  { name: "山香教资幼儿园保教知识", subtitle: "2026新版全4册", price: 16.1, category: "jiaoshi", feature: "综合素质+保教知识", rating: 4.7, sold: 28000, link: "https://s.click.taobao.com/htwwN9l" },
  { name: "未来教育计算机二级MS Office", subtitle: "备考2026年9月", price: 15.8, category: "computer", feature: "上机题库+核心考点", rating: 4.8, sold: 76000, link: "https://s.click.taobao.com/mPUtN9l" },
  { name: "未来教育计算机二级Python", subtitle: "2026年考试", price: 19.9, category: "computer", feature: "题库+视频+模拟", rating: 4.6, sold: 18000, link: "https://s.click.taobao.com/DbdpN9l" },
  { name: "中公国家公务员一本通", subtitle: "2027国考备考", price: 40.0, category: "gongkao", feature: "笔试一本通+题库", rating: 4.8, sold: 55000, link: "https://s.click.taobao.com/lnFwuIl" },
  { name: "粉笔公考行测5000题", subtitle: "2027国省考通用", price: 38.2, category: "gongkao", feature: "决战行测五千题", rating: 4.9, sold: 43000, link: "https://s.click.taobao.com/pzTuuIl" }
];

const CAT_NAMES = { cet4: "英语四级", cet6: "英语六级", kaoyan: "考研", jiaoshi: "教师资格证", computer: "计算机等级考试", gongkao: "公务员考试" };
const CAT_ICONS = { cet4: "📝", cet6: "🎯", kaoyan: "📚", jiaoshi: "👩‍🏫", computer: "💻", gongkao: "🏛" };

module.exports = (req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  const category = req.query.category || '';
  const products = category ? PRODUCT_DB.filter(p => p.category === category) : PRODUCT_DB;
  res.json({
    code: 200,
    msg: 'success',
    data: {
      category,
      categoryName: CAT_NAMES[category] || category,
      icon: CAT_ICONS[category] || "📖",
      products
    }
  });
};
