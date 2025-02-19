package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
@AllArgsConstructor
public class SubscriberService {
	private final SubscriberRepository subscriberRepository;
	private final SkillRepository skillRepository;

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

}
