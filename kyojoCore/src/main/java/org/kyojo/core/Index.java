package org.kyojo.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GlobalData gbd = GlobalData.getInstance(getServletContext());
		SessionData ssd = new SessionData(request, gbd);
		RequestData rqd = new RequestData(request, gbd);
		ResponseData rpd = new ResponseData(response);
		TemplateEngine te = new TemplateEngine(gbd, ssd, rqd, rpd);

		te.printContent();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
