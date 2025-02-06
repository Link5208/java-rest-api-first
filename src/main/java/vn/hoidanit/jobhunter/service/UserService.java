package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCompanyForUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final CompanyService companyService;

	public UserService(UserRepository userRepository, CompanyService companyService) {
		this.userRepository = userRepository;
		this.companyService = companyService;
	}

	public User handleCreateUser(User user) {
		if (!this.userRepository.existsByEmail(user.getEmail()))
			return this.userRepository.save(user);
		else
			return null;
	}

	public void handleDeleteUser(long id) {
		fetchUserById(id);
		this.userRepository.deleteById(id);
	}

	public void handleDeleteUserByCompanyID(long id) {
		Company company = this.companyService.fetchCompanyByID(id);
		List<User> users = this.userRepository.findByCompany(company);
		this.userRepository.deleteAllByCompany(company);
		
	}

	public User fetchUserById(long id) throws UsernameNotFoundException {
		Optional<User> userOptional = this.userRepository.findById(id);
		if (userOptional.isPresent()) {
			return userOptional.get();
		} else {
			throw new UsernameNotFoundException("There is not any user having ID = " + id);
		}
	}

	public boolean isEmailExist(String email) {
		return this.userRepository.existsByEmail(email);
	}

	public ResCreateUserDTO convertToResCreateUserDTO(User user, Company company) {
		ResCreateUserDTO dto = new ResCreateUserDTO();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setCreatedAt(user.getCreatedAt());

		dto.setCompany(this.companyService.convertToResCompanyDTO(company));

		return dto;
	}

	public ResUserDTO convertToResUserDTO(User user) {
		ResUserDTO dto = new ResUserDTO();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setCreatedAt(user.getCreatedAt());
		dto.setUpdatedAt(user.getUpdatedAt());
		dto.setCompany(new ResCompanyForUserDTO(user.getCompany().getId(),
				user.getCompany().getName()));
		return dto;
	}

	public ResUpdateUserDTO convertToResUpdateUserDTO(User user, Company company) {
		ResUpdateUserDTO dto = new ResUpdateUserDTO();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setUpdatedAt(user.getUpdatedAt());
		dto.setCompany(this.companyService.convertToResCompanyDTO(company));
		return dto;
	}

	public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable) {
		Page<User> page = this.userRepository.findAll(specification, pageable);
		ResultPaginationDTO result = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(pageable.getPageNumber() + 1);
		meta.setPageSize(pageable.getPageSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		result.setMeta(meta);

		List<ResUserDTO> dtos = page.getContent()
				.stream().map(user -> new ResUserDTO(
						user.getId(),
						user.getName(),
						user.getEmail(),
						user.getGender(),
						user.getAddress(),
						user.getAge(),
						user.getCreatedAt(),
						user.getUpdatedAt(),
						this.companyService.convertToResCompanyDTO(user.getCompany())))
				.collect(Collectors.toList());

		result.setResult(dtos);

		return result;
	}

	public User handleUpdateUser(User reqUser) {
		User currentUser = fetchUserById(reqUser.getId());
		currentUser.setName(reqUser.getName());
		currentUser.setGender(reqUser.getGender());
		currentUser.setAddress(reqUser.getAddress());
		currentUser.setAge(reqUser.getAge());
		currentUser.setCompany(reqUser.getCompany());

		// update
		currentUser = this.userRepository.save(currentUser);
		return currentUser;

	}

	public User handleGetUserByUsername(String username) {
		return this.userRepository.findByEmail(username);
	}

	public void updateUserToken(String token, String email) {
		User currentUser = this.handleGetUserByUsername(email);
		if (currentUser != null) {
			currentUser.setRefreshToken(token);
			this.userRepository.save(currentUser);
		}
	}

	public User getUserByRefreshTokenAndEmail(String token, String email) {
		return this.userRepository.findByRefreshTokenAndEmail(token, email);
	}
}
