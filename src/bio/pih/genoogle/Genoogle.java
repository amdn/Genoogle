/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009,2010  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import bio.pih.genoogle.index.ValueOutOfBoundsException;
import bio.pih.genoogle.interfaces.Console;
import bio.pih.genoogle.io.AbstractSequenceDataBank;
import bio.pih.genoogle.io.AbstractSimpleSequenceDataBank;
import bio.pih.genoogle.io.InvalidConfigurationException;
import bio.pih.genoogle.io.RemoteSimilaritySequenceDataBank;
import bio.pih.genoogle.io.Utils;
import bio.pih.genoogle.io.XMLConfigurationReader;
import bio.pih.genoogle.io.proto.Io.StoredSequence;
import bio.pih.genoogle.io.reader.ParseException;
import bio.pih.genoogle.search.SearchManager;
import bio.pih.genoogle.search.SearchParams;
import bio.pih.genoogle.search.SearchParams.Parameter;
import bio.pih.genoogle.search.UnknowDataBankException;
import bio.pih.genoogle.search.results.SearchResults;
import bio.pih.genoogle.seq.IllegalSymbolException;
import bio.pih.genoogle.seq.LightweightSymbolList;
import bio.pih.genoogle.seq.SymbolList;

import com.google.common.collect.Lists;

/**
 * The main class of Genoogle. To get a Genoogle instance, use the getInstance() method.
 * 
 * @author albrecht
 */
public final class Genoogle {

	public static final File CONF_LOG4J_PROPERTIES_FILE = new File(getHome(), "conf/log4j.properties");

	public static String line = System.getProperty("line.separator");

	public static String SOFTWARE_NAME = "Genoogle";
	public static Double VERSION = 0.98;
	public static String AUTHOR = "Felipe Albrecht (felipe.albrecht@gmail.com).";
	public static String WEB_PAGE = "http://genoogle.pih.bio.br";
	public static String COPYRIGHT = "Copyright (C) 2008,2009,2010,2011,2012 Felipe Fernandes Albrecht";

	public static String COPYRIGHT_NOTICE = line
			+ "-----------------------------------------------------------------------------------------" + line
			+ SOFTWARE_NAME + " Copyright (C) 2008, 2009, 2010, 2011  " + AUTHOR + line
			+ "This program comes with ABSOLUTELY NO WARRANTY;" + line
			+ "This is free software, and you are welcome to redistribute it under certain conditions;" + line
			+ "See the LICENCE file or check at http://www.gnu.org/licenses/gpl-3.0.html for full license." + line
			+ "-------------------------------------------------------------------------------------------";

	SearchManager sm = null;
	private static volatile Genoogle singleton = null;

	static Logger logger = Logger.getLogger(Genoogle.class.getName());

	private static boolean forceFormatting = true;

	/**
	 * Get the {@link Genoogle} execution instance.
	 * 
	 * @return {@link Genoogle} singleton instance or <code>null</code> if an error did happen.
	 */
	public synchronized static Genoogle getInstance() {
		if (singleton == null) {
			logger.info("Starting Genoogle .");
			try {
				singleton = new Genoogle();
			} catch (IOException e) {
				logger.fatal(e.getMessage(), e);
				return null;
			} catch (ValueOutOfBoundsException e) {
				logger.fatal(e.getMessage(), e);
				return null;
			} catch (InvalidConfigurationException e) {
				logger.fatal(e.getMessage(), e);
				return null;
			}

		}
		return singleton;
	}

	/**
	 * Private constructor.
	 */
	private Genoogle() throws IOException, ValueOutOfBoundsException, InvalidConfigurationException {
		PropertyConfigurator.configure(CONF_LOG4J_PROPERTIES_FILE.getAbsolutePath());
		sm = XMLConfigurationReader.getSearchManager();
	}

	/**
	 * Classes which use Genoogle and should be notified about changes.
	 */
	private List<GenoogleListener> listerners = Lists.newLinkedList();

	/**
	 * Add a new listener to Genoogle which will be notified about changes.
	 * 
	 * @param listerner
	 */
	public void addListerner(GenoogleListener listerner) {
		listerners.add(listerner);
	}

	/**
	 * Finish {@link Genoogle} and notify the listeners to finish.
	 */
	public synchronized void finish() {
		for (GenoogleListener listerner : listerners) {
			listerner.finish();
		}
		try {
			sm.shutdown();
		} catch (InterruptedException e) {
			logger.fatal(e);
		}
	}

	/**
	 * Get the data bank name where the searches are performed when the data bank is not specified.
	 * 
	 * @return Default data bank name
	 */
	public String getDefaultDatabank() {
		return sm.getDefaultDataBankName();
	}

	/**
	 * Get a {@link Collection} of all available data banks
	 * 
	 * @return {@link Collection} of all {@link AbstractSequenceDataBank} which it is possible to
	 *         execute a query.
	 */
	public Collection<AbstractSequenceDataBank> getDatabanks() {
		return sm.getDatabanks();
	}

	/**
	 * Do the search at the default data bank, reading the queries from the given
	 * {@link BufferedReader} and returning the execution line only after all searches are finished.
	 * 
	 * @param in
	 *            {@link BufferedReader} where the sequences are read.
	 * @return {@link List} of {@link SearchResults}, being one {@link SearchResults} for each input
	 *         sequence inside the given {@link BufferedReader}.
	 */
	public List<SearchResults> doBatchSyncSearch(BufferedReader in) throws IOException, UnknowDataBankException,
			InterruptedException, ExecutionException, NoSuchElementException, IllegalSymbolException, ParseException {
		String defaultDataBankName = sm.getDefaultDataBankName();
		return doBatchSyncSearch(in, defaultDataBankName);
	}

	/**
	 * Do the search at the specified data bank, reading the queries from the given
	 * {@link BufferedReader} and returning the execution line only after all searches are finished.
	 * 
	 * @param in
	 *            {@link BufferedReader} where the sequences are read.
	 * @param databankName
	 *            Data bank name where the search will be made.
	 * @return {@link List} of {@link SearchResults}, being one {@link SearchResults} for each input
	 *         sequence inside the given {@link BufferedReader}.
	 */
	public List<SearchResults> doBatchSyncSearch(BufferedReader in, String databankName) throws IOException,
			UnknowDataBankException, InterruptedException, ExecutionException, NoSuchElementException,
			IllegalSymbolException, ParseException {
		return doBatchSyncSearch(in, databankName, null);
	}

	/**
	 * Do the search at the specified data bank, reading the queries from the given
	 * {@link BufferedReader}, using the specified {@link Map} of {@link Parameter} as parameters,
	 * and returning the execution line only after all searches are finished.
	 * 
	 * @param in
	 *            {@link BufferedReader} where the sequences are read.
	 * @param databankName
	 *            Data bank name where the search will be made.
	 * @param parameters
	 *            {@link Map} of {@link Parameter} which will be used in these searches.
	 * 
	 * @return {@link List} of {@link SearchResults}, being one {@link SearchResults} for each input
	 *         sequence inside the given {@link BufferedReader}.
	 */
	public List<SearchResults> doBatchSyncSearch(BufferedReader in, String databankName,
			Map<Parameter, Object> parameters) throws IOException, UnknowDataBankException, InterruptedException,
			ExecutionException, NoSuchElementException, IllegalSymbolException, ParseException {

		return sm.doSyncSearch(in, databankName, parameters);
	}

	/**
	 * Do the search of the given sequence at the default data bank and returning the execution line
	 * only after all searches are finished.
	 * 
	 * @param inputSequence
	 *            input sequence for the searching.
	 * @return A {@link SearchResults} containing the results of this search.
	 */
	public SearchResults doSyncSearch(String inputSequence) {
		String defaultDataBankName = sm.getDefaultDataBankName();
		return doSyncSearch(inputSequence, defaultDataBankName);
	}

	/**
	 * Do the search of the given sequence at the informed data bank and returning the execution
	 * line only after all searches are finished.
	 * 
	 * @param seqString
	 *            input sequence for the searching.
	 * @param dataBankName
	 *            data bank name where the search will be performed.
	 * 
	 * @return A {@link SearchResults} containing the results of this search.
	 */
	public SearchResults doSyncSearch(String seqString, String dataBankName) {
		SearchResults sr = null;
		seqString = seqString.trim();
		try {
			SymbolList sequence = LightweightSymbolList.createDNA(seqString);
			SearchParams sp = new SearchParams(sequence, dataBankName);
			sr = sm.doSyncSearch(sp);
		} catch (UnknowDataBankException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalSymbolException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			// TODO: Stop thread (do for all interrupted exceptions)
			logger.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
		}

		return sr;
	}

	/**
	 * Do the search of the given sequence at the informed data bank, using the specified
	 * {@link Map} of {@link Parameter} as parameters, and returning the execution line only after
	 * all searches are finished.
	 * 
	 * @param seqString
	 *            input sequence for the searching.
	 * @param dataBankName
	 *            data bank name where the search will be performed.
	 * @param parameters
	 *            {@link Map} of {@link Parameter} which will be used in these searches.
	 * 
	 * @return A {@link SearchResults} containing the results of this search.
	 */
	public SearchResults doSyncSearch(String seqString, String dataBankName, Map<Parameter, Object> parameters) {
		SearchResults sr = null;
		seqString = seqString.trim();
		try {
			SymbolList sequence = LightweightSymbolList.createDNA(seqString);
			SearchParams sp = new SearchParams(sequence, dataBankName, parameters);
			sr = sm.doSyncSearch(sp);
		} catch (UnknowDataBankException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalSymbolException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
		}

		return sr;
	}
	

	public String getSequence(String db, int id) {
		AbstractSequenceDataBank databank = sm.getDatabank(db);
		if (databank instanceof RemoteSimilaritySequenceDataBank) {
			try {
				RemoteSimilaritySequenceDataBank abstractSimpleSequenceDataBank = (RemoteSimilaritySequenceDataBank) databank;
				StoredSequence sequence = abstractSimpleSequenceDataBank.getSequenceFromId(id);				
				int[] encodedDatabankSequence = Utils.getEncodedSequenceAsArray(sequence);
				return abstractSimpleSequenceDataBank.getAaEncoder().decodeIntegerArrayToString(encodedDatabankSequence);				
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Databank " + db + " is a collection. Get sequence is not supported. Yet.");
		}
	}

	/**
	 * Main method: Use the "-g" option to encode and create inverted index for the data banks or
	 * "-b file" to execute the commands specified at the file or do not use parameters and use the
	 * console.
	 */
	public static void main(String[] args) throws IOException, ValueOutOfBoundsException, InvalidConfigurationException {
		PropertyConfigurator.configure(CONF_LOG4J_PROPERTIES_FILE.getAbsolutePath());
		logger.info(COPYRIGHT_NOTICE);

		System.err.println(getHome());

		List<AbstractSequenceDataBank> dataBanks = XMLConfigurationReader.getDataBanks();

		if (args.length == 0) {
			Console console = new Console();
			new Thread(console).start();
		} else {

			String option = args[0];
			System.out.println("Options: " + option);

			if (option.equals("-h")) {
				showHelp();
			}

			if (option.equals("-g")) {
				logger.info("Searching for non encoded data banks.");

				for (AbstractSequenceDataBank dataBank : dataBanks) {
					if (!dataBank.check()) {
						dataBank.delete();
						logger.info("Data bank " + dataBank.getName() + " is not encoded.");
						try {
							dataBank.encodeSequences(forceFormatting);
						} catch (Exception e) {							
							logger.fatal(e, e);
							return;
						}
					}
				}
				logger.info("All specified data banks are encoded. You can do yours searchs now.");
				return;
			}

			else if (args.length >= 2 && option.equals("-b")) {
				String inputFile = args[1];
				Console console = new Console(new File(inputFile));
				new Thread(console).start();

			} else {
				showHelp();
			}
		}
	}

	private volatile static File home = null;

	public static File getHome() {
		if (home == null) {
			String homeEnv = System.getenv("GENOOGLE_HOME");
			if (homeEnv != null) {
				home = new File(homeEnv);
			} else {
				home = new File(".");
			}
		}
		return home;
	}

	private static void showHelp() {
		logger.info("Options for Genoogle console mode execution:");
		logger.info(" -h              : this help.");
		logger.info(" -g              : encode all not encoded databanks specified at conf/genoogle.conf .");
		logger.info(" -b <BATCH_FILE> : starts genoogle and execute the <BATCH_FILE> .");
	}

}
