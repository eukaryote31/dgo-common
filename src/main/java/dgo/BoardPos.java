package dgo;

import java.io.Serializable;

import dgo.goban.Goban;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardPos implements Serializable {

	@Getter
	private final int x;

	@Getter
	private final int y;
	
	private transient int hashcode;

	private static transient final BoardPos[] positions;

	static {
		// initialize instances

		positions = new BoardPos[Goban.WIDTH * Goban.HEIGHT];

		for (int y = 0; y < Goban.HEIGHT; y++) {
			for (int x = 0; x < Goban.WIDTH; x++) {
				positions[y * Goban.WIDTH + x] = new BoardPos(x, y);
			}
		}
	}
	
	{
		// initialize hashcode ahead of time
		
		// populate most of the bits
		hashcode = getX() + getY() << 8 + getX() << 16 + getY() << 24;
		
		// avalanche the bits
		hashcode = ((hashcode >>> 16) ^ hashcode) * 0x45d9f3b;
		hashcode = ((hashcode >>> 16) ^ hashcode) * 0x45d9f3b;
	}

	public static final BoardPos of(int x, int y) {
		if (x < 0 || y < 0 || x >= Goban.WIDTH || y >= Goban.HEIGHT)
			throw new IllegalArgumentException("BoardPos (" + x + ", " + y + ") out of bounds!");

		return positions[y * Goban.WIDTH + x];
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(x).append(", ").append(y).append(")").toString();
	}

	@Override
	public boolean equals(Object other) {
		// all objects with same pos are same object in same memory slot
		return this == other;
	}
	
	@Override
	public int hashCode() {
		// precomputed hashcode
		return hashcode;
	}
}
