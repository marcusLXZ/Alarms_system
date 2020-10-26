package alarms;

import java.util.ArrayList;
import java.util.List;

public class CameraData {

	private final List<Frame> frames = new ArrayList<Frame>();
	private final int height;
	private final int width;
	private final boolean floatingAllowed;
	
	/**
	 * @param height
	 * @param width
	 */
	CameraData(int height, int width, boolean floatingAllowed) {
		assert height > 0 : "height must be positive";
		assert width > 0 : "width must be positive";
		
		this.height = height;
		this.width = width;
		this.floatingAllowed = floatingAllowed;
	}
	
	void addFrame(List<Boolean> data) {
		assert data.size() == frameDataLength() : "data is wrong size";
		
		frames.add(Frame.of(height, width, data));
	}
	
	int frameDataLength() {
		return height * width;
	}
	
	boolean changeInBoxes() {
		//Create a list of all frames. If floating frames are not allowed, then don't include them.
		List<Frame> filteredFrames;
		if (floatingAllowed) {
			filteredFrames = frames;
		}
		else {
			//Create a copy because we don't want to modify frames
			filteredFrames = frames;
			for (Frame frame : filteredFrames) {
				frame.removeFloatingPixels();
			}
		}
		List<FramePair> changes = new ArrayList<FramePair>();
		
		//Create a list of FramePairs corresponding to each change in the frames.
		for (int i = 0; i < filteredFrames.size() - 1; i++) {
			Frame current = filteredFrames.get(i);
			Frame next = filteredFrames.get(i + 1);
			//Add FramePair if it is different
			if (current != next) {
				changes.add(new FramePair(current, next));
			}
		}
		
		//Remove the changes caused by noise. It is considered noise if there are consecutive frame changes
		//where the frame before both changes is the same as the frame after both changes
		List<FramePair> removeList = new ArrayList<FramePair>();
		
		for (int i = 0; i < changes.size() - 1; i++) {
			FramePair currentPair = changes.get(i);
			FramePair nextPair = changes.get(i + 1);
			//Check if frameSet returns back in next change
			if (currentPair.current == nextPair.next) {
				removeList.add(currentPair);
			}
		}
		
		changes.removeAll(removeList);
		
		//Remove the changes due to a camera shift
		for (FramePair pair : changes) {
			if (pair.isShifted()) {
				changes.remove(pair);
			}
		}
		
		//Return whether there are changes not caused by camera error/other causes besides the boxes
		//There must be some changes in the boxes
		return !changes.isEmpty();
	}
	
	/**
	 * Used to represent a change in Frames.
	 *
	 */
	static class FramePair {
		final Frame current;
		final Frame next;
		/**
		 * @param current
		 * @param next
		 */
		FramePair(Frame current, Frame next) {
			this.current = current;
			this.next = next;
		}
		
		boolean isShifted() {
			return current.isShifted(next);
		}
	}
	
}
