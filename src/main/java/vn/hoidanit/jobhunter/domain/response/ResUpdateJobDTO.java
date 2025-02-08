package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUpdateJobDTO {
	private long id;
	private String name;
	private String location;
	private double salary;
	private int quantity;
	private LevelEnum level;
	private String description;
	private Instant startDate;
	private Instant endDate;
	private boolean active;
	private Instant updatedAt;
	private String updatedBy;
	private List<String> skills;

}
