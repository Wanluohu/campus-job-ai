package org.example.mydemo.controller;

import org.example.mydemo.other.Result;
import org.example.mydemo.service.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/career")
public class CareerController {

    @Autowired
    private CareerService careerService;

    // ==================== AI 简历优化（v2.0） ====================

    @PostMapping("/resume/analyze")
    public Result analyzeResume(@RequestBody Map<String, Object> body) {
        String resumeText = (String) body.get("resume");
        if (resumeText == null || resumeText.trim().isEmpty()) {
            Result r = new Result();
            r.setCode(400);
            r.setMsg("请提供简历内容");
            return r;
        }

        String scenario = (String) body.getOrDefault("scenario", "fresh_graduate");
        String versionType = (String) body.getOrDefault("versionType", "full");
        String targetPosition = (String) body.getOrDefault("targetPosition", "");

        Map<String, Object> analysis = careerService.analyzeResume(
                resumeText.trim(), scenario, versionType, targetPosition);

        Result r = new Result();
        if (analysis.containsKey("error")) {
            r.setCode(500);
            r.setMsg((String) analysis.get("error"));
        } else {
            r.setCode(200);
            r.setMsg("简历分析完成");
            r.setData(analysis);
        }
        return r;
    }

    // ==================== AI 面试模拟（v2.0） ====================

    @PostMapping("/interview/start")
    public Result startInterview(@RequestBody Map<String, Object> body) {
        String jobType = (String) body.getOrDefault("jobType", "通用岗位");
        String difficulty = (String) body.getOrDefault("difficulty", "初级");
        String interviewType = (String) body.getOrDefault("interviewType", "campus_single");
        int roundCount = body.containsKey("roundCount")
                ? ((Number) body.get("roundCount")).intValue() : 5;
        String mode = (String) body.getOrDefault("mode", "free");

        // 限制轮数范围
        if (roundCount < 3) roundCount = 3;
        if (roundCount > 10) roundCount = 10;

        Map<String, Object> interviewData = careerService.startInterview(
                jobType, difficulty, interviewType, roundCount, mode);

        Result r = new Result();
        if (interviewData.containsKey("error")) {
            r.setCode(500);
            r.setMsg((String) interviewData.get("error"));
        } else {
            r.setCode(200);
            r.setMsg("面试已开始");
            r.setData(interviewData);
        }
        return r;
    }

    @PostMapping("/interview/answer")
    public Result answerInterview(@RequestBody Map<String, Object> body) {
        String sessionId = (String) body.get("sessionId");
        String answer = (String) body.get("answer");

        if (sessionId == null || sessionId.trim().isEmpty()) {
            Result r = new Result();
            r.setCode(400);
            r.setMsg("缺少会话ID");
            return r;
        }
        if (answer == null || answer.trim().isEmpty()) {
            Result r = new Result();
            r.setCode(400);
            r.setMsg("请提供回答内容");
            return r;
        }

        Map<String, Object> result = careerService.answerInterview(sessionId, answer.trim());

        Result r = new Result();
        if (result.containsKey("error")) {
            r.setCode(500);
            r.setMsg((String) result.get("error"));
        } else {
            r.setCode(200);
            r.setMsg("success");
            r.setData(result);
        }
        return r;
    }

    @PostMapping("/interview/end")
    public Result endInterview(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        if (sessionId != null) {
            careerService.endInterview(sessionId);
        }
        Result r = new Result();
        r.setCode(200);
        r.setMsg("面试已结束");
        return r;
    }

    @PostMapping("/interview/bank-question")
    public Result getBankQuestion(@RequestBody Map<String, String> body) {
        String interviewType = body.getOrDefault("interviewType", "campus_single");
        String jobType = body.getOrDefault("jobType", "");
        String difficulty = body.getOrDefault("difficulty", "初级");

        Map<String, Object> questionData = careerService.getBankQuestion(interviewType, jobType, difficulty);

        Result r = new Result();
        if (questionData.containsKey("error")) {
            r.setCode(500);
            r.setMsg((String) questionData.get("error"));
        } else {
            r.setCode(200);
            r.setMsg("success");
            r.setData(questionData);
        }
        return r;
    }

    // ==================== AI 兼职/工作匹配（v2.0） ====================

    @PostMapping("/job-match")
    public Result matchJobs(@RequestBody Map<String, Object> body) {
        String major = (String) body.getOrDefault("major", "");
        String skills = (String) body.getOrDefault("skills", "");

        if (major.isEmpty() && skills.isEmpty()) {
            Result r = new Result();
            r.setCode(400);
            r.setMsg("请至少填写专业或技能信息");
            return r;
        }

        Map<String, Object> matchResult = careerService.matchJobs(body);

        Result r = new Result();
        if (matchResult.containsKey("error")) {
            r.setCode(500);
            r.setMsg((String) matchResult.get("error"));
        } else {
            r.setCode(200);
            r.setMsg("匹配完成");
            r.setData(matchResult);
        }
        return r;
    }

    // ==================== 反馈与日志 ====================

    @PostMapping("/feedback")
    public Result saveFeedback(@RequestBody Map<String, Object> body) {
        String page = (String) body.getOrDefault("page", "未知");
        int rating = body.containsKey("rating") ? ((Number) body.get("rating")).intValue() : 0;
        String suggestion = (String) body.getOrDefault("suggestion", "");
        String contact = (String) body.getOrDefault("contact", "");

        Map<String, Object> fbResult = careerService.saveFeedback(page, rating, suggestion, contact);

        Result r = new Result();
        r.setCode(200);
        r.setMsg("感谢反馈！");
        r.setData(fbResult);
        return r;
    }

    @GetMapping("/changelog")
    public Result getChangelog() {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("success");
        r.setData(careerService.getChangelog());
        return r;
    }
}
