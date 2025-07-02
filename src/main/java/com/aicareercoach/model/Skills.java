package com.aicareercoach.model;

import com.aicareercoach.enurmation.SkillLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skills implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;
    @ManyToOne
    @JsonIgnore
    private JobCandidate jobCandidate;
}
