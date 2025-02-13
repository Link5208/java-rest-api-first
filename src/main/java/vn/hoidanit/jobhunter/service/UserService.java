package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final CompanyService companyService;
	private final RoleService roleService;

	public User handleCreateUser(User user) {
		if (!this.userRepository.existsByEmail(user.getEmail())) {
			if (user.getCompany() != null) {
				Company company = this.companyService.fetchCompanyByID(user.getCompany().getId());
				user.setCompany(company);
			}
			if (user.getRole() != null) {
				Role role = this.roleService.handleFetchRoleById(user.getRole().getId());
				user.setRole(role);
			}
			return this.userRepository.save(user);
		}
		return null;
	}

	public void handleDeleteUser(long id) {
		fetchUserById(id);
		this.userRepository.deleteById(id);
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

	public ResCreateUserDTO convertToResCreateUserDTO(User user) {
		ResCreateUserDTO dto = new ResCreateUserDTO();
		ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser();
		ResCreateUserDTO.RoleUser role = new ResCreateUserDTO.RoleUser();

		if (user.getRole() != null) {
			role.setId(user.getRole().getId());
			role.setName(user.getRole().getName());
			dto.setRole(role);

		}

		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setCreatedAt(user.getCreatedAt());

		if (user.getCompany() != null) {
			company.setId(user.getCompany().getId());
			company.setName(user.getCompany().getName());
			dto.setCompany(company);

		}
		return dto;
	}

	public ResUserDTO convertToResUserDTO(User user) {
		ResUserDTO dto = new ResUserDTO();
		ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
		ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();

		if (user.getRole() != null) {
			role.setId(user.getRole().getId());
			role.setName(user.getRole().getName());
			dto.setRole(role);

		}
		if (user.getCompany() != null) {
			company.setId(user.getCompany().getId());
			company.setName(user.getCompany().getName());
			dto.setCompany(company);

		}

		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setCreatedAt(user.getCreatedAt());
		dto.setUpdatedAt(user.getUpdatedAt());

		return dto;
	}

	public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
		ResUpdateUserDTO dto = new ResUpdateUserDTO();
		ResUpdateUserDTO.CompanyUser company = new ResUpdateUserDTO.CompanyUser();

		if (user.getCompany() != null) {
			company.setId(user.getCompany().getId());
			company.setName(user.getCompany().getName());
			dto.setCompany(company);

		}

		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setUpdatedAt(user.getUpdatedAt());
		ResUpdateUserDTO.RoleUser role = new ResUpdateUserDTO.RoleUser();

		if (user.getRole() != null) {
			role.setId(user.getRole().getId());
			role.setName(user.getRole().getName());
			dto.setRole(role);

		}

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
				.stream().map(user -> convertToResUserDTO(user))
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

		if (reqUser.getCompany() != null) {
			Company company = this.companyService.fetchCompanyByID(reqUser.getCompany().getId());
			currentUser.setCompany(company);
		}

		if (reqUser.getRole() != null) {
			Role role = this.roleService.handleFetchRoleById(reqUser.getRole().getId());
			currentUser.setRole(role);

		}

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
