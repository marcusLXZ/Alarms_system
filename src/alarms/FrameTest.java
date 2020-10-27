package alarms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FrameTest {
	
	private ArrayList<ArrayList<Boolean>> matrix;
	private List<Boolean> data;
	private List<Boolean> shiftedData;
	
	@Before
	public void setUp() {
		ArrayList<Boolean> row1 = new ArrayList<Boolean>();
		ArrayList<Boolean> row2 = new ArrayList<Boolean>();
		
		row1.add(true);
		row1.add(false);
		row1.add(true);
		
		row2.add(false);
		row2.add(true);
		row2.add(false);
		
		matrix = new ArrayList<ArrayList<Boolean>>();
		matrix.add(row1);
		matrix.add(row2);
		
		data = List.of(true, false, true, false, true, false);
		shiftedData = List.of(false, true, false, false, false, true);
	}
	
	@Test
	public void testGet() {
		Frame frame = Frame.of(2, 3, data);
		Frame.TestHook test = frame.new TestHook();
		test.setMatrix(matrix);
		
		assertTrue(frame.pixelValue(0, 0));
		assertFalse(frame.pixelValue(2, 1));
	}
	
	@Test
	public void testOf() {
		Frame frame = Frame.of(2, 3, data);
		Frame.TestHook test = frame.new TestHook();
		
		assertEquals(matrix, test.getMatrix());
	}
	
	@Test
	public void testIsShifted() {
		Frame frame = Frame.of(2, 3, data);
		Frame frame2 = Frame.of(2, 3, shiftedData);

		assertTrue(frame.isShifted(frame2));
	}

	@Test
	public void testEquals() {
		Frame frame = Frame.of(2, 3, data);
		Frame frame2 = Frame.of(2, 3, shiftedData);

		Frame frame5 = Frame.of(2, 3, data);


		assertNotEquals(frame, frame2);
		assertNotEquals(frame, frame2);
		assertEquals(frame,frame5);

	}

}
