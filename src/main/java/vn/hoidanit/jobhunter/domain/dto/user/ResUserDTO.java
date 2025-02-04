package vn.hoidanit.jobhunter.domain.dto.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
	private long id;
	private String name;
	private String email;
	private GenderEnum gender;
	private String address;
	private int age;
	private Instant createdAt;
	private Instant updatedAt;

}
