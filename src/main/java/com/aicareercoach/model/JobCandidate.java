package com.aicareercoach.model;

import com.aicareercoach.enurmation.CareerLevel;
import com.aicareercoach.enurmation.EmploymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JobCandidate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long candidateId;
    private String lastName;
    private String firstName;
    private String candidateAddress;
    private String github;
    private String role;
    private String phoneNumber;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    @Column(columnDefinition = "TEXT",length = 1000)
    private String jobDescription;
    @Enumerated(EnumType.STRING)
    private CareerLevel careerLevel;
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;
    @OneToMany(targetEntity = Skills.class, fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "candidateId",referencedColumnName = "candidateId")
    private List<Skills> skills;
    @OneToMany(targetEntity = Education.class, fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "candidateId")
    private List<Education> levelOfEducation;
    @OneToMany(targetEntity = WorkExperience.class, fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "candidateId",referencedColumnName = "candidateId")
    private List<WorkExperience> workExperience;
    @OneToMany(targetEntity = Language.class, fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "candidateId",referencedColumnName = "candidateId")
    private List<Language> languages;
    private String softSkills;
    private String interests;
    private String location;
    @OneToMany(targetEntity = Certification.class, fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "candidateId",referencedColumnName = "candidateId")
    private List<Certification> certifications;
}
