package it.unina.gaeframework.geneticalgorithm.stopcriteria;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

/**
 * Definizione di un criterio di terminazione del GA
 * 
 * @author barren
 * 
 */
public interface GAStopCriterion {

	/**
	 * metodo che consente di definire il criterio di Stop del GA. se ritorna
	 * true allora il criterio è verificato e l'algoritmo deve interrompersi
	 * 
	 * @param geneticStatistics
	 *            statistiche dell'algoritmo genetico necessarie a verificare il
	 *            criterio di stop
	 * @return true in caso il criterio di stop sia verificato, false altrimenti
	 */
	public Boolean checkGAStopCriterion(GeneticStatistics geneticStatistics);
}
