package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
@AllArgsConstructor
public class SkillService {
	private final SkillRepository skillRepository;

	public Skill handleSaveSkill(Skill skill) {
		return this.skillRepository.save(skill);
	}

	public boolean isSkillNameExisted(Skill skill) {
		return this.skillRepository.existsByName(skill.getName());
	}

	public Skill fetchSkillByID(long id) {
		Optional<Skill> skillOptional = this.skillRepository.findById(id);
		return skillOptional.isPresent() ? skillOptional.get() : null;
	}

	public ResultPaginationDTO handleFetchAllSkills(Specification<Skill> specification, Pageable pageable) {
		Page<Skill> page = this.skillRepository.findAll(specification, pageable);
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

	public void handleDeleteSkill(long id) {

		Skill skill = fetchSkillByID(id);
		skill.getJobs().forEach(job -> job.getSkills().remove(skill));
		skill.getSubscribers().forEach(sub -> sub.getSkills().remove(skill));
		this.skillRepository.delete(skill);
	}
}
