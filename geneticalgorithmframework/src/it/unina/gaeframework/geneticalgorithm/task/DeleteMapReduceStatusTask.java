package it.unina.gaeframework.geneticalgorithm.task;

import it.unina.tools.datastore.DatastoreLoadAndSave;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteMapReduceStatusTask extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Logger log = Logger.getLogger(DeleteDataViaTask.class.getName());
//		String kindToDelete= req.getParameter("kindToDelete");
		//System.out.println("entità da cancellare: "+ kindToDelete);
		
		DatastoreLoadAndSave data = new DatastoreLoadAndSave();
		data.deleteAllMapReduceState();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req, resp);
	}
}
