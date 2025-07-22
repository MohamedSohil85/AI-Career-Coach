package com.aicareercoach.persistence;

import com.aicareercoach.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
}
