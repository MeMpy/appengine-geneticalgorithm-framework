package it.unina.gaeframework.geneticalgorithm.stopcriteria;

import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

public abstract class UserStopCriterionFactory {
	
	public static GAStopCriterion getUserStopCriterion() {
		// lettura del file di configurazione geneticalgorithm.xml
		GeneticAlgorithmXMLConfigReader geneticConfig = GeneticAlgorithmXMLConfigReader
				.getInstance();

		// lettura del nome del ConcreteFitnessEvaluator
		String userStopCriterionClassName = geneticConfig
				.getUserStopCriterionClass();
		if (userStopCriterionClassName == null || userStopCriterionClassName.equals(""))
			return null;

		GAStopCriterion sc = null;

		// istanziazione del ConcreteFitnessEvaluator
		try {
			sc = (GAStopCriterion) Class.forName(
					userStopCriterionClassName).newInstance();
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
