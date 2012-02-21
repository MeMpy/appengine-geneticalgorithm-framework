package it.gafexemple.dejong;

import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.ChromosomeFactory;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class DeJongChrFactory implements ChromosomeFactory {

	@Override
	public List<Chromosome> createPopulation(Integer populationSize) {
		List<Chromosome> population= new LinkedList<Chromosome>();
		
				
		for(int i=0;i<populationSize;i++){
			population.add(new DeJongChromosome());
		}
		
		return population;
	}

	@Override
	public Chromosome convertEntityToChromosome(Entity ent, Key key) {
		DeJongChromosome realType = new DeJongChromosome();

		realType.setKey(key);
		realType
				.setFitness((Double) ent.getProperty(Chromosome.FITNESS_COLUMN));
		realType.setGenerationId(((Long) ent
				.getProperty(Chromosome.GENERATION_ID_COLUMN)).intValue());
		realType.setGenesFromBlob((Blob) ent
				.getProperty(Chromosome.GENES_COLUMN));
		realType.setIslandId(((Long) ent
				.getProperty(Chromosome.ISLAND_ID_COLUMN)).intValue());
		
		return realType;
	}

	@Override
	public Entity convertChromosomeToEntity(Chromosome chr, Key key) {
		Entity entity = null;
		DeJongChromosome chromosome = null;
		// controlliamo che il chromosome sia di tipo TestCaseChromosome
		if (!(chr instanceof DeJongChromosome)) {
			// TODO: Gestire l'errore
		} else {
			chromosome = (DeJongChromosome) chr;
		}

		
		entity = new Entity(key);

		entity.setUnindexedProperty(Chromosome.FITNESS_COLUMN, chromosome.getFitness());
		entity.setUnindexedProperty(Chromosome.GENERATION_ID_COLUMN, chromosome
				.getGenerationId().longValue());
		entity
				.setUnindexedProperty(Chromosome.GENES_COLUMN, chromosome
						.getGenesAsBlob());
		entity.setUnindexedProperty(Chromosome.ISLAND_ID_COLUMN, chromosome
				.getIslandId().longValue());
		return entity;
	}

	@Override
	public Class<?> getConcreteChromosomeClass() {
		return DeJongChromosome.class;
	}

}
