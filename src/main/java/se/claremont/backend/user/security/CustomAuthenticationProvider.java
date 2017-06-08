package se.claremont.backend.user.security;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.entities.User;

// TODO fix real integration to Outlook API

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private static final String OUTLOOK_API = "https://outlook.office365.com/EWS/Exchange.asmx";
	@Autowired
	private UserRepository userDao;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String)authentication.getCredentials();

		// set up Exchange service
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		service.setCredentials(new WebCredentials(username, password));
		
		try {
			service.setUrl(new URI(OUTLOOK_API));
		} catch (URISyntaxException e) {
			throw new AuthenticationServiceException("Invalid outlook endpoint", e);
		}
		
		try {
			/*
			 * 1. Check if the username exists in database. If so, the user has successfully logged in before and has
			 * received a confirmation email.
			 */
			if(userDao.findByUsername(username) != null){
				/* try binding a well known folder to verify username and password without sending a mail to the user.
				 * If binding fails exception will be catched and BadCredentialsException will be thrown.
				 */
				Folder.bind(service, WellKnownFolderName.Inbox);
				return authentication;
			}
		} catch(Exception e){
			throw new BadCredentialsException("login failed", e);
		}


		/*
		 * 2. If the user logins for the first time: Try to send a mail to the
		 * user for confirmation that login was successful.
		 *
		 * <!-- -------------START "login first time"------------ -->
		 */
		
		try {
			EmailMessage msg = new EmailMessage(service);
			msg.setSubject("Välkommen till Clarty!");
			msg.setBody(MessageBody.getMessageBodyFromText("Du är nu registrerad på Clarty. Detta mail går inte att svara på."));
			msg.getToRecipients().add(username);
			msg.send();
		} catch (Exception e) {
			throw new BadCredentialsException("Login failed", e);
		}

		// <!-- -------------END "login first time"------------ -->


		/*
		 * 3. If the user has successfully sent a mail, the username will be
		 * stored in a database. This will enable the backend to check against
		 * the database if there has been a successful login at a previous
		 * login attempt.
		 */
		userDao.save(new User(username));
		
		return new UsernamePasswordAuthenticationToken(username, password);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
