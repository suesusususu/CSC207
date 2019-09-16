package warehouse;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;



public class PickerBarcodeReaderTest {

  public final ExpectedException exception = ExpectedException.none();
  @Before
  public void setUp() throws FileNotFoundException {}
    PickerBarcodeReader picker = new PickerBarcodeReader("Alice");
    List<String> orders = new ArrayList<>();
    List<String> Skus = new ArrayList<>();
    PickingRequest pr1 = new PickingRequest(orders, Skus, 1);
    
  
  @Test
  public void testGetPickingOrderQueue() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 0 0 0");
    a.add("1");
    picker.getPickingOrderQueue().add(a);
    assertEquals(a, picker.getPickingOrderQueue().poll());
  }
  
  @Test
  public void testVerifyTrue() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 0 0 0");
    a.add("1");
    ArrayList<String> b = new ArrayList<>();
    b.add("A 1 1 1");
    b.add("2");
    ArrayList<String> c = new ArrayList<>();
    c.add("A 1 1 0");
    c.add("3");
    picker.getPickingOrderQueue().add(a);
    picker.getPickingOrderQueue().add(b);
    picker.getPickingOrderQueue().add(c);
    assertEquals(true, picker.verify("1"));
    assertEquals(false, picker.verify("4"));
    assertEquals(true, picker.verify("2"));
    assertEquals(true, picker.verify("3"));
  }
  
  @Test
  public void testVerifyFalse() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 0 0 0");
    a.add("1");
    picker.getPickingOrderQueue().add(a);
    assertEquals(false, picker.verify("2"));
  }

  @Test
  public void testGetCurrentPickingRequest() {
    orders.add("S White");
    orders.add("SE Beige");
    orders.add("SES Beige");
    orders.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    ArrayList<String> a = new ArrayList<>();
    a.add("A 0 0 0");
    a.add("1");
    picker.assignPickingRequest(pr1);
    assertEquals(pr1, picker.getCurrRequest());
  }
  
  @Test
  public void testAssignPickingRequest() throws FileNotFoundException {
    orders.add("S White");
    orders.add("SE Beige");
    orders.add("SES Beige");
    orders.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    picker.assignPickingRequest(pr1);
    List<ArrayList<String>> a = WarehousePicking.optimize(pr1.getSkus());
    assertEquals(a.get(0), picker.getPickingOrderQueue().poll());
   
  }
  
  @Test(expected = FileNotFoundException.class)
  public void testAssignPickingRequestWithoutFile() throws FileNotFoundException {
    WarehousePicking.setTraversalTable("File");
    orders.add("S White");
    orders.add("SE Beige");
    orders.add("SES Beige");
    orders.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    List<ArrayList<String>> a = WarehousePicking.optimize(pr1.getSkus());
    picker.assignPickingRequest(pr1);
    assertEquals(null, a);
  }
  
  @Test
  public void testSendToNextStageFalse() {
    orders.add("S White");
    orders.add("SE Beige");
    orders.add("SES Beige");
    orders.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    picker.assignPickingRequest(pr1);
    assertEquals(false, picker.sendToNextStage());
  }
  
  @Test
  public void testSendToNextStageTrue() {
    orders.add("S White");
    orders.add("SE Beige");
    orders.add("SES Beige");
    orders.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    picker.assignPickingRequest(pr1);
    picker.verify("1");
    picker.verify("2");
    picker.verify("11");
    picker.verify("12");
    picker.verify("13");
    picker.verify("14");
    picker.verify("31");
    picker.verify("32");
    assertEquals(true, picker.sendToNextStage());
  }
  
  @Test
  public void testRescan() {
    picker.rescan();
  }
}
