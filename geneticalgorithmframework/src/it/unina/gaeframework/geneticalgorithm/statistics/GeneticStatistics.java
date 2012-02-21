package it.unina.gaeframework.geneticalgorithm.statistics;

import it.unina.gaeframework.geneticalgorithm.util.GAKeyFactory;
import it.unina.gaeframework.geneticalgorithm.util.GeneticAlgorithmXMLConfigReader;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang.SerializationUtils;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * Classe che mantiene i risultati parziali e finali relativi al GA
 * 
 * @author barren
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable
public class GeneticStatistics implements Serializable {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	@Column(name="bfi")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Double bestFitness;

	@Persistent
	@Column(name="ofl")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Blob oldBestFitnessList;

	@Persistent
	@Column(name="bid")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Long bestIndividualId;

	@Persistent
	@Column(name="gid")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer newGenerationId;

	@Persistent
	@Column(name="ait")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer actualIteration;

	@Persistent
	@Column(name="mit")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer maxIterations;

	@Persistent
	@Column(name="pop")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer populationSize;

	@Persistent
	@Column(name="noi")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer newOffspringForIteration;

	@Persistent
	@Column(name="inu")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer islandNum;

	@Persistent
	@Column(name="cid")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Long chromosomeContainerId;


	@Persistent
	@Column(name="inf")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Integer iterationsWithNoFitnessIncrement;

	@Persistent
	@Column(name="fin")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Double fitnessIncrement;
	
	@Persistent
	@Column(name="mfr")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Double minFitnessToReach;

	@Persistent
	@Column(name="tid")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private String idTask;


	@Persistent
	@Column(name="stm")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Long startTimeInMillis;
	
	@Persistent
	@Column(name="cst")
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
	private Boolean checkStop;

	public GeneticStatistics() {
		super();
		// leggiamo i parametri dal file di configurazione usando l'appositan
		// classe
		GeneticAlgorithmXMLConfigReader geneticConfig = GeneticAlgorithmXMLConfigReader
				.getInstance();

		setNewOffspringForIteration(geneticConfig.getNewOffspringForIteration());
		setPopulationSize(geneticConfig.getPopulationSize());
		setIslandNum(geneticConfig.getIslandNum());

		setMaxIterations(geneticConfig.getMaxIterations());
		setIterationsWithNoFitnessIncrement(geneticConfig
				.getIterationsWithNoFitnessIncrement());
		setFitnessIncrement(geneticConfig.getFitnessIncrement());
		setMinFitnessToReach(geneticConfig.getMinFitnessToReach());
		

		// Inizializzazioni di default
		this.actualIteration = 1;
		setBestFitness(null);
		setOldBestFitnessList(new LinkedList<Double>());

		this.bestIndividualId = null;
		this.newGenerationId = 0;
		this.idTask = null;

		this.startTimeInMillis = Calendar.getInstance().getTimeInMillis();
		
		this.checkStop = null;

		// settiamo la chiave di geneticStatistics
		setKey(GAKeyFactory.getGAKeyFactoryInstance().getGenetisStatisticsKey());
	}

	public Key getKey() {
		return key;
	}

	private void setKey(Key key) {
		this.key = key;
	}

	public Integer getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(Integer maxIterations) {
		this.maxIterations = maxIterations;
	}

	public Integer getActualIteration() {
		return actualIteration;
	}

	public void setActualIteration(Integer actualIteration) {
		this.actualIteration = actualIteration;
	}

	public Long getBestIndividualId() {
		return bestIndividualId;
	}

	public void setBestIndividualId(Long bestIndividualId) {
		this.bestIndividualId = bestIndividualId;
	}

	public Integer getNewGenerationId() {
		return newGenerationId;
	}

	public void setNewGenerationId(Integer newGenerationId) {
		this.newGenerationId = newGenerationId;
	}

	public Integer getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}

	public Integer getNewOffspringForIteration() {
		return newOffspringForIteration;
	}

	public void setNewOffspringForIteration(Integer newOffspringForIteration) {
		this.newOffspringForIteration = newOffspringForIteration;
	}

	public void setIslandNum(Integer islandNum) {
		this.islandNum = islandNum;
	}

	public Integer getIslandNum() {
		return islandNum;
	}
	
	public Double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(Double bestFitness) {
		if (bestFitness != null && this.bestFitness != null) {
			LinkedList<Double> old = getOldBestFitnessList();
			old.push(this.bestFitness);
			setOldBestFitnessList(old);
		}
		this.bestFitness = bestFitness;
	}

	public Long getChromosomeContainerId() {
		return chromosomeContainerId;
	}

	public void setChromosomeContainerId(Long chromosomeContainerId) {
		this.chromosomeContainerId = chromosomeContainerId;
	}


	public void setOldBestFitnessList(LinkedList<Double> oldBestFitnessList) {
		this.oldBestFitnessList = new Blob(SerializationUtils
				.serialize(oldBestFitnessList));
	}

	public LinkedList<Double> getOldBestFitnessList() {
		if (oldBestFitnessList == null)
			return null;
		return (LinkedList<Double>) SerializationUtils
				.deserialize(oldBestFitnessList.getBytes());
	}

	public Integer getIterationsWithNoFitnessIncrement() {
		return iterationsWithNoFitnessIncrement;
	}

	public void setIterationsWithNoFitnessIncrement(
			Integer iterationsWithNoFitnessIncrement) {
		this.iterationsWithNoFitnessIncrement = iterationsWithNoFitnessIncrement;
	}

	public Double getFitnessIncrement() {
		return fitnessIncrement;
	}

	public void setFitnessIncrement(Double fitnessIncrement) {
		this.fitnessIncrement = fitnessIncrement;
	}

	public void setMinFitnessToReach(Double minFitnessToReach) {
		this.minFitnessToReach = minFitnessToReach;
	}

	public Double getMinFitnessToReach() {
		return minFitnessToReach;
	}

	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public String getIdTask() {
		return idTask;
	}

	public void setStartTimeInMillis(Long startTimeInMillis) {
		this.startTimeInMillis = startTimeInMillis;
	}

	public Long getStartTimeInMillis() {
		return startTimeInMillis;
	}

	public void setCheckStop(Boolean checkStop) {
		this.checkStop = checkStop;
	}

	public Boolean getCheckStop() {
		return checkStop;
	}
}
