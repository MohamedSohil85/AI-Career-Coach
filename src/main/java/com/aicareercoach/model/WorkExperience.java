package com.aicareercoach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperience implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    @ManyToOne
    @JsonIgnore
    private JobCandidate jobCandidate;
}
