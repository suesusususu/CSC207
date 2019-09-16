package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PickingRequestTest {
  
  private PickingRequest pr1;
  private PickingRequest pr2;
  private PickingRequest pr3;
  
  @Before
  public void setUp() {
    List<String> orders = new ArrayList<>();
    List<String> skus = new ArrayList<>();
    this.pr1 = new PickingRequest(orders, skus, 1);
    this.pr2 = new PickingRequest(orders, skus, 2);
    this.pr3 = new PickingRequest(orders, skus, 3);
  }

  @Test
  public void testGetOdersEmpty() {
    assertEquals(true, pr1.getOders().isEmpty());
  }

  @Test
  public void testGetSkusEmpty() {
    assertEquals(true, pr1.getSkus().isEmpty());
  }

  @Test
  public void testGetId() {
    assertEquals(1, pr1.getId());
    assertEquals(2, pr2.getId());
    assertEquals(3, pr3.getId());
  }

  @Test
  public void testCompareTo() {
    assertEquals(-1, pr1.compareTo(pr2));
    assertEquals(2, pr3.compareTo(pr1));
    assertEquals(0, pr2.compareTo(pr2));
  }

  @Test
  public void testGetIsVerified() {
    assertEquals(false, pr1.getIsVerified());
    
  }

  @Test
  public void testSetIsVerified() {
    pr1.setIsVerified(true);
    assertEquals(true, pr1.getIsVerified());
  }

}
