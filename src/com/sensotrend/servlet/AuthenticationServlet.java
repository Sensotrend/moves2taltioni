package com.sensotrend.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth.HeaderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.sensotrend.data.AccessTokenStorage;
import com.sensotrend.data.TaltioniDataAccess;

/**
 * Servlet implementation class AuthenticationServlet
 */
@WebServlet("/Authentication")
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String REDIRECT_URI = TaltioniDataAccess.getInstance().getProperty("REDIRECT_URI");
    public static final String LOCAL_REDIRECT_URI = TaltioniDataAccess.getInstance().getProperty("LOCAL_REDIRECT_URI");
	public static final String CLIENT_ID = TaltioniDataAccess.getInstance().getProperty("CLIENT_ID");
	public static final String AUTHORIZATION_LOCATION = TaltioniDataAccess.getInstance().getProperty("AUTHORIZATION_LOCATION");
	public static final String TOKEN_LOCATION = TaltioniDataAccess.getInstance().getProperty("TOKEN_LOCATION");
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String code = request.getParameter("code");
		if (code == null) {
			// WE GOT AN ERROR!
			String error = request.getParameter("error");
			String errorDescription = request.getParameter("error_description");
			if (error != null) {
				if (error.equals("access_denied") ||
						error.equals("unauthorized_client")) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, errorDescription);
				} else if (error.equals("invalid_request") ||
						error.equals("unsupported_response_type") ||
						error.equals("server_error")) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorDescription);
				} else if (error.equals("temporiraly_unavailable")) {
					response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, errorDescription);
				}
			} else {
				// No error indicated.
				// Either this request is not a redirect from Taltioni, or there is an error in configuration.
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
			return;
		} else {
			// we got the auth code from the auth server, let's exchange it to
			// the access token

			OAuthClientRequest tokenRequest;
			TokenRequestBuilder builder;
			try {
                String redirect = REDIRECT_URI;
                if (request.getServerName().contains("localhost"))
                    redirect = LOCAL_REDIRECT_URI;
				builder = OAuthClientRequest
						.tokenLocation(TOKEN_LOCATION)
						.setGrantType(GrantType.AUTHORIZATION_CODE)
						.setCode(code)
						.setRedirectURI(redirect)
						.setClientId(CLIENT_ID);
				tokenRequest = builder.buildBodyMessage();
			} catch (OAuthSystemException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			String header = buildBasicAuth(TaltioniDataAccess.getInstance().getProperty("CLIENT_ID"),
			        TaltioniDataAccess.getInstance().getProperty("APPLICATION_ID"));
			tokenRequest.addHeader(HeaderType.AUTHORIZATION, header);
			
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			
			OAuthJSONAccessTokenResponse oAuthResponse;
			
			try {
				oAuthResponse = oAuthClient.accessToken(tokenRequest);
			} catch (OAuthSystemException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			} catch (OAuthProblemException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getDescription());
				return;
			}
			
			String key = AccessTokenStorage.getInstance().storeToken(oAuthResponse.getAccessToken());
			HttpSession session = request.getSession(true);
			session.setAttribute("uid", key);
			
			String state = request.getParameter("state");
			if (state != null) {
				String redirect = URLDecoder.decode(state, "UTF-8");
				response.sendRedirect(redirect);
			}
		}
	}
	
	private String buildBasicAuth(String username, String password) {
		if (username == null || password == null) {
			throw new NullPointerException();
		}
		return "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes());
	}
}
