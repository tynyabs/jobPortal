package com.jobapp.job_service.Controller;

import com.jobapp.job_service.DTO.JobRequest;
import com.jobapp.job_service.Model.Job;
import com.jobapp.job_service.Repository.JobRepo;
import com.jobapp.job_service.Service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobRepo jobRepository;
    private final JobService jobService;

    // Create a new job
    @PostMapping("/createJob")
    public ResponseEntity<Object> createJob(@RequestBody JobRequest jobRequest) {
        try {
            jobService.createJob(jobRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all jobs
    @GetMapping("/getAllJobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        try {
            List<Job> jobs = jobRepository.findAll();
            if (jobs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(jobs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a job by ID
    @GetMapping("/getJob/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") Long id) {
        Optional<Job> jobData = jobRepository.findById(id);
        return jobData.map(job -> new ResponseEntity<>(job, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a job by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable("id") Long id, @RequestBody JobRequest jobRequest) {
        try {
            jobService.updateJob(id, jobRequest);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Delete a job by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteJob(@PathVariable("id") Long id) {
        try {
            jobRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete all jobs
    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteAllJobs() {
        try {
            jobRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.GONE);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 for invalid query
        }

        try {
            List<Job> jobs = jobService.searchJobs(query);
            if (jobs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 if no jobs found
            }
            return new ResponseEntity<>(jobs, HttpStatus.OK); // Return 200 with job list
        } catch (Exception e) {
            // Log the exception for debugging purposes
            // logger.error("Error occurred while searching for jobs", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Return 500 for server error
        }
    }
    @GetMapping("/searchBySalary")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam("salary") double salary) {
        if (salary < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 for invalid query
        }

        try {
            List<Job> jobs = jobService.searchJobBySalary(salary);
            if (jobs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 if no jobs found
            }
            return new ResponseEntity<>(jobs, HttpStatus.OK); // Return 200 with job list
        } catch (Exception e) {
            // Log the exception for debugging purposes
            // logger.error("Error occurred while searching for jobs", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Return 500 for server error
        }
    }
}

