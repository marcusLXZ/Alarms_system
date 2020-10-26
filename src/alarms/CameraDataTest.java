package alarms;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CameraDataTest {

	@Test
	public void testChangeInBoxes() {
		CameraData camera = new CameraData(2, 3, true);
		
		List<Boolean> data1 = List.of(true, true, true, true, true, true);
		List<Boolean> data2 = List.of(false, false, false, false, false, true);
		
		camera.addFrame(data1);
		camera.addFrame(data2);
		camera.addFrame(data1);
		
		assertFalse(camera.changeInBoxes());
	}

}
