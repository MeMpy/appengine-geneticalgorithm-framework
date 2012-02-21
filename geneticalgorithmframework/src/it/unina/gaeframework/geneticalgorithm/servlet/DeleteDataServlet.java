package it.unina.gaeframework.geneticalgorithm.servlet;

import it.unina.gaeframework.geneticalgorithm.AbstractChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.statistics.ChromosomeDataContainer;
import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;
import it.unina.tools.datastore.ByteArrayDataClass;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class DeleteDataServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Logger log = Logger.getLogger(DeleteDataServlet.class.getName());

		resp.setContentType("text/html");

		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions url1 = TaskOptions.Builder.withUrl("/deletedataviatask");
		TaskOptions url2 = TaskOptions.Builder.withUrl("/deletedataviatask");
		TaskOptions url4 = TaskOptions.Builder.withUrl("/deletedataviatask");
		TaskOptions url5 = TaskOptions.Builder.withUrl("/deletedataviatask");
		TaskOptions url6 = TaskOptions.Builder
				.withUrl("/deletemapreducestatustask");
		TaskOptions url7 = TaskOptions.Builder.withUrl("/deletecacheviatask");
		
		url1.param("kindToDelete", AbstractChromosomeFactory.getConcreteChromosomeFactory()
				.getConcreteChromosomeClass().getName());
		url2.param("kindToDelete", GeneticStatistics.class.getName());
		url4.param("kindToDelete", ChromosomeDataContainer.class.getName());
		url5.param("kindToDelete", ByteArrayDataClass.class.getName());

		queue.add(url1);
		queue.add(url2);
		queue.add(url4);
		queue.add(url5);
		queue.add(url6);
		queue.add(url7);

		resp.getWriter().print("Dati in cancellazione...");
		resp.getWriter().print("<a href=\"index.html\"> indietro</a>");

	}
}
