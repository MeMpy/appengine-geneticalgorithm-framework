package it.unina.gaeframework.geneticalgorithm.task;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;
import it.unina.gaeframework.geneticalgorithm.util.GAEUtils;
import it.unina.gaeframework.geneticalgorithm.util.GAKeyFactory;
import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;
import it.unina.tools.datastore.DatastoreLoadAndSave;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;

import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
public class CheckStopTask extends HttpServlet {

	public static final String POST_PROCESSING_TASK_NAME = "postProcessingTask";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// recuperiamo l'oggetto geneticStatistics
		DatastoreLoadAndSave data = new DatastoreLoadAndSave();
		Cache cache = GAEUtils.getCache();

		Key geneticStatisticsKey = GAKeyFactory.getGAKeyFactoryInstance()
				.getGenetisStatisticsKey();

		// proviamo a recuperarlo dalla cache
		GeneticStatistics geneticStatistics = (GeneticStatistics) cache
				.get(geneticStatisticsKey);

		if (geneticStatistics == null) {
			// se il caricamento dalla cache non ha funzionato lo carichiamo dal
			// datastore
			geneticStatistics = data.loadObjectById(GeneticStatistics.class,
					geneticStatisticsKey.getId());
		}

		if (geneticStatistics == null) {
			// Errore: geneticStatistics non presente nel datastore
			throw new RuntimeException(
					"CheckStopTask: GeneticStatistics non presente nel datastore\n"
							+ "Impossibile continuare l'algoritmo genetico");
		} else {
			String idTask = req.getParameter("idTask");
			String actualIteration = req.getParameter("actualIteration");

			if (geneticStatistics.getCheckStop()) {
				// Il criterio di stop è verificato, quindi, se è stato
				// specificato, lanciamo il task di	postprocessing

				String taskUrl = GeneticAlgorithmXMLConfigReader.getInstance()
						.getPostProcessingTaskUrl();

				if (taskUrl != null && !taskUrl.equals("")) {
					// accodiamo il postProcessingTask
					String taskName = POST_PROCESSING_TASK_NAME + idTask
							+ actualIteration;
					String queueName = "geneticqueue";

					Map<String, String> httpParameters = new HashMap<String, String>();
					httpParameters.put("idTask", idTask);

					GAEUtils.enqueueTask(taskName, queueName, taskUrl,
							httpParameters);
				}

			} else {
				// IL CRITERIO DI STOP NON E' SODDISFATTO QUINDI RILANCIAMO IL
				// MAP
				geneticStatistics.setActualIteration(Integer
						.parseInt(actualIteration) + 1);

				// salviamo geneticStatistics nella chache e nel datastore
				data.save(geneticStatistics);
				cache.put(geneticStatistics.getKey(), geneticStatistics);

				String taskName = InitializePopolationTask.MAP_TASK_NAME
						+ geneticStatistics.getIdTask()
						+ geneticStatistics.getActualIteration();
				String queueName = null;
				GAEUtils.startMapJob("geneticmapworker", taskName, queueName);
			}
		}
	}
}
