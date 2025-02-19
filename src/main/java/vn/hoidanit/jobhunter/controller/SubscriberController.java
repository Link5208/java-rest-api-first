package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class SubscriberController {
	private final SubscriberService subscriberService;

	@PostMapping("/subscribers")
	public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber postmanSubscriber)
			throws IdInvalidException {
		if (this.subscriberService.isSubscriberExistByEmail(postmanSubscriber.getEmail())) {
			throw new IdInvalidException(("Email " + postmanSubscriber.getEmail() + " has already existed!"));
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(this.subscriberService.handleCreateSubscriber(postmanSubscriber));
	}

	@PutMapping("/subscribers")
	public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber postmanSubscriber)
			throws IdInvalidException {
		Subscriber updatedSubscriber = this.subscriberService.handleFetchSubscriberById(postmanSubscriber.getId());
		if (updatedSubscriber == null) {
			throw new IdInvalidException(("Subscriber with ID = " + postmanSubscriber.getId() + " does not exist!"));
		}

		return ResponseEntity.ok(this.subscriberService.handleUpdateSubscriber(postmanSubscriber));
	}

}
