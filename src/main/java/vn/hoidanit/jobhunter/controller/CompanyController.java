package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<ResultPaginationDTO> getAllCompanies(
			@RequestParam("current") Optional<String> currentOptional,
			@RequestParam("pageSize") Optional<String> pageSizeOptional) {

		String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
		String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
		int current = Integer.parseInt(sCurrent);
		int pageSize = Integer.parseInt(sPageSize);
		Pageable pageable = PageRequest.of(current - 1, pageSize);
		// List<Company> companies = this.companyService.fetchAllCompanies();
		ResultPaginationDTO result = this.companyService.fetchAllCompanies(pageable);
		return ResponseEntity.ok().body(result);
	}

	@PutMapping("/companies")
	public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company postmanCompany) {
		Company updatedCompany = this.companyService.handleUpdateCompany(postmanCompany);

		return ResponseEntity.ok(updatedCompany);
	}

	@DeleteMapping("/companies/{id}")
	public ResponseEntity<Void> deleleCompany(@PathVariable("id") long id) throws IdInvalidException {
		if (id >= 1500) {
			throw new IdInvalidException("ID is not bigger than 1500");
		}
		this.companyService.deleleCompanyByID(id);
		return ResponseEntity.ok(null);
	}

}
