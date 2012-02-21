package it.unina.gaeframework.geneticalgorithm;

import java.util.List;

/**
 * interfaccia che definisce un metodo che viene eseguito dopo che sono stati
 * valuitati tutti gli individui e prima della fase di Survive
 * 
 * @author barren
 * 
 */
public interface PostEvaluationProcessing {

	/**
	 * metodo che viene eseguito dopo la valutazione e prima del survive
	 * @param popolation popolazione da processare
	 */
	public void postEvaluationProcessing(List<Chromosome> popolation);
}
