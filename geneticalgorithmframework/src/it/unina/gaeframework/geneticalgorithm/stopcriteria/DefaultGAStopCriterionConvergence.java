package it.unina.gaeframework.geneticalgorithm.stopcriteria;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

import java.util.LinkedList;

/**
 * Classe che definisce un criterio di stop di Default per il GA basato sulla
 * convergenza della fitness trovata.
 * 
 * Il criterio è soddisfatto se nelle ultime 'iterationswithnofitnessincrement'
 * iterazioni il miglior risultato ottenuto non ha migliorato il precedente di
 * almeno la quantità 'fitnessincrement' ad ogni iterazione
 * 
 * Sia iterationswithnofitnessincrement che fitnessincrement si possono
 * impostare nel file geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public class DefaultGAStopCriterionConvergence implements GAStopCriterion {

	@Override
	public Boolean checkGAStopCriterion(GeneticStatistics geneticStatistics) {
		// recupera i dati necessari da geneticStatistics
		Integer iterationsWithNoFitnessIncrement = geneticStatistics
				.getIterationsWithNoFitnessIncrement();
		Double fitnessIncrement = geneticStatistics.getFitnessIncrement();
		LinkedList<Double> oldBestFitnessList = geneticStatistics
				.getOldBestFitnessList();
		Double newBestFitness = geneticStatistics.getBestFitness();

		// nel caso in cui non ci siano precedenti valutazioni il criterio non è
		// soddisfatto
		if (oldBestFitnessList == null || oldBestFitnessList.size() <= 0)
			return false;

		// nel caso in cui non c'è nessuna attuale iterazione il criterio non è
		// soddisfatto
		if (newBestFitness == null)
			return false;

		// se non sono state fatte abbastanza iterazioni per verificare il
		// criterio di stop allora il criterio non è soddisfatto
		if (oldBestFitnessList.size() < iterationsWithNoFitnessIncrement)
			return false;

		// se ci sono abbastanza vecchie valutazioni, e quindi sono state fatte
		// abbastanza iterazioni da verificare il criterio di stop, vediamo se è
		// soddisfatto
		Double oldBestFitness = oldBestFitnessList.pop();
		Double diff = Math.abs(newBestFitness - oldBestFitness);

		if (diff > fitnessIncrement)
			return false;

		for (Integer i = 0; i < (iterationsWithNoFitnessIncrement - 1); i++) {
			newBestFitness = oldBestFitness;
			oldBestFitness = oldBestFitnessList.pop();
			diff = Math.abs(newBestFitness - oldBestFitness);

			if (diff > fitnessIncrement)
				return false;

		}

		return true;
	}
}
