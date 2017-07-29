package dgo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;

import de.smartics.util.test.theories.ObjectTheories;
import dgo.goban.Goban;

public class TestBoardPos extends ObjectTheories {
	@DataPoints
	public static BoardPos[] data = { BoardPos.of(0, 0), BoardPos.of(0, 18), BoardPos.of(18, 0), BoardPos.of(18, 18),
			BoardPos.of(7, 12), BoardPos.of(12, 7), BoardPos.of(9, 9), BoardPos.of(3, 3), BoardPos.of(16, 8),
			BoardPos.of(6, 15) };

	@Test(expected = IllegalArgumentException.class)
	public void testOutOfBounds() {
		BoardPos.of(19, 19);
	}
	
	@Test
	public void noOverlappingHashes() {
		Set<BoardPos> bpos = new HashSet<>(361);
		for (int y = 0; y < Goban.HEIGHT; y++) {
			for (int x = 0; x < Goban.WIDTH; x++) {
				bpos.add(BoardPos.of(x, y));
			}
		}
		
		// make sure there are	 no hashcode duplicates
		assertEquals(361, bpos.size());
	}
}
