package warehouse;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {BarcodeReaderManagerFactoryTest.class, FasciaOrderManagerTest.class, 
  InventoryManagerTest.class, LoaderBarcodeReaderManagerTest.class, LoaderBarcodeReaderTest.class, 
  PickerBarcodeReaderManagerTest.class, PickerBarcodeReaderTest.class, PickingRequestTest.class, 
  ReplenisherBarcodeReaderManagerTest.class, ReplenisherBarcodeReaderTest.class,
  SequencerBarcodeReaderManagerTest.class, SequencerBarcodeReaderTest.class, WarehousePickingTest.class})
public class TestSuite {

}
