package com.gwtent.acegi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.logout.LogoutHandler;
import org.acegisecurity.ui.rememberme.NullRememberMeServices;
import org.acegisecurity.ui.rememberme.RememberMeServices;
import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.apache.log4j.Logger;
import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

import com.gwtent.service.Security;


public class SecurityAcegiImpl implements Security, ApplicationEventPublisherAware {
	
	static Logger logger = Logger.getLogger(SecurityAcegiImpl.class.getName());
	
	private AuthenticationManager authenticationManager;
	private RememberMeServices rememberMeServices = new NullRememberMeServices();

	private ApplicationEventPublisher eventPublisher;
	
	private boolean invalidateHttpSession = true;
	
	private LogoutHandler[] logoutHandlers;
	
	public SecurityAcegiImpl(){
		
	}

	public boolean isInvalidateHttpSession() {
		return invalidateHttpSession;
	}


	public void setInvalidateHttpSession(boolean invalidateHttpSession) {
		this.invalidateHttpSession = invalidateHttpSession;
	}


	protected Authentication attemptAuthenticationlogin(String username, String password) {
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

		Authentication auth = getAuthenticationManager().authenticate(authRequest);
		
			//认证成功后就保存起来
		//SecurityContextImpl context = new SecurityContextImpl();
		//context.setAuthentication(auth);
		//SecurityContextHolder.setContext(context);
		return auth;
	}
	
	
	public boolean login(String username, String password, boolean rememberMe) {
		Authentication authResult;

		HttpServletRequest request = ServletUtils.getRequest();
		HttpServletResponse response = ServletUtils.getResponse();
		
		try {			
			authResult = attemptAuthenticationlogin(username, password);
			
			successfulAuthentication(request, response, authResult, rememberMe);

			return true;
		}
		catch (AuthenticationException failed) {
			// Authentication failed
			unsuccessfulAuthentication(request, response, failed);

			return false;
		}
	}

	public void logout(String username) {
		HttpServletRequest request = ServletUtils.getRequest();
		HttpServletResponse response = ServletUtils.getResponse();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try{
			for (int i = 0; i < logoutHandlers.length; i++){
				logoutHandlers[i].logout(request, response, auth);
			}
		}finally{			
			if (invalidateHttpSession) {
				HttpSession session = ServletUtils.getRequest().getSession(false);
				if (session != null) {
					session.invalidate();
				}
			}

			SecurityContextHolder.clearContext();
		}	
	}
	
	
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) {
		
		SecurityContextHolder.getContext().setAuthentication(null);

		if (logger.isDebugEnabled()) {
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
		}

		if ((rememberMeServices != null) && (request != null) && (response != null)){
			rememberMeServices.loginFail(request, response);
		}
	}
	
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult, boolean rememberMe) {
		if (logger.isDebugEnabled()) {
			logger.debug("Authentication success: " + authResult.toString());
		}

		SecurityContextHolder.getContext().setAuthentication(authResult);

		if (logger.isDebugEnabled()) {
			logger.debug("Updated SecurityContextHolder to contain the following Authentication: '" + authResult + "'");
		}
		
		//Sorry we're hard use TokenBasedRememberMeServices here
		boolean lastAlwaysRemember = false;
		if (authResult.isAuthenticated() && (rememberMeServices != null) && (rememberMeServices instanceof TokenBasedRememberMeServices) && rememberMe){
			//request.setAttribute(((TokenBasedRememberMeServices)rememberMeServices).getParameter(), "true");
			lastAlwaysRemember = ((TokenBasedRememberMeServices)rememberMeServices).isAlwaysRemember();
			((TokenBasedRememberMeServices)rememberMeServices).setAlwaysRemember(true);
		}
		try{
			if ((rememberMeServices != null) && (request != null) && (response != null)){
				rememberMeServices.loginSuccess(request, response, authResult);
			}
		}finally{
			if (authResult.isAuthenticated() && (rememberMeServices != null) && (rememberMeServices instanceof TokenBasedRememberMeServices) && rememberMe){
				((TokenBasedRememberMeServices)rememberMeServices).setAlwaysRemember(lastAlwaysRemember);
			}
		}
		

		// Fire event
		if (this.eventPublisher != null) {
			eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}
	}

	

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public RememberMeServices getRememberMeServices() {
		return rememberMeServices;
	}

	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public LogoutHandler[] getLogoutHandlers() {
		return logoutHandlers;
	}

	public void setLogoutHandlers(LogoutHandler[] logoutHandlers) {
		this.logoutHandlers = logoutHandlers;
	}

}