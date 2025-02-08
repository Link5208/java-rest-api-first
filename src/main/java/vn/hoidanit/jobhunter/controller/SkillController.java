package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class SkillController {
	private final SkillService skillService;

	@PostMapping("/skills")
	@ApiMessage("Create a new skill")
	public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill postmanSkill)
			throws IdInvalidException {
		if (this.skillService.isSkillNameExisted(postmanSkill)) {
			throw new IdInvalidException("Skill named " + postmanSkill.getName() + " existed!");
		}
		Skill skill = this.skillService.handleSaveSkill(postmanSkill);

		return ResponseEntity.status(HttpStatus.CREATED).body(skill);
	}

	@PutMapping("/skills")
	@ApiMessage("Update a skill")
	public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill postmanSkill)
			throws IdInvalidException {
		if (this.skillService.isSkillNameExisted(postmanSkill)) {
			throw new IdInvalidException("Skill named " + postmanSkill.getName() + " existed!");
		}
		Skill skill = this.skillService.fetchSkillByID(postmanSkill.getId());
		if (skill == null) {
			throw new IdInvalidException("Skill with ID = " + postmanSkill.getId() + " does not exist!");
		}

		skill.setName(postmanSkill.getName());
		this.skillService.handleSaveSkill(skill);
		return ResponseEntity.ok(skill);
	}

	@GetMapping("/skills")
	public ResponseEntity<ResultPaginationDTO> getAllSkills(
			@Filter Specification<Skill> specification,
			Pageable pageable) {

		ResultPaginationDTO dto = this.skillService.handleFetchAllSkills(specification, pageable);
		return ResponseEntity.ok(dto);
	}

}
