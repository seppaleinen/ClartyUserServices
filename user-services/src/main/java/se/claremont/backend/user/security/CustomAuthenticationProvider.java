package se.claremont.backend.user.security;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String)authentication.getCredentials();
		// TODO Alternative solution: Authenticate against outlooks api if it's
		// possible

		/*
		 * TODO create an if-statement that checks if the username exists in a
		 * database. If true, the user has successfully logged in before and has
		 * received a confirmation-mail. return authentication.true
		 */

		/*
		 * 1. When the user logins for the first time: Try to send a mail to the
		 * user itself for confirmation that login was successful
		 * <!-- START "login first time" -->
		 */
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials(username, password);
		service.setCredentials(credentials);
		
		try {
			URI ewsUrl = new URI("https://outlook.office365.com/EWS/Exchange.asmx");
			service.setUrl(ewsUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		EmailMessage msg;
		try {
			msg = new EmailMessage(service);
			msg.setSubject("Email från Clarty :D:D:D:D:D:D:D");
			msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API. Har lyckats få till det att skicka ett mail när man försöker logga in via Clarty :D. Tanken är att man skickar från sig själv, till sig själv"));
			msg.getToRecipients().add(username);
			msg.send();
		} catch (Exception e) {
			authentication = new UsernamePasswordAuthenticationToken(username, password);
		}
		
		// <!-- END "login first time" -->

		/*
		 * TODO: If the user has successfully sent a mail, the username will be
		 * stored in a database. This will enable the backend to check against
		 * the database if there has been a successful login at a previous
		 * loginattempt.
		 */
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
