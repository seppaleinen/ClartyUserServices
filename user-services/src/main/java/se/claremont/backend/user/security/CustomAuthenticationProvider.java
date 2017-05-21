package se.claremont.backend.user.security;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.entities.User;

// TODO fix real integration to Outlook API

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	private UserRepository userDao;
	private static String outlookApi = "https://outlook.office365.com/EWS/Exchange.asmx";
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String)authentication.getCredentials();

		/*
		 * 1. Check if the username exists in database. If so, the user has successfully logged in before and has
		 * received a confirmation email. Return authenticated user.
		 */
		try {
			userDao.findByUsername(username);
			return authentication;
		} catch(UsernameNotFoundException e){
			// continue
		}


		/*
		 * 2. If the user logins for the first time: Try to send a mail to the
		 * user for confirmation that login was successful.
		 *
		 * <!-- -------------START "login first time"------------ -->
		 */
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials(username, password);
		service.setCredentials(credentials);
		
		try {
			URI ewsUrl = new URI(outlookApi);
			service.setUrl(ewsUrl);
		} catch (URISyntaxException e) {
			throw new AuthenticationServiceException("Invalid outlook endpoint", e);
		}
		
		EmailMessage msg;
		try {
			msg = new EmailMessage(service);
			msg.setSubject("Välkommen till Clarty!");
			msg.setBody(MessageBody.getMessageBodyFromText("Du är nu registrerad på Clarty. Detta mail går inte att svara på."));
			msg.getToRecipients().add(username);
			msg.send();
		} catch (Exception e) {
			throw new BadCredentialsException("Login failed", e);
		}

		authentication = new UsernamePasswordAuthenticationToken(username, password);
		
		// <!-- -------------END "login first time"------------ -->


		/*
		 * 3. If the user has successfully sent a mail, the username will be
		 * stored in a database. This will enable the backend to check against
		 * the database if there has been a successful login at a previous
		 * login attempt.
		 */
		User user = new User();
		user.setUsername(username);
		userDao.save(user);
		
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public UserRepository getUserDao() {
		return userDao;
	}

	public void setUserDao(UserRepository userDao) {
		this.userDao = userDao;
	}

}
