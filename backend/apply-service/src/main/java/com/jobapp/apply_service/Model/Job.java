package com.jobapp.job_service.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String location;
    private String company;
    private String employmentType; // e.g., Full-Time, Part-Time, Contract
    private double salary;

    // toString method for easy logging
    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", company='" + company + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", salary=" + salary +
                '}';
    }
}
