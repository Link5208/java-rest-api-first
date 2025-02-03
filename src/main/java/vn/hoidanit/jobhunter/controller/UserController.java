package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {
	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/users")
	public ResponseEntity<User> createNewUser(@RequestBody User postManUser) {
		String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
		postManUser.setPassword(hashPassword);
		User newUser = this.userService.handleCreateUser(postManUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") long id)
			throws IdInvalidException {
		if (id >= 1500) {
			throw new IdInvalidException("Id isn't bigger than 1500");
		}

		this.userService.handleDeleteUser(id);
		return ResponseEntity.ok("Delete user");
		// return ResponseEntity.status(HttpStatus.OK).body("ericUser");
	}

	// fetch user by id
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
		User fetchUser = this.userService.fetchUserById(id);
		// return ResponseEntity.ok(fetchUser);
		return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
	}

	// fetch all users
	@GetMapping("/users")
	public ResponseEntity<ResultPaginationDTO> getAllUser(
			@RequestParam("current") Optional<String> currentOptional,
			@RequestParam("pageSize") Optional<String> pageSizeOptional) {
		String currentString = currentOptional.isPresent() ? currentOptional.get() : "";
		String pageSizeString = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
		int current = Integer.parseInt(currentString);
		int pageSize = Integer.parseInt(pageSizeString);
		Pageable pageable = PageRequest.of(current - 1, pageSize);
		ResultPaginationDTO result = this.userService.fetchAllUser(pageable);
		// return ResponseEntity.ok(this.userService.fetchAllUser());
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@PutMapping("/users")
	public ResponseEntity<User> updateUser(@RequestBody User postmanUser) {
		User user = this.userService.handleUpdateUser(postmanUser);
		return ResponseEntity.ok(user);
	}

}
