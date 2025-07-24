package com.aicareercoach.resource;

import com.aicareercoach.model.JobCandidate;
import com.aicareercoach.service.JobCandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class JobCandidateResource {

    private final JobCandidateService jobCandidateService;

    public JobCandidateResource(JobCandidateService jobCandidateService) {
        this.jobCandidateService = jobCandidateService;
    }
   @PostMapping(value = "/candidate",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobCandidate> saveJobCandidate(@RequestBody JobCandidate jobCandidate) {
        return new ResponseEntity<>(jobCandidateService.saveNewCandidate(jobCandidate), HttpStatus.CREATED);
    }
    @PostMapping(value = "/quiz")
    public ResponseEntity<String> generateQuiz(@RequestParam("skill")String skill ,@RequestParam("difficulty")String difficulty) throws IOException {
        return new ResponseEntity<>(jobCandidateService.generateSkillsQuiz(skill, difficulty),HttpStatus.OK);
    }
    @PostMapping(value = "/resume/{id}")
    public ResponseEntity<String> resumeJobCandidate(@PathVariable("id")Long id) throws IOException {
        return new ResponseEntity<>(jobCandidateService.generateCV(id),HttpStatus.OK);
    }
}
