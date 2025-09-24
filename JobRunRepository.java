package com.example.demo.repo;

import com.example.demo.model.JobRun;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRunRepository extends JpaRepository<JobRun, Long> {

    @Query("SELECT j FROM JobRun j WHERE j.status = :status ORDER BY j.startedAt DESC")
    List<JobRun> findByStatus(@Param("status") String status);

}
