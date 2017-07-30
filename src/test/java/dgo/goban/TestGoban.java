package dgo.goban;

import static org.junit.Assert.*;

import org.junit.Test;

import dgo.exception.InvalidMoveException;
import nl.jqno.equalsverifier.EqualsVerifier;

public class TestGoban {
	@Test
	public void testGobanPlace() {
		// just simple getter setter stuff
		Goban gb = Goban.emptyGoban();

		Goban gb2 = gb.setState(0, 0, Goban.BLACK);
		gb2 = gb2.placeStone(1, 0, Goban.WHITE);
		gb2 = gb2.placeStone(2, 0, Goban.BLACK);
		assertEquals(Goban.BLACK, gb2.getState(0, 0));

		Goban gb3 = gb2.placeStone(7, 16, Goban.WHITE);
		gb3 = gb3.placeStone(18, 18, Goban.BLACK);
		gb3 = gb3.placeStone(17, 18, Goban.BLACK);
		gb3 = gb3.placeStone(18, 17, Goban.BLACK);

		assertEquals(Goban.BLACK, gb3.getState(18, 18));
		assertEquals(Goban.BLACK, gb3.getState(17, 18));
		assertEquals(Goban.BLACK, gb3.getState(18, 17));
		assertEquals(Goban.WHITE, gb3.getState(7, 16));
	}

	@Test
	public void testGobanPlaceDead() {
		// make sure dead stones are removed
		Goban gb = Goban.emptyGoban();

		gb = gb.placeStone(1, 0, Goban.WHITE);
		gb = gb.placeStone(0, 1, Goban.WHITE);
		gb = gb.placeStone(1, 1, Goban.WHITE);
		gb = gb.placeStone(2, 0, Goban.BLACK);
		gb = gb.placeStone(2, 1, Goban.BLACK);
		gb = gb.placeStone(0, 2, Goban.BLACK);
		gb = gb.placeStone(1, 2, Goban.BLACK);
		gb = gb.placeStone(0, 0, Goban.BLACK);
		
		assertEquals(Goban.NONE, gb.getState(1, 1));
	}

	@Test
	public void testGobanSuicide() {
		Goban gb = Goban.emptyGoban();

		gb = gb.placeStone(1, 0, Goban.WHITE);
		gb = gb.placeStone(0, 1, Goban.WHITE);
		
		assertNull(gb.placeStone(0, 0, Goban.BLACK));
		
		
		// TODO: multi stone suicide detection is borked
		
		gb = Goban.emptyGoban();

		gb = gb.placeStone(1, 0, Goban.WHITE);
		gb = gb.placeStone(0, 1, Goban.WHITE);
		gb = gb.placeStone(1, 1, Goban.WHITE);
		gb = gb.placeStone(2, 0, Goban.BLACK);
		gb = gb.placeStone(2, 1, Goban.BLACK);
		gb = gb.placeStone(0, 2, Goban.BLACK);
		gb = gb.placeStone(1, 2, Goban.BLACK);
		gb = gb.placeStone(0, 0, Goban.WHITE);
		
		assertNull(gb);
	}
	
	@Test
	public void testGobanNonSuicide() {
		
		// ###
		// #OO#
		// #O O
		//  #O
		
		
		Goban gb = Goban.emptyGoban();

		gb = gb.placeStone(3, 3, Goban.BLACK);
		gb = gb.placeStone(3, 4, Goban.BLACK);
		gb = gb.placeStone(3, 5, Goban.BLACK);
		gb = gb.placeStone(4, 6, Goban.BLACK);
		
		gb = gb.placeStone(4, 3, Goban.BLACK);
		gb = gb.placeStone(5, 3, Goban.BLACK);
		gb = gb.placeStone(6, 4, Goban.BLACK);

		gb = gb.placeStone(4, 4, Goban.WHITE);
		gb = gb.placeStone(4, 5, Goban.WHITE);
		gb = gb.placeStone(5, 4, Goban.WHITE);
		gb = gb.placeStone(5, 6, Goban.WHITE);
		gb = gb.placeStone(6, 5, Goban.WHITE);
		
		gb = gb.placeStone(5, 5, Goban.WHITE);
		
		assertNotNull(gb);
	}
}
