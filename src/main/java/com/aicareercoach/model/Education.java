package com.aicareercoach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Education implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String degree;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    private double grade;
    private String location;
    @ManyToOne
    @JsonIgnore
    private JobCandidate jobCandidate;
}
