/*
 * Genoogle: Similar DNA Sequences Searching Engine and Tools. (http://genoogle.pih.bio.br)
 * Copyright (C) 2008,2009  Felipe Fernandes Albrecht (felipe.albrecht@gmail.com)
 *
 * For further information check the LICENSE file.
 */

package bio.pih.genoogle.tests;

import junit.framework.TestCase;

import org.junit.Test;

import bio.pih.genoogle.util.CircularArrayList;

public class TestCircularArrayList extends TestCase {

	@Test
	public void testInsert() {
		CircularArrayList cl = new CircularArrayList(4);

		cl.add(0, 0, 10);
		cl.add(1, 1, 10);
		cl.add(2, 2, 10);
		cl.add(3, 3, 10);
		cl.add(4, 4, 10);
		cl.add(5, 5, 10);
		cl.add(6, 6, 10);
		cl.add(7, 7, 10);
		cl.add(8, 8, 10);
		cl.add(9, 9, 10);

		int i = 0;
		while (i < 10 && i < cl.size()) {
			assertEquals(cl.get(i).getQueryAreaBegin(), i);
			i++;
		}
	}

	@Test
	public void testInsertAndRemove() {
		CircularArrayList cl = new CircularArrayList(4);

		cl.add(0, 0, 10);
		cl.add(1, 1, 10);
		cl.add(2, 2, 10);
		cl.add(3, 3, 10);
		cl.add(4, 4, 10);
		cl.add(5, 5, 10);
		cl.add(6, 6, 10);
		cl.add(7, 7, 10);
		cl.add(8, 8, 10);
		cl.add(9, 9, 10);

		cl.removeElements(9);

		cl.add(10, 0, 10);
		cl.add(11, 1, 10);
		cl.add(12, 2, 10);
		cl.add(13, 3, 10);
		cl.add(14, 4, 10);
		cl.add(15, 5, 10);
		cl.add(16, 6, 10);
		cl.add(17, 7, 10);
		cl.add(18, 8, 10);
		cl.add(19, 9, 10);

		int i = 0;
		while (i < 10 && i < cl.size()) {
			assertEquals(cl.get(i).getQueryAreaBegin(), i+9);
			i++;
		}
	}

	@Test
	public void testInsertAndRemove2() {
		CircularArrayList cl = new CircularArrayList(4);

		cl.add(0, 0, 10);
		cl.removeElements(1);
		cl.add(1, 1, 10);
		cl.removeElements(1);
		cl.add(2, 2, 10);
		cl.removeElements(1);
		cl.add(3, 3, 10);
		cl.removeElements(1);
		cl.add(4, 4, 10);
		cl.removeElements(1);
		cl.add(5, 5, 10);

		assertEquals(cl.get(0).getQueryAreaBegin(), 5);
	}

	@Test
	public void testInsertAndRemove3() {
		CircularArrayList cl = new CircularArrayList(4);

		cl.add(0, 0, 10);
		cl.add(1, 1, 10);
		cl.add(2, 2, 10);
		cl.add(3, 3, 10);
		cl.add(4, 4, 10);
		cl.add(5, 5, 10);
		cl.add(6, 6, 10);
		cl.removeElements(3);
		cl.add(7, 7, 10);
		cl.add(8, 8, 10);
		cl.add(9, 9, 10);

		int i = 3;
		while (i < 7 && i < cl.size()) {
			assertEquals(cl.get(i).getQueryAreaBegin(), i+3);
			i++;
		}
	}

	@Test
	public void testInsertAndRemove5() {
		CircularArrayList cl = new CircularArrayList(4);

		cl.add(0, 0, 10);
		cl.add(1, 1, 10);
		cl.add(2, 2, 10);
		cl.add(3, 3, 10);
		cl.add(4, 4, 10);
		cl.add(5, 5, 10);
		cl.add(6, 6, 10);
		cl.removeElements(5);
		cl.add(7, 7, 10);
		cl.add(8, 8, 10);
		cl.add(9, 9, 10);

		int i = 5;
		while (i < cl.size()) {
			assertEquals(cl.get(i).getQueryAreaBegin(), i);
			i++;
		}
	}
}
