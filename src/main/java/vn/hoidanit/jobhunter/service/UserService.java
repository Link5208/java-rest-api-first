package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User handleCreateUser(User user) {
		return this.userRepository.save(user);
	}

	public void handleDeleteUser(long id) {
		this.userRepository.deleteById(id);
	}

	public User fetchUserById(long id) {
		Optional<User> userOptional = this.userRepository.findById(id);
		if (userOptional.isPresent()) {
			return userOptional.get();
		}
		return null;
	}

	public List<User> fetchAllUser() {
		return this.userRepository.findAll();
	}

	public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable) {
		Page<User> page = this.userRepository.findAll(specification, pageable);
		ResultPaginationDTO result = new ResultPaginationDTO();
		Meta meta = new Meta();

		meta.setPage(page.getNumber() + 1);
		meta.setPageSize(page.getSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		result.setMeta(meta);
		result.setResult(page.getContent());

		return result;
	}

	public User handleUpdateUser(User reqUser) {
		User currentUser = this.fetchUserById(reqUser.getId());
		if (currentUser != null) {
			currentUser.setEmail(reqUser.getEmail());
			currentUser.setName(reqUser.getName());
			currentUser.setPassword(reqUser.getPassword());
			// update
			currentUser = this.userRepository.save(currentUser);
		}
		return currentUser;
	}

	public User handleGetUserByUsername(String username) {
		return this.userRepository.findByEmail(username);
	}
}
