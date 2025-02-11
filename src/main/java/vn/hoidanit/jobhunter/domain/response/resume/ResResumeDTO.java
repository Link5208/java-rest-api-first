package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeStatusEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResResumeDTO {
	private long id;
	private String email;
	private String url;
	private ResumeStatusEnum status;
	private Instant createdAt;
	private Instant updatedAt;
	private String createdBy;
	private String updatedBy;
	private String companyName;
	private ResumeUser user;
	private ResumeJob job;

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResumeUser {
		private long id;
		private String name;

	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResumeJob {
		private long id;
		private String name;

	}
}
