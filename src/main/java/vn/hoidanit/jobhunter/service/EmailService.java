package vn.hoidanit.jobhunter.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {

	private final MailSender mailSender;

	public void sendEmail() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo("hoanglong1292004@gmail.com");
		message.setSubject("Testing from Spring Boot");
		message.setText("Hello World from Spring Boot email");
		this.mailSender.send(message);

	}
}
