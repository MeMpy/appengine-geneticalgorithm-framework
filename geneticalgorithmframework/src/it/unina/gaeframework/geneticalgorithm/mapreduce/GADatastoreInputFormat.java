package it.unina.gaeframework.geneticalgorithm.mapreduce;

import it.unina.gaeframework.geneticalgorithm.AbstractChromosomeFactory;
import it.unina.gaeframework.geneticalgorithm.util.GAKeyFactory;
import it.unina.tools.datastore.DatastoreLoadAndSave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.DatastoreInputFormat;
import com.google.appengine.tools.mapreduce.DatastoreInputSplit;

public class GADatastoreInputFormat extends DatastoreInputFormat {
	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException {
		Integer shardCount = context.getConfiguration().getInt(SHARD_COUNT_KEY,
				DEFAULT_SHARD_COUNT);
		List<Key> chromosomeKeys = GAKeyFactory.getGAKeyFactoryInstance()
				.getChromosomeKeyList();

		List<InputSplit> inputSplitList = new ArrayList<InputSplit>();

		Integer numKeys = chromosomeKeys.size();
		int[] partitionSizes = new int[shardCount];

		for (int i = 0; i < numKeys; i++) {
			// System.out.print("\ni = " + i + " - i % " + shardCount + " = " +
			// i % shardCount + "\n");
			partitionSizes[i % shardCount]++;
		}

		Key startKey = chromosomeKeys.get(0);
		Key currentKey = null;

		int index = 0;
		for (int i = 0; i < shardCount - 1; i++) {
			index += partitionSizes[i];
			currentKey = chromosomeKeys.get(index);
			inputSplitList.add(new DatastoreInputSplit(startKey, currentKey));
			startKey = currentKey;
		}

		Key dummyKey = DatastoreLoadAndSave.generateKey(
				AbstractChromosomeFactory.getConcreteChromosomeFactory()
						.getConcreteChromosomeClass(), numKeys + 1L);
		inputSplitList.add(new DatastoreInputSplit(startKey, dummyKey));

		return inputSplitList;
	}

	@Override
	public RecordReader<Key, Entity> createRecordReader(InputSplit split,
			TaskAttemptContext context) {
		return new GADatastoreRecordReader();
	}

}
