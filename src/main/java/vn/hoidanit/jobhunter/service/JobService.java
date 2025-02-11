package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
@AllArgsConstructor
public class JobService {
	private final JobRepository jobRepository;
	private final SkillRepository skillRepository;
	private final CompanyRepository companyRepository;

	public ResCreateJobDTO handleCreateNewJob(Job postmanJob) {

		if (postmanJob.getSkills() != null) {
			List<Long> ids = postmanJob.getSkills()
					.stream().map(x -> x.getId())
					.collect(Collectors.toList());
			List<Skill> skills = this.skillRepository.findByIdIn(ids);
			postmanJob.setSkills(skills);
		}

		if (postmanJob.getCompany() != null) {
			Optional<Company> optional = this.companyRepository.findById(postmanJob.getCompany().getId());
			if (optional.isPresent()) {
				postmanJob.setCompany(optional.get());
			}
		}

		Job job = this.jobRepository.save(postmanJob);
		ResCreateJobDTO createJobDTO = convertToResCreateJobDTO(job);

		return createJobDTO;
	}

	public ResUpdateJobDTO handleUpdateJob(Job postmanJob) {
		Optional<Job> jobOptional = this.jobRepository.findById(postmanJob.getId());
		if (!jobOptional.isPresent()) {
			return null;
		}
		Job updatedJob = jobOptional.get();
		if (postmanJob.getCompany() != null) {
			Optional<Company> optional = this.companyRepository.findById(postmanJob.getCompany().getId());
			if (optional.isPresent()) {
				updatedJob.setCompany(postmanJob.getCompany());
			}
		}
		updatedJob.setName(postmanJob.getName());
		updatedJob.setLocation(postmanJob.getLocation());
		updatedJob.setSalary(postmanJob.getSalary());
		updatedJob.setQuantity(postmanJob.getQuantity());
		updatedJob.setLevel(postmanJob.getLevel());
		updatedJob.setDescription(postmanJob.getDescription());
		updatedJob.setStartDate(postmanJob.getStartDate());
		updatedJob.setEndDate(postmanJob.getEndDate());
		updatedJob.setActive(postmanJob.isActive());

		if (postmanJob.getSkills() != null) {
			List<Long> ids = postmanJob.getSkills()
					.stream().map(skill -> skill.getId()).toList();
			List<Skill> skills = this.skillRepository.findByIdIn(ids);
			updatedJob.setSkills(skills);
		}
		this.jobRepository.save(updatedJob);
		return this.converToResUpdateJobDTO(updatedJob);
	}

	public ResCreateJobDTO convertToResCreateJobDTO(Job job) {
		ResCreateJobDTO jobDTO = new ResCreateJobDTO();
		jobDTO.setName(job.getName());
		jobDTO.setLocation(job.getLocation());
		jobDTO.setSalary(job.getSalary());
		jobDTO.setQuantity(job.getQuantity());
		jobDTO.setLevel(job.getLevel());
		jobDTO.setDescription(job.getDescription());
		jobDTO.setStartDate(job.getStartDate());
		jobDTO.setEndDate(job.getEndDate());
		jobDTO.setActive(job.isActive());
		jobDTO.setCreatedAt(job.getCreatedAt());
		jobDTO.setCreatedBy(job.getCreatedBy());
		jobDTO.setId(job.getId());

		if (job.getSkills() != null) {
			List<String> jobSkills = job.getSkills()
					.stream().map(skill -> skill.getName())
					.collect(Collectors.toList());
			jobDTO.setSkills(jobSkills);
		}
		return jobDTO;
	}

	public ResUpdateJobDTO converToResUpdateJobDTO(Job job) {
		ResUpdateJobDTO jobDTO = new ResUpdateJobDTO();
		jobDTO.setName(job.getName());
		jobDTO.setLocation(job.getLocation());
		jobDTO.setSalary(job.getSalary());
		jobDTO.setQuantity(job.getQuantity());
		jobDTO.setLevel(job.getLevel());
		jobDTO.setDescription(job.getDescription());
		jobDTO.setStartDate(job.getStartDate());
		jobDTO.setEndDate(job.getEndDate());
		jobDTO.setActive(job.isActive());
		jobDTO.setUpdatedAt(job.getUpdatedAt());
		jobDTO.setUpdatedBy(job.getUpdatedBy());
		jobDTO.setId(job.getId());

		List<String> jobSkills = job.getSkills()
				.stream().map(skill -> skill.getName()).toList();

		jobDTO.setSkills(jobSkills);
		return jobDTO;
	}

	public void handleDeleteJob(long id) {
		this.jobRepository.deleteById(id);
	}

	public boolean isJobExistedById(long id) {
		return this.jobRepository.existsById(id);
	}

	public Job handleFetchJobByID(long id) {
		Optional<Job> optional = this.jobRepository.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

	public ResultPaginationDTO handleFetchAllJobs(Specification<Job> specification, Pageable pageable) {
		Page<Job> page = this.jobRepository.findAll(specification, pageable);
		ResultPaginationDTO result = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(page.getNumber() + 1);
		meta.setPageSize(page.getSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		result.setMeta(meta);
		result.setResult(page.getContent());
		return result;
	}

}