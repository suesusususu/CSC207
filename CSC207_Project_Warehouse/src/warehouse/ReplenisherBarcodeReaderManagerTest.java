package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Before;
import org.junit.Test;

public class ReplenisherBarcodeReaderManagerTest {

  @Before
  public void setUp() throws Exception {}
    ReplenisherBarcodeReaderManager rbm = new ReplenisherBarcodeReaderManager();

  @Test
  public void testSetReadyNewWorker() {
    rbm.setReady("Bob");
    assertEquals(true, rbm.getWorkerMap().containsKey("Bob"));
  }

  @Test
  public void testSetReady() {
    ReplenisherBarcodeReader a = new ReplenisherBarcodeReader("Bob");
    rbm.workerMap.put("Bob", a);
    rbm.setReady("Bob");
    assertEquals(a, rbm.getReadyWorkers().poll());
  }
  
  @Test
  public void testAssignRequest() {
    rbm.setReady("Bob");
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rbm.addRequest((Object) a);
    rbm.assignRequest();
    assertEquals(a, rbm.getWorkerMap().get("Bob").getCurrRequest());
  }

  @Test
  public void testAssignRequestWithoutReadyWorker() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rbm.addRequest((Object) a);
    rbm.assignRequest();
    assertEquals(a, rbm.getReplenishQueue().poll());
  }
  
  @Test
  public void testAssignRequestWithoutRequest() {
    rbm.setReady("Bob");
    rbm.assignRequest();
    assertEquals("Bob", rbm.readyWorkers.poll().getName());
  }
  
  @Test
  public void testVerify() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rbm.setReady("Bob");
    rbm.getReplenishQueue().add(a);
    rbm.assignRequest();
    boolean result = rbm.verify("Bob", "1");
    assertEquals(true, result);
  }

  @Test
  public void testAddRequest() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rbm.addRequest(a);
    assertEquals(a, rbm.getReplenishQueue().poll());
  }

  @Test
  public void testGetReplenishQueue() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rbm.getReplenishQueue().add(a);
    assertEquals(a, rbm.getReplenishQueue().poll());
  }

  @Test
  public void testSendToPending() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rbm.setReady("Alice");
    rbm.sendToPending("Alice");
    rbm.getReplenishQueue().add(a);
    assertEquals(a, rbm.getReplenishQueue().poll());
  }
  
  @Test
  public void testSendToNextStage() {
    assertEquals(false, rbm.sendToNextStage("Alice"));
  }
}
