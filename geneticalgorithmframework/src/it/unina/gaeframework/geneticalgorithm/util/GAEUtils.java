package it.unina.gaeframework.geneticalgorithm.util;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.apache.hadoop.conf.Configuration;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.tools.mapreduce.ConfigurationXmlUtil;
import com.google.appengine.tools.mapreduce.MapReduceXml;

/**
 * classe di utilità per Google App Engine. Fornisce una serie di funzionalità
 * utilizzate di frequente come inserire un task in una Task Queue oppure far
 * partire un Map
 * 
 * @author barren
 * 
 */
public class GAEUtils {

	/**
	 * metodo che accoda un task in una task queue specificata
	 * 
	 * @param taskName
	 *            nome da assegnare al task. Inserire null se non si vuole
	 *            specificare un nome
	 * @param queueName
	 *            nome della coda in cui accodare il task. Inserire null per
	 *            accodare alla coda di default
	 * @param taskUrl
	 *            utl del task da accodare.
	 * @param parameters
	 *            Parametri HTTP da passare al task. Inserire null se nopn si
	 *            vogliono specificare parametri
	 */
	public static void enqueueTask(String taskName, String queueName,
			String taskUrl, Map<String, String> parameters) {
		try {
			// creazione del TaskOptions relativo all'URL passata
			TaskOptions t = TaskOptions.Builder.withUrl(taskUrl);

			// se il taskName è stato inserito lo settiamo
			if (taskName != null && !taskName.equals(""))
				t.taskName(taskName);

			// se sono stati inseriti dei parametri li aggiungiamo
			if (parameters != null) {
				Set<String> keys = parameters.keySet();
				if (keys.size() > 0) {
					for (String key : keys) {
						String value = parameters.get(key);
						t.param(key, value);
					}
				}
			}

			// recuperiamo la coda avente il nome passato come argomento
			// se queueName è null recuperiamo la coda di Default
			Queue queue = null;
			if (queueName == null)
				queue = QueueFactory.getDefaultQueue();
			else
				queue = QueueFactory.getQueue(queueName);

			// aggiungiamo il task così creato in coda
			queue.add(t);

		} catch (TaskAlreadyExistsException t) {
			t.printStackTrace();
		}
	}

	private static Configuration getMapReduceConfiguration() {
		Configuration conf = null;
		try {
			MapReduceXml mrXml = MapReduceXml.getMapReduceXmlFromFile();
			Map<String, String> params = new HashMap<String, String>();
			conf = mrXml.instantiateConfiguration("geneticmapworker", params);
			Integer mapWorkerCount = GeneticAlgorithmXMLConfigReader
					.getInstance().getMapWorkerCount();
			if (mapWorkerCount != null && mapWorkerCount > 0)
				conf.setInt("mapreduce.mapper.shardcount", mapWorkerCount);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conf;
	}

	/**
	 * metodo per far partire un map
	 */
	public static void startMapJob(String mapName, String taskName,
			String queueName) {

		TaskOptions task = buildStartJob(mapName);
		if (taskName != null)
			task.taskName(taskName);

		// addJobParam(task, StatusBlobInputFormat.SAMPLE_ID_PARAMETER,
		// statusBlob.getSampleId());
		// addJobParam(task, StatusBlobInputFormat.DATE_PARAMETER,
		// statusBlob.getDate().getMillis());
		//        
		// recuperiamo la coda avente il nome passato come argomento
		// se queueName è null recuperiamo la coda di Default
		Queue queue = null;
		if (queueName == null)
			queue = QueueFactory.getDefaultQueue();
		else
			queue = QueueFactory.getQueue(queueName);
		queue.add(task);
	}

	private static TaskOptions buildStartJob(String jobName) {
		Configuration conf = getMapReduceConfiguration();
		String xml = ConfigurationXmlUtil.convertConfigurationToXml(conf);
		
		// return TaskOptions.Builder.withUrl("/mapreduce/command/start_job")
		// .method(Method.POST).header("X-Requested-With",
		// "XMLHttpRequest") // hack: we need to fix
		// // appengine-mapper so we can
		// // properly call start_job without
		// // need to pretend to be an
		// // ajaxmethod
		// .param("name", jobName)
		// .param("configuration", xml);
		return TaskOptions.Builder.withDefaults().url("/mapreduce/start")
				.method(Method.POST).param("configuration", xml);
	}

	/**
	 * Ritorna un'istanza della cache di appengine
	 * 
	 * @return memcache
	 */
	public static Cache getCache() {
		Cache cache = null;

		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			e.printStackTrace();
		}

		return cache;
	}
}
