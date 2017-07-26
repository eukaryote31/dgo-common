package dgo.goban;

/**
 * A mutable 19x19 array of doubles.
 * 
 * @author eukaryote
 *
 */
public class GobanValuesD {
	private double[] state;
	
	public GobanValuesD(double[] state) {
		this.state = state;
	}

	public double getState(int x, int y) {
		validateXY(x, y);
		return state[x + y * Goban.WIDTH];
	}
	
	public double[] getState() {
		return state;
	}

	public void setState(int x, int y, double nstate) {
		validateXY(x, y);
		state[x + y * Goban.WIDTH] = nstate;
	}

	public boolean validateXY(int x, int y) {
		return !(x < 0 || y < 0 || x >= Goban.WIDTH || y >= Goban.HEIGHT);
	}

}
