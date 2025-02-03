package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
	private final CompanyRepository companyRepository;

	/**
	 * @param companyRepository
	 */
	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	public Company handleSaveCompany(Company company) {
		return this.companyRepository.save(company);
	}

	public List<Company> fetchAllCompanies() {
		return this.companyRepository.findAll();
	}

	public Company fetchCompanyByID(long id) {
		Optional<Company> optionalCompany = this.companyRepository.findById(id);

		return optionalCompany.isPresent() ? optionalCompany.get() : null;
	}

	public void deleleCompanyByID(long id) {
		this.companyRepository.deleteById(id);
	}
}
