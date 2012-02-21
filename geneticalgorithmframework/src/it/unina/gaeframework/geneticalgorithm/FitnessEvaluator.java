package it.unina.gaeframework.geneticalgorithm;


/**
 * classe che fornisce un metodo per valutare un Chromosome
 * 
 * @author barren
 * 
 */
public interface FitnessEvaluator {

	/**
	 * metodo che valuta la fitness di un Chromosome passato come argomento
	 * 
	 * @param chromosome
	 *            cromosoma di cui si vuole valutare la fitness
	 * @param appEngineContext
	 *            AppEngineContext del Map utile per recuperare informazioni
	 *            all'interno della funzione di fitness o per effettuare
	 *            salvatagi "batch"
	 * @return valore di fitness calcolato
	 */
	public Double computeFitness(Chromosome chromosome);
}
