package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class EmailController {
	private final SubscriberService subscriberService;

	@GetMapping("/email")
	@ApiMessage("Sending simple email")
	public String sendEmail() {
		// this.emailService.sendEmailSync("hoanglong1292004@gmail.com", "test and
		// email", "<h1><b>Hello</b></h1>", false,
		// true);
		this.subscriberService.sendSubscribersEmailJobs();
		return "sended";
	}

}
