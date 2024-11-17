package com.jobapp.job_service.Repository;

import com.jobapp.job_service.Model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface JobRepo extends JpaRepository<Job, Long> {
    Job findJobById(Long id);

    // Or using a custom JPQL query
    @Query("SELECT j FROM Job j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.location) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.company) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.employmentType) LIKE LOWER(CONCAT('%', :query, '%')) " )
    List<Job> searchJobs(@Param("query") String query);

    @Query("SELECT j FROM Job j WHERE j.salary = :salary")
    List<Job> searchJobBySalary(@Param("salary") double salary);
}