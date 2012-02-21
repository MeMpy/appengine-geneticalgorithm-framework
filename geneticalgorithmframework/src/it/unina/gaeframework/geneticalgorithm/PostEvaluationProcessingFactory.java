package it.unina.gaeframework.geneticalgorithm;

import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

/**
 * Factory che fornisce il PostEvaluationProcessing specificato nel file di
 * configurazione geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public abstract class PostEvaluationProcessingFactory {

	/**
	 * metodo che recupera il PostEvaluationProcessing specificato nel file di
	 * configurazione geneticalgorithm.xml
	 * 
	 * @return PostEvaluationProcessing se è stato specificato nel file xml,
	 *         null altrimenti
	 */
	public static PostEvaluationProcessing getConcretePostEvaluationProcessing() {
		// lettura del file di configurazione geneticalgorithm.xml
		GeneticAlgorithmXMLConfigReader geneticConfig = GeneticAlgorithmXMLConfigReader
				.getInstance();

		// lettura del nome del ConcreteFitnessEvaluator
		String concretePostEvaluationProcessingClassName = geneticConfig
				.getPostEvaluationProcessingClass();
		if (concretePostEvaluationProcessingClassName == null)
			return null;

		PostEvaluationProcessing cf = null;

		// istanziazione del ConcreteFitnessEvaluator
		try {
			cf = (PostEvaluationProcessing) Class.forName(
					concretePostEvaluationProcessingClassName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return cf;
	}
}
