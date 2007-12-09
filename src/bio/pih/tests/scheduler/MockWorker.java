package bio.pih.tests.scheduler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.biojava.bio.dist.Distribution;
import org.biojava.bio.dist.DistributionTools;
import org.biojava.bio.dist.UniformDistribution;
import org.biojava.bio.seq.DNATools;

import bio.pih.scheduler.AbstractWorker;
import bio.pih.search.AlignmentResult;
import bio.pih.search.SearchInformation;
import bio.pih.search.SearchInformation.Step;

/**
 * Mock class for unit tests
 * 
 * @author albrecht
 */
public class MockWorker extends AbstractWorker {

	volatile int threadsCount = 0;

	/**
	 * @param port
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public MockWorker(int port) throws IOException, ClassNotFoundException {
		super(port);
	}

	@Override
	protected void doSearch(SearchInformation searchInformation) {
		new Thread(new Searcher(searchInformation), "Searcher at " + this.getIdentifier()).start();
	}

	private class Searcher implements Runnable {
		SearchInformation searchInformation;

		/**
		 * @param searchInformation
		 */
		public Searcher(SearchInformation searchInformation) {
			this.searchInformation = searchInformation;
		}

		@Override
		public void run() {
			threadsCount++;
			
			do {
				doSearch();
			} while ((this.searchInformation = getNextSearchInformation(this.searchInformation)) != null);
			
			threadsCount--;
		}

		/**
		 * @param searchInformation
		 */
		public void doSearch() {
			try {
				System.out.println("Sao " + threadsCount + " threads");
				System.out.println("doing search: " + this.searchInformation);
				this.searchInformation.setActualStep(Step.SEEDS);
				long sleepTime = Math.round(Math.random() * 1000);
				Thread.sleep(sleepTime);

				this.searchInformation.setActualStep(Step.ALIGNMENT);
				sleepTime = Math.round(Math.random() * 1000);
				Thread.sleep(sleepTime);

				this.searchInformation.setActualStep(Step.SELECTING);
				sleepTime = Math.round(Math.random() * 1000);
				Thread.sleep(sleepTime);

				this.searchInformation.setActualStep(Step.FINISHED);

				List<AlignmentResult> results = new LinkedList<AlignmentResult>();

				Distribution dist = new UniformDistribution(DNATools.getDNA());
				long seqs = Math.round(Math.random() * 10) + 1; // 1 to 11
				while (seqs-- > 0) {
					results.add(new AlignmentResult(DistributionTools.generateSequence("random seq " + seqs, dist, 700), (int) seqs * 2));
				}

				System.out.println("Finished " + searchInformation + " in " + getIdentifier());
				ResultSender resultSender = new ResultSender(searchInformation.getDb(), searchInformation.getQuery(), searchInformation.getCode(), results.toArray(new AlignmentResult[results.size()]));

				new Thread(resultSender).start();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
