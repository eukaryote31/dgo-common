package dgo.util;

import dgo.goban.Goban;

public final class ZobristHash {
	private ZobristHash() { throw new AssertionError(); }
	
	public static int getZobristHash(Goban gb) {
		int h = 0;

		for (int x = 0; x < Goban.WIDTH; x++) {
			for (int y = 0; y < Goban.HEIGHT; y++) {
				h ^= getIndice(x, y, gb.getState(x, y));
			}
		}

		return h;
	}

	public static int getIndice(int x, int y, int state) {
		if (state == 0)
			return 0;

		if (state != -1 && state != 1)
			throw new IllegalArgumentException("Illegal state!");
		
		int uniqueid = (y * Goban.WIDTH + x) * 2 + ((state == 1) ? 1 : 0);

		// we set top 4 bits to make state of (0, 0) matter too
		final int magic = 0xF0000000;
		uniqueid ^= magic;
		
		return hash(uniqueid);
	}
	
	public static int applyIndice(int hash, int x, int y, int state) {
		return getIndice(x, y, state) ^ hash;
	}

	// fast int hash function
	private static int hash(int x) {
		x = ((x >>> 16) ^ x) * 0x45d9f3b;
		x = ((x >>> 16) ^ x) * 0x45d9f3b;
		x = (x >>> 16) ^ x;
		return x;
	}
}
