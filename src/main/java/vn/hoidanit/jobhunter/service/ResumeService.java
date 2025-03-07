package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Service
@AllArgsConstructor
public class ResumeService {
	private final ResumeRepository resumeRepository;
	private final FilterParser filterParser;
	private final FilterSpecificationConverter filterSpecificationConverter;

	public Resume handleSaveResume(Resume resume) {
		return this.resumeRepository.save(resume);
	}

	public ResCreateResumeDTO convertToResCreateResumeDTO(Resume resume) {
		return new ResCreateResumeDTO(resume.getId(), resume.getCreatedAt(), resume.getCreatedBy());
	}

	public Resume handleFetchResumeByID(long id) {
		Optional<Resume> optional = this.resumeRepository.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

	public ResUpdateResumeDTO convertToResUpdateResumeDTO(Resume resume) {
		return new ResUpdateResumeDTO(resume.getUpdatedAt(), resume.getUpdatedBy());
	}

	public boolean isResumeExistedById(long id) {
		return this.resumeRepository.existsById(id);
	}

	public void handleDeleteResume(long id) {
		this.resumeRepository.deleteById(id);
	}

	public ResResumeDTO convertToResResumeDTO(Resume resume) {
		return new ResResumeDTO(
				resume.getId(),
				resume.getEmail(),
				resume.getUrl(),
				resume.getStatus(),
				resume.getCreatedAt(),
				resume.getUpdatedAt(),
				resume.getCreatedBy(),
				resume.getUpdatedBy(),
				resume.getJob() != null ? resume.getJob().getCompany().getName() : null,
				new ResResumeDTO.ResumeUser(resume.getUser().getId(), resume.getUser().getName()),
				new ResResumeDTO.ResumeJob(resume.getJob().getId(), resume.getJob().getName()));
	}

	public ResultPaginationDTO handleFetchAllResumes(Specification<Resume> specification, Pageable pageable) {
		Page<Resume> page = this.resumeRepository.findAll(specification, pageable);
		ResultPaginationDTO dto = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(page.getNumber() + 1);
		meta.setPageSize(page.getSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		dto.setMeta(meta);
		List<ResResumeDTO> list = page.getContent().stream().map(resume -> convertToResResumeDTO(resume)).toList();

		dto.setResult(list);
		return dto;
	}

	public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
		String email = SecurityUtil.getCurrentUserLogin().isPresent()
				? SecurityUtil.getCurrentUserLogin().get()
				: "";

		FilterNode node = filterParser.parse("email='" + email + "'");
		FilterSpecification<Resume> specification = filterSpecificationConverter.convert(node);
		Page<Resume> page = this.resumeRepository.findAll(specification, pageable);

		ResultPaginationDTO dto = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(page.getNumber() + 1);
		meta.setPageSize(page.getSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		dto.setMeta(meta);
		List<ResResumeDTO> list = page.getContent().stream().map(resume -> convertToResResumeDTO(resume)).toList();

		dto.setResult(list);
		return dto;
	}
}
