 package warehouse;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class WarehousePickingTest {

  @Test
  public void testConstructor() {
    WarehousePicking wp = new WarehousePicking();
    assertEquals(wp.equals(null), false);
  }
  
  @Test
  public void testSetTraTable() throws FileNotFoundException {
    List<String> skulst = new ArrayList<String>();
    skulst.add("1");
    skulst.add("2");
    skulst.add("3");
    skulst.add("4");
    skulst.add("5");
    skulst.add("6");
    skulst.add("7");
    skulst.add("8");
    List<ArrayList<String>> finallist2 = WarehousePicking.optimize(skulst);
    List<ArrayList<String>> lst2 = new ArrayList<ArrayList<String>>();
    ArrayList<String> pairs1 = new ArrayList<String>();
    pairs1.add("A 0 0 0");
    pairs1.add("1");
    lst2.add(pairs1);
    ArrayList<String> pairs2 = new ArrayList<String>();
    pairs2.add("A 0 0 1");
    pairs2.add("2");
    lst2.add(pairs2);
    ArrayList<String> pairs3 = new ArrayList<String>();
    pairs3.add("A 0 0 2");
    pairs3.add("3");
    lst2.add(pairs3);
    ArrayList<String> pairs4 = new ArrayList<String>();
    pairs4.add("A 0 0 3");
    pairs4.add("4");
    lst2.add(pairs4);
    ArrayList<String> pairs5 = new ArrayList<String>();
    pairs5.add("A 0 1 0");
    pairs5.add("5");
    lst2.add(pairs5);
    ArrayList<String> pairs6 = new ArrayList<String>();
    pairs6.add("A 0 1 1");
    pairs6.add("6");
    lst2.add(pairs6);
    ArrayList<String> pairs7 = new ArrayList<String>();
    pairs7.add("A 0 1 2");
    pairs7.add("7");
    lst2.add(pairs7);
    ArrayList<String> pairs8 = new ArrayList<String>();
    pairs8.add("A 0 1 3");
    pairs8.add("8");
    lst2.add(pairs8);
    assertEquals(true, lst2.equals(finallist2));
    WarehousePicking.setTraversalTable("traversal_table_test.csv");
    List<ArrayList<String>> finallist1 = WarehousePicking.optimize(skulst);
    List<ArrayList<String>> lst1 = new ArrayList<ArrayList<String>>();
    ArrayList<String> pair1 = new ArrayList<String>();
    pair1.add("A 0 0 0");
    pair1.add("8");
    lst1.add(pair1);
    ArrayList<String> pair2 = new ArrayList<String>();
    pair2.add("A 0 0 1");
    pair2.add("6");
    lst1.add(pair2);
    ArrayList<String> pair3 = new ArrayList<String>();
    pair3.add("A 0 0 2");
    pair3.add("7");
    lst1.add(pair3);
    ArrayList<String> pair4 = new ArrayList<String>();
    pair4.add("A 0 0 3");
    pair4.add("3");
    lst1.add(pair4);
    ArrayList<String> pair5 = new ArrayList<String>();
    pair5.add("A 0 1 0");
    pair5.add("4");
    lst1.add(pair5);
    ArrayList<String> pair6 = new ArrayList<String>();
    pair6.add("A 0 1 1");
    pair6.add("5");
    lst1.add(pair6);
    ArrayList<String> pair7 = new ArrayList<String>();
    pair7.add("A 0 1 2");
    pair7.add("2");
    lst1.add(pair7);
    ArrayList<String> pair8 = new ArrayList<String>();
    pair8.add("A 0 1 3");
    pair8.add("1");
    lst1.add(pair8);
    assertEquals(true, lst1.equals(finallist1));
  }

}
