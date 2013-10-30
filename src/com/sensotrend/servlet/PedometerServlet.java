package com.sensotrend.servlet;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sensotrend.data.AccessTokenStorage;
import com.sensotrend.data.TaltioniDataAccess;
import com.sensotrend.filter.OAuthFilter;

import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AccessToken;

/**
 * Servlet implementation class PedometerServlet
 */
@WebServlet(description = "Store and get pedometer data from Taltioni.", urlPatterns = { "/Taltioni/Activity" })
public class PedometerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Calendar from = TaltioniDataAccess.getInstance().getCalendar(2013, Calendar.JANUARY, 1);
        Calendar to = TaltioniDataAccess.getInstance().getCalendar();
        AccessToken token = AccessTokenStorage.getInstance().getToken((String)request.getSession().getAttribute(OAuthFilter.UID_ATTRIBUTE));
        response.setContentType("text/plain");
        response.getWriter().write(TaltioniDataAccess.getInstance().getObservationsJSON("ExerciseSteps", from, to, token));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
