package com.aicareercoach.persistence;

import com.aicareercoach.model.JobCandidate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCandidateRepository extends CrudRepository<JobCandidate, Long> {
}
