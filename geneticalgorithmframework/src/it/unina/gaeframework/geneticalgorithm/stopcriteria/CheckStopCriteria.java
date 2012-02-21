package it.unina.gaeframework.geneticalgorithm.stopcriteria;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

import java.util.LinkedList;
import java.util.List;

/**
 * Classe che consente di verificare se sono verificati i criteri di stop del GA
 * Sono presenti criteri di default i cui parametri sono settabili nel file
 * geneticalgorithm.xml, ed inoltre è possibile aggiungere un criterio di stop
 * personalizzato, definendo una classe che implementa l'interfaccia
 * GAStopCriterion ed indicandola nel file di configurazione
 * geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public class CheckStopCriteria {

	private List<GAStopCriterion> stopCriteria = null;
	private GeneticStatistics geneticStatistics = null;

	public CheckStopCriteria(GeneticStatistics geneticStatistics) {
		this.geneticStatistics = geneticStatistics;

		// inizializza la lista con quelli di default
		initializeDefaults();

		// aggiunge il criterio di stop definito dall'utente
		initializeUserCriterion();
	}

	/**
	 * metodo che istanzia i criteri di default
	 */
	private void initializeDefaults() {
		this.stopCriteria = new LinkedList<GAStopCriterion>();
		this.stopCriteria.add(new DefaultGAStopCriterionIteration());
		this.stopCriteria.add(new DefaultGAStopCriterionMinFitnessToReach());
		this.stopCriteria.add(new DefaultGAStopCriterionConvergence());
	}

	/**
	 * metodo che istanzia il criterio di stop definito dall'utente, indicato
	 * dal parametro userCriterionClassName
	 */
	private void initializeUserCriterion() {
		GAStopCriterion userStopCriterion = null;
		userStopCriterion = UserStopCriterionFactory.getUserStopCriterion();
		
		if (userStopCriterion != null) {
			this.stopCriteria.add(userStopCriterion);
		}
	}

	/**
	 * metodo che consente di verificare se il criterio di stop è verificato o
	 * meno
	 * 
	 * @return true se il criterio di stop è verificato, per cui bisogna fermare
	 *         l'algoritmo GA; false altrimenti
	 */
	public Boolean checkStopCriteria() {
		for (GAStopCriterion stopCriterion : this.stopCriteria) {
			if (stopCriterion.checkGAStopCriterion(this.geneticStatistics))
				return true;
		}
		return false;
	}
}
