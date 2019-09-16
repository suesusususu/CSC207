package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PickerBarcodeReaderManagerTest {

  @Before
  public void setUp() throws Exception {}
    PickerBarcodeReaderManager pbm = new PickerBarcodeReaderManager();
    PickingRequestManager prm = new PickingRequestManager();
    List<String> pickReq = new ArrayList<>();
    List<String> Skus = new ArrayList<>();
    PickingRequest pr1 = new PickingRequest(pickReq, Skus, 1);
    List<String> pickReq1 = new ArrayList<>();
    List<String> Skus1 = new ArrayList<>();
    PickingRequest pr2 = new PickingRequest(pickReq1, Skus1, 2);


  @Test
  public void testSetReadyNotExistPicker() {
    pbm.setReady("Alice");
    PickerBarcodeReader pr = new PickerBarcodeReader("Alice");
    assertEquals("Alice", pbm.getReadyWorkers().poll().getName());
  }

  @Test
  public void testSetReadyExistPicker() {
    PickerBarcodeReader picker = new PickerBarcodeReader("Alice");
    pbm.getWorkerMap().put("Alice", picker);
    pbm.setReady("Alice");
    assertEquals(picker, pbm.getReadyWorkers().poll());
  }
  
  @Test
  public void testAssignRequestFromRequestQueue() {
    pickReq.add("S White");
    pickReq.add("SE Beige");
    pickReq.add("SES Beige");
    pickReq.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    pbm.addRequest(pr1);
    pbm.setReady("Alice");
    PickerBarcodeReader pb = (PickerBarcodeReader) pbm.getWorkerMap().get("Alice");
    boolean result = pbm.getReadyWorkers().contains(pb);
    assertEquals(false, result);
    pbm.setReady("Bob");
    pickReq1.add("S White");
    pickReq1.add("SE Beige");
    pickReq1.add("SES Beige");
    pickReq1.add("SEL Green");
    Skus1.add("1");
    Skus1.add("11");
    Skus1.add("13");
    Skus1.add("31");
    Skus1.add("2");
    Skus1.add("12");
    Skus1.add("14");
    Skus1.add("32");
    pbm.addRetrievedReq(pr2);
    pbm.assignRequest();
    assertEquals(true, pbm.getRetrievedQueue().isEmpty());
    List<String> pickReq1 = new ArrayList<>();
    List<String> Skus1 = new ArrayList<>();
    pickReq1.add("S White");
    pickReq1.add("SE Beige");
    pickReq1.add("SES Beige");
    pickReq1.add("SEL Green");
    Skus1.add("1");
    Skus1.add("11");
    Skus1.add("13");
    Skus1.add("31");
    Skus1.add("2");
    Skus1.add("12");
    Skus1.add("14");
    Skus1.add("32");
    PickingRequest pr2 = new PickingRequest(pickReq, Skus, 2);
    pbm.addRequest(pr2);
    pbm.setReady("John");
    pbm.assignRequest();
    assertEquals(true, pbm.getPickingRequestQueue().isEmpty());
  }

  @Test
  public void testVerifyTrue() {
    pbm.setReady("Alice");
    pbm.assignRequest();
    PickerBarcodeReader picker = (PickerBarcodeReader) pbm.getWorkerMap().get("Alice");
    ArrayList<String> a = new ArrayList<>();
    a.add("A 0 0 0");
    a.add("1");
    picker.getPickingOrderQueue().add(a);
    boolean result1 = pbm.verify("Alice", "1");
    assertEquals(true, result1);
  }
  
  @Test
  public void testVerifyFalse() {
    pbm.setReady("Alice");
    pbm.assignRequest();
    PickerBarcodeReader picker = (PickerBarcodeReader) pbm.getWorkerMap().get("Alice");
    ArrayList<String> a = new ArrayList<>();
    a.add("A 0 0 0");
    a.add("1");
    picker.getPickingOrderQueue().add(a);
    boolean result1 = pbm.verify("Alice", "2");
    assertEquals(false, result1);
  }

  @Test
  public void testSendToNextStage() {
    pickReq.add("S White");
    pickReq.add("SE Beige");
    pickReq.add("SES Beige");
    pickReq.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    pbm.setReady("Alice");
    PickerBarcodeReader picker = (PickerBarcodeReader) pbm.getWorkerMap().get("Alice");
    picker.assignPickingRequest(pr1);
    picker.verify("1");
    picker.verify("2");
    picker.verify("11");
    picker.verify("12");
    picker.verify("13");
    picker.verify("14");
    picker.verify("31");
    picker.verify("32");
    picker.sendToNextStage();
    assertEquals(true, pbm.sendToNextStage("Alice"));
  }
  @Test
  public void testAddRequest() {
    pickReq.add("S White");
    pickReq.add("SE Beige");
    pickReq.add("SES Beige");
    pickReq.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    pbm.addRequest(pr1);
    pbm.getPickingRequestQueue().add(pr1);
    assertEquals(pr1, pbm.getPickingRequestQueue().peek());
  }
  
  @Test
  public void testAddRetrievedReq() {
    pickReq.add("S White");
    pickReq.add("SE Beige");
    pickReq.add("SES Beige");
    pickReq.add("SEL Green");
    Skus.add("1");
    Skus.add("11");
    Skus.add("13");
    Skus.add("31");
    Skus.add("2");
    Skus.add("12");
    Skus.add("14");
    Skus.add("32");
    pbm.addRetrievedReq(pr1);
    assertEquals(pr1, pbm.getRetrievedQueue().peek());
  }
  
  @Test
  public void testSendToPending() {
    pbm.setReady("Alice");
    pbm.sendToPending("Alice");
  }
  
}

