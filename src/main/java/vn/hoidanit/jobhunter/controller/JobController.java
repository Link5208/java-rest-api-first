package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class JobController {
	private final JobService jobService;

	@PostMapping("/jobs")
	@ApiMessage("Create a new job")
	public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job postmanJob) {
		ResCreateJobDTO createJobDTO = this.jobService.handleCreateNewJob(postmanJob);

		return ResponseEntity.status(HttpStatus.CREATED).body(createJobDTO);
	}

	@PutMapping("/jobs")
	@ApiMessage("Update a job")
	public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job postmanJob)
			throws IdInvalidException {
		// TODO: process PUT request
		ResUpdateJobDTO updateJobDTO = this.jobService.handleUpdateJob(postmanJob);
		if (updateJobDTO == null) {
			throw new IdInvalidException("Job with ID = " + postmanJob.getId() + " does not exist");
		}
		return ResponseEntity.ok(updateJobDTO);
	}

	@DeleteMapping("/jobs/{id}")
	@ApiMessage("Delete a job")
	public ResponseEntity<Void> deleteJob(@PathVariable("id") long id)
			throws IdInvalidException {
		if (!this.jobService.isJobExistedById(id)) {
			throw new IdInvalidException("Job with ID = " + id + " does not exist");
		}
		this.jobService.handleDeleteJob(id);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/jobs/{id}")
	@ApiMessage("Fetch job by ID")
	public ResponseEntity<Job> fetchJobByID(@PathVariable("id") long id)
			throws IdInvalidException {
		Job job = this.jobService.handleFetchJobByID(id);
		if (job == null) {
			throw new IdInvalidException("Job with ID = " + id + " does not exist");
		}
		return ResponseEntity.ok(job);
	}

	@GetMapping("/jobs")
	@ApiMessage("Fetch all jobs")
	public ResponseEntity<ResultPaginationDTO> fetchAllJobs(
			@Filter Specification<Job> specification,
			Pageable pageable) {
		ResultPaginationDTO dto = this.jobService.handleFetchAllJobs(specification, pageable);
		return ResponseEntity.ok(dto);
	}

}
