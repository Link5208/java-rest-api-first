package vn.hoidanit.jobhunter.domain.dto.user;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUserDTO {
	private long id;
	private String name;
	private String email;

	@Enumerated(EnumType.STRING)
	private GenderEnum gender;
	private String address;
	private int age;
	private Instant createdAt;
	private Instant updatedAt;

}
