package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResUserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
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

	public User fetchUserById(long id) throws UsernameNotFoundException {
		Optional<User> userOptional = this.userRepository.findById(id);
		if (userOptional.isPresent()) {
			return userOptional.get();
		} else {
			throw new UsernameNotFoundException("There is not any user having ID = " + id);

		}

	}

	public List<User> fetchAllUser() {
		return this.userRepository.findAll();
	}

	public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable) {
		Page<User> page = this.userRepository.findAll(specification, pageable);
		ResultPaginationDTO result = new ResultPaginationDTO();
		Meta meta = new Meta();

		meta.setPage(pageable.getPageNumber() + 1);
		meta.setPageSize(pageable.getPageSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		result.setMeta(meta);
		List<User> users = page.getContent();
		List<ResUserDTO> dtos = new ArrayList<ResUserDTO>() {
			{

				for (User user : users) {
					ResUserDTO dto = new ResUserDTO();
					dto.setId(user.getId());
					dto.setName(user.getName());
					dto.setEmail(user.getEmail());
					dto.setGender(user.getGender());
					dto.setAddress(user.getAddress());
					dto.setAge(user.getAge());
					dto.setCreatedAt(user.getCreatedAt());
					dto.setUpdatedAt(user.getUpdatedAt());
					add(dto);

				}
			}

		};
		result.setResult(dtos);

		return result;
	}

	public User handleUpdateUser(User reqUser) {
		User currentUser = this.fetchUserById(reqUser.getId());

		currentUser.setName(reqUser.getName());
		currentUser.setGender(reqUser.getGender());
		currentUser.setAddress(reqUser.getAddress());
		currentUser.setAge(reqUser.getAge());
		currentUser.handleBeforeUpdate();

		// update
		currentUser = this.userRepository.save(currentUser);
		return currentUser;

	}

	public User handleGetUserByUsername(String username) {
		return this.userRepository.findByEmail(username);
	}
}
