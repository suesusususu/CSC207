package warehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WarehousePicking {
  /** The translation table to be taken to translate. */
  private static String traTable = "traversal_table.csv";
  /** The map of sku to location. */
  private static Map<String, String> skuToLocation = new HashMap<String, String>();
  
  /**
   * To initialize a warehouse picking software.
   */
  public WarehousePicking() { 
  }
  
  /**
   * Set the traversal table.
   * 
   * @param fileName
   *        A traversal table of location and location's stock.
   * @throws FileNotFoundException 
   *         The destination file may be incorrect or does not exist.
   */
  public static void setTraversalTable(String fileName) throws FileNotFoundException {
    WarehousePicking.traTable = fileName;
    WarehousePicking.readTraversalFile(fileName);
  } 
  
  /**
   * Read the traversal table, and interpret it into the SKU to Location map
   * in system.
   * 
   * @param traversalTable
   *           the floor layout file
   * @throws FileNotFoundException
   *            if the file not exists.
   */
  private static void readTraversalFile(String traversalTable) throws FileNotFoundException {
    Scanner sc = new Scanner(new File(traversalTable));
    while (sc.hasNextLine()) {
      // Get the location list from each line in file.
      String location = "";
      String[] record = sc.nextLine().trim().split(",");
      for (int i = 0; i < record.length - 1; i++) {
        location += record[i] + " ";
      }
      location = location.trim();
      // Get the SKU corresponding to each location.
      String sku  = record[record.length - 1];
      // Add entry to the map.
      skuToLocation.put(sku, location);
    }
    sc.close();
  }
  
  /**
   * Sort a list of Skus by optimizing the the shortest route in the warehouse.
   * 
   * @param skus
   *        The list of SKU's to be be optimized.
   * @return List<ArrayList<String>>
   *        The list of list of location and sku.
   * @throws FileNotFoundException 
   *         The file traTable may be incorrect or does not exist.
   */
  public static List<ArrayList<String>> optimize(List<String> skus) throws FileNotFoundException {
    // Keeps the list to be sorted. 
    List<String> locations = new ArrayList<String>();
    // Keeps the pairs as hash map.
    Map<String, String> pairs = new HashMap<String, String>();
    if (skuToLocation.size() == 0) {
      readTraversalFile(traTable);
    }
    for (String sku : skus) {
      String loc = skuToLocation.get(sku);
      locations.add(loc);
      pairs.put(loc, sku);
    }
    Collections.sort(locations);
    // This is the final list to be returned.
    List<ArrayList<String>> finallist = new ArrayList<ArrayList<String>>();
    for (int i = 0; i < 8; i++) {
      ArrayList<String> locsku = new ArrayList<String>();
      locsku.add(locations.get(i));
      locsku.add(pairs.get(locations.get(i)));
      finallist.add(locsku);
    }
    return finallist;
  }

}
