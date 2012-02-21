package it.unina.gaeframework.geneticalgorithm.stopcriteria;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

/**
 * Classe che definisce un criterio di stop di Default per il GA basato sul
 * minimo di fitness da raggiungere
 * 
 * Il criterio è soddisfatto se la fitness raggiunta è maggiore o uguale a
 * 'minfitnesstoreach'
 * 
 * minfitnesstoreach si può impostare nel file geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public class DefaultGAStopCriterionMinFitnessToReach implements GAStopCriterion {

	@Override
	public Boolean checkGAStopCriterion(GeneticStatistics geneticStatistics) {
		Double bestFitness = geneticStatistics.getBestFitness();
		Double minFitnessToReach = geneticStatistics.getMinFitnessToReach();
		if (bestFitness == null)
			return false;
		return (bestFitness <= minFitnessToReach);
	}
}
