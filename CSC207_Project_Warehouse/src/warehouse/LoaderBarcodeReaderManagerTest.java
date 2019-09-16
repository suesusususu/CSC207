package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class LoaderBarcodeReaderManagerTest {
  
  private LoaderBarcodeReaderManager lbm;
  private PickingRequest pr1;
  private PickingRequest pr2;
  private PickingRequest pr3;
  private LoaderBarcodeReader loader1;
  private LoaderBarcodeReader loader2;
  
  @Before
  public void setUp(){
    this.lbm = new LoaderBarcodeReaderManager();
    List<String> orders = new ArrayList<>();
    List<String> Skus = new ArrayList<>();
    Skus.add("1");
    Skus.add("2");
    this.pr1 = new PickingRequest(orders,Skus,1);
    this.pr2 = new PickingRequest(orders,Skus,2);
    this.pr3 = new PickingRequest(orders,Skus,3);
    this.loader1 = new LoaderBarcodeReader("Bill");
    this.loader2 = new LoaderBarcodeReader("Alice");
    this.lbm.workerMap.put("Bill", loader1);
    this.lbm.workerMap.put("Alice", loader2);
  }
 
  @Test
  public void testSetReadyInWorkerMap(){
    assertEquals(true, lbm.getWorkerMap().containsKey("Bill"));
    lbm.setReady("Bill");
    assertEquals(false,lbm.getReadyWorkers().isEmpty());
    assertEquals(lbm.getWorkerMap().get("Bill"), lbm.getReadyWorkers().peek());
  }
  
  @Test
  public void testSetReadyNotInWorkerMap(){
    lbm.setReady("Tom");
    assertEquals(true, lbm.getWorkerMap().containsKey("Tom"));
    assertEquals(lbm.workerMap.get("Tom"), lbm.readyWorkers.peek());
  }
  
  @Test
  public void testAssignRequestPendingEmpty() {
    lbm.addRequest(pr1);
    lbm.addRequest(pr2);
    lbm.setReady("Bill");
    PickingRequest pr = ((PickingRequest) lbm.workerMap.get("Bill").getCurrRequest());
    assertEquals(pr1.getId(), pr.getId());
    assertEquals(pr2.getId(), lbm.getUnverifiedQueue().peek().getId());
  }
  
  @Test
  public void testAssignRequestLoadPending(){
    lbm.addRequest(pr1);
    lbm.setReady("Bill");
    pr1.setIsVerified(true);
    lbm.sendToPending("Bill");
    lbm.setReady("Alice");
    PickingRequest pr = (PickingRequest) loader2.getCurrRequest();
    assertEquals(1, pr.getId());
  }
  
  @Test
  public void testAssignRequestNoReadyWorker(){
    lbm.addRequest(pr1);
    lbm.assignRequest();
    assertEquals(pr1, lbm.getUnverifiedQueue().peek());
  }
  
  @Test
  public void testAssignRequestPendingNotEmpty(){
    lbm.addRequest(pr2);
    lbm.setReady("Bill");
    pr2.setIsVerified(true);
    lbm.sendToPending("Bill");
    lbm.addRequest(pr3);
    lbm.setReady("Alice");
    assertEquals(3, ((PickingRequest) loader2.getCurrRequest()).getId());
  }

  @Test
  public void testVerifyTrue() {
    lbm.addRequest(pr1);
    lbm.setReady("Bill");
    boolean flag1 = lbm.verify("Bill", "1");
    assertEquals(true, flag1);
    PickingRequest pr = (PickingRequest) loader1.getCurrRequest();
    assertEquals(false, pr.getIsVerified());
    boolean flag2 = lbm.verify("Bill", "2");
    assertEquals(true, flag2);
    assertEquals(true, pr.getIsVerified());
  }
  
  @Test
  public void testVerifyFalse(){
    lbm.addRequest(pr1);
    lbm.setReady("Bill");
    boolean flag1 = lbm.verify("Bill", "0");
    assertEquals(false, flag1);
    PickingRequest pr = (PickingRequest) lbm.workerMap.get("Bill").getCurrRequest();
    assertEquals(false, pr.getIsVerified());
  }
  
  @Test
  public void testVerifyFinished(){
    lbm.addRequest(pr2);
    lbm.setReady("Bill");
    lbm.verify("Bill", "1");
    lbm.verify("Bill", "2");
    assertEquals(true, pr2.getIsVerified());
  }

  @Test
  public void testAddRequest() {
    lbm.addRequest(pr1);
    assertEquals(1, lbm.getUnverifiedQueue().peek().getId());
  }


  @Test
  public void testSendToPendingValid() {
    lbm.addRequest(pr1);
    lbm.setReady("Bill");
    pr1.setIsVerified(true);
    lbm.sendToPending("Bill");
    assertEquals(pr1, lbm.getVerifiedQueue().peek());
    assertEquals(null, lbm.workerMap.get("Bill").getCurrRequest());
  }
  
  @Test
  public void testSendToPendingInvalid(){
    lbm.addRequest(pr1);
    lbm.setReady("Bill");
    lbm.sendToPending("Bill");
    assertEquals(true, lbm.getVerifiedQueue().isEmpty());
    assertEquals(1, ((PickingRequest)lbm.workerMap.get("Bill").getCurrRequest()).getId());
  }

  @Test
  public void testLoadOntoTruckValid() {
    lbm.addRequest(pr1);
    lbm.addRequest(pr2);
    lbm.addRequest(pr3);
    lbm.setReady("Bill");
    lbm.readyWorkers.add(loader2);
    pr1.setIsVerified(true);
    boolean flag = lbm.sendToNextStage("Bill");
    assertEquals(true, flag);
    assertEquals(2, lbm.getCurrId());
    assertEquals(null, lbm.workerMap.get("Bill").getCurrRequest()); 
    assertEquals(pr2, lbm.workerMap.get("Alice").getCurrRequest());
    assertEquals(3, lbm.getUnverifiedQueue().peek().getId());
 }
  
 @Test
 public void testLoadOntoTruckInvalidVerified() {
   lbm.addRequest(pr1);
   lbm.addRequest(pr2);
   lbm.setReady("Bill");
   boolean flag = lbm.sendToNextStage("Bill");
   assertEquals(false, flag);
   assertEquals(1, lbm.getCurrId());
   assertEquals(false, pr1.getIsVerified());
 }
 
 @Test
 public void testLoadOntoTruckInvalidId(){
   lbm.addRequest(pr1);
   lbm.addRequest(pr2);
   lbm.addRequest(pr3);
   lbm.setReady("Bill");
   pr1.setIsVerified(true);
   lbm.setId(2);
   boolean flag = lbm.sendToNextStage("Bill");
   assertEquals(false, flag);
   assertEquals(2, lbm.getCurrId());
   assertEquals(1, ((PickingRequest)lbm.workerMap.get("Bill").getCurrRequest()).getId());
 }

}
