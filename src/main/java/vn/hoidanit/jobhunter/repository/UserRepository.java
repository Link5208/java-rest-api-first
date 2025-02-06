package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.Company;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	User findByEmail(String email);

	Boolean existsByEmail(String email);

	User findByRefreshTokenAndEmail(String refreshToken, String email);

	void deleteAllByCompany(Company company);

	List<User> findByCompany(Company company);

}
