package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.service.CompanyService;
import org.springframework.web.bind.annotation.RequestBody;

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

		Company newCompany = this.companyService.handleCreateCompany(postmanCompany);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
	}

}
