package dgo.goban;

/**
 * A mutable 19x19 array of ints.
 * 
 * @author eukaryote
 *
 */
public class GobanValuesI {
	private int[] state;
	
	public GobanValuesI(int[] state) {
		this.state = state;
	}

	public int getState(int x, int y) {
		validateXY(x, y);
		return state[x + y * Goban.WIDTH];
	}
	
	public int[] getState() {
		return state;
	}

	public void setState(int x, int y, int nstate) {
		validateXY(x, y);
		state[x + y * Goban.WIDTH] = nstate;
	}

	public boolean validateXY(int x, int y) {
		return !(x < 0 || y < 0 || x >= Goban.WIDTH || y >= Goban.HEIGHT);
	}

}
