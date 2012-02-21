package it.unina.gaeframework.geneticalgorithm;

import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

/**
 * Definizione di un Factory che fornisce metodi per creare una popolazione di
 * Chromosome, per convertire un Entity del Datastore di Google App Engine in un
 * Chromosome e per ottenere la classe concreta dei Chromosome utilizzati
 * 
 * @author barren
 * 
 */
public interface ChromosomeFactory {

	/**
	 * crea una popolazione di Chromosome<T>
	 * 
	 * @param populationSize
	 *            numero di individui da creare
	 * @return la popolazione creata
	 */
	public List<Chromosome> createPopulation(Integer populationSize);

	/**
	 * metodo che converte un Entity del datastore di Google App Engine in un
	 * Chromosome
	 * 
	 * @param ent
	 *            Entity del Datastore
	 * @param key
	 *            Key associata all'Entity
	 * @return il Chromosome mappato nell'Entity
	 */
	public Chromosome convertEntityToChromosome(Entity ent, Key key);

	/**
	 * metodo che converte un Chromosome in un Entity del datastore di Google
	 * App Engine, necessario per l'ottimizzazione di MapReduce
	 * 
	 * @param chr
	 *            Chromosome del Datastore
	 * @param key
	 *            Key associata all'Entity
	 * @return Entity che mappa il Chromosome
	 */
	public Entity convertChromosomeToEntity(Chromosome chr, Key key);

	/**
	 * metodo che restituisce la classe del Chromosome utilizzato nel GA
	 * 
	 * @return Class<?> del Chromosome utilizzato
	 */
	public Class<?> getConcreteChromosomeClass();
}
