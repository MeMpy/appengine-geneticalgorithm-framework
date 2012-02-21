package it.unina.gaeframework.geneticalgorithm.task;

import it.unina.gaeframework.geneticalgorithm.AbstractChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.ChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;
import it.unina.gaeframework.geneticalgorithm.util.GAEUtils;
import it.unina.gaeframework.geneticalgorithm.util.GAKeyFactory;
import it.unina.tools.datastore.DatastoreLoadAndSave;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;

import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
public class InitializePopolationTask extends HttpServlet {
	public static final String MAP_TASK_NAME = "mapWorker";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DatastoreLoadAndSave data = new DatastoreLoadAndSave();
		// Inizializzazione dell'entità che mantiene le statistiche
		GeneticStatistics geneticStatistics = new GeneticStatistics();

		// recuperiamo il parametro idTask che contiene un identificativo
		// univoco per
		// i nomi dei task utilizzati dal GA
		String idTask = req.getParameter("idTask");
		if (idTask == null || idTask.equals(""))
			// l'idTask non è presente, quindi emettiamo un warning e il task
			// termina
			System.err
					.println("Indicare un identificativo univoco per eseguire il GA\n"
							+ "aggiungere al task il parametro idTask con un valore univoco");
		else {
			// settiamo in geneticStatistics l'idTask che sarà usato per tutto
			// il GA
			geneticStatistics.setIdTask(idTask);

			// salviamo l'oggetto geneticStatistics
			data.save(geneticStatistics);

			// Inizializzazione della popolazione
			ChromosomeFactory chromosomeFactory = AbstractChromosomeFactory
					.getConcreteChromosomeFactory();

			Integer populationSize = geneticStatistics.getPopulationSize();
			Integer islandNum = geneticStatistics.getIslandNum();
			Integer newOffspringForIteration = geneticStatistics
					.getNewOffspringForIteration();

			// creiamo una lista di key contenente le chiavi da associare ai
			// chromosomi
			List<Key> keyList = GAKeyFactory.getGAKeyFactoryInstance()
					.getChromosomeKeyList();

			List<Chromosome> population = chromosomeFactory
					.createPopulation(populationSize + newOffspringForIteration);

			if (population.size() == populationSize + newOffspringForIteration) {
				// settiamo key, fitness e islandId

				Iterator<Key> keyIterator = keyList.iterator();
				Iterator<Chromosome> chrIterator = population.iterator();

				// gli individui da 0 a populationsize sono inizializzati con
				// fitness null perchè costituiscono la popolazione iniziale e
				// devono essere valutati dai MapWorker
				Integer islandId = 0;
				Integer individualsPerIsland = new Double(Math
						.ceil(populationSize / islandNum)).intValue();
				Chromosome chr;
				for (Integer i = 0; i < populationSize; i++) {
					chr = chrIterator.next();
					chr.setFitness(null);
					chr.setKey(keyIterator.next());
					if (i % individualsPerIsland == 0)
						islandId++;
					chr.setIslandId(islandId);
					// initializedPopulation.add(chr);
				}

				// gli individui da populationsize a populationSize +
				// newOffspringForiteration sono inizializzati con fitness
				// Double.MIN_VALUE perchè dovranno essere selezionati dal
				// ReduceWorker come "riutilizzabili"
				islandId = 0;
				individualsPerIsland = new Double(Math
						.ceil(newOffspringForIteration / islandNum)).intValue();
				for (Integer i = 0; i < newOffspringForIteration; i++) {
					chr = chrIterator.next();
					chr.setFitness(Double.MAX_VALUE);
					chr.setKey(keyIterator.next());
					if (i % individualsPerIsland == 0)
						islandId++;
					chr.setIslandId(islandId);
					// initializedPopulation.add(chr);
				}

				data.saveAll(population);

				/**
				 * ------------------------------------------------------------
				 * -- INSERMIENTO DELLA POPOLAZIONE NELLA CACHE
				 * 
				 * inseriamo i cromosomi creati e l'oggetto GeneticStatistics
				 * ------------------------------------------------------------
				 */
				Cache c = GAEUtils.getCache();
				
				c.put(geneticStatistics.getKey(), geneticStatistics);
				for (Chromosome temp : population) {
					c.put(temp.getKey(), temp);
				}

				String taskName = MAP_TASK_NAME + geneticStatistics.getIdTask()
						+ geneticStatistics.getActualIteration();
				String queueName = null;
				GAEUtils.startMapJob("geneticmapworker", taskName, queueName);
			} else {
				// TODO: Gestione dell'errore
			}
		}
	}

}
