package it.unina.gaeframework.geneticalgorithm.mapreduce;

import it.unina.gaeframework.geneticalgorithm.AbstractChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.ChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.util.GAEUtils;
import it.unina.gaeframework.geneticalgorithm.util.GAKeyFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.mapreduce.DatastoreInputSplit;
import com.google.common.base.Preconditions;

public class GADatastoreRecordReader extends RecordReader<Key, Entity>
		implements Writable {
	private static final Logger log = Logger
			.getLogger(GADatastoreRecordReader.class.getName());

	// The split that this reader iterates over.
	private DatastoreInputSplit split;

	// The lazily generated datastore iterator for this reader.
	private Iterator<Entity> iterator;

	// The most current key and value of this reader.
	private Key currentKey;
	private Entity currentValue;

	/**
	 * Implemented solely for RecordReader interface.
	 */
	@Override
	public void close() {
	}

	/**
	 * Completely meaningless. Implemented as part of RecordReader.
	 */
	// TODO(frew): Make meaningful: return some measure of
	// (currentKey - startKey) / (endKey - startKey)
	@Override
	public float getProgress() {
		return 0;
	}

	@Override
	public Key getCurrentKey() {
		// For consistency, if we've just deserialized and haven't iterated
		// return null for both currentKey and currentValue.
		// System.out.println("ENTITA' RECUPERATA "+ currentKey);
		return currentValue == null ? null : currentKey;
	}

	@Override
	public Entity getCurrentValue() {
		return currentValue;
	}

	private void createIteratorFromDatastore() {
		Preconditions.checkState(iterator == null);

		Query q = new Query(split.getEntityKind());
		if (currentKey == null) {
			q.addFilter(Entity.KEY_RESERVED_PROPERTY,
					FilterOperator.GREATER_THAN_OR_EQUAL, split.getStartKey());
		} else {
			q.addFilter(Entity.KEY_RESERVED_PROPERTY,
					FilterOperator.GREATER_THAN, currentKey);
		}

		if (split.getEndKey() != null) {
			q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.LESS_THAN,
					split.getEndKey());
		}

		q.addSort(Entity.KEY_RESERVED_PROPERTY);

		DatastoreService dsService = DatastoreServiceFactory
				.getDatastoreService();
		iterator = dsService.prepare(q).asQueryResultIterator(
				FetchOptions.Builder.withChunkSize(split.getBatchSize()));
	}

	private void createIteratorFromCache() {
		iterator = null;

		GAKeyFactory gaKeyFactory = GAKeyFactory.getGAKeyFactoryInstance();
		List<Key> keySet = gaKeyFactory.getChromosomeKeyList();

		List<Entity> entityList = new LinkedList<Entity>();

		Iterator<Key> keyIterator = keySet.iterator();
		Chromosome chr = null;
		Entity entity = null;
		ChromosomeFactory chrFactory = AbstractChromosomeFactory
				.getConcreteChromosomeFactory();
		Cache cache = GAEUtils.getCache();
		Key currKey = null;
		boolean exit = false;
		while (keyIterator.hasNext() && entityList != null && !exit) {
			currKey = keyIterator.next();
			if (currKey.getId() >= split.getStartKey().getId()
					&& currKey.getId() < split.getEndKey().getId()) {
				if (currentKey != null) {
					if (currKey.getId() > currentKey.getId()) {

						chr = (Chromosome) cache.get(currKey);
						if (chr != null) {
							entity = chrFactory.convertChromosomeToEntity(chr,
									chr.getKey());
							entityList.add(entity);
						} else {
							entityList = null;
						}
					}
				} else {
					chr = (Chromosome) cache.get(currKey);
					if (chr != null) {
						entity = chrFactory.convertChromosomeToEntity(chr, chr
								.getKey());
						entityList.add(entity);
					} else {
						entityList = null;
					}
				}
			} else if (currKey.getId() >= split.getEndKey().getId()) {
				exit = true;
			}
		}

		if (entityList != null)
			iterator = entityList.iterator();
		// System.out.println(iterator);
	}

	private void createIterator() {
		createIteratorFromCache();
		if (iterator == null) {
			createIteratorFromDatastore();
		}

		if (iterator == null) {
			// TODO: Gestire l'errore
		}
	}

	// private void createIterator() {
	// Preconditions.checkState(iterator == null);
	//
	// Query q = new Query(split.getEntityKind());
	// if (currentKey == null) {
	// q.addFilter(Entity.KEY_RESERVED_PROPERTY,
	// FilterOperator.GREATER_THAN_OR_EQUAL, split.getStartKey());
	// } else {
	// q.addFilter(Entity.KEY_RESERVED_PROPERTY,
	// FilterOperator.GREATER_THAN, currentKey);
	// }
	//
	// if (split.getEndKey() != null) {
	// q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.LESS_THAN,
	// split.getEndKey());
	// }
	//
	// q.addSort(Entity.KEY_RESERVED_PROPERTY);
	//
	// DatastoreService dsService = DatastoreServiceFactory
	// .getDatastoreService();
	// iterator = dsService.prepare(q).asQueryResultIterator(
	// withChunkSize(split.getBatchSize()));
	// }

	@Override
	public boolean nextKeyValue() {
		if (currentKey == null) {
			createIterator();
		}

		// è possibile che l'iterator diventi null durante l'esecuzione, non
		// sappiamo perchè, specialmente quando il map ci mette molto per
		// terminare
		if (iterator == null) {
			createIterator();
			// System.out.println("\nITERATOR NULL!!!\n" +
			// "currentKey = "+ currentKey + "\n"
			// + "tosplit start= "+ split.getStartKey() + " end= "+
			// split.getEndKey());
		}
		if (!iterator.hasNext()) {
			return false;
		}

		currentValue = iterator.next();
		currentKey = currentValue.getKey();

		return true;
		/*
		 * 
		 * if (currentKey == null) { currentKey = split.getStartKey(); } else {
		 * currentKey = DatastoreLoadAndSave.generateKey(chrFactory
		 * .getConcreteChromosomeClass(), currentKey.getId() + 1); }
		 * 
		 * if (currentKey.getId() >= split.getEndKey().getId()) { return false;
		 * }
		 * 
		 * Entity entity = null;
		 * 
		 * // proviamo a recuperare il Chromosome dalla Cache Cache cache =
		 * GAEUtils.getCache(); Chromosome chr = (Chromosome)
		 * cache.get(currentKey);
		 * 
		 * if (chr != null) { // se l'oggetto era presente nella cache
		 * convertirlo in un Entity entity =
		 * chrFactory.convertChromosomeToEntity(chr, currentKey); } else { // se
		 * l'oggetto non era presente nella cache recuperarlo come Entity // dal
		 * datastore DatastoreService dsService = DatastoreServiceFactory
		 * .getDatastoreService(); try {
		 * log.warning("RECUPERA L'ENTITY DAL DATASTORE!!!"); entity =
		 * dsService.get(currentKey); } catch (EntityNotFoundException e) { //
		 * TODO: gestire l'errore e.printStackTrace(); } }
		 * 
		 * if(entity == null){ //TODO: Gestire l'errore }
		 * 
		 * // impostiamo i nuovi valori della Key e dell'Entity corrrenti
		 * currentKey = entity.getKey(); currentValue = entity; //
		 * System.out.println("ENTITA' CORRENTE IMPOSTATA: "+ currentKey);
		 * return true;
		 */

	}

	@Override
	public void readFields(DataInput in) throws IOException {
		currentKey = readKeyOrNull(in);
		log.fine("DatastoreRecordReader reconstituted: " + currentKey);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		log.fine("DatastoreRecordReader serialization: " + currentKey);
		writeKeyOrNull(out, currentKey);
	}

	/**
	 * Initialize the reader.
	 * 
	 * @throws IOException
	 *             if the split provided isn't a DatastoreInputSplit
	 */
	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException {
		Preconditions.checkNotNull(split);
		if (!(split instanceof DatastoreInputSplit)) {
			throw new IOException(getClass().getName()
					+ " initialized with non-DatastoreInputSplit");
		}
		this.split = (DatastoreInputSplit) split;
	}

	/**
	 * COPIATI DALLA CLASSE DatastoreSerializationUtil
	 */

	/**
	 * Serializes a datastore key, gracefully handling nulls.
	 * 
	 * @param o
	 *            the DataOutput to serialize to
	 * 
	 * @param key
	 *            the datastore key to serialize
	 * 
	 * @throws IOException
	 *             if the serialization goes horribly awry
	 */
	private void writeKeyOrNull(DataOutput o, Key key) throws IOException {
		if (key == null) {
			o.writeUTF("null");
		} else {
			o.writeUTF(KeyFactory.keyToString(key));
		}
	}

	/**
	 * Deserializes a datastore key, gracefully handling nulls.
	 * 
	 * @param i
	 *            the DataInput to deserialize from
	 * @return the deserialized datastore key or {@code null}
	 * @throws IOException
	 *             if the deserialization goes horribly awry
	 */
	private Key readKeyOrNull(DataInput i) throws IOException {
		String keyString = i.readUTF();
		if (keyString.equals("null")) {
			return null;
		}
		return KeyFactory.stringToKey(keyString);
	}

}