package warehouse;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BarcodeReaderManagerFactoryTest {


  @Test
  public void testGetBarcodeReaderManager() {
    BarcodeReaderManagerFactory brf = new BarcodeReaderManagerFactory();
    assertEquals(null, brf.getBarcodeReaderManager(null));
    assertEquals(true, brf.getBarcodeReaderManager("PICKER") instanceof PickerBarcodeReaderManager);
    assertEquals(true, brf.getBarcodeReaderManager("SEQUENCER") instanceof SequencerBarcodeReaderManager);
    assertEquals(true, brf.getBarcodeReaderManager("LOADER") instanceof LoaderBarcodeReaderManager);
    assertEquals(true, brf.getBarcodeReaderManager("REPLENISHER") instanceof ReplenisherBarcodeReaderManager);
    assertEquals(null, brf.getBarcodeReaderManager("MANAGER"));
  }
}
