package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
@AllArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public boolean IsRoleNameExisted(Role role) {
		return this.roleRepository.existsByName(role.getName());
	}

	public boolean IsRoleIdExisted(long id) {
		return this.roleRepository.existsById(id);
	}

	public Role handleCreateRole(Role postmanRole) {
		List<Long> list = postmanRole.getPermissions().stream().map(permission -> permission.getId()).toList();
		List<Permission> permissions = this.permissionRepository.findByIdIn(list);
		postmanRole.setPermissions(permissions);

		return this.roleRepository.save(postmanRole);

	}

	public Role handleFetchRoleById(long id) {
		Optional<Role> optional = this.roleRepository.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

	public Role handleUpdateRole(Role postmanRole) {
		Role updatedRole = handleFetchRoleById(postmanRole.getId());
		updatedRole.setName(postmanRole.getName());
		updatedRole.setDescription(postmanRole.getDescription());
		updatedRole.setActive(postmanRole.isActive());

		List<Long> list = postmanRole.getPermissions().stream().map(permission -> permission.getId()).toList();
		List<Permission> permissions = this.permissionRepository.findByIdIn(list);
		updatedRole.setPermissions(permissions);

		return this.roleRepository.save(updatedRole);

	}

	public ResultPaginationDTO handleFetchAllRoles(Specification<Role> specification, Pageable pageable) {
		Page<Role> page = this.roleRepository.findAll(specification, pageable);
		ResultPaginationDTO dto = new ResultPaginationDTO();
		ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

		meta.setPage(page.getNumber() + 1);
		meta.setPageSize(page.getSize());
		meta.setPages(page.getTotalPages());
		meta.setTotal(page.getTotalElements());

		dto.setMeta(meta);
		dto.setResult(page.getContent());
		return dto;
	}

	public void handleDeleteRole(long id) {
		this.roleRepository.deleteById(id);
	}
}
