package com.sensotrend.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import com.sensotrend.data.AccessTokenStorage;
import com.sensotrend.servlet.AuthenticationServlet;

/**
 * This Filter checks for the availability of the OAuth access token for the
 * request. It does not validate the scope, though.
 */
@WebFilter(description = "Obtains the access token for the user, if necessary", urlPatterns = { "/Taltioni/*" })
public class OAuthFilter implements Filter {
	
	public static final String UID_ATTRIBUTE = "uid";

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse) {
			// we're only interested in HTTP traffic
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			
			String uid = null;
			HttpSession session = httpRequest.getSession(false);
			if (session != null) {
				Object uidObject = session.getAttribute(UID_ATTRIBUTE);
				if (uidObject != null && uidObject instanceof String) {
					uid = (String)uidObject;
				}
			}

			if (AccessTokenStorage.getInstance().containsTokenFor(uid)) {
				// user is authenticated, OK
			} else {
				// no access token ready, need to authenticate

				String uri = httpRequest.getScheme()
						+ "://"
						+ httpRequest.getServerName()
						+ ("http".equals(httpRequest.getScheme())
								&& httpRequest.getServerPort() == 80
								|| "https".equals(httpRequest.getScheme())
								&& httpRequest.getServerPort() == 443 ? ""
								: ":" + httpRequest.getServerPort())
						+ httpRequest.getRequestURI()
						+ (httpRequest.getQueryString() != null ? "?"
								+ httpRequest.getQueryString() : "");

				try {
					OAuthClientRequest authRequest = OAuthClientRequest
							.authorizationLocation(
									AuthenticationServlet.AUTHORIZATION_LOCATION)
							.setResponseType(ResponseType.CODE.toString())
							.setClientId(AuthenticationServlet.CLIENT_ID)
                            .setState(uri)
							.setRedirectURI(AuthenticationServlet.REDIRECT_URI)
							.buildQueryMessage();
					// send the client to the authentication process
					((HttpServletResponse) response).sendRedirect(authRequest
							.getLocationUri());
					return;
				} catch (OAuthSystemException e) {
					throw new ServletException(e);
				}
			}
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

}
