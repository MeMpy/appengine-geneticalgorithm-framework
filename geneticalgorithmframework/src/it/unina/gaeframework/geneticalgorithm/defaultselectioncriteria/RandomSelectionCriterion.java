package it.unina.gaeframework.geneticalgorithm.defaultselectioncriteria;

import it.unina.gaeframework.geneticalgorithm.Chromosome;
import it.unina.gaeframework.geneticalgorithm.SelectionCriterion;
import it.unina.gaeframework.geneticalgorithm.statistics.GeneticStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSelectionCriterion implements SelectionCriterion {

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

		for (int i = 0; i < numChromosomeToSelect; i++) {

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
					i--;
				else
					selectedForCrossover.add(c);
			} else {

				// se non ci sono più elementi verifica di averne estratti
				// un numero pari. in caso contrario elimina un elemento
				// dalla lista
				if (selectedForCrossover.size() % 2 != 0)
					selectedForCrossover
							.remove(selectedForCrossover.size() - 1);
				return selectedForCrossover;
			}
		}
		return selectedForCrossover;
	}
}
