package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final CompanyService companyService;

	/**
	 * @param userService
	 * @param passwordEncoder
	 * @param companyService
	 */
	public UserController(UserService userService, PasswordEncoder passwordEncoder, CompanyService companyService) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.companyService = companyService;
	}

	@PostMapping("/users")
	@ApiMessage("Create a new user")
	public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser)
			throws IdInvalidException {

		boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
		if (isEmailExist) {
			throw new IdInvalidException("Email " + postManUser.getEmail() + " was not existed!!!");
		}

		Company company = this.companyService.fetchCompanyByID(postManUser.getCompany().getId());
		if (company == null) {
			throw new IdInvalidException("Company with ID = " + postManUser.getCompany().getId() + " was not existed!!!");

		}

		String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
		postManUser.setPassword(hashPassword);

		User newUser = this.userService.handleCreateUser(postManUser);

		return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser, company));
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
		this.userService.handleDeleteUser(id);
		return ResponseEntity.ok(null);
		// return ResponseEntity.status(HttpStatus.OK).body("ericUser");
	}

	// fetch user by id
	@GetMapping("/users/{id}")
	@ApiMessage("Delete an user")
	public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
		User fetchUser = this.userService.fetchUserById(id);

		return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
	}

	// fetch all users
	@GetMapping("/users")
	@ApiMessage("Fetch all users")
	public ResponseEntity<ResultPaginationDTO> getAllUser(
			@Filter Specification<User> specification,
			Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(specification, pageable));

	}

	@PutMapping("/users")
	@ApiMessage("Update an user")
	public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User postmanUser)
			throws IdInvalidException {
		User user = this.userService.handleUpdateUser(postmanUser);
		Company company = this.companyService.fetchCompanyByID(postmanUser.getCompany().getId());
		if (company == null) {
			throw new IdInvalidException("Company with ID = " + postmanUser.getCompany().getId() + " was not existed!!!");

		}
		return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(user, company));
	}

}
