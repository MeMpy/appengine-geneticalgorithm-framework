package it.unina.gaeframework.geneticalgorithm;

import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang.SerializationUtils;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * Definizione di un cromosoma da utilizzare in un algoritmo genetico
 * 
 * @author barren
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Chromosome implements Serializable,
		Comparable<Chromosome> {
	public static final String GENES_COLUMN = "gen";
	public static final String FITNESS_COLUMN = "fit";
	public static final String GENERATION_ID_COLUMN = "idg";
	public static final String ISLAND_ID_COLUMN = "idi";

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	@Column(name = GENES_COLUMN)
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Blob genes;

	@Persistent
	@Column(name = FITNESS_COLUMN)
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Double fitness;

	@Persistent
	@Column(name = GENERATION_ID_COLUMN)
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Integer generationId;

	@Persistent
	@Column(name = ISLAND_ID_COLUMN)
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer islandId;

	/**
	 * Costruttore di default
	 */
	protected Chromosome() {
		super();
	}

	/**
	 * Costruttore protected da richiamare dal costruttore delle sottoclassi
	 * alternativamente impostare i valori usando i metodi set nel costruttore
	 * della sottoclasse
	 * 
	 * @param genes
	 * @param fitness
	 * @param generationId
	 * @param islandId
	 */
	protected Chromosome(List<?> genes, Double fitness, Integer generationId,
			Integer islandId) {
		setGenes(genes);
		setFitness(fitness);
		setGenerationId(generationId);
		setIslandId(islandId);
	}

	/**
	 * metodo che effettua una mutazione del cromosoma sul quale è invocato
	 * 
	 * @return una flag che indica se la mutazione è andata a buon fine o meno
	 */
	public Boolean mutation() {
		List<?> oldGenes = this.getGenes();
		List<Object> newGenes = new LinkedList<Object>();

		// recuperiamo i range dal TestConfXMLConfigReader
		GeneticAlgorithmXMLConfigReader geneticAlgorithmXML = GeneticAlgorithmXMLConfigReader
				.getInstance();
		Double mutationProbability = geneticAlgorithmXML
				.getMutationProbability();
		Double extraction = null;
		for (Object o : oldGenes) {
			extraction = Math.random();
			if (extraction <= mutationProbability) {
				newGenes.add(mutateGene(o));
			} else {
				newGenes.add(o);
			}
		}
		// reimpostiamo il codice genetico
		this.setGenes(newGenes);

		return true;
	}

	/**
	 * 
	 * @param oldGene
	 * @return
	 */
	protected abstract Object mutateGene(Object oldGene);

	/**
	 * metodo che effettua un crossover generando due cromosomi figli a partire
	 * dal cromosoma su cui è invocato e da un partner passato come argomento
	 * 
	 * @param partner
	 */

	public final void crossOver(Chromosome partner, Chromosome firstOffspring,
			Chromosome secondOffspring) {

		/**
		 * ================================================================
		 * RECUPERIAMO IL CODICE GENETICO DEI GENITORI E INIZIALIZZIAMO QUELLO
		 * DEI FIGLI
		 * ================================================================
		 */
		List<?> ownGenes = this.getGenes();
		List<?> partnerGenes = partner.getGenes();

		List<?> firstOffSpringGenes = new LinkedList<Object>();
		List<?> secondOffSpringGenes = new LinkedList<Object>();

		/**
		 * ================================================================
		 * GENERAZIONE DEL CODICE GENETICO DEI FIGLI
		 * ================================================================
		 */
		List<List<?>> newGenes = crossover(ownGenes, partnerGenes);

		if (newGenes.size() < 2) {
			// TODO: Gestire l'errore
		}

		firstOffSpringGenes = newGenes.get(0);
		secondOffSpringGenes = newGenes.get(1);

		/**
		 * ================================================================
		 * IMPOSTIAMO I FIGLI CON I NUOVI CODICI GENETICI RESETTANDO ANCHE LA
		 * FITNESS
		 * ================================================================
		 */
		firstOffspring.setGenes(firstOffSpringGenes);
		firstOffspring.setFitness(null);
		secondOffspring.setGenes(secondOffSpringGenes);
		secondOffspring.setFitness(null);

	}

	/**
	 * metodo che a partire da due codici genetici dei genitori passati come
	 * argomento restituisce una lista contenente i due codici genetici dei
	 * figli ottenuti tramite crossover
	 * 
	 * @param ownGenes
	 *            codice genetico del primo genitore
	 * @param partnerGenes
	 *            codice genetico del secondo genitore
	 * @return la lista dei codici genetici dei figli
	 */
	protected abstract List<List<?>> crossover(List<?> ownGenes, List<?> partnerGenes);
	
	/**
	 * metodo che restituisce i geni che compongono il cromosoma
	 * 
	 * @return una lista di Object che forma il codice genetico del cromosoma
	 */
	public List<?> getGenes() {
		if (genes == null)
			return null;
		return (List<?>) SerializationUtils.deserialize(genes.getBytes());
	}

	/**
	 * metodo che assegna il codice genetico al cromosoma
	 * 
	 * @param genes
	 *            codice genetico da assegnare al cromosoma
	 */
	public void setGenes(List<?> genes) {
		this.genes = new Blob(SerializationUtils
				.serialize((LinkedList<?>) genes));
	}

	/**
	 * metodo per recuperare il codice genetico come Blob
	 * 
	 * @return il Blob contenente il codice genetico
	 */
	public Blob getGenesAsBlob() {
		return this.genes;
	}

	/**
	 * metodo per settare il codice genetico passandogli direttamente un oggetto
	 * Blob del datastore
	 * 
	 * @param genes
	 *            blob contenente il codice genetico
	 */
	public void setGenesFromBlob(Blob genes) {
		this.genes = genes;
	}

	/**
	 * metodo che restituisce il valore di fitness associato al cromosoma
	 * 
	 * @return valore di fitness associato
	 */
	public Double getFitness() {
		return this.fitness;
	}

	/**
	 * metodo che assegna un valore di fitness al cromosoma
	 * 
	 * @param fitness
	 *            valore di fitness da assegnare
	 */
	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	/**
	 * metodo che restituisce un id univoco del cromosoma
	 * 
	 * @return l'id del cromosoma
	 */
	public Key getKey() {
		return this.key;
	}

	/**
	 * metodo che assegna un id al cromosoma
	 * 
	 * @param id
	 *            id da assegnare al cromosoma
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * metodo che restituisce un id univoco dell'isola a cui appartiene il
	 * cromosoma
	 * 
	 * @return l'id dell'isola del cromosoma
	 */
	public final Integer getIslandId() {
		return this.islandId;
	}

	/**
	 * metodo che assegna un id dell'isola al cromosoma
	 * 
	 * @param id
	 *            id dall'isola da assegnare al cromosoma
	 */
	public final void setIslandId(Integer id) {
		this.islandId = id;
	}

	/**
	 * metodo che restituisce un id univoco della generazione a cui appartiene
	 * il cromosoma
	 * 
	 * @return l'id della generazione del cromosoma
	 */
	public final Integer getGenerationId() {
		return this.generationId;
	}

	/**
	 * metodo che assegna un id della generazione al cromosoma
	 * 
	 * @param id
	 *            id della generazione da assegnare al cromosoma
	 */
	public final void setGenerationId(Integer id) {
		this.generationId = id;
	}

	/**
	 * definiamo il metodo compareTo in modo che l'ordinamento indotto sia
	 * crescente in base alla fitness, con eventuali fitness null alla fine
	 */
	@Override
	public final int compareTo(Chromosome o2) {
		Chromosome o1 = this;
		int ret = 0;
		if (o1 == null && o2 == null) {
			ret = 0;
		} else if (o1 == null) {
			ret = 1;
		} else if (o2 == null) {
			ret = -1;
		} else if (o1.getFitness() == null && o2.getFitness() == null) {
			ret = 0;
		} else if (o1.getFitness() == null) {
			ret = 1;
		} else if (o2.getFitness() == null) {
			ret = -1;
		} else
			ret = Double.compare(o1.getFitness(), o2.getFitness());
		return ret;
	}
	
	/**
	 * NECESSARIO (FORSE) PER LA MEMCACHE
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Chromosome)
			return getKey().equals(((Chromosome) obj).getKey());
		else return false;
	}
}
