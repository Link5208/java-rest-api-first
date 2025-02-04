package vn.hoidanit.jobhunter.domain.dto.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Setter
@Getter
public class ResCreateUserDTO {
	private long id;
	private String name;
	private String email;
	private int age;
	private GenderEnum gender;
	private String address;
	private Instant createdAt;
}
