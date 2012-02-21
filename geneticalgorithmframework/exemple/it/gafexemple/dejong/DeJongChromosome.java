package it.gafexemple.dejong;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.jdo.annotations.PersistenceCapable;

import it.unina.gaeframework.geneticalgorithm.Chromosome;

@SuppressWarnings("serial")
@PersistenceCapable
public class DeJongChromosome extends Chromosome {
	
	
	private Double singleBitMutationProbability = 0.001;
	private final Integer lowerBound = -512;
	private final Integer upperBound = 512;
	
	private Integer deJongNumVar=10;
	
	public DeJongChromosome(){
		super();
		List<Integer> genes = new LinkedList<Integer>();
		for(int i=0; i< deJongNumVar; i++){
			genes.add(initializeValue());
		}
		this.setGenes(genes);
		this.setFitness(Double.MAX_VALUE);
		this.setGenerationId(0);
		
		
	}
	
	
	private Integer initializeValue() {
		Random rand = new Random();
		Integer value;
		Long interval = new Long(upperBound) - new Long(lowerBound);
		if (interval > Integer.MAX_VALUE)
			value = rand.nextInt();
		else
			value = rand.nextInt(interval.intValue()) + lowerBound;
		
		return value;
	}
	
	
	@Override
	protected List<List<?>> crossover(List<?> ownGenes, List<?> partnerGenes) {
		List<List<?>> newGenes = new LinkedList<List<?>>();

		List<Object> firstOffspringGenes = new LinkedList<Object>();
		List<Object> secondOffspringGenes = new LinkedList<Object>();

		/**
		 * ================================================================
		 * SINGLE POINT CROSSOVER
		 * ================================================================
		 */
		Random r = new Random();
		

		Integer point = r.nextInt(ownGenes.size() - 1);

		Iterator<?> ownGenesIterator = ownGenes.iterator();
		Iterator<?> partnerGenesIterator = partnerGenes.iterator();

		Integer index = 0;

		while (ownGenesIterator.hasNext()) {
			if (index <= point) {
				firstOffspringGenes.add(ownGenesIterator.next());
				secondOffspringGenes.add(partnerGenesIterator.next());
			} else {
				firstOffspringGenes.add(partnerGenesIterator.next());
				secondOffspringGenes.add(ownGenesIterator.next());
			}
			index++;
		}

		/**
		 * ================================================================
		 * aggiungiamo i due codici genetici alla lista ritornata
		 * ================================================================
		 */
		newGenes.add(firstOffspringGenes);
		newGenes.add(secondOffspringGenes);
		return newGenes;
	}
	
	@Override
	protected Object mutateGene(Object oldGene) {
		Integer value = (Integer) oldGene;
		
		Random r = new Random();
		
		Integer greyCodeValue = encodeGreyCode(value);
		Integer mutationNumber = 0;
		for (Integer i = 0; i < 31; i++) {
			if (r.nextDouble() <= singleBitMutationProbability) {
				mutationNumber = new Double(Math.pow(2d, new Double(i)))
						.intValue();
				greyCodeValue = greyCodeValue ^ mutationNumber;
				
			}
		}
		if (r.nextDouble() <= singleBitMutationProbability) {
			mutationNumber = Integer.MIN_VALUE;
			greyCodeValue = greyCodeValue ^ mutationNumber;
		}
		
		value = decodeGreyCode(greyCodeValue);
		return value;
	}
	
	
	private Integer decodeGreyCode(Integer num) {
		for(int i = (num >>> 1); i!=0; ){
			num^=i;
			i >>>= 1;
		}
		return num;
	}

	private Integer encodeGreyCode(Integer num) {
		return num ^ (num >>> 1);
	}
	
	
	@Override
	public String toString() {
		String toString="";
		toString+=getKey()+" \n";
		toString+="codice: ";
		List<Integer> codice= (List<Integer>) this.getGenes();
		for(Integer i: codice)
			toString+=i+ ", ";
		toString+="\n fitness= "+ this.getFitness();
		
		return toString;
	}
	
	
//	public static void main(String args[]){
//		Chromosome c= new DeJongChromosome();
//		System.out.println(c);
//	}

}
