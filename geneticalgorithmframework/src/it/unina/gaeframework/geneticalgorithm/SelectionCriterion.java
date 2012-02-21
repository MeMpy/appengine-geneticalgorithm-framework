package it.unina.gaeframework.geneticalgorithm;

import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

import java.util.List;

/**
 * interfaccia che definisce un criterio di selezione
 * @author barren
 *
 */
public interface SelectionCriterion {

	/**
	 * metodo che serve a selezionare gli individui da far accoppiare gli
	 * individui saranno accoppiati a due a due secondo l'ordine si inserimento
	 * nella lista
	 * 
	 * @param population
	 *            lista di individui su cui effettuare la selezione
	 * @param geneticStatistics
	 *            informazioni generali sull'esecuzione
	 * @return lista di individui selezionati
	 */
	public List<Chromosome> selectedForCrossover(List<Chromosome> population,
			GeneticStatistics geneticStatistics);
}
