package com.sensotrend.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sensotrend.data.AccessTokenStorage;
import com.sensotrend.data.TaltioniDataAccess;
import com.sensotrend.data.TaltioniDataAccess.SensotrendFileType;
import com.sensotrend.filter.OAuthFilter;

import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AccessToken;

/**
 * Servlet implementation class MovesServlet
 */
@WebServlet(description = "Get your Moves data to Taltioni", urlPatterns = { "/Taltioni/Moves" })

public class MovesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain"); // should be application/json
		String uid = request.getParameter("uid"); 
        if (uid == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Need uid parameter");
            return;
        }
        // add yesterday's activities
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		URL url = new URL("https://api.moves-app.com/api/v1/user/activities/daily/" +
				format.format(cal.getTime())+ 
				"?access_token="+
		        AccessTokenStorage.getInstance().getMovesToken(uid));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

// handy for debugging 
//		org.apache.commons.io.IOUtils.copy(reader, response.getWriter());
		
		reader.mark(0);
        AccessToken token = AccessTokenStorage.getInstance().getToken((String)request.getSession().getAttribute(OAuthFilter.UID_ATTRIBUTE));
        int count = TaltioniDataAccess.getInstance().storeFileValues(SensotrendFileType.MOVES_JSON, null,
                reader, token);
        response.getWriter().write(count + " records stored to Taltioni.");
	}
}
