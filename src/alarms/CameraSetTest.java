package alarms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CameraSetTest {
	
	@Test
	public void testShouldSoundAlarmFalse() {
		List<Boolean> data1 = List.of(true, false, true, false, true, false, true, false, true);
		List<Boolean> data2 = List.of(false, false, false, false, false, false, false, false, false);
		List<Boolean> shiftedData = List.of(false, true, false, false, false, true, false, true, false);
		
		CameraSet data = CameraSet.of(3, 3, 3);
		
		data.addFrames(data1, data1, data1);
		data.addFrames(data1, data2, data1);
		data.addFrames(data1, data1, data1);
		data.addFrames(data1, data1, shiftedData);
		
		assertFalse(data.shouldTriggerAlarm());
	}
	
	@Test
	public void testShouldSoundAlarmTrue() {
		List<Boolean> data1 = List.of(true, false, true, false, true, false, true, false, true);
		List<Boolean> data2 = List.of(false, true, false, false, false, true, false, false, false);
		
		CameraSet data = CameraSet.of(3, 3, 3);
		
		data.addFrames(data1, data1, data1);
		data.addFrames(data1, data1, data2);
		
		assertTrue(data.shouldTriggerAlarm());
	}

}
