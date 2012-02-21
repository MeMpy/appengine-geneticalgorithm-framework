package it.unina.gaeframework.geneticalgorithm;

import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

/**
 * Factory che fornisce il SelectionCriterion specificato nel file di
 * configurazione geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public abstract class SelectionCriterionFactory {

	/**
	 * metodo che recupera il SelectionCriterion specificato nel file di
	 * configurazione geneticalgorithm.xml
	 * 
	 * @return SelectionCriterion se è stato specificato nel file xml, null
	 *         altrimenti
	 */
	public static SelectionCriterion getConcreteSelectionCriterion() {
		// lettura del file di configurazione geneticalgorithm.xml
		GeneticAlgorithmXMLConfigReader geneticConfig = GeneticAlgorithmXMLConfigReader
				.getInstance();

		// lettura del nome del ConcreteFitnessEvaluator
		String concreteSelectionCriterionClassName = geneticConfig
				.getSelectionCriterion();
		if (concreteSelectionCriterionClassName == null)
			return null;

		SelectionCriterion sc = null;

		// istanziazione del ConcreteFitnessEvaluator
		try {
			sc = (SelectionCriterion) Class.forName(
					concreteSelectionCriterionClassName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return sc;
	}
}
