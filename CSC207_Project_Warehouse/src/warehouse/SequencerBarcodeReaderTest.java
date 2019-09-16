package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SequencerBarcodeReaderTest {
  
  private SequencerBarcodeReader worker;
  
  @Before
  public void setUp() {
    this.worker = new SequencerBarcodeReader("Bill");
    List<String> orders = new ArrayList<>();
    List<String> Skus = new ArrayList<>();
    Skus.add("1");
    Skus.add("2");
    Skus.add("3");
    PickingRequest pr = new PickingRequest(orders, Skus, 1);
    worker.currRequest = pr;
  }
  
  @Test
  public void testGetCurrRequest(){
    assertEquals("1", ((PickingRequest) worker.currRequest).getSkus().get(0));
  }
  
  @Test
  public void testScannedListEmpty(){
    assertEquals(0, worker.getScannedSkus().size());
  }

  @Test
  public void testVerify() {
    boolean flag = worker.verify("1");
    assertEquals(true, flag);
    assertEquals(1, worker.getScannedSkus().size());
    boolean flag2 = worker.verify("0");
    assertEquals(false, flag2);
    assertEquals(2, worker.getScannedSkus().size());
    worker.verify("3");
    assertEquals(3, worker.getScannedSkus().size());
    assertEquals(false, ((PickingRequest) worker.currRequest).getIsVerified());
    boolean flag3 = worker.verify("3");
    assertEquals(4, worker.getScannedSkus().size());
    assertEquals(false, flag3);
  }

  @Test
  public void testRescan() {
    worker.verify("1");
    worker.verify("3");
    assertEquals(2,worker.getScannedSkus().size());
    worker.rescan();
    assertEquals(0, worker.getScannedSkus().size());
    assertEquals(false, ((PickingRequest)worker.getCurrRequest()).getIsVerified());
  }

  @Test
  public void testVerifyRequestTrue() {
    worker.verify("1");
    worker.verify("2");
    worker.verify("3");
    assertEquals(true, ((PickingRequest) worker.getCurrRequest()).getIsVerified());
  }
  
  @Test
  public void testVerifyRequestDifferentLength() {
    worker.verify("1");
    assertEquals(false, ((PickingRequest) worker.getCurrRequest()).getIsVerified());
  }
  
  @Test
  public void testVerifyRequestFalseSameLength() {
    worker.verify("1");
    worker.verify("0");
    worker.verify("3");
    assertEquals(false, ((PickingRequest) worker.getCurrRequest()).getIsVerified());
  }
 

}
