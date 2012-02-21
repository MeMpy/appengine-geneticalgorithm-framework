package it.unina.gaeframework.geneticalgorithm.defaultselectioncriteria;

import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.SelectionCriterion;
import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class HybridSelectionCriterion implements SelectionCriterion {

	@Override
	public List<Chromosome> selectedForCrossover(List<Chromosome> population,
			GeneticStatistics geneticStatistics) {

		// numero di individui da selezionare
		Integer numChromosomeToSelect = geneticStatistics
				.getNewOffspringForIteration();
		
		// lista che conterrà i Chromosome selezionati
		List<Chromosome> selectedForCrossover = new ArrayList<Chromosome>();

		// crea una copia della lista dei chromosomi su cui operare
		List<Chromosome> popolationToSelect = new ArrayList<Chromosome>(
				population);

		Random r = new Random();
		Integer extracted = null;
		Chromosome c = null;
		int i = 0;

		// prendiamo la prima metà dai migliori
		Integer numToSelect = numChromosomeToSelect / 2;
		List<Chromosome> bestSelected = new ArrayList<Chromosome>();
		for (i = 0; i < numToSelect; i++) {

			// controlla che ci siano ancora elementi da cui estrarre i
			// chromosomi
			if (popolationToSelect.size() > 0) {
				// se ci sono elementi effettuiamo l'estrazione
				c = popolationToSelect.remove(0);

				// potrebbe ipoteticamente succedere che in lista ci siano
				// elementi null. Se un tale elemento viene estratto lo
				// scartiamo e proseguiamo con l'estrazione.
				if (c == null)
					i--;
				else
					bestSelected.add(c);
			} else {
				//
				// // se non ci sono più elementi verifica di averne
				// estratti
				// // un numero pari. in caso contrario elimina un elemento
				// // dalla lista
				// if (selectedForCrossover.size() % 2 != 0)
				// selectedForCrossover
				// .remove(selectedForCrossover.size() - 1);
				// return selectedForCrossover;

			}
		}

		List<Chromosome> randomSelected = new ArrayList<Chromosome>();

		// prendiamo i restanti a caso
		for (int j = 0; j < numToSelect; j++) {

			// controlla che ci siano ancora elementi da cui estrarre i
			// chromosomi
			if (popolationToSelect.size() > 0) {
				// se ci sono elementi effettuiamo l'estrazione
				extracted = r.nextInt(popolationToSelect.size());
				c = popolationToSelect.remove(extracted.intValue());

				// potrebbe ipoteticamente succedere che in lista ci siano
				// elementi null. Se un tale elemento viene estratto lo
				// scartiamo e proseguiamo con l'estrazione.
				if (c == null)
					j--;
				else
					randomSelected.add(c);
			} else {
				//
				// // se non ci sono più elementi verifica di averne
				// estratti
				// // un numero pari. in caso contrario elimina un elemento
				// // dalla lista
				// if (selectedForCrossover.size() % 2 != 0)
				// selectedForCrossover
				// .remove(selectedForCrossover.size() - 1);
				// return selectedForCrossover;

			}

		}

		if (bestSelected.isEmpty() || randomSelected.isEmpty()
				|| bestSelected.size() != randomSelected.size())
			// TODO: Gestione dell'errore
			throw new RuntimeException("Hybrid selection: error");
		else {
			Iterator<Chromosome> randomIterator = randomSelected.iterator();
			for (Chromosome chr : bestSelected) {
				selectedForCrossover.add(chr);
				chr = randomIterator.next();
				selectedForCrossover.add(chr);
			}

			return selectedForCrossover;
		}
	}

}
