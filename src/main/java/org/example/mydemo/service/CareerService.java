package org.example.mydemo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CareerService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    private final ConcurrentHashMap<String, List<Map<String, String>>> interviewSessions;
    private final ConcurrentHashMap<String, Map<String, Object>> interviewConfigs;

    // 版本日志（硬编码）
    private static final List<Map<String, Object>> CHANGELOG = Arrays.asList(
        createVersionEntry("v2.0", "2026-05-30",
            new String[]{"新增求职场景选择（应届生/社招/转行/实习/兼职）", "新增5维度简历评分（措辞、匹配度、结构、亮点、语病）", "新增面试类型选择（校招群面/单面/技术面/HR面/兼职面试）", "新增自定义面试轮数（3/5/8轮）和题库模式", "新增兼职匹配筛选（工作类型/时长/副业推荐）", "新增隐私保护声明和安全提示"},
            new String[]{"功能新增", "体验优化", "安全加固"}),
        createVersionEntry("v1.0", "2026-05-20",
            new String[]{"AI简历分析与优化", "AI模拟面试（5轮问答+综合评价）", "AI兼职/实习岗位匹配推荐"},
            new String[]{"功能新增"})
    );

    public CareerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.interviewSessions = new ConcurrentHashMap<>();
        this.interviewConfigs = new ConcurrentHashMap<>();
    }

    // ==================== AI 简历优化（v2.0 升级） ====================

    public Map<String, Object> analyzeResume(String resumeText) {
        return analyzeResume(resumeText, "fresh_graduate", "full", "");
    }

    public Map<String, Object> analyzeResume(String resumeText, String scenario,
                                              String versionType, String targetPosition) {
        String scenarioDesc = buildScenarioDesc(scenario);
        String versionDesc = buildVersionDesc(versionType);
        String targetStr = (targetPosition != null && !targetPosition.isEmpty())
                ? targetPosition : "通用岗位";

        String systemPrompt =
            "你是一位资深职业顾问和简历优化专家，拥有10年以上HR经验。\n" +
            "\n" +
            "【用户情景】\n" +
            "- 求职类型：" + scenarioDesc + "\n" +
            "- 目标岗位：" + targetStr + "\n" +
            "- 优化版本：" + versionDesc + "\n" +
            "\n" +
            "【分析要求】\n" +
            "请从以下5个维度单独评分（每项满分100）：\n" +
            "1. 措辞专业度 - 用词是否专业、精准、符合行业规范\n" +
            "2. 岗位匹配度 - 内容与目标岗位的契合程度\n" +
            "3. 逻辑结构 - 简历结构是否清晰、层次分明、易读\n" +
            "4. 亮点突出度 - 核心优势和成就是否突出展示\n" +
            "5. 语病错误 - 是否存在语法、拼写、标点、格式问题\n" +
            "\n" +
            "每个维度给出分数和一句简要点评（10字以内）。\n" +
            "然后提取5-10个简历中的关键技术/能力关键词。\n" +
            "最后根据\"" + versionDesc + "\"的要求生成优化版本。\n" +
            "\n" +
            "请严格按照以下JSON格式返回（不要包含其他文字）：\n" +
            "{\n" +
            "  \"score\": 整体加权评分(1-100),\n" +
            "  \"dimensions\": {\n" +
            "    \"wording\": {\"label\":\"措辞专业度\",\"score\":70,\"comment\":\"简要点评\"},\n" +
            "    \"matching\": {\"label\":\"岗位匹配度\",\"score\":65,\"comment\":\"简要点评\"},\n" +
            "    \"structure\": {\"label\":\"逻辑结构\",\"score\":80,\"comment\":\"简要点评\"},\n" +
            "    \"highlights\": {\"label\":\"亮点突出度\",\"score\":60,\"comment\":\"简要点评\"},\n" +
            "    \"grammar\": {\"label\":\"语病错误\",\"score\":85,\"comment\":\"简要点评\"}\n" +
            "  },\n" +
            "  \"strengths\": [\"优点1\",\"优点2\",\"优点3\"],\n" +
            "  \"weaknesses\": [\"不足1\",\"不足2\",\"不足3\"],\n" +
            "  \"suggestions\": [\"建议1\",\"建议2\",\"建议3\",\"建议4\",\"建议5\"],\n" +
            "  \"optimizedVersion\": \"优化后的简历\",\n" +
            "  \"keywords\": [\"关键词1\",\"关键词2\",\"关键词3\",\"关键词4\",\"关键词5\"]\n" +
            "}";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", "请帮我分析优化以下简历：\n\n" + resumeText));

        try {
            String reply = callDeepSeek(messages, 4096);
            String json = extractJson(reply);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(json, Map.class);
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "分析失败: " + e.getMessage());
            errorResult.put("score", 0);
            return errorResult;
        }
    }

    // ==================== AI 面试模拟（v2.0 升级） ====================

    public Map<String, Object> startInterview(String jobType, String difficulty) {
        return startInterview(jobType, difficulty, "campus_single", 5, "free");
    }

    public Map<String, Object> startInterview(String jobType, String difficulty,
                                               String interviewType, int roundCount, String mode) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        String systemPrompt = buildInterviewPrompt(interviewType, jobType, difficulty, roundCount, mode);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", "我准备好了，请开始面试。"));

        try {
            String reply = callDeepSeek(messages);
            messages.add(createMessage("assistant", reply));

            interviewSessions.put(sessionId, messages);

            Map<String, Object> config = new LinkedHashMap<>();
            config.put("jobType", jobType);
            config.put("difficulty", difficulty);
            config.put("interviewType", interviewType);
            config.put("totalQuestions", roundCount);
            config.put("currentQuestion", 1);
            config.put("mode", mode);
            interviewConfigs.put(sessionId, config);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sessionId", sessionId);
            result.put("question", reply);
            result.put("questionNumber", 1);
            result.put("totalQuestions", roundCount);
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "面试启动失败: " + e.getMessage());
            return errorResult;
        }
    }

    public Map<String, Object> answerInterview(String sessionId, String answer) {
        List<Map<String, String>> messages = interviewSessions.get(sessionId);
        Map<String, Object> config = interviewConfigs.get(sessionId);

        if (messages == null || config == null) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "面试会话不存在或已过期");
            return errorResult;
        }

        int currentQ = (int) config.get("currentQuestion");
        int totalQ = (int) config.get("totalQuestions");

        messages.add(createMessage("user", answer));

        try {
            String reply = callDeepSeek(messages);
            messages.add(createMessage("assistant", reply));

            currentQ++;
            config.put("currentQuestion", currentQ);
            boolean isComplete = currentQ > totalQ;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sessionId", sessionId);
            result.put("questionNumber", currentQ - 1);

            if (isComplete) {
                result.put("isComplete", true);
                result.put("overallEvaluation", reply);
                result.put("totalQuestions", totalQ);
                interviewSessions.remove(sessionId);
                interviewConfigs.remove(sessionId);
            } else {
                result.put("isComplete", false);
                result.put("feedback", reply);
                result.put("totalQuestions", totalQ);
            }
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "面试回答处理失败: " + e.getMessage());
            return errorResult;
        }
    }

    // ==================== AI 兼职/工作匹配（v2.0 升级） ====================

    public Map<String, Object> matchJobs(Map<String, Object> userProfile) {
        String major = (String) userProfile.getOrDefault("major", "未提供");
        String skills = (String) userProfile.getOrDefault("skills", "未提供");
        String interests = (String) userProfile.getOrDefault("interests", "未提供");
        String city = (String) userProfile.getOrDefault("city", "未提供");
        String expectedSalary = (String) userProfile.getOrDefault("expectedSalary", "未提供");
        String workType = (String) userProfile.getOrDefault("workType", "");
        String workDuration = (String) userProfile.getOrDefault("workDuration", "");
        boolean includeSideHustle = Boolean.TRUE.equals(userProfile.get("includeSideHustle"));

        String filterDesc = buildJobFilterDesc(workType, workDuration);

        String systemPrompt =
            "你是一位资深职业规划师，擅长为大学生推荐合适的兼职、实习和校招岗位。\n" +
            "\n" +
            "【筛选条件】\n" +
            "- 工作类型偏好：" + (filterDesc.isEmpty() ? "不限" : filterDesc) + "\n" +
            "- 所在城市：" + city + "\n" +
            "- 期望薪资：" + expectedSalary + "\n" +
            "\n" +
            "请推荐5个最适合的方向，每个方向附带2-3个标签（可选标签：远程、高薪、专业对口、前景好、上手快、大厂、弹性工作、可转正、技能提升、不限专业）。\n" +
            (includeSideHustle ? "另外再推荐2个副业/自由职业方向，放在 sideHustles 字段中。\n" : "") +
            "\n" +
            "请严格按照以下JSON格式返回（不要包含其他文字）：\n" +
            "{\n" +
            "  \"recommendations\": [\n" +
            "    {\n" +
            "      \"title\": \"岗位名称\",\n" +
            "      \"type\": \"兼职/实习/校招\",\n" +
            "      \"matchScore\": 匹配度分数(1-100),\n" +
            "      \"salaryRange\": \"薪资范围\",\n" +
            "      \"reason\": \"推荐理由\",\n" +
            "      \"skillsNeeded\": \"需要补充的技能\",\n" +
            "      \"howToFind\": \"如何找到这类工作\",\n" +
            "      \"tags\": [\"标签1\",\"标签2\",\"标签3\"]\n" +
            "    }\n" +
            "  ]" +
            (includeSideHustle ? ",\n  \"sideHustles\": [\n    {\"title\": \"副业名称\", \"description\": \"副业描述与建议\"}\n  ]" : "") +
            "\n}";

        String userMessage = String.format(
            "我的信息如下：\n" +
            "- 专业：%s\n" +
            "- 已掌握的技能：%s\n" +
            "- 兴趣方向：%s\n" +
            "- 所在城市：%s\n" +
            "- 期望薪资：%s\n" +
            "\n" +
            "请为我推荐合适的兼职/实习方向。",
            major, skills, interests, city, expectedSalary);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", userMessage));

        try {
            String reply = callDeepSeek(messages, 4096);
            String json = extractJson(reply);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(json, Map.class);
            result.put("profile", userProfile);
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "匹配失败: " + e.getMessage());
            errorResult.put("recommendations", Collections.emptyList());
            return errorResult;
        }
    }

    public void endInterview(String sessionId) {
        interviewSessions.remove(sessionId);
        interviewConfigs.remove(sessionId);
    }

    // ==================== 题库模式 ====================

    public Map<String, Object> getBankQuestion(String interviewType, String jobType, String difficulty) {
        String systemPrompt =
            "你是一位专业面试官。请从\"" + buildInterviewTypeName(interviewType) + "\"的常见面试题库中，" +
            "随机抽取一道" + (difficulty != null ? difficulty : "初级") + "难度的面试题。" +
            (jobType != null && !jobType.isEmpty() ? "岗位方向为：" + jobType + "。" : "") +
            "直接给出题目，不需要任何前缀说明。";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", "请出一道面试题。"));

        try {
            String reply = callDeepSeek(messages, 1024);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("question", reply);
            result.put("interviewType", interviewType);
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("error", "获取题目失败: " + e.getMessage());
            return errorResult;
        }
    }

    // ==================== 版本日志 ====================

    public Map<String, Object> getChangelog() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("versions", CHANGELOG);
        return result;
    }

    // ==================== 反馈收集 ====================

    public Map<String, Object> saveFeedback(String page, int rating, String suggestion, String contact) {
        // 当前版本打印到控制台，后续可接入数据库或飞书通知
        System.out.println("===== 用户反馈 =====");
        System.out.println("页面: " + page);
        System.out.println("评分: " + rating + " 星");
        System.out.println("建议: " + suggestion);
        System.out.println("联系方式: " + (contact != null ? contact : "未提供"));
        System.out.println("时间: " + new Date());
        System.out.println("===================");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("received", true);
        result.put("message", "感谢你的反馈！我们会认真考虑。");
        return result;
    }

    // ==================== 私有辅助方法 ====================

    private String callDeepSeek(List<Map<String, String>> messages) throws Exception {
        return callDeepSeek(messages, 4096);
    }

    private String callDeepSeek(List<Map<String, String>> messages, int maxTokens) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", maxTokens);
        body.put("temperature", 0.7);
        body.put("messages", messages);

        String requestJson = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(90))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API 调用失败: HTTP " + response.statusCode() + " - " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> msg = new LinkedHashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }

    private String extractJson(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf("\n");
            int end = trimmed.lastIndexOf("```");
            if (start != -1 && end != -1) {
                trimmed = trimmed.substring(start + 1, end).trim();
            }
        }
        return trimmed;
    }

    // --- 场景描述映射 ---

    private String buildScenarioDesc(String scenario) {
        if ("experienced".equals(scenario)) return "社招（有工作经验，跳槽/晋升）";
        if ("career_change".equals(scenario)) return "转行（跨行业/跨岗位求职）";
        if ("intern".equals(scenario)) return "实习（在校生找实习机会）";
        if ("part_time".equals(scenario)) return "兼职（在校生找兼职工作）";
        return "应届生（校招，无全职工作经验）"; // fresh_graduate 默认
    }

    private String buildVersionDesc(String versionType) {
        if ("concise".equals(versionType)) return "精简版（200字以内，提炼核心卖点，适合网申）";
        if ("simple".equals(versionType)) return "简洁版（仅优化措辞不改结构，适合快速修改）";
        return "完整版（完整重写所有模块，适合正式投递）"; // full 默认
    }

    private String buildJobFilterDesc(String workType, String workDuration) {
        StringBuilder sb = new StringBuilder();
        if (workType != null && !workType.isEmpty()) sb.append(workType);
        if (workDuration != null && !workDuration.isEmpty()) {
            if (sb.length() > 0) sb.append("、");
            sb.append(workDuration);
        }
        return sb.toString();
    }

    private String buildInterviewTypeName(String interviewType) {
        if ("campus_group".equals(interviewType)) return "校招群面（无领导小组讨论）";
        if ("tech".equals(interviewType)) return "社招技术面";
        if ("hr".equals(interviewType)) return "HR面";
        if ("part_time".equals(interviewType)) return "兼职面试";
        return "校招单面"; // campus_single 默认
    }

    private String buildInterviewPrompt(String interviewType, String jobType,
                                         String difficulty, int roundCount, String mode) {
        String typeName = buildInterviewTypeName(interviewType);

        if ("campus_group".equals(interviewType)) {
            return String.format(
                "你是一位经验丰富的校招群面面试官，正在主持一场%d轮的无领导小组讨论模拟（%s级别）。\n" +
                "\n" +
                "面试规则：\n" +
                "- 模拟无领导小组讨论场景\n" +
                "- 第1轮：发布讨论题目，要求用户陈述观点\n" +
                "- 第2-%d轮：追问、挑战观点、考察协作与表达能力\n" +
                "- 最后一轮后：给出综合评价（领导力/沟通/逻辑/团队协作/时间管理 五个维度）\n" +
                "%s\n" +
                "\n" +
                "请直接开始第1题，不需要寒暄。",
                roundCount, difficulty, roundCount - 1,
                "bank".equals(mode) ? "注意：使用常见群面题库中的经典题目。" : "");
        }

        if ("tech".equals(interviewType)) {
            return String.format(
                "你是一位资深技术面试官，正在进行一场%d轮%s技术面试（%s级别）。\n" +
                "\n" +
                "面试规则：\n" +
                "- 问题从基础到深入递进\n" +
                "- 考察维度：基础知识、项目经验、系统设计、问题解决、代码思维\n" +
                "- 第1-%d轮：提出问题，用户回答后给出1-2句反馈，再提下一问\n" +
                "- 最后一轮后：给出综合评价（专业知识/项目能力/逻辑思维/沟通表达/学习潜力 五个维度，每个维度1-5星）\n" +
                "%s\n" +
                "\n" +
                "请直接开始第1个技术问题，不需要寒暄。",
                roundCount, jobType, difficulty, roundCount - 1,
                "bank".equals(mode) ? "注意：使用常见技术面试题库中的经典题目。" : "");
        }

        if ("hr".equals(interviewType)) {
            return String.format(
                "你是一位经验丰富的HR面试官，正在进行一场%d轮HR面试。\n" +
                "\n" +
                "面试规则：\n" +
                "- 考察维度：职业规划、沟通能力、抗压能力、团队协作、价值观匹配\n" +
                "- 第1-%d轮：提出HR常见问题，用户回答后给出简短反馈\n" +
                "- 最后一轮后：给出综合评价（职业规划/沟通表达/抗压能力/团队协作/文化匹配 五个维度）\n" +
                "%s\n" +
                "\n" +
                "请直接开始第1个问题。",
                roundCount, roundCount - 1,
                "bank".equals(mode) ? "注意：使用常见HR面试题库中的经典题目。" : "");
        }

        if ("part_time".equals(interviewType)) {
            return String.format(
                "你是一位兼职/实习招聘负责人，正在进行一场%d轮兼职面试。\n" +
                "\n" +
                "面试规则：\n" +
                "- 问题偏向实用性，关注时间配合、技能基础、工作态度\n" +
                "- 第1-%d轮：提问，用户回答后给简短反馈\n" +
                "- 最后一轮后：给出综合评价\n" +
                "\n" +
                "请直接开始第1个问题。",
                roundCount, roundCount - 1);
        }

        // campus_single 默认
        return String.format(
            "你是一位经验丰富的校招面试官，正在进行一场%d轮面试（%s级别），岗位方向：%s。\n" +
            "\n" +
            "面试规则：\n" +
            "- 总共进行%d轮问答\n" +
            "- 每轮你提出一个专业问题，用户回答后你给出简短反馈，然后进入下一题\n" +
            "- 问题难度递进：从基础到深入\n" +
            "- 第1-%d轮：提出问题，等用户回答后给出1-2句反馈，再提下一问\n" +
            "- 最后一轮后：给出综合面试评价（优点、不足、提升建议、面试结果：通过/待定/不通过）\n" +
            "%s\n" +
            "\n" +
            "现在开始第1个问题，直接提问，不需要寒暄。",
            roundCount, difficulty, jobType, roundCount, roundCount - 1,
            "bank".equals(mode) ? "注意：使用常见校招面试题库中的经典题目。" : "");
    }

    private static Map<String, Object> createVersionEntry(String version, String date,
                                                           String[] changes, String[] tags) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("version", version);
        entry.put("date", date);
        entry.put("changes", Arrays.asList(changes));
        entry.put("tags", Arrays.asList(tags));
        return entry;
    }
}
