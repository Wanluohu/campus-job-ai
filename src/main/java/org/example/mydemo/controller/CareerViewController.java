package org.example.mydemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/career")
public class CareerViewController {

    @GetMapping("/index")
    public String index() {
        return "career/index";
    }

    @GetMapping("/resume")
    public String resume() {
        return "career/resume";
    }

    @GetMapping("/interview")
    public String interview() {
        return "career/interview";
    }

    @GetMapping("/jobmatch")
    public String jobmatch() {
        return "career/jobmatch";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "career/feedback";
    }

    @GetMapping("/changelog")
    public String changelog() {
        return "career/changelog";
    }
}
