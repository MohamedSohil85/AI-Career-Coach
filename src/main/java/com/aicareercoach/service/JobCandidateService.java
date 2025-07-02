package com.aicareercoach.service;

import com.aicareercoach.persistence.JobCandidateRepository;
import org.springframework.stereotype.Service;

@Service
public class JobCandidateService {

private final JobCandidateRepository jobCandidateRepository;

    public JobCandidateService(JobCandidateRepository jobCandidateRepository) {
        this.jobCandidateRepository = jobCandidateRepository;
    }
}
