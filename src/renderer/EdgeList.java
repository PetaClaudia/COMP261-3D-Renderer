package renderer;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
	int startY;
	int endY;
	float[][] edgeList;
	
	public EdgeList(int startY, int endY) {
		// TODO fill this in.
		this.startY = startY;
		this.endY = endY;
		edgeList = new float[4][endY-startY + 1];
	}

	public int getStartY() {
		// TODO fill this in.
		return startY;
	}

	public int getEndY() {
		// TODO fill this in.
		return endY;
	}

	public float getLeftX(int y) {
		// TODO fill this in.
		return edgeList[0][y - startY];
	}

	public float getRightX(int y) {
		// TODO fill this in.
		return edgeList[2][y - startY];
	}

	public float getLeftZ(int y) {
		// TODO fill this in.
		return edgeList[1][y - startY];
	}

	public float getRightZ(int y) {
		// TODO fill this in.
		return edgeList[3][y - startY];
	}

	public void xMinYCol(int y, float xLeft, float zLeft) {
		// TODO Auto-generated method stub
		if(!(y< 0) && !(y > endY)) {
			edgeList[0][y - startY] = xLeft;
			edgeList[1][y - startY] = zLeft;
		}
	}

	public void xMaxYCol(int y, float xRight, float zRight) {
		// TODO Auto-generated method stub
		if(!(y< 0) && !(y > endY)) {
			edgeList[2][y - startY] = xRight;
			edgeList[3][y - startY] = zRight;
		}

	}
}

// code for comp261 assignments
