package alarms;

import java.util.ArrayList;
import java.util.List;

public class CameraData {

	private final List<Frame> frames = new ArrayList<>();
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
		List<Frame> filteredFrames = frames;  //???is frames changing when filteredFrames changes
		boolean cameraShiftDownwards = false;

		if (!floatingAllowed) {
			filteredFrames.get(0).removeFloatingPixels(1); //assume not moved
			
			for (int i = 0; i < filteredFrames.size()-1 ; i++) {
				Frame current = filteredFrames.get(i);
				Frame next = filteredFrames.get(i+1);
				//check if downwardShifted
				if(current.isShifted(next)){
					cameraShiftDownwards = false;
					if(current.isDownwardsShift()){
						cameraShiftDownwards = true;
					}
				}

				if(cameraShiftDownwards){
					next.removeFloatingPixels(2);
				}
				else{
					next.removeFloatingPixels(1);
				}
			}
		}

		List<FramePair> changes = new ArrayList<FramePair>();

		//Create a list of FramePairs corresponding to each change in the frames.
		addDifferentPairs(filteredFrames, changes);

		//Remove the changes caused by noise. It is considered noise if there are consecutive frame changes
		//where the frame before both changes is the same as the frame after both changes  //???what if only on change, but plane?
		List<FramePair> removeList = new ArrayList<FramePair>();
		removeNoisePairs(changes, removeList);

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

	private void removeNoisePairs(List<FramePair> changes, List<FramePair> removeList) {
		for (int i = 0; i < changes.size() - 1; i++) {
			FramePair currentPair = changes.get(i);
			FramePair nextPair = changes.get(i + 1);
			//Check if frameSet returns back in next change
			if (currentPair.current.equals(nextPair.next)) {
				removeList.add(currentPair);
				removeList.add(nextPair);
			}
		}
	}

	private void addDifferentPairs(List<Frame> filteredFrames, List<FramePair> changes) {
		for (int i = 0; i < filteredFrames.size() - 1; i++) {
			Frame current = filteredFrames.get(i);
			Frame next = filteredFrames.get(i + 1);
			//Add FramePair if it is different
			if (!current.equals(next)) {
				changes.add(new FramePair(current, next));
			}
		}
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
