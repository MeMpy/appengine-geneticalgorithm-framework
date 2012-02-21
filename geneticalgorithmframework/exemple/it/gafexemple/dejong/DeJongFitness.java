package it.gafexemple.dejong;

import java.util.List;

import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.FitnessEvaluator;

public class DeJongFitness implements FitnessEvaluator {

	@Override
	public Double computeFitness(Chromosome chromosome) {
		DeJongChromosome realChr=(DeJongChromosome) chromosome;
		
		List<Integer> input= (List<Integer>) realChr.getGenes();
		
		Double fitness=computeDeJongFunction(input);
		
		return fitness;
	}

	private Double computeDeJongFunction(List<Integer> input) {
		Double sum=0d;
		for(Integer x: input){
			sum+= Math.pow(x, 2);
		}
		return sum;
	}
	
	

}
