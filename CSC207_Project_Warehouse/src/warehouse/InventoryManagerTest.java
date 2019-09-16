package warehouse;



import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InventoryManagerTest {
  
  private InventoryManager inventory;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Before
  public void setUp() throws FileNotFoundException {
    this.inventory = new InventoryManager("initial.csv", "traversal_table.csv");
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(null);
  }

  @Test
  public void testGetStock() {
    assertEquals(inventory.getStock("A 0 1 1"), 6);
    assertEquals(inventory.getStock("B 1 0 0"), 30);
  }
  
  @Test
  public void testUpdateStock() {
    inventory.updateStock("6");
    assertEquals(inventory.getStock("A 0 1 1"), 5);
    assertEquals("Stock at A 0 1 1 has only 5 left, request replenishing.", 
        outContent.toString().trim());
    inventory.updateStock("20");
    assertEquals(inventory.getStock("A 1 1 3"), 19);
    inventory.updateStock("37");
    assertEquals(inventory.getStock("B 1 0 0"), 29);
  }
  
  @Test
  public void testReplenish() {
    inventory.replenish("A 0 1 1");
    assertEquals(inventory.getStock("A 0 1 1"), 31);
    assertEquals("Stock at A 0 1 1 has been replenished to 31.", outContent.toString().trim());
    inventory.replenish("B 1 1 0");
    assertEquals(inventory.getStock("B 1 1 0"), 55);
    assertEquals("Stock at A 0 1 1 has been replenished to 31."
        + "\nStock at B 1 1 0 has been replenished to 55.",
        outContent.toString().trim());
  }
  
  @Test
  public void testWriteInventoryFile() throws IOException, FileNotFoundException {
    this.inventory.writeInventoryFile("inventory_wrt.csv");
    Scanner sc = new Scanner(new File("inventory_wrt.csv"));
    String nxtline = sc.nextLine();
    sc.close();
    System.out.println(nxtline);
    assertEquals("A,0,0,0,6", outContent.toString().trim());
  }

}
