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

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.EmailExistsAlreadyException;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/users")
	public ResponseEntity<ResCreateUserDTO> createNewUser(@RequestBody User postManUser)
			throws EmailExistsAlreadyException {
		String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
		postManUser.setPassword(hashPassword);
		User newUser = this.userService.handleCreateUser(postManUser);
		if (newUser == null) {
			throw new EmailExistsAlreadyException("Email " + postManUser.getEmail() + " was existed!!!");
		}

		ResCreateUserDTO dto = new ResCreateUserDTO();
		dto.setId(newUser.getId());
		dto.setName(newUser.getName());
		dto.setEmail(newUser.getEmail());
		dto.setGender(newUser.getGender());
		dto.setAddress(newUser.getAddress());
		dto.setAge(newUser.getAge());
		dto.setCreatedAt(newUser.getCreatedAt());

		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
			throws IdInvalidException {
		if (id >= 1500) {
			throw new IdInvalidException("Id isn't bigger than 1500");
		}

		this.userService.handleDeleteUser(id);
		return ResponseEntity.ok(null);
		// return ResponseEntity.status(HttpStatus.OK).body("ericUser");
	}

	// fetch user by id
	@GetMapping("/users/{id}")
	public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) {
		User fetchUser = this.userService.fetchUserById(id);

		ResUserDTO dto = new ResUserDTO();
		dto.setId(fetchUser.getId());
		dto.setName(fetchUser.getName());
		dto.setEmail(fetchUser.getEmail());
		dto.setGender(fetchUser.getGender());
		dto.setAddress(fetchUser.getAddress());
		dto.setAge(fetchUser.getAge());
		dto.setCreatedAt(fetchUser.getCreatedAt());
		dto.setUpdatedAt(fetchUser.getUpdatedAt());

		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	// fetch all users
	@GetMapping("/users")
	@ApiMessage("fetch all users")
	public ResponseEntity<ResultPaginationDTO> getAllUser(
			@Filter Specification<User> specification,
			Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(specification, pageable));

	}

	@PutMapping("/users")
	public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User postmanUser) {
		User user = this.userService.handleUpdateUser(postmanUser);

		ResUpdateUserDTO dto = new ResUpdateUserDTO();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setGender(user.getGender());
		dto.setAddress(user.getAddress());
		dto.setAge(user.getAge());
		dto.setUpdatedAt(user.getUpdatedAt());

		return ResponseEntity.ok(dto);
	}

}
