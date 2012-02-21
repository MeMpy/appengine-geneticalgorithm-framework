package it.unina.gaeframework.geneticalgorithm.task;

import it.unina.gaeframework.geneticalgorithm.AbstractChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.PostEvaluationProcessing;
import it.unina.gaeframework.geneticalgorithm.PostEvaluationProcessingFactory;
import it.unina.gaeframework.geneticalgorithm.SelectionCriterion;
import it.unina.gaeframework.geneticalgorithm.SelectionCriterionFactory;
import it.unina.gaeframework.geneticalgorithm.statistics.ChromosomeDataContainer;
import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;
import it.unina.gaeframework.geneticalgorithm.stopcriteria.CheckStopCriteria;
import it.unina.gaeframework.geneticalgorithm.util.GAEUtils;
import it.unina.gaeframework.geneticalgorithm.util.GAKeyFactory;
import it.unina.tools.datastore.DatastoreLoadAndSave;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;

import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
public class GeneticReduceWorkerTask extends HttpServlet {

	public static final String CHECK_STOP_TASK_NAME = "stopTask";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		DatastoreLoadAndSave data = new DatastoreLoadAndSave();
		Cache cache = GAEUtils.getCache();

		List<Chromosome> popolation = null;
		GAKeyFactory gaKeyFactory = GAKeyFactory.getGAKeyFactoryInstance();

		// recuperiamo l'oggetto geneticStatistics
		Key geneticStatisticsKey = gaKeyFactory.getGenetisStatisticsKey();

		// proviamo a recuperarlo prima dalla Cache
		GeneticStatistics geneticStatistics = (GeneticStatistics) cache
				.get(geneticStatisticsKey);

		if (geneticStatistics == null) {
			// nel caso in cui il caricamento dalla cache non è andato a buon
			// fine carichiamo dal datastore
			geneticStatistics = data.loadObjectById(GeneticStatistics.class,
					geneticStatisticsKey.getId());
		}

		/**
		 * RECUPERIAMO LA LISTA DI CHROMOSOME SU CUI OPERARE
		 */

		// proviamo a recuperarla dalla Cache
		// TODO: se viene fatto per isole recuperare solo gli individui di una
		// data isola
		List<Key> keySet = gaKeyFactory.getChromosomeKeyList();
		popolation = new LinkedList<Chromosome>();

		Iterator<Key> keyIterator = keySet.iterator();
		Chromosome chr = null;

		while (keyIterator.hasNext() && popolation != null) {
			chr = (Chromosome) cache.get(keyIterator.next());
			if (chr != null)
				popolation.add(chr);
			else
				popolation = null;
		}

		// Se la lista dei cromosomi non è più nella cache prenderla dal
		// datastore
		if (popolation == null) {
//			Map<String, Object> parameters = new HashMap<String, Object>();
			// TODO: SE VIENE FATTO IN PARALLELO PER ISOLA RECUPERARE L'ID
			// DELL'ISOLA DAL CONTEXT
			// Gli individui sono tutti sulla stessa isola con id 1
//			Integer islandId = 1;
//			parameters.put("islandId", islandId);

			Class<?> concreteChromosomeClass = AbstractChromosomeFactory
					.getConcreteChromosomeFactory()
					.getConcreteChromosomeClass();
//			popolation = (List<Chromosome>) data.load(parameters,
//					concreteChromosomeClass);
			
			popolation = (List<Chromosome>) data.load(null, concreteChromosomeClass);
		}

		// invochiamo il metodo di postevaluationprocessing, se specificato nel
		// file di configurazione
		PostEvaluationProcessing postEvaluationProcessing = PostEvaluationProcessingFactory
				.getConcretePostEvaluationProcessing();
		if (postEvaluationProcessing != null)
			postEvaluationProcessing.postEvaluationProcessing(popolation);

		// ordiniamo la popolazione in base alla fitness
		Collections.sort(popolation);

		/**
		 * VERIFICHIAMO IL CRITERIO DI STOP
		 */
		Chromosome bestIndividual = popolation.get(0);

		geneticStatistics.setBestFitness(bestIndividual.getFitness());
		geneticStatistics.setBestIndividualId(bestIndividual.getKey().getId());
		geneticStatistics.setNewGenerationId(geneticStatistics
				.getActualIteration() + 1);

		CheckStopCriteria checkStopCriteria = new CheckStopCriteria(
				geneticStatistics);
		geneticStatistics.setCheckStop(checkStopCriteria.checkStopCriteria());
		if (!geneticStatistics.getCheckStop()) {
			/**
			 * SURVIVE: ELIMINIAMO DAL DATASTORE LE ENTITA' CHE NON SOPRAVVIVONO
			 * TODO: Bisogna considerare la possibilità di utilizzare criteri
			 * diversi (vedi selezione) Se il nuovo criterio non presuppone un
			 * ordinamento bisogna cambiare la selezione del miglior individuo
			 */
			Integer individualsToSave = geneticStatistics.getPopulationSize();

			List<Chromosome> survivors = null;
			List<Chromosome> individualsToKill = null;

			// recuperiamo la lista dei sopravvissuti e la lista degli individui
			// da
			// eliminare
			if (popolation.size() > individualsToSave) {
				survivors = popolation.subList(0, individualsToSave);
				individualsToKill = popolation.subList(individualsToSave,
						popolation.size());
			} else {
				survivors = new LinkedList<Chromosome>(popolation);
				individualsToKill = new LinkedList<Chromosome>();
			}

			// gli individui da eliminare verranno rimpiazzati dai nuovi
			// individui.
			// in questo modo si risparmia una cancellazione nel datastore
			List<Key> individualsToKillKeys = new LinkedList<Key>();
			for (Chromosome individualToKill : individualsToKill)
				individualsToKillKeys.add(individualToKill.getKey());

			/**
			 * SELEZIONE COPPIE PER IL CROSSOVER
			 */

			List<Chromosome> selectedForCrossover = null;

			SelectionCriterion selectionCriterion = SelectionCriterionFactory
					.getConcreteSelectionCriterion();
			if (selectionCriterion == null)
				throw new RuntimeException(
						"GENETICREDUCEWORKER: SelectionCriterion non specificato");

			selectedForCrossover = selectionCriterion.selectedForCrossover(
					popolation, geneticStatistics);

			// Recuperiamo il numero di individui da generare
			Integer numOffsprings = geneticStatistics
					.getNewOffspringForIteration();

			// verifichiamo che la selezione abbia restituito il numero giusto
			// di individui
			if (selectedForCrossover == null
					|| selectedForCrossover.size() < numOffsprings)
				throw new RuntimeException(
						"GENETICREDUCEWORKER: il SelectionCriterion specificato non ha selezionato un numero sufficiente di individui");

			/**
			 * CROSSOVER
			 */

			List<Chromosome> offsprings = new LinkedList<Chromosome>();
			Iterator<Chromosome> parentIterator = selectedForCrossover
					.iterator();

			Iterator<Chromosome> recycleIterator = individualsToKill.iterator();

			Chromosome chrParentA = null;
			Chromosome chrParentB = null;
			Chromosome chrFirstOffspring = null;
			Chromosome chrSecondOffspring = null;

			// TODO: controllare che le due liste siano della stessa dimensione
			// ed entrambe PARI!!!
			while (parentIterator.hasNext()) {
				try {
					chrParentA = parentIterator.next();
					chrParentB = parentIterator.next();
					chrFirstOffspring = recycleIterator.next();
					chrSecondOffspring = recycleIterator.next();
				} catch (NoSuchElementException e) {
					chrParentA = null;
					chrParentB = null;
					chrFirstOffspring = null;
					chrSecondOffspring = null;
				}
				if (chrParentA != null && chrParentB != null) {
					if (chrFirstOffspring != null && chrSecondOffspring != null) {

						chrParentA.crossOver(chrParentB, chrFirstOffspring,
								chrSecondOffspring);
						offsprings.add(chrFirstOffspring);
						offsprings.add(chrSecondOffspring);
					}
				}
			}

			/**
			 * MUTATION SUI NUOVI settiamo anche l'attributo Key dei nuovi
			 * oggetti
			 */

			Iterator<Key> individualsKeyIterator = individualsToKillKeys
					.iterator();
			for (Chromosome cr : offsprings) {
				cr.mutation();
				cr.setKey(individualsKeyIterator.next());
				cr.setFitness(null);
				cr.setGenerationId(geneticStatistics.getActualIteration());
			}

			/**
			 * SALVATAGGIO NEL DATASTORE
			 */

			data.saveAll(offsprings);

			/**
			 * ---------------------------------------------------------------
			 * AGGIORNIAMO ANCHE LA CACHE CON LA NUOVA LISTA DI CROMOSOMI
			 * ---------------------------------------------------------------
			 */
			for (Chromosome offSpring : offsprings) {
				// keySet.add(chr.getKey());
				cache.put(offSpring.getKey(), offSpring);
			}

		} else {
			// Salviamo i dati del cromosoma migliore in una entità che non
			// verrà cancellata
			ChromosomeDataContainer chrData = new ChromosomeDataContainer(
					bestIndividual);
			chrData.setTotalIterationDone(geneticStatistics
					.getActualIteration());

			Long computationTimeInMillis = Calendar.getInstance()
					.getTimeInMillis()
					- geneticStatistics.getStartTimeInMillis();

			chrData.setComputationTimeInMillis(computationTimeInMillis);

			Integer totalEvaluationDone = geneticStatistics.getPopulationSize()
					+ ((geneticStatistics.getActualIteration() - 1) * geneticStatistics
							.getNewOffspringForIteration());

			chrData.setTotalEvaluationDone(totalEvaluationDone);
			data.save(chrData);

			geneticStatistics
					.setChromosomeContainerId(chrData.getKey().getId());
		}

		// salviamo geneticStatistics nel datastore e nella cache
		data.save(geneticStatistics);
		cache.put(geneticStatistics.getKey(), geneticStatistics);

		// lanciamo il task che verifica il criterio di stop e rilancia i map
		// recuperiamo idTask e iterazione attuale
		String idTask = geneticStatistics.getIdTask();
		String actualIteration = "" + geneticStatistics.getActualIteration();

		// accodiamo il checkStopTask
		String taskName = CHECK_STOP_TASK_NAME + idTask + actualIteration;
		String queueName = "geneticqueue";
		String taskUrl = "/checkstoptask";
		Map<String, String> httpParameters = new HashMap<String, String>();
		httpParameters.put("idTask", idTask);
		httpParameters.put("actualIteration", actualIteration);

		GAEUtils.enqueueTask(taskName, queueName, taskUrl, httpParameters);
	}

	// /**
	// * metodo per selezionare la lista di individui per il crossover
	// *
	// * @param popolation
	// * lista di Chromosome da cui selezionare gli individui per il
	// * crossover
	// * @param numChromosomeToSelect
	// * numero di individui da selezionare
	// * @param selectionCriterion
	// * criterio con cui effettuare la selezione
	// * @return la lista di individui selezionati
	// */
	// private List<Chromosome> selectionForCrossover(List<Chromosome>
	// popolation,
	// Integer numChromosomeToSelect, String selectionCriterion) {
	//
	// // lista che conterrà i Chromosome selezionati
	// List<Chromosome> selectedForCrossover = new ArrayList<Chromosome>();
	//
	// // crea una copia della lista dei chromosomi su cui operare
	// List<Chromosome> popolationToSelect = new ArrayList<Chromosome>(
	// popolation);
	//
	// if (selectionCriterion.equals(GeneticStatistics.RANDOM_SELECTION)) {
	// Random r = new Random();
	// Integer extracted = null;
	// Chromosome c = null;
	//
	// for (int i = 0; i < numChromosomeToSelect; i++) {
	//
	// // controlla che ci siano ancora elementi da cui estrarre i
	// // chromosomi
	// if (popolationToSelect.size() > 0) {
	// // se ci sono elementi effettuiamo l'estrazione
	// extracted = r.nextInt(popolationToSelect.size());
	// c = popolationToSelect.remove(extracted.intValue());
	//
	// // potrebbe ipoteticamente succedere che in lista ci siano
	// // elementi null. Se un tale elemento viene estratto lo
	// // scartiamo e proseguiamo con l'estrazione.
	// if (c == null)
	// i--;
	// else
	// selectedForCrossover.add(c);
	// } else {
	//
	// // se non ci sono più elementi verifica di averne estratti
	// // un numero pari. in caso contrario elimina un elemento
	// // dalla lista
	// if (selectedForCrossover.size() % 2 != 0)
	// selectedForCrossover
	// .remove(selectedForCrossover.size() - 1);
	// return selectedForCrossover;
	// }
	// }
	// } else if (selectionCriterion.equals(GeneticStatistics.BEST_SELECTION)) {
	//
	// Chromosome c = null;
	//
	// for (int i = 0; i < numChromosomeToSelect; i++) {
	//
	// // controlla che ci siano ancora elementi da cui estrarre i
	// // chromosomi
	// if (popolationToSelect.size() > 0) {
	// // se ci sono elementi effettuiamo l'estrazione
	// c = popolationToSelect.remove(0);
	//
	// // potrebbe ipoteticamente succedere che in lista ci siano
	// // elementi null. Se un tale elemento viene estratto lo
	// // scartiamo e proseguiamo con l'estrazione.
	// if (c == null)
	// i--;
	// else
	// selectedForCrossover.add(c);
	//
	// } else {
	// // se non ci sono più elementi verifica di averne estratti
	// // un numero pari. in caso contrario elimina un elemento
	// // dalla lista
	// if (selectedForCrossover.size() % 2 != 0)
	// selectedForCrossover
	// .remove(selectedForCrossover.size() - 1);
	// return selectedForCrossover;
	// }
	// }
	// } else if (selectionCriterion
	// .equals(GeneticStatistics.HYBRID_SELECTION)) {
	// Random r = new Random();
	// Integer extracted = null;
	// Chromosome c = null;
	// int i = 0;
	//
	// // prendiamo la prima metà dai migliori
	// Integer numToSelect = numChromosomeToSelect / 2;
	// List<Chromosome> bestSelected = new ArrayList<Chromosome>();
	// for (i = 0; i < numToSelect; i++) {
	//
	// // controlla che ci siano ancora elementi da cui estrarre i
	// // chromosomi
	// if (popolationToSelect.size() > 0) {
	// // se ci sono elementi effettuiamo l'estrazione
	// c = popolationToSelect.remove(0);
	//
	// // potrebbe ipoteticamente succedere che in lista ci siano
	// // elementi null. Se un tale elemento viene estratto lo
	// // scartiamo e proseguiamo con l'estrazione.
	// if (c == null)
	// i--;
	// else
	// bestSelected.add(c);
	// } else {
	// //
	// // // se non ci sono più elementi verifica di averne
	// // estratti
	// // // un numero pari. in caso contrario elimina un elemento
	// // // dalla lista
	// // if (selectedForCrossover.size() % 2 != 0)
	// // selectedForCrossover
	// // .remove(selectedForCrossover.size() - 1);
	// // return selectedForCrossover;
	//
	// }
	// }
	//
	// List<Chromosome> randomSelected = new ArrayList<Chromosome>();
	//
	// // prendiamo i restanti a caso
	// for (int j = 0; j < numToSelect; j++) {
	//
	// // controlla che ci siano ancora elementi da cui estrarre i
	// // chromosomi
	// if (popolationToSelect.size() > 0) {
	// // se ci sono elementi effettuiamo l'estrazione
	// extracted = r.nextInt(popolationToSelect.size());
	// c = popolationToSelect.remove(extracted.intValue());
	//
	// // potrebbe ipoteticamente succedere che in lista ci siano
	// // elementi null. Se un tale elemento viene estratto lo
	// // scartiamo e proseguiamo con l'estrazione.
	// if (c == null)
	// j--;
	// else
	// randomSelected.add(c);
	// } else {
	// //
	// // // se non ci sono più elementi verifica di averne
	// // estratti
	// // // un numero pari. in caso contrario elimina un elemento
	// // // dalla lista
	// // if (selectedForCrossover.size() % 2 != 0)
	// // selectedForCrossover
	// // .remove(selectedForCrossover.size() - 1);
	// // return selectedForCrossover;
	//
	// }
	//
	// }
	//
	// if (bestSelected.isEmpty() || randomSelected.isEmpty()
	// || bestSelected.size() != randomSelected.size())
	// // TODO: Gestione dell'errore
	// throw new RuntimeException("Hybrid selection: error");
	// else {
	// Iterator<Chromosome> randomIterator = randomSelected.iterator();
	// for (Chromosome chr : bestSelected) {
	// selectedForCrossover.add(chr);
	// chr = randomIterator.next();
	// selectedForCrossover.add(chr);
	// }
	//
	// return selectedForCrossover;
	// }
	// } else
	// throw new UnsupportedOperationException();
	// return selectedForCrossover;
	// }
}
