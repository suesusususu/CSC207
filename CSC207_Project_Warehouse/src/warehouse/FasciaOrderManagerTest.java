package warehouse;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FasciaOrderManagerTest {

  @Test
  public void testProcessOrder() throws FileNotFoundException {
    PickingRequestManager prm = new PickingRequestManager();
    FasciaOrderManager fom = new FasciaOrderManager(prm);
    fom.translateTable("translation.csv");
    String odr1 = "S White";
    String odr2 = "SE White";
    String odr3 = "S Beige";
    String odr4 = "SES Red";
    List<String> sku = new ArrayList<String>();
    sku.add("1");
    sku.add("3");
    sku.add("9");
    sku.add("21");
    sku.add("2");
    sku.add("4");
    sku.add("10");
    sku.add("22");
    assertEquals(fom.processOrder(odr1), null);
    assertEquals(fom.processOrder(odr2), null);
    assertEquals(fom.processOrder(odr3), null);
    assertEquals(sku.equals((fom.processOrder(odr4)).getSkus()), true);
  }
  
  @Test
  public void testAddHistoryPicReq() throws FileNotFoundException {
    PickingRequestManager prm = new PickingRequestManager();
    FasciaOrderManager fom = new FasciaOrderManager(prm);
    fom.translateTable("translation.csv");
    List<String> odrs = new ArrayList<String>();
    String odr1 = "S White";
    String odr2 = "SE White";
    String odr3 = "S Beige";
    String odr4 = "SES Red";
    odrs.add(odr1);
    odrs.add(odr2);    
    odrs.add(odr3);
    odrs.add(odr4);
    List<String> sku = new ArrayList<String>();
    sku.add("1");
    sku.add("3");
    sku.add("9");
    sku.add("21");
    sku.add("2");
    sku.add("4");
    sku.add("10");
    sku.add("22");
    PickingRequest pr = new PickingRequest(odrs, sku, 1);
    fom.addHistoryPickReq(pr);
    List<PickingRequest> his = prm.getHistoryPr();
    assertEquals(pr, his.get(his.size() - 1));
  }
}
