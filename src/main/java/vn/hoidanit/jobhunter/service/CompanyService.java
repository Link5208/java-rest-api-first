package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
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

	public ResultPaginationDTO fetchAllCompanies(Pageable pageable) {
		Page<Company> page = this.companyRepository.findAll(pageable);
		ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
		Meta meta = new Meta();

		meta.setPage(page.getNumber());
		meta.setPageSize(page.getSize());

		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		resultPaginationDTO.setMeta(meta);
		resultPaginationDTO.setResult(page.getContent());
		return resultPaginationDTO;
	}

	public Company fetchCompanyByID(long id) {
		Optional<Company> optionalCompany = this.companyRepository.findById(id);

		return optionalCompany.isPresent() ? optionalCompany.get() : null;
	}

	public void deleleCompanyByID(long id) {
		this.companyRepository.deleteById(id);
	}

	public Company handleUpdateCompany(Company postmanCompany) {
		Company updatedCompany = fetchCompanyByID(postmanCompany.getId());
		if (updatedCompany == null) {
			return null;
		}
		updatedCompany.setName(postmanCompany.getName());
		updatedCompany.setDescription(postmanCompany.getDescription());
		updatedCompany.setAddress(postmanCompany.getAddress());
		updatedCompany.setLogo(postmanCompany.getLogo());
		updatedCompany.handleBeforeUpdate();
		handleSaveCompany(updatedCompany);

		return updatedCompany;
	}
}
