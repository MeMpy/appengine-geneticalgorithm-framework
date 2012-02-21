package it.unina.gaeframework.geneticalgorithm.servlet;

import it.unina.gaeframework.geneticalgorithm.util.GAEUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GAStartServlet extends HttpServlet {
	public static final String INITIALIZE_TASK_NAME = "initializeTask";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Long time = Calendar.getInstance().getTimeInMillis();
		String idTask = "RUN"+time+"-1";
		
		// accodiamo il task che farà partire il GA		
		//TODO: il nome del task deve essere spostato in qualche classe che li contiene tutti
		String taskName = INITIALIZE_TASK_NAME + idTask;
		String queueName = "geneticqueue";
		String taskUrl = "/initializepopolationtask";

		Map<String, String> httpParameters = new HashMap<String, String>();
		httpParameters.put("idTask", idTask);

		GAEUtils.enqueueTask(taskName, queueName, taskUrl, httpParameters);
		resp.sendRedirect("/monitorservlet");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}
}
