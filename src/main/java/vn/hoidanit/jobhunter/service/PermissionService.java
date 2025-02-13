package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
@AllArgsConstructor
public class PermissionService {
	private final PermissionRepository permissionRepository;

	public boolean checkExistPermission(Permission permission) {
		return this.permissionRepository.existsByApiPathAndMethodAndModule(
				permission.getApiPath(),
				permission.getMethod(),
				permission.getModule());
	}

	public Permission handleSavePermission(Permission permission) {
		return this.permissionRepository.save(permission);
	}

	public Permission handleFetchPermissionById(long id) {
		Optional<Permission> optional = this.permissionRepository.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

	public ResultPaginationDTO handleFetchAllPermissions(Specification<Permission> specification, Pageable pageable) {
		Page<Permission> page = this.permissionRepository.findAll(specification, pageable);
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

	public void handleDeletePermission(Permission permission) {

		permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
		this.permissionRepository.delete(permission);

	}
}
