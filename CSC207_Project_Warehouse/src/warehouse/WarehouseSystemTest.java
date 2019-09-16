package warehouse;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class WarehouseSystemTest {
  
  private WarehouseSystem warehouse;
  private FasciaOrderManager orderManager;
  private PickerBarcodeReaderManager pickerManager;
  private LoaderBarcodeReaderManager loaderManager;
  private SequencerBarcodeReaderManager sequencerManager;
  private ReplenisherBarcodeReaderManager replenisherManager;

  @Before
  public void setUp() throws Exception {
    this.warehouse = new WarehouseSystem();
    PickingRequestManager pr = new PickingRequestManager();
    this.orderManager = new FasciaOrderManager(pr);
    this.pickerManager = new PickerBarcodeReaderManager();
    this.loaderManager = new LoaderBarcodeReaderManager();
    this.sequencerManager = new SequencerBarcodeReaderManager();
    this.replenisherManager = new ReplenisherBarcodeReaderManager();
  }
  
  @Test
  public void testSetOrderManager() {
    warehouse.setOrderManager(orderManager);
    assertEquals(orderManager, warehouse.getOrderManager());
  }
  
  @Test
  public void testTranslateTableException() {
    try{
      warehouse.translateTable("123");
    } catch (FileNotFoundException e) { 
      System.out.println("Invalid file !" );
    } finally{
    }
  }

  @Test
  public void testSetWorkerManagerSameLength() {
    List<String> types = new ArrayList<>();
    List<BarcodeReaderManager> managers = new ArrayList<>();
    types.add("picker");
    types.add("sequencer");
    managers.add(pickerManager);
    managers.add(sequencerManager);
    String[] types2 = types.toArray(new String[2]);
    warehouse.setWorkerManager(types2, managers);
    assertEquals(true, warehouse.getWorkerManager().containsKey("picker"));
    assertEquals(true, warehouse.getWorkerManager().containsKey("sequencer"));
    assertEquals(pickerManager, warehouse.getWorkerManager().get("picker"));
    assertEquals(sequencerManager, warehouse.getWorkerManager().get("sequencer"));
  }
  
  @Test
  public void testSetWorkerManagerDifferentLength() {
    List<String> types = new ArrayList<>();
    List<BarcodeReaderManager> managers = new ArrayList<>();
    types.add("picker");
    types.add("sequencer");
    types.add("loader");
    managers.add(pickerManager);
    managers.add(sequencerManager);
    String[] types2 = types.toArray(new String[2]);
    warehouse.setWorkerManager(types2, managers);
    assertEquals(true, warehouse.getWorkerManager().isEmpty());
  }

  @Test
  public void testProcessOrderNoReturn() {
    List<String> types = new ArrayList<>();
    List<BarcodeReaderManager> managers = new ArrayList<>();
    types.add("picker");
    types.add("sequencer");
    managers.add(pickerManager);
    managers.add(sequencerManager);
    String[] types2 = types.toArray(new String[2]);
    warehouse.setWorkerManager(types2, managers);
    warehouse.processOrder("S White");
    PickerBarcodeReaderManager pbm =  ((PickerBarcodeReaderManager) warehouse.getWorkerManager().get("picker"));
    assertEquals(true, pbm.getPickingRequestQueue().isEmpty());
  }
  
  @Test
  public void testProcessOrderHasReturn() {
    List<String> types = new ArrayList<>();
    List<BarcodeReaderManager> managers = new ArrayList<>();
    types.add("picker");
    types.add("sequencer");
    managers.add(pickerManager);
    managers.add(sequencerManager);
    String[] types2 = types.toArray(new String[2]);
    warehouse.setWorkerManager(types2, managers);
    
  }

  @Test
  public void testSetReady() {
    fail("Not yet implemented");
  }

  @Test
  public void testVerify() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetInventoryManager() {
    fail("Not yet implemented");
  }

  @Test
  public void testSendToNextStage() {
    fail("Not yet implemented");
  }

  @Test
  public void testSendToPending() {
    fail("Not yet implemented");
  }

  @Test
  public void testAddRequest() {
    fail("Not yet implemented");
  }

  @Test
  public void testRetrievePickReq() {
    fail("Not yet implemented");
  }

  @Test
  public void testRescan() {
    fail("Not yet implemented");
  }

  @Test
  public void testReplenish() {
    fail("Not yet implemented");
  }

  @Test
  public void testOptimize() {
    fail("Not yet implemented");
  }

}
