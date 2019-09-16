package warehouse;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ReplenisherBarcodeReaderTest {

  @Before
  public void setUp() {}
    ReplenisherBarcodeReader rep = new ReplenisherBarcodeReader("Bob");
    
    
  @Test
  public void testVerify() {
    
    ArrayList<String> request = new ArrayList<>();
    request.add("A 1 1 1");
    request.add("1");
    rep.assignRequest(request);
    assertEquals(true, rep.verify("1"));
    assertEquals(false, rep.verify("2"));
  }
  
  @Test
  public void testAssignRequest() {
    ArrayList<String> a = new ArrayList<>();
    a.add("A 1 1 1");
    a.add("1");
    rep.assignRequest(a);
    assertEquals(a, rep.getCurrRequest());
  }
  
  @Test
  public void testReplenish() {
    ArrayList<String> request = new ArrayList<>();
    request.add("A 1 1 1");
    request.add("1");
    rep.assignRequest(request);
    rep.replenish("A 1 1 1");
    ArrayList<String> a = new ArrayList<>();
    assertEquals(a, rep.getCurrRequest());
  }

  @Test
  public void testRescan() {
    rep.rescan();
  }
}
