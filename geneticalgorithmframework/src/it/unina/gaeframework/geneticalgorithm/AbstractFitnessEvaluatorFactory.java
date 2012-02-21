package it.unina.gaeframework.geneticalgorithm;

import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

/**
 * AbstractFactory che fornisce il FitnessEvaluator specificato nel file di
 * configurazione geneticalgorithm.xml specifico per i cromosomi usati nel GA
 * 
 * @author barren
 * 
 */
public abstract class AbstractFitnessEvaluatorFactory {

	/**
	 * metodo che recupera il FitnessEvaluator specificato nel file di
	 * configurazione geneticalgorithm.xml
	 * 
	 * @return FitnessEvaluator specifico per i cromosomi usati nel GA
	 */
	public static FitnessEvaluator getConcreteFitnessEvaluator() {
		// lettura del file di configurazione geneticalgorithm.xml
		GeneticAlgorithmXMLConfigReader geneticConfig = GeneticAlgorithmXMLConfigReader
				.getInstance();

		// lettura del nome del ConcreteFitnessEvaluator
		String concreteFitnessEvaluatorClassName = geneticConfig
				.getFitnessEvaluatorClass();
		FitnessEvaluator cf = null;

		// istanziazione del ConcreteFitnessEvaluator
		try {
			cf = (FitnessEvaluator) Class.forName(
					concreteFitnessEvaluatorClassName).newInstance();
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
