package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class EmailController {
	private final EmailService emailService;

	@GetMapping("/email")
	@ApiMessage("Sending simple email")
	public String sendEmail() {
		this.emailService.sendEmail();
		return "sended";
	}

}
