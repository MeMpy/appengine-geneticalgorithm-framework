package it.unina.gaeframework.geneticalgorithm.mapreduce;

import it.unina.gaeframework.geneticalgorithm.AbstractChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.AbstractFitnessEvaluatorFactory;
import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.FitnessEvaluator;
import it.unina.gaeframework.geneticalgorithm.util.GAEUtils;

import java.util.logging.Logger;

import org.apache.hadoop.io.NullWritable;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;

/**
 * Definizione del Map Worker dell'algoritmo genetico
 * 
 * @author barren
 * 
 */
public class GeneticMapWorker extends
		AppEngineMapper<Key, Entity, NullWritable, NullWritable> {
	private static final Logger log = Logger.getLogger(GeneticMapWorker.class
			.getName());

	// private DatastoreService datastore;
	//
	// private long count = 1;

	public GeneticMapWorker() {
	}

//	@Override
//	public void taskSetup(Context context) {
//		// this.datastore = DatastoreServiceFactory.getDatastoreService();
//	}
//
//	@Override
//	public void taskCleanup(Context context) throws IOException, InterruptedException {
////		 log.warning("Doing per-task cleanup");
//		super.taskCleanup(context);
//	}
//
//	@Override
//	public void setup(Context context) {
//		// log.warning("Doing per-worker setup");
//	}
//
//	@Override
//	public void cleanup(Context context) {
////		 log.warning("Doing per-worker cleanup");
//	}

	@Override
	public void map(Key key, Entity value, Context context) {
		// log.warning("Mapping key: " + key);

		// recuperiamo il Chromosome contenuto nell'Entity mappato dalla
		// funzione di map
		Chromosome individual = AbstractChromosomeFactory
				.getConcreteChromosomeFactory().convertEntityToChromosome(
						value, key);

		// verifichiamo se la fitness è già stata calcolata
		if (individual.getFitness() == null) {
			// se la fitness è null bisogna valutarla

			// istanziamo il FitnessEvaluator indicato nel file di
			// configurazione geneticalgorithm.xml
			FitnessEvaluator val = AbstractFitnessEvaluatorFactory
					.getConcreteFitnessEvaluator();

			// valutiamo l'individuo
			Double fitness = val.computeFitness(individual);
			individual.setFitness(fitness);
			value.setUnindexedProperty(Chromosome.FITNESS_COLUMN, fitness);

			// recuperiamo l'AppEngineContext
			AppEngineContext appEngineContext = getAppEngineContext(context);

			//accodiamo nel MutationPool l'operazione di salvataggio nel datastore
			DatastoreMutationPool mutationPool = appEngineContext.getMutationPool();
			mutationPool.put(value);
			
			// salviamo anche nella Cache l'individuo valutato
			GAEUtils.getCache().put(individual.getKey(), individual);
		}
	}

	// public static void main(String[] args){
	// Set<String> a=new HashSet<String>();
	// String s="ciao";
	// a.add(s);
	// a.add(s);
	//
	// System.out.println(a.size());
	// }

}
