package dgo.goban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import dgo.BoardPos;
import dgo.exception.InvalidMoveException;
import dgo.util.ZobristHash;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

/**
 * Goban is just any (immutable) board position. Uses a Zobrist hash for
 * efficiency.
 * 
 * @author eukaryote
 *
 */
@Slf4j
public class Goban implements Serializable {
	private static final long serialVersionUID = -6415776252103592499L;

	public static final int HEIGHT = 19;
	public static final int WIDTH = 19;

	public static final byte WHITE = -1;
	public static final byte NONE = 0;
	public static final byte BLACK = 1;

	private static final Goban EMPTY = new Goban(new byte[WIDTH * HEIGHT], 0);

	private final byte[] state;
	private final int zobristhash;

	public Goban(byte[] state) {
		// input sanitation
		for (int i = 0; i < state.length; i++) {
			validateState(state[i]);
		}

		this.state = state;

		// compute zobrist hash
		zobristhash = ZobristHash.getZobristHash(this);
	}

	private Goban(byte[] state, int zobrist) {
		this.state = state;
		zobristhash = zobrist;
	}

	public static Goban emptyGoban() {
		return EMPTY;
	}

	@Override
	public final boolean equals(Object another) {
		// use zobrist hash because comparing two integers is a lot faster than
		// comparing two arrays [citation needed]
		if (another == null)
			return false;
		if (this == another)
			return true;
		if (another instanceof Goban) {
			if (another.hashCode() == this.hashCode())
				return true;
		}

		return false;
	}

	public byte[] getState() {
		return state;
	}

	public int getState(int x, int y) {
		validateXY(x, y);
		return state[x + y * WIDTH];
	}

	public int getState(BoardPos pos) {
		return getState(pos.getX(), pos.getY());
	}

	@Override
	public final int hashCode() {
		return zobristhash;
	}

	public Goban setState(int x, int y, byte nstate) {
		// sanitize your inputs!"); DROP TABLE users; --
		validateState(nstate);

		if (!validateXY(x, y))
			throw new IllegalArgumentException("Position (" + x + ", " + y + ") out of bounds!");

		// if nothing changes just return this
		if (nstate == this.getState(x, y))
			return this;

		int newhash;
		if (nstate == NONE) {
			// undo state at (x, y) by xoring it by itself
			newhash = ZobristHash.applyIndice(zobristhash, x, y, this.getState(x, y));
		} else {
			newhash = ZobristHash.applyIndice(zobristhash, x, y, nstate);
		}

		// make copy of states array with new state set
		byte[] newstate = Arrays.copyOf(getState(), WIDTH * HEIGHT);
		newstate[x + y * WIDTH] = nstate;

		return new Goban(newstate, newhash);
	}
	
	public Goban setState(BoardPos pos, byte nstate) {
		return setState(pos.getX(), pos.getY(), nstate);
	} 

	public Goban placeStone(int x, int y, byte nstate) {
		// placing stones on stones is illegal
		if (this.getState(x, y) != NONE) {
			// log.debug("Tried to put stone on occupied spot");
			return null;
		}

		if (nstate == NONE)
			throw new IllegalArgumentException();

		List<BoardPos> neighbors = getNeighbors(x, y);

		// check if placed stone has liberties itself
		boolean hasLiberties = false;
		for (BoardPos p : neighbors) {
			if (getState(p.getX(), p.getY()) == NONE) {
				hasLiberties = true;
				break;
			}
		}

		List<BoardPos> toremove = new LinkedList<>();

		int numoppositeadj = 0;
		for (BoardPos p : neighbors) {
			if (getState(p.getX(), p.getY()) == NONE)
				continue;

			if (getState(p.getX(), p.getY()) == nstate) {
				// neighbor same state as placed stone

				if (hasLiberties) {
					// has liberties and is connected, therefore alive
					continue;
				} else {
					Set<BoardPos> s = checkGroup(p, BoardPos.of(x, y), nstate);

					// if current spot would lead to death of group, then move
					// is suicide
					if (!s.isEmpty()) {
						return null;
					}
				}
			} else {
				// neighbor different state as placed stone

				numoppositeadj++;

				Set<BoardPos> s = checkGroup(p, BoardPos.of(x, y), nstate);

				toremove.addAll(s);
			}
		}

		// its surrounded on all sides by opposite color
		if (numoppositeadj == neighbors.size() && toremove.isEmpty()) {
			return null;
		}

		if (toremove.isEmpty())
			return this.setState(x, y, nstate);

		byte[] retstate = Arrays.copyOf(this.getState(), WIDTH * HEIGHT);
		for (BoardPos p : toremove) {
			// clear dead stones from board
			retstate[p.getX() + WIDTH * p.getY()] = NONE;
		}

		retstate[x + WIDTH * y] = nstate;

		return new Goban(retstate);
	}
	
	public Goban placeStone(BoardPos pos, byte nstate) {
		return placeStone(pos.getX(), pos.getY(), nstate);
	}

	/**
	 * Internal method for checking if a group would be alive given a placed
	 * stone.
	 */
	private Set<BoardPos> checkGroup(BoardPos pt, BoardPos newpt, byte newstate) {
		Set<BoardPos> connected = new HashSet<>();
		Queue<BoardPos> queue = new LinkedList<>();
		queue.add(pt);
		connected.add(pt);

		int nstate = getState(pt.getX(), pt.getY());

		// simple, naïve floodfill

		while (!queue.isEmpty()) {
			BoardPos p = queue.poll();

			for (BoardPos conn : getNeighbors(p)) {
				int state = conn.equals(newpt) ? newstate : getState(conn.getX(), conn.getY());

				if (state == NONE) {
					// if theres a liberty, the entire group is alive
					return Collections.emptySet();
				} else if (state != nstate) {
					// ignore if its opposite color
					continue;
				} else {
					if (connected.contains(conn)) {
						// ignore if we've already visited it
						continue;
					} else {
						// new one we havent visited yet
						queue.add(conn);
						connected.add(conn);
					}
				}
			}
		}

		return connected;
	}

	public List<BoardPos> getNeighbors(int x, int y) {
		List<BoardPos> ret = new ArrayList<>(4);

		if (validateXY(x - 1, y))
			ret.add(BoardPos.of(x - 1, y));
		if (validateXY(x, y - 1))
			ret.add(BoardPos.of(x, y - 1));
		if (validateXY(x + 1, y))
			ret.add(BoardPos.of(x + 1, y));
		if (validateXY(x, y + 1))
			ret.add(BoardPos.of(x, y + 1));

		return ret;
	}
	
	public List<BoardPos> getNeighbors(BoardPos pos) {
		return getNeighbors(pos.getX(), pos.getY());
	}

	private static void validateState(int state) {
		if (state != BLACK && state != NONE && state != WHITE)
			throw new IllegalArgumentException("State can only be -1, 0, or 1, but got " + state);
	}

	private static boolean validateXY(int x, int y) {
		return !(x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT);
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int y = 0; y < Goban.HEIGHT; y++) {
			for (int x = 0; x < Goban.WIDTH; x++) {
				if (getState(x, y) == BLACK)
					ret.append('#');
				else if (getState(x, y) == WHITE)
					ret.append('O');
				else
					ret.append('+');
			}

			ret.append('\n');
		}

		return ret.toString();
	}
}
