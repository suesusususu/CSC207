package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SequencerBarcodeReaderManagerTest {
  private SequencerBarcodeReaderManager sbm;
  private PickingRequest pr1;
  private PickingRequest pr2;
  private SequencerBarcodeReader worker1;
  private SequencerBarcodeReader worker2;
  
  @Before
  public void setUp() {
    this.sbm = new SequencerBarcodeReaderManager();
    List<String> orders = new ArrayList<>();
    List<String> Skus = new ArrayList<>();
    Skus.add("1");
    Skus.add("2");
    this.pr1 = new PickingRequest(orders,Skus,1);
    this.pr2 = new PickingRequest(orders,Skus,2);
    this.worker1 = new SequencerBarcodeReader("Bill");
    this.worker2 = new SequencerBarcodeReader("Alice");
    this.sbm.workerMap.put("Bill", worker1);
    this.sbm.workerMap.put("Alice", worker2);
  }

  @Test
  public void testSetReadyInWorkerMap() {
    assertEquals(true, sbm.getWorkerMap().containsKey("Bill"));
    sbm.setReady("Bill");
    assertEquals(false,sbm.getReadyWorkers().isEmpty());
    assertEquals(sbm.getWorkerMap().get("Bill"), sbm.getReadyWorkers().peek());
  }
  
  @Test
  public void testSetReadyNotInWorkerMap(){
    sbm.setReady("Tom");
    assertEquals(true, sbm.getWorkerMap().containsKey("Tom"));
    assertEquals(sbm.workerMap.get("Tom"), sbm.readyWorkers.peek());
  }

  @Test
  public void testAssignRequest() {
    sbm.addRequest(pr1);
    sbm.addRequest(pr2);
    sbm.setReady("Bill");
    PickingRequest pr = ((PickingRequest) sbm.workerMap.get("Bill").getCurrRequest());
    assertEquals(pr1.getId(), pr.getId());
    assertEquals(pr2.getId(), sbm.getMarshalingQueue().peek().getId());
  }
  
  @Test
  public void testAssignRequestNoReadyWorker(){
    sbm.addRequest(pr1);
    sbm.assignRequest();
    assertEquals(pr1, sbm.getMarshalingQueue().peek());
  }

  @Test
  public void testVerifyTrue() {
    sbm.addRequest(pr1);
    sbm.setReady("Bill");
    boolean flag1 = sbm.verify("Bill", "1");
    assertEquals(true, flag1);
    PickingRequest pr = (PickingRequest) worker1.getCurrRequest();
    assertEquals(false, pr.getIsVerified());
    boolean flag2 = sbm.verify("Bill", "2");
    assertEquals(true, flag2);
    assertEquals(true, pr.getIsVerified());
  }
  
  @Test
  public void testVerifyFalse(){
    sbm.addRequest(pr1);
    sbm.setReady("Bill");
    boolean flag1 = sbm.verify("Bill", "0");
    assertEquals(false, flag1);
    PickingRequest pr = (PickingRequest) sbm.workerMap.get("Bill").getCurrRequest();
    assertEquals(false, pr.getIsVerified());
  }

  @Test
  public void testAddRequestValid() {
    sbm.addRequest(pr1);
    assertEquals(1, sbm.getMarshalingQueue().peek().getId());
  }
  
  @Test
  public void testAddRequestInvalid(){
    sbm.addRequest("123");
    assertEquals(true, sbm.getMarshalingQueue().isEmpty());
  }


  @Test
  public void testSendToNextStageValid() {
    sbm.addRequest(pr1);
    sbm.addRequest(pr2);
    sbm.setReady("Bill");
    pr1.setIsVerified(true);
    boolean flag = sbm.sendToNextStage("Bill");
    assertEquals(true, flag);
    assertEquals(null, sbm.workerMap.get("Bill").getCurrRequest()); 
    assertEquals(false, pr1.getIsVerified());
    assertEquals(2, sbm.getMarshalingQueue().peek().getId());
  }
  
  @Test
  public void testSendToNextStageFalse() {
    sbm.addRequest(pr1);
    sbm.setReady("Bill");
    boolean flag = sbm.sendToNextStage("Bill");
    assertEquals(false, flag);
    assertEquals(false, pr1.getIsVerified());
  }
  
  @Test
  public void testSendToPending() {
    sbm.sendToPending("Bill");
    assertEquals(1, pr1.getId());
  }

}
