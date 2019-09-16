package warehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;

/** 
 * An inventory manager for warehouses.
 */
public class InventoryManager {
  
  /** The map of the warehouse floor layout , the key is fascia's sku number and the value is 
   * its location. */
  private Map<String, String> skuToLocation;
  /** The current stock of each location in the warehouse, ordered by location
   * alphabetically. */
  private ConcurrentSkipListMap<String, Integer> warehouseStock;
  /** The maximum stock level each location in the warehouse should hold. */
  public static final int MAX_STOCK = 30;
  /** The minimum stock level each location in the warehouse must remain. */
  public static final int MIN_STOCK = 5;
  /** The amount to be replenished each time. */
  public static final int REP_AMOUNT = 25;
  
  /** 
   * Create a new InventoryManager from an initial inventory level file.
   * 
   * @param iniFile
   *            a file containing the initial inventory state of any locations
   *            in the warehouse that are less than MAX_STOCK.
   * @throws FileNotFoundException 
   *            if the file does not exist.
   */
  public InventoryManager(String iniFile, String travTable) throws FileNotFoundException {
    this.warehouseStock = new ConcurrentSkipListMap<String, Integer>();
    this.skuToLocation = new HashMap<>();
    this.readIniFile(iniFile);
    this.readTraversalTable(travTable);
  }
  
  /**
   * Reads the initial inventory level file, and add contents to
   * warehouseStock.
   * 
   * @param iniFile
   *            a file containing the initial inventory state of any locations
   *            in the warehouse that are less than MAX_STOCK.
   * @throws FileNotFoundException
   *            if the file does not exist.
   */
  private void readIniFile(String iniFile) throws FileNotFoundException {
    // Read from file iniFile to add contents to warehouseStock.
    Scanner sc = new Scanner(new File(iniFile));
    while (sc.hasNextLine()) {
      // Get the location string from each line in file.
      String location = "";
      String[] record = sc.nextLine().trim().split(",");
      for (int i = 0; i < record.length - 1; i++) {
        location += record[i] + " ";
      }
      location = location.trim();
      // Get the amount with each location.
      Integer amount = Integer.valueOf(record[record.length - 1]);
      // Add an entry to the map warehouseStock.
      this.warehouseStock.put(location, amount);
    }
    sc.close();
  }
  
  /**
   * Read the location-sku table, so that we can interpret it into the skuToLocation map
   * in system.
   * @param file
   *           the floor layout file
   * @throws FileNotFoundException
   *            if the file not exists.
   */
  private void readTraversalTable(String file) throws FileNotFoundException {
    Scanner sc = new Scanner(new File(file));
    while (sc.hasNextLine()) {
      // Get the location list from each line in file.
      String location = "";
      String[] record = sc.nextLine().trim().split(",");
      for (int i = 0; i < record.length - 1; i++) {
        location += record[i] + " ";
      }
      location = location.trim();
      String sku  = record[record.length - 1];
      skuToLocation.put(sku, location);
    }
    sc.close();
  }
  
  /**
   * Return the current stock level of a location in the warehouse.
   * 
   * @param location
   *            a valid location string composed of Zone, Aisle, Rack, Level,
   *            and separated by a space.
   * @return the current stock level of a location.
   */
  public int getStock(String location) {
    // If the location is not in warehouseStock yet, set it to MAX_STOCK.
    if (!this.warehouseStock.containsKey(location)) {
      this.warehouseStock.put(location, MAX_STOCK);
    }
    return this.warehouseStock.get(location);
  }
  
  /**
   * Pick one item from the location, trigger a replenishing request if the 
   * remaining stock level in that location reaches MIN_STOCK and print out
   * a replenishing request message.
   * 
   * @param location
   *            a valid location string composed of Zone, Aisle, Rack, Level,
   *            and separated by a space.
   */
  public List<String> updateStock(String sku) {
    String location = this.skuToLocation.get(sku);
    int stock = this.getStock(location);
    // Pick one from the current location.
    this.warehouseStock.replace(location, stock - 1);
    // Check whether a replenishing request should be triggered.
    if (this.warehouseStock.get(location) == MIN_STOCK) {
      System.out.println("Stock at " + location + " has only " + MIN_STOCK 
          + " left, request replenishing.");
      List<String> repleReq = new ArrayList<>();
      repleReq.add(location);
      repleReq.add(sku);
      return repleReq;
    }
    return null;
  }
  
  /**
   * Replenish the stock at location by REP_AMOUNT and print out a replenish
   * message.
   * 
   * @param location
   *            a valid location string composed of Zone, Aisle, Rack, Level,
   *            and separated by a space.
   */
  public void replenish(String location) {
    int stock = this.getStock(location) + REP_AMOUNT;
    this.warehouseStock.put(location, stock);
    System.out.println("Stock at " + location + " has been replenished to " + stock + ".");
  }
  
  /**
   * Create a final.csv file containing the final inventory state of warehouse 
   * locations with less than MAX_STOCK stock remaining.
   * 
   * @throws IOException
   *            throws an IOException
   */
  public void writeInventoryFile(String fileName) throws IOException {
    FileWriter writer = new FileWriter(fileName);
    
    // Get contents of warehouseStock.
    while (!this.warehouseStock.isEmpty()) {
      // Get and remove the first entry from warehouseStock.
      Entry<String, Integer> entry = this.warehouseStock.pollFirstEntry();
      // Record this entry as one line in the file if it has less than MAX_STOCK
      // stock remaining at this location.
      int stock = entry.getValue();
      if (stock < MAX_STOCK) {
        String line = entry.getKey() + " " + stock;
        // Split the string into tokens belong to separate cells.
        String[] cells = line.trim().split(" ");
        
        for (int i = 0; i < cells.length - 1; i++) {
          writer.append(cells[i]);
          writer.append(',');
        }
        
        writer.append(cells[cells.length - 1]);
        writer.append('\n');
      }
    }
    
    writer.flush();
    writer.close();
  }

}
