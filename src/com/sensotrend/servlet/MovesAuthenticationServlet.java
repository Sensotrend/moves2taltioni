package com.sensotrend.servlet;

import java.io.IOException;

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
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import com.sensotrend.data.AccessTokenStorage;
import com.sensotrend.data.TaltioniDataAccess;

/**
 * Servlet implementation class MovesAuthenticationServlet
 */
@WebServlet("/Taltioni/MovesAuthentication")
public class MovesAuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String REDIRECT_URI = TaltioniDataAccess.getInstance().getProperty("MOVES_REDIRECT_URI");
	public static final String CLIENT_ID = TaltioniDataAccess.getInstance().getProperty("MOVES_CLIENT_ID");
	public static final String AUTHORIZATION_LOCATION = TaltioniDataAccess.getInstance().getProperty("MOVES_AUTHORIZATION_LOCATION");
	public static final String TOKEN_LOCATION = TaltioniDataAccess.getInstance().getProperty("MOVES_TOKEN_LOCATION");
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String code = request.getParameter("code");
		if (code == null) {
			String error = request.getParameter("error");
			String errorDescription = request.getParameter("error_description");
			if (error != null) {
	            // WE GOT AN ERROR!
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
				// Initiate Moves connection.
                try {
                    String redirect = REDIRECT_URI;
                    if (request.getServerName().contains("localhost"))
                        redirect = TaltioniDataAccess.getInstance().getProperty("LOCAL_MOVES_REDIRECT_URI");
                    
                    OAuthClientRequest authRequest = OAuthClientRequest
                            .authorizationLocation(AUTHORIZATION_LOCATION)
                            .setResponseType(ResponseType.CODE.toString())
                            .setClientId(CLIENT_ID)
                            .setRedirectURI(redirect)
                            .setScope("activity")
                            .buildQueryMessage();
                    // send the client to the authentication process
                    ((HttpServletResponse) response).sendRedirect(authRequest
                            .getLocationUri());
                    return;
                } catch (OAuthSystemException e) {
                    throw new ServletException(e);
                }
			}
			return;
		} else {
			// we got the auth code from the auth server, let's exchange it to
			// the access token
            String redirect = REDIRECT_URI;
            if (request.getServerName().contains("localhost"))
                redirect = TaltioniDataAccess.getInstance().getProperty("LOCAL_MOVES_REDIRECT_URI");
		            
			OAuthClientRequest tokenRequest;
			TokenRequestBuilder builder;
			try {
				builder = OAuthClientRequest
						.tokenLocation(TOKEN_LOCATION)
						.setGrantType(GrantType.AUTHORIZATION_CODE)
						.setCode(code)
						.setRedirectURI(redirect)
						.setClientId(CLIENT_ID)
				        .setClientSecret(TaltioniDataAccess.getInstance().getProperty("MOVES_CLIENT_SECRET"));
				tokenRequest = builder.buildBodyMessage();
			} catch (OAuthSystemException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			//String header = buildBasicAuth(CLIENT_ID, "ox68GkRgPK6tSiiUE_hG2faHmJcRyprxXVUjeGAij8oc4GxGE3Gw2W5dNiI2OY8c");
			//tokenRequest.addHeader(HeaderType.AUTHORIZATION, header);
			
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
			
			// should identify the user based on the token, and store accordingly
			HttpSession session = request.getSession(true);
			String key = (String)session.getAttribute("uid");
			AccessTokenStorage.getInstance().setMovesToken(key, oAuthResponse.getAccessToken());
            response.getWriter().write("<html><body><h1>Moves connected to Taltioni.</h1>\n<p>UID="+key+"</p>\n");
            response.getWriter().write("<a href=\"/Moves2Taltioni/Taltioni/Moves?uid="+key+"\">Transfer values!</a>\n</body></html>");
		}
	}
	
	private String buildBasicAuth(String username, String password) {
		if (username == null || password == null) {
			throw new NullPointerException();
		}
		return "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes());
	}
}
