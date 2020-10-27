package alarms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Frame {
	
	private ArrayList<ArrayList<Boolean>> matrix;
	private final int height;
	private final int width;
	private boolean downwardsShift;

	public boolean isDownwardsShift() {
		return downwardsShift;
	}
	
	/**
	 * @param matrix Each element of this ArrayList is a row of the frame represented by an ArrayList. The rows should
	 * be ordered top to bottom. The rows are ordered from left to right.
	 * @param height The height of the frame
	 * @param width The width of the frame
	 */
	private Frame(ArrayList<ArrayList<Boolean>> matrix, int height, int width) {
		this.matrix = matrix;
		this.height = height;
		this.width = width;
	}

	/**
	 * 
	 * @param height The height of the frame
	 * @param width The width of the frame
	 * @param data A list of booleans with the frame data starting from the top left corner and continuing left to right
	 * then top to bottom.
	 * @return
	 */
	static Frame of(int height, int width, List<Boolean> data) { //!!!create
		assert data.size() == width * height : "data wrong length";
		assert height > 0 : "height must be positive";
		assert width > 0 : "width must be positive";

		ArrayList<ArrayList<Boolean>> matrix = new ArrayList<>();

		//Populate the matrix
		for (int y = 0; y < height; y++) {
			matrix.add(new ArrayList<>());
			for (int x = 0; x < width; x++) {
				matrix.get(y).add(false);//??
				matrix.get(y).set(x, data.get(x + width * y));
			}
		}
		
		return new Frame(matrix, height, width);
	}
	
	boolean pixelValue(int x, int y) {
		assert 0 <= y && y < height : "y is out of bounds";
		assert 0 <= x && x < width : "x is out of bounds";
		
		return matrix.get(y).get(x);
	}
	
	boolean isShifted(Frame frame) {
		assert this.width == frame.width : "width does not match";
		assert this.height == frame.height : "height does not match";
		
		//Check for all shifts within 1 pixel
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (isShiftedBy(x, y, frame)) {
					if(y==-1){
						downwardsShift = true;
					}
					return true;
				}
			}
		}
		
		//No shifts detected
		return false;
	}
	/**
	 * Returns whether each (x,y) coordinate in this frame is the same as the coordinate (x+xShift, y+yShift) in the
	 * given frame as long as (x+xShift, y+yShift) is within the bounds of frame.
	 * @param xShift Horizontal shift
	 * @param yShift Vertical shift
	 * @param frame The frame being compared to. frame is assumed to have the same dimensions as this.
	 * @return True if every (x,y) coordinate is the same as the coordinate (x+xShift, y+yShift) in the
	 * given frame. False if there is at least one mismatch between a (x,y) coordinate and (x+xShift, y+yShift)
	 * in the given frame.
	 */
	private boolean isShiftedBy(int xShift, int yShift, Frame frame) {
		assert Objects.nonNull(frame): "the input frame is null";
		assert this.width == frame.width : "width does not match";
		assert this.height == frame.height : "height does not match";
		
		assert Math.abs(xShift) <= 1 : "xShift too large";
		assert Math.abs(yShift) <= 1 : "yShift too large";
		
		//Check if each entry is shifted
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				//Check if there is a mismatch
				try {
					if (frame.pixelValue(x + xShift, y + yShift) != this.pixelValue(x, y)) {
						//The pixel in frame is different
						return false;
					}
				} catch (AssertionError e) {
					//We have gone out of bounds
				}
			}
		}
		
		//All the entries are shifted
		return true;
	}
	
	void removeFloatingPixels(int level) { //!!
		for (int y = height - level; y > 0; y--) {
			for (int x = 0; x < width; x++) {
				//Check if current pixel is on, but the pixel below is off
				if (pixelValue(x, y) && !pixelValue(x, y - 1)) {
					matrix.get(y).set(x, false);
				}
			}
		}
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Frame frame = (Frame) o;
		return getHeight() == frame.getHeight() &&
				getWidth() == frame.getWidth() &&
				isSameMatrix(frame);
	}

	private boolean isSameMatrix(Frame frame){
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (this.pixelValue(x, y) != frame.pixelValue(x, y)) {
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public int hashCode() {
		return Objects.hash(matrix, getHeight(), getWidth());
	}

	/**
	 * @return the height
	 */
	int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	int getWidth() {
		return width;
	}

	class TestHook {
		
		boolean get(int x, int y) {
			return Frame.this.pixelValue(x, y);
		}
		
		void setMatrix(ArrayList<ArrayList<Boolean>> m) {
			matrix = m;
		}
		
		ArrayList<ArrayList<Boolean>> getMatrix() {
			return matrix;
		}
		
		boolean isShiftedBy(int x, int y, Frame f) {
			return Frame.this.isShiftedBy(x, y, f);
		}
		
	}

}
