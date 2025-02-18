package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ResumeController {
	private final ResumeService resumeService;
	private final UserService userService;
	private final JobService jobService;
	private final FilterSpecificationConverter filterSpecificationConverter;
	private final FilterBuilder filterBuilder;

	@PostMapping("/resumes")
	@ApiMessage("Create a new resume")
	public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume postmanResume)
			throws IdInvalidException {
		User user = this.userService.fetchUserById(postmanResume.getUser().getId());
		Job job = this.jobService.handleFetchJobByID(postmanResume.getJob().getId());
		if (job == null) {
			throw new IdInvalidException("Job with ID = " + postmanResume.getJob().getId() + " does not exist!");
		}
		postmanResume.setUser(user);
		postmanResume.setJob(job);
		Resume resume = this.resumeService.handleSaveResume(postmanResume);
		ResCreateResumeDTO createResumeDTO = this.resumeService.convertToResCreateResumeDTO(resume);

		return ResponseEntity.status(HttpStatus.CREATED).body(createResumeDTO);
	}

	@PutMapping("/resumes")
	@ApiMessage("Update a resume")
	public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume postmanResume)
			throws IdInvalidException {
		Resume resume = this.resumeService.handleFetchResumeByID(postmanResume.getId());
		if (resume == null) {
			throw new IdInvalidException("Resume with ID = " + postmanResume.getId() + " does not exist!");
		}
		resume.setStatus(postmanResume.getStatus());
		resume = this.resumeService.handleSaveResume(resume);
		ResUpdateResumeDTO dto = this.resumeService.convertToResUpdateResumeDTO(resume);

		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/resumes/{id}")
	@ApiMessage("Delete a resume")
	public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
		if (!this.resumeService.isResumeExistedById(id)) {
			throw new IdInvalidException("Resume with ID = " + id + " does not exist!");
		}

		this.resumeService.handleDeleteResume(id);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/resumes/{id}")
	@ApiMessage("Fetch a resume by Id")
	public ResponseEntity<ResResumeDTO> fetchResumeById(@PathVariable("id") long id) throws IdInvalidException {
		Resume resume = this.resumeService.handleFetchResumeByID(id);
		if (resume == null) {
			throw new IdInvalidException("Resume with ID = " + id + " does not exist!");
		}
		ResResumeDTO dto = this.resumeService.convertToResResumeDTO(resume);
		return ResponseEntity.ok(dto);
	}

	// @GetMapping("/resumes")
	// @ApiMessage("Fetch all resumes")
	// public ResponseEntity<ResultPaginationDTO> fetchAllResumes(
	// @Filter Specification<Resume> specification,
	// Pageable pageable) {
	// ResultPaginationDTO dto =
	// this.resumeService.handleFetchAllResumes(specification, pageable);
	// return ResponseEntity.ok(dto);
	// }

	@GetMapping("/resumes")
	@ApiMessage("Fetch all resumes")
	public ResponseEntity<ResultPaginationDTO> fetchAllResumes(
			@Filter Specification<Resume> specification,
			Pageable pageable) {

		List<Long> jobIdList = null;
		String email = SecurityUtil.getCurrentUserLogin().isPresent()
				? SecurityUtil.getCurrentUserLogin().get()
				: "";
		User currentUser = this.userService.handleGetUserByUsername(email);
		if (currentUser != null) {
			Company userCompany = currentUser.getCompany();
			if (userCompany != null) {
				List<Job> companyJobs = userCompany.getJobs();
				if (companyJobs != null && companyJobs.size() > 0) {
					jobIdList = companyJobs.stream().map(job -> job.getId()).toList();
				}
			}
		}

		Specification<Resume> jobSpecification = filterSpecificationConverter.convert(filterBuilder
				.field("job").in(filterBuilder.input(jobIdList)).get());
		Specification<Resume> finalSpecification = jobSpecification.and(specification);

		return ResponseEntity.ok(this.resumeService.handleFetchAllResumes(finalSpecification, pageable));
	}

	@PostMapping("/resumes/by-user")
	@ApiMessage("Get list resumes by user")
	public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
		return ResponseEntity.ok(this.resumeService.fetchResumeByUser(pageable));
	}

}
