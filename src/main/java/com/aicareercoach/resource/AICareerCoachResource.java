package com.aicareercoach.resource;

import com.aicareercoach.service.AICareerCoachService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/ai-career-coach")
public class AICareerCoachResource {


    private final AICareerCoachService aicareerCoachService;


    public AICareerCoachResource(AICareerCoachService aicareerCoachService) {
        this.aicareerCoachService = aicareerCoachService;
    }

    @PostMapping(value = "/check-cv")
    public ResponseEntity<String> checkCV(@RequestParam("resume") MultipartFile resume) {
        return new ResponseEntity<>(aicareerCoachService.careerCoach(resume), HttpStatus.OK);
    }
    @PostMapping(value = "/generateCoverLetter")
    public ResponseEntity<String> generateCoverLetter(@RequestParam("resume") MultipartFile resume,@RequestParam("role") String role,@RequestParam("jobDescription") String jobDescription,@RequestParam("companyName") String companyName,@RequestParam("place") String place) throws IOException {
        return new ResponseEntity<>(aicareerCoachService.generateCoverLetter(resume, role, jobDescription, companyName, place),HttpStatus.OK);
    }
}
