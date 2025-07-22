package com.aicareercoach.resource;

import com.aicareercoach.model.JobCandidate;
import com.aicareercoach.service.JobCandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class JobCandidateResource {

    private final JobCandidateService jobCandidateService;

    public JobCandidateResource(JobCandidateService jobCandidateService) {
        this.jobCandidateService = jobCandidateService;
    }

    public ResponseEntity<JobCandidate> saveJobCandidate(JobCandidate jobCandidate) {
        return new ResponseEntity<>(jobCandidateService.saveNewCandidate(jobCandidate), HttpStatus.CREATED);
    }
}
