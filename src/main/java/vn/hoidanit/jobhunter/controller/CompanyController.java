package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class CompanyController {
	private CompanyService companyService;

	/**
	 * @param companyService
	 */
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@PostMapping("/companies")
	public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company postmanCompany)
			throws Exception {

		Company newCompany = this.companyService.handleSaveCompany(postmanCompany);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
	}

	@GetMapping("/companies")
	public ResponseEntity<List<Company>> fetchAllCompanies() {
		return ResponseEntity.ok().body(this.companyService.fetchAllCompanies());
	}

	@PutMapping("/companies")
	public ResponseEntity<Company> updateCompany(@RequestBody Company postmanCompany) {
		Company updatedCompany = this.companyService.fetchCompanyByID(postmanCompany.getId());

		updatedCompany.setName(postmanCompany.getName());
		updatedCompany.setDescription(postmanCompany.getDescription());
		updatedCompany.setAddress(postmanCompany.getAddress());
		updatedCompany.setLogo(postmanCompany.getLogo());
		updatedCompany.handleBeforeUpdate();
		this.companyService.handleSaveCompany(updatedCompany);

		return ResponseEntity.ok().body(updatedCompany);
	}

	@DeleteMapping("/companies/{id}")
	public ResponseEntity<Void> deleleCompany(@PathVariable("id") long id) throws IdInvalidException {
		if (id >= 1500) {
			throw new IdInvalidException("ID is not bigger than 1500");
		}
		this.companyService.deleleCompanyByID(id);
		return ResponseEntity.ok().body(null);
	}

}
