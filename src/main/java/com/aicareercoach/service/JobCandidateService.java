package com.aicareercoach.service;

import com.aicareercoach.model.JobCandidate;
import com.aicareercoach.persistence.*;
import org.springframework.stereotype.Service;

@Service
public class JobCandidateService {

private final JobCandidateRepository jobCandidateRepository;
private final WorkExperienceRepository workexperienceRepository;
private final CertificationRepository CertificationRepository;
private final SkillsRepository skillsRepository;
private final EducationRepository educationRepository;
private final LanguageRepository languageRepository;

    public JobCandidateService(JobCandidateRepository jobCandidateRepository, WorkExperienceRepository workexperienceRepository, com.aicareercoach.persistence.CertificationRepository certificationRepository, SkillsRepository skillsRepository, EducationRepository educationRepository, LanguageRepository languageRepository) {
        this.jobCandidateRepository = jobCandidateRepository;
        this.workexperienceRepository = workexperienceRepository;
        CertificationRepository = certificationRepository;
        this.skillsRepository = skillsRepository;
        this.educationRepository = educationRepository;
        this.languageRepository = languageRepository;
    }
    public JobCandidate saveNewCandidate(JobCandidate jobCandidate) {

        var skills = jobCandidate.getSkills();
        var languages = jobCandidate.getLanguages();
        var workExperiences = jobCandidate.getWorkExperience();
        var certifications = jobCandidate.getCertifications();
        var educations=jobCandidate.getLevelOfEducation();

        jobCandidate.setSkills(skills);
        jobCandidate.setLanguages(languages);
        jobCandidate.setWorkExperience(workExperiences);
        jobCandidate.setCertifications(certifications);
        jobCandidate.setLevelOfEducation(educations);

        skillsRepository.saveAll(skills);
        languageRepository.saveAll(languages);
        workexperienceRepository.saveAll(workExperiences);
        CertificationRepository.saveAll(certifications);
        educationRepository.saveAll(educations);


        return jobCandidateRepository.save(jobCandidate);
    }


}
