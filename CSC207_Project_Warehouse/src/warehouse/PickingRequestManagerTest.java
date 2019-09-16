package warehouse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PickingRequestManagerTest {

    

  @Test
  public void testCreatePickReq() {
    List<String> odr = new ArrayList<String>();
    String odr1 = "S White";
    String odr2 = "SE White";
    String odr3 = "S Beige";
    String odr4 = "SES Red";
    odr.add(odr1);
    odr.add(odr2);
    odr.add(odr3);
    odr.add(odr4);
    List<String> sku = new ArrayList<String>();
    sku.add("1");
    sku.add("3");
    sku.add("9");
    sku.add("21");
    sku.add("2");
    sku.add("4");
    sku.add("10");
    sku.add("22");
    PickingRequestManager prm = new PickingRequestManager();
    prm.createPickReq(odr, sku);
  }

  @Test
  public void testAddHistoryPickReq() {
    List<String> odr = new ArrayList<String>();
    String odr1 = "S White";
    String odr2 = "SE White";
    String odr3 = "S Beige";
    String odr4 = "SES Red";
    odr.add(odr1);
    odr.add(odr2);
    odr.add(odr3);
    odr.add(odr4);
    List<String> sku = new ArrayList<String>();
    sku.add("1");
    sku.add("3");
    sku.add("9");
    sku.add("21");
    sku.add("2");
    sku.add("4");
    sku.add("10");
    sku.add("22");
    PickingRequestManager prm = new PickingRequestManager();
    PickingRequest pr = prm.createPickReq(odr, sku);
    prm.addHistoryPickReq(pr);
    assertEquals((prm.getHistoryPr()).size(), 1);
	}

}
