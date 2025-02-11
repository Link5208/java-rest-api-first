package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
	private CompanyService companyService;

	/**
	 * @param companyService
	 * @param userService
	 */
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;

	}

	@PostMapping("/companies")
	@ApiMessage("Create a new company")
	public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company postmanCompany)
			throws Exception {

		Company newCompany = this.companyService.handleSaveCompany(postmanCompany);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
	}

	@GetMapping("/companies")
	@ApiMessage("Fetch companies")
	public ResponseEntity<ResultPaginationDTO> getAllCompanies(
			@Filter Specification<Company> specification,
			Pageable pageable) {

		return ResponseEntity.ok().body(this.companyService.fetchAllCompanies(specification, pageable));
	}

	@PutMapping("/companies")
	@ApiMessage("Update a company")
	public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company postmanCompany) {
		Company updatedCompany = this.companyService.handleUpdateCompany(postmanCompany);

		return ResponseEntity.ok(updatedCompany);
	}

	@DeleteMapping("/companies/{id}")
	@ApiMessage("Delete a company")
	public ResponseEntity<Void> deleleCompany(@PathVariable("id") long id) {

		this.companyService.deleleCompanyByID(id);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/companies/{id}")
	@ApiMessage("Fetch a company by Id")
	public ResponseEntity<ResCompanyDTO> fetchCompanyById(
			@PathVariable("id") long id) throws IdInvalidException {
		Company company = this.companyService.fetchCompanyByID(id);
		if (company == null) {
			throw new IdInvalidException("Company with ID = " + id + " does not exist!");
		}

		return ResponseEntity.ok(this.companyService.convertToResCompanyDTO(company));
	}

}
