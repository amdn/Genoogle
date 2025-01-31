/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle.alignment;



/**
 * Sequences alignment output generation.
 * 
 * @author Biojava and changes by Felipe Albrecht for faster alignment methods
 */
public abstract class GenoogleSequenceAlignment {

	/**
	 * @param query
	 * @param subject
	 * @return the score of the alignment
	 */
//	public abstract int pairwiseAlignment(SymbolList query, SymbolList subject);

	/**
	 * @return {@link String} containing the representation of the query aligned.
	 */
	public abstract String getQueryAligned();

	/**
	 * @return {@link String} containing the representation of the target aligned.
	 */
	public abstract String getTargetAligned();

	/**
	 * @return {@link String} containing the representation of the alignment path.
	 */
	public abstract String getPath();

	/**
	 * @return where the alignment begins at the query sequence.
	 */
	public abstract int getQueryStart();

	/**
	 * @return where the alignment ends at the query sequence.
	 */
	public abstract int getQueryEnd();

	/**
	 * @return where the alignment begins at the target sequence.
	 */
	public abstract int getTargetStart();

	/**
	 * @return where the alignment ends at the target sequence.
	 */
	public abstract int getTargetEnd();

	/**
	 * @return alignment score.
	 */
	public abstract int getScore();

	/**
	 * Get the identity size, it is, how many exact matches occurred in the alignment.
	 * 
	 * @return the alignment identity size.
	 */
	public abstract int getIdentitySize();
	
	/**
	 * This just computes the maximum of four integers.
	 * 
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 * @return the maximum of four <code>int</code>s.
	 */
	public int max(int w, int x, int y, int z) {
		if ((w > x) && (w > y) && (w > z))
			return w;
		if ((x > y) && (x > z))
			return x;
		if ((y > z))
			return y;
		return z;
	}

	
	/**
	 * This method provides a BLAST-like formated alignment from the given <code>String</code>s, in which the sequence coordinates and the information "Query" or "Target", respectively is added to each line. Each line contains 60 sequence characters including the gap symbols plus the meta information. There is one white line between two pairs of sequences.
	 * 
	 * @param queryName
	 *            name of the query sequence
	 * @param targetName
	 *            name of the target sequence
	 * @param align
	 *            a <code>String</code>-array, where the index 0 is the query sequence and index 1 the target sequence (for instance <code>new String[] {myQuerySequence.seqString(), myTargetSequence.seqString()}</code>)
	 * @param path
	 *            the "path", that means a String containing white spaces and pipe ("|") symbols, which makes matches visible. The two strings in <code>align</code> have to have the same length and also the same length than this <code>path</code>.
	 * @param queryStart
	 *            the start position in the query, where the alignment starts. For example zero for normal Needleman-Wunsch-Alignments.
	 * @param queryEnd
	 *            the end position, that means the sequence coordinate, which is the last symbol of the query sequence. Counting starts at zero!
	 * @param queryLength
	 *            The length of the query sequence without gaps.
	 * @param targetStart
	 *            These are all the same for the target. Have a look at these above.
	 * @param targetEnd
	 * @param targetLength
	 * @param editdistance
	 * @param time
	 *            The time in milliseconds, which was needed to generate the alignment.
	 * @param queryOffset
	 * @param targetOffset
	 * @return formated String.
	 */
	public static String formatOutput(String queryName, String targetName, String[] align, String path, int queryStart, int queryEnd, long queryLength, int targetStart, int targetEnd, long targetLength, double editdistance, long time, int queryOffset, int targetOffset) {

		String output = System.getProperty("line.separator") + " Time (ms):\t" + time + System.getProperty("line.separator") + " Length:\t" + align[0].length() + System.getProperty("line.separator");
		output += "  Score:\t" + (-1) * editdistance + System.getProperty("line.separator");
		output += "  Query:\t" + queryName + ",\tLength:\t" + queryLength + System.getProperty("line.separator");
		output += "  Target:\t" + targetName + ",\tLength:\t" + targetLength + System.getProperty("line.separator") + System.getProperty("line.separator");

		// counts the absolute position within the String
		int currline = Math.min(60, align[0].length()), i, j, k, l;
		
		StringBuilder space = new StringBuilder();
		StringBuilder kspace = new StringBuilder();
		StringBuilder jspace = new StringBuilder();
		
		for (k = 0; k < Integer.valueOf(Math.max(queryEnd+queryOffset, targetEnd+targetOffset)).toString().length(); k++)
			space.append(' ');
		for (k = Integer.valueOf(queryStart + 1 + queryOffset).toString().length(); k <= Integer.valueOf(Math.max(queryEnd + queryOffset, targetEnd + targetOffset)).toString().length(); k++)
			kspace.append(' ');
		for (k = Integer.valueOf(targetStart + 1 + targetOffset).toString().length(); k <= Integer.valueOf(Math.max(queryEnd + queryOffset, targetEnd + targetOffset)).toString().length(); k++)
			jspace.append(' ');

		i = k = queryStart;
		j = l = targetStart;
		output += System.getProperty("line.separator") + "Query:\t" + kspace + (k + 1 + queryOffset) + " ";
		for (i = currline - Math.min(60, align[0].length()); i < currline; i++) {
			if ((align[0].charAt(i) != '-') && (align[0].charAt(i) != '~'))
				k++;
			if ((align[1].charAt(i) != '-') && (align[1].charAt(i) != '~'))
				j++;
		}
		output += align[0].substring(0, currline) + " " + (k + queryOffset);
		output += " " + System.getProperty("line.separator") + "        " + space + path.substring(0, currline);
		output += " " + System.getProperty("line.separator") + "Target:\t" + jspace + (l + 1 + targetOffset) + " " + align[1].substring(0, currline) + " " + (j + targetOffset) + " " + System.getProperty("line.separator");

		for (; currline + 60 < path.length(); currline += 60) {
			l = Math.min(j + 1, targetEnd + targetOffset);
			kspace.append(' ');
			for (int n = Integer.valueOf(k + 1 + queryOffset).toString().length() - 1; n < Integer.valueOf(Math.max(queryEnd + queryOffset, targetEnd + targetOffset)).toString().length(); n++)
				kspace.append(' ');
			for (int n = Integer.valueOf(j + targetOffset).toString().length() - 1; n < Integer.valueOf(Math.max(queryEnd + queryOffset, targetEnd + targetOffset)).toString().length(); n++)
				jspace.append(' ');
			output += " " + System.getProperty("line.separator") + "Query:\t" + kspace + Math.min(k + 1 + queryOffset, queryEnd + queryOffset) + " ";
			for (i = currline; i < currline + 60; i++) {
				if ((align[0].charAt(i) != '-') && (align[0].charAt(i) != '~'))
					k++;
				if ((align[1].charAt(i) != '-') && (align[1].charAt(i) != '~'))
					j++;
			}
			output += align[0].substring(currline, currline + 60) + " " + (k + queryOffset);
			output += " " + System.getProperty("line.separator") + "        " + space + path.substring(currline, currline + 60);
			output += " " + System.getProperty("line.separator") + "Target:\t" + jspace + (l + 1 + targetOffset) + " " + align[1].substring(currline, currline + 60) + " " + (j + targetOffset) +  " " + System.getProperty("line.separator");
		}
		align[0] += " " + queryEnd + queryOffset;
		align[1] += " " + targetEnd + targetOffset;
		if (currline + 1 < path.length()) {
			kspace = new StringBuilder();
			jspace = new StringBuilder();
			for (int n = Integer.valueOf(k + queryOffset).toString().length() - 1; n < Integer.valueOf(Math.max(queryEnd + queryOffset, targetEnd + targetOffset)).toString().length(); n++)
				kspace.append(' ');
			for (int n = Integer.valueOf(j + targetOffset).toString().length() - 1; n < Integer.valueOf(Math.max(queryEnd + queryOffset, targetEnd + targetOffset)).toString().length(); n++)
				jspace.append(' ');
			output += " " + System.getProperty("line.separator") + "Query:\t" + kspace + Math.min(k + 1 + queryOffset, queryEnd + queryOffset) + " " + align[0].substring(currline, align[0].length());
			output += " " + System.getProperty("line.separator") + "        " + space + path.substring(currline, path.length());
			output += " " + System.getProperty("line.separator") + "Target:\t" + jspace + Math.min(j + targetOffset, targetEnd + targetOffset) + " " + align[1].substring(currline, align[1].length()) + System.getProperty("line.separator");
		}

		return output += System.getProperty("line.separator");
	}
}
