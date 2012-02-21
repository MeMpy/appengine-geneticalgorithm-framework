package it.unina.gaeframework.geneticalgorithm.stopcriteria;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

/**
 * Classe che definisce un criterio di stop di Default per il GA basato sul
 * numero di iterazioni effettuate
 * 
 * Il criterio è soddisfatto se è stato effettuato il numero di iterazioni
 * massime 'maxiterations'
 * 
 * maxiterations si può impostare nel file geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public class DefaultGAStopCriterionIteration implements GAStopCriterion {

	@Override
	public Boolean checkGAStopCriterion(GeneticStatistics geneticStatistics) {
		return geneticStatistics.getActualIteration() > geneticStatistics
				.getMaxIterations();
	}
}
