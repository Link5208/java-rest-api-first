package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;

	/**
	 * @param companyRepository
	 * @param userRepository
	 */
	public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
		this.companyRepository = companyRepository;
		this.userRepository = userRepository;
	}

	public Company handleSaveCompany(Company company) {
		return this.companyRepository.save(company);
	}

	public List<Company> fetchAllCompanies() {
		return this.companyRepository.findAll();
	}

	public ResultPaginationDTO fetchAllCompanies(Specification<Company> specification, Pageable pageable) {
		Page<Company> page = this.companyRepository.findAll(specification, pageable);
		ResultPaginationDTO result = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(pageable.getPageNumber() + 1);
		meta.setPageSize(pageable.getPageSize());

		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		result.setMeta(meta);
		result.setResult(page.getContent());
		return result;
	}

	public Company fetchCompanyByID(long id) {
		Optional<Company> optionalCompany = this.companyRepository.findById(id);

		return optionalCompany.isPresent() ? optionalCompany.get() : null;
	}

	public void deleleCompanyByID(long id) {
		Optional<Company> companyOptional = this.companyRepository.findById(id);
		if (companyOptional.isPresent()) {
			Company company = companyOptional.get();
			List<User> users = this.userRepository.findByCompany(company);
			this.userRepository.deleteAll(users);
		}
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

		handleSaveCompany(updatedCompany);

		return updatedCompany;
	}

	public ResCompanyDTO convertToResCompanyDTO(Company company) {
		return new ResCompanyDTO(
				company.getId(),
				company.getName(),
				company.getDescription(),
				company.getAddress(),
				company.getLogo(),
				company.getCreatedAt(),
				company.getUpdatedAt(),
				company.getCreatedBy(),
				company.getUpdatedBy());
	}

}
