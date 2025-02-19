package vn.hoidanit.jobhunter.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {

	private final MailSender mailSender;
	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine springTemplateEngine;

	public void sendEmail() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo("hoanglong1292004@gmail.com");
		message.setSubject("Testing from Spring Boot");
		message.setText("Hello World from Spring Boot email");
		this.mailSender.send(message);

	}

	public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, isHtml);
			this.javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			System.out.println(">>> ERROR SEND EMAIL: " + e);
		}

	}

	public void sendEmailFromTemplateSync(String to, String subject, String templateName) {
		Context context = new Context();
		String content = this.springTemplateEngine.process(templateName, context);
		this.sendEmailSync(to, subject, content, false, true);
	}
}
