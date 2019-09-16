package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class LoaderBarcodeReaderTest {

  private LoaderBarcodeReader loader;

  @Before
  public void setUp()  {
    this.loader = new LoaderBarcodeReader("Bill");
    List<String> orders = new ArrayList<>();
    List<String> Skus = new ArrayList<>();
    Skus.add("1");
    Skus.add("2");
    Skus.add("3");
    PickingRequest pr = new PickingRequest(orders, Skus, 1);
    loader.currRequest = pr;
  }
  
  @Test
  public void testGetName() {
    assertEquals("Bill", loader.getName());
  }

  @Test
  public void testGetCurrRequest() {
    assertEquals("1", ((PickingRequest) loader.currRequest).getSkus().get(0));
  }

  @Test
  public void testScannedListEmpty() {
    assertEquals(0, loader.getScannedSkus().size());
  }


  @Test
  public void testVerify() {
    boolean flag = loader.verify("1");
    assertEquals(true, flag);
    assertEquals(1, loader.getScannedSkus().size());
    boolean flag2 = loader.verify("0");
    assertEquals(false, flag2);
    assertEquals(2, loader.getScannedSkus().size());
  }

  @Test
  public void testRescan() {
    loader.verify("1");
    loader.verify("3");
    assertEquals(2, loader.getScannedSkus().size());
    loader.rescan();
    assertEquals(0, loader.getScannedSkus().size());
    assertEquals(false, ((PickingRequest) loader.getCurrRequest()).getIsVerified());
  }

  @Test
  public void testIsFinishedTrue() {
    loader.verify("1");
    loader.verify("2");
    loader.verify("3");
    assertEquals(true, ((PickingRequest) loader.getCurrRequest()).getIsVerified());
  }

  @Test
  public void testIsFinishedFalseDifferentLength() {
    loader.verify("1");
    assertEquals(false, ((PickingRequest) loader.getCurrRequest()).getIsVerified());
  }

  @Test
  public void testIsFinishedFalseSameLength() {
    loader.verify("1");
    loader.verify("0");
    loader.verify("3");
    assertEquals(false, ((PickingRequest) loader.getCurrRequest()).getIsVerified());
  }

}
