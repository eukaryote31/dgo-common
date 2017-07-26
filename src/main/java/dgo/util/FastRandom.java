package dgo.util;

import java.security.SecureRandom;
import java.util.Random;

public class FastRandom extends Random {
	private static final long serialVersionUID = 941381735932193968L;

	private long state;

	// for making seeds
	private static final SecureRandom srng = new SecureRandom();

	public FastRandom(int seed) {
		this.state = seed;
	}

	public FastRandom() {
		this(srng.nextInt());
	}

	@Override
	protected int next(int nbits) {
		long x = this.state;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		this.state = x;
		x &= ((1L << nbits) - 1);
		return (int) x;
	}
}
