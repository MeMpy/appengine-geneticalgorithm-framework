package it.unina.gaeframework.geneticalgorithm;

import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

/**
 * AbstractFactory che fornisce il Factory per i Cromosomi specificato nel file
 * di configurazione geneticalgorithm.xml
 * 
 * @author barren
 * 
 */
public abstract class AbstractChromosomeFactory {

	/**
	 * metodo che recupera il ChromosomeFactory specificato nel file di
	 * configurazione geneticalgorithm.xml
	 * 
	 * @return un ChromosomeFactory specifico per i cromosomi usati nel GA
	 */
	public static ChromosomeFactory getConcreteChromosomeFactory() {
		// legge il file di configurazione geneticalgorithm.xml
		GeneticAlgorithmXMLConfigReader geneticConfig = GeneticAlgorithmXMLConfigReader
				.getInstance();

		// recupera il nome del Concrete Factory
		String concreteFactoryName = geneticConfig.getChromosomeFactoryClass();
		ChromosomeFactory cf = null;

		// istanziazione del ChromosomeFactory
		try {
			cf = (ChromosomeFactory) Class.forName(concreteFactoryName)
					.newInstance();
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
