package com.jobapp.job_service.DTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class JobRequest {
    private String title;
    private String description;
    private String location;
    private String company;
    private String employmentType; // e.g., Full-Time, Part-Time, Contract
    private double salary;
}
