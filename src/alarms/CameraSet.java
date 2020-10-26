package alarms;

import java.util.List;
import java.util.Objects;

public class CameraSet {
	
	private final CameraData[] cameras;
	
	private CameraSet(CameraData[] cameras) {
		this.cameras = cameras;
	}

	/**
	 * Creates a new CameraSet with the specified dimensions
	 */
	public static CameraSet of(int width, int depth, int height) {
		CameraData[] cameras = new CameraData[3];
		//0 means front, 1 means top, and 2 means side
		cameras[0] = new CameraData(height, width, false);
		cameras[1] = new CameraData(depth, width, true);
		cameras[2] = new CameraData(height, depth, false);
		
		return new CameraSet(cameras);
	}
	
	/**
	 * Parses the data from all three cameras.
	 * @param frontData Data from front camera
	 * @param sideData Data from side camera
	 * @param topData Data from top camera
	 */
	public void addFrames(List<Boolean> frontData, List<Boolean> sideData, List<Boolean> topData) {
		Objects.requireNonNull(frontData);
		Objects.requireNonNull(sideData);
		Objects.requireNonNull(topData);
		
		if (frontData.stream().anyMatch(b -> b == null) || sideData.stream().anyMatch(b -> b == null) || topData.stream().anyMatch(b -> b == null)) {
			throw new IllegalArgumentException("Camera data cannot contain null values");
		}
		
		if (cameras[0].frameDataLength() != frontData.size() || cameras[1].frameDataLength() != sideData.size() || cameras[2].frameDataLength() != topData.size()) {
			throw new IllegalArgumentException("Camera data not correct size");
		}
		
		cameras[0].addFrame(frontData);
		cameras[1].addFrame(sideData);
		cameras[2].addFrame(topData);
	}
	
	/**
	 * @return True if any of the cameras detected a change not due to camera errors/other causes besides the boxes.
	 * False if none of the cameras detected any change in the boxes.
	 */
	public boolean shouldTriggerAlarm() {
		if (cameras[0].changeInBoxes() && cameras[1].changeInBoxes() && cameras[2].changeInBoxes()) {
			return true;
		}
		else {
			return false;
		}
	}

}
