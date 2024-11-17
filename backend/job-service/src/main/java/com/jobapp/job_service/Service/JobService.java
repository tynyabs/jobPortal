package com.jobapp.job_service.Service;

import com.jobapp.job_service.DTO.JobRequest;
import com.jobapp.job_service.Model.Job;
import com.jobapp.job_service.Repository.JobRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobService {

    @Autowired
    private final JobRepo jobRepository;

    public void createJob(JobRequest jobRequest) {
        Job job= Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .location(jobRequest.getLocation())
                .company(jobRequest.getCompany())
                .employmentType(jobRequest.getEmploymentType())
                .salary(jobRequest.getSalary())
                .build();

        // Save the job entity to the database
        Job savedJob = jobRepository.save(job);
        log.info("Job {} is saved", savedJob.getId());
    }

    public void updateJob(Long id,JobRequest jobRequest) {
        Job job = jobRepository.findJobById(id);

        if (job != null) { // Check if the job exists
            if (jobRequest.getTitle() != null) {
                job.setTitle(jobRequest.getTitle());
            }
            if (jobRequest.getDescription() != null) {
                job.setDescription(jobRequest.getDescription());
            }
            if (jobRequest.getLocation() != null) {
                job.setLocation(jobRequest.getLocation());
            }
            if (jobRequest.getCompany() != null) {
                job.setCompany(jobRequest.getCompany());
            }
            if (jobRequest.getEmploymentType() != null) {
                job.setEmploymentType(jobRequest.getEmploymentType());
            }
            if (jobRequest.getSalary() >= 0) {
                job.setSalary(jobRequest.getSalary());
            }
            jobRepository.save(job);
        }
    }
    public List<Job> searchJobs(String query) {
        return jobRepository.searchJobs(query);
    }
    public List<Job> searchJobBySalary(double salary) {
        return jobRepository.searchJobBySalary(salary);
    }

}

