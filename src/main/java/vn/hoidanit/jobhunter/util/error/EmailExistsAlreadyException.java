package vn.hoidanit.jobhunter.util.error;

import org.springframework.security.core.AuthenticationException;

public class EmailExistsAlreadyException extends AuthenticationException {

	public EmailExistsAlreadyException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
