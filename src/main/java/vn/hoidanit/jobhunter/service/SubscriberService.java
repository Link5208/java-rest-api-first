package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
@AllArgsConstructor
public class SubscriberService {
	private final SubscriberRepository subscriberRepository;
	private final SkillRepository skillRepository;
	private final EmailService emailService;
	private final JobRepository jobRepository;

	public boolean isSubscriberExistByEmail(String email) {
		return this.subscriberRepository.existsByEmail(email);
	}

	public Subscriber handleCreateSubscriber(Subscriber postmanSubscriber) {
		List<Long> idList = postmanSubscriber.getSkills().stream().map(skill -> skill.getId()).toList();
		List<Skill> skills = this.skillRepository.findByIdIn(idList);
		postmanSubscriber.setSkills(skills);
		return this.subscriberRepository.save(postmanSubscriber);
	}

	public Subscriber handleFetchSubscriberById(long id) {
		Optional<Subscriber> optional = this.subscriberRepository.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

	public Subscriber handleUpdateSubscriber(Subscriber postmanSubscriber) {
		Subscriber subscriber = handleFetchSubscriberById(postmanSubscriber.getId());
		List<Long> idList = postmanSubscriber.getSkills().stream().map(skill -> skill.getId()).toList();
		List<Skill> skills = this.skillRepository.findByIdIn(idList);
		subscriber.setSkills(skills);
		return this.subscriberRepository.save(subscriber);
	}

	public ResEmailJob convertToResEmailJob(Job job) {
		ResEmailJob resEmailJob = new ResEmailJob();
		resEmailJob.setName(job.getName());
		resEmailJob.setSalary(job.getSalary());
		resEmailJob.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
		List<Skill> skills = job.getSkills();
		List<ResEmailJob.SkillEmail> skillEmails = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
				.toList();
		resEmailJob.setSkills(skillEmails);
		return resEmailJob;
	}

	public void sendSubscribersEmailJobs() {
		List<Subscriber> subscribers = this.subscriberRepository.findAll();
		if (subscribers != null && subscribers.size() > 0) {
			for (Subscriber subscriber : subscribers) {
				List<Skill> skills = subscriber.getSkills();
				if (skills != null && skills.size() > 0) {
					List<Job> jobs = this.jobRepository.findBySkillsIn(skills);
					if (jobs != null && jobs.size() > 0) {

						List<ResEmailJob> emailJobs = jobs.stream().map(job -> convertToResEmailJob(job)).toList();

						this.emailService.sendEmailFromTemplateSync(
								subscriber.getEmail(),
								"Hot career oppotunities!!! Discover",
								"job",
								subscriber.getName(),
								emailJobs);
					}
				}
			}
		}
	}

	// @Scheduled(cron = "*/10 * * * * *")
	// public void testCron() {
	// System.out.println(">>> TEST CRON");
	// }

}
