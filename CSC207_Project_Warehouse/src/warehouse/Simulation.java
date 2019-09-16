package warehouse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * A warehouse managing system simulation.
 */
public class Simulation {
  
  /** The file containing all locations in the warehouse with stock level 
   * less than 30. */
  public static final String INI_FILE = "initial.csv";
  /** The traversal table showing locations of different products by SKUs. */
  public static final String TRAVERSAL = "traversal_table.csv";
  /** The translation table showing the SKUs for different minivan models. */
  public static final String TRANS_FILE = "translation.csv";
  /** The input event file lists all the events happened. */
  public static final String EVENT_FILE = "test2.txt";
  /** The output orders file name containing the orders get loaded. */
  public static final String ORDER_FILE = "orders.csv";
  /** The output file containing the inventory level of locations with less
   * than 30 stock left. */
  public static final String FINAL_FILE = "final.csv";
  /** The worker types this warehouse has. */
  public static final String[] WORKER_TYPES = {"picker", "sequencer", "loader", "replenisher"};
  /** The event handler this system uses. */
  private static EventHandler eventHandler;
  
  /**
   * Turn on the simulation system.
   * 
   * @param args ignored.
   * 
   * @throws IOException
   *            throws an IOException.
   */
  public static void main(String[] args) throws IOException {
    // Setup the logger named at the package to be used by all classes.
    Logger logger = Logger.getLogger("warehouse");
    logger.setLevel(Level.ALL);
    // Customize own logging format.
    Formatter formatter = new MyFormatter();
    // Setup console handler.
    ConsoleHandler consoleHandler = new ConsoleHandler();
    logger.addHandler(consoleHandler);
    consoleHandler.setLevel(Level.ALL);
    consoleHandler.setFormatter(formatter);
    // Setup file handler.
    FileHandler fileHandler = new FileHandler("testlog.txt");
    logger.addHandler(fileHandler);
    fileHandler.setLevel(Level.ALL);
    fileHandler.setFormatter(formatter);
    // Suppress default handler at the root.
    logger.setUseParentHandlers(false);
    
    // Get the relative path to be configured with file names.
    String path = System.getProperty("user.dir");
    path = path + "/";  // Path to be used in eclipse.
//    path = path.substring(0, path.length() - 3);  // Path used in command line.
    
    // Initialize an appropriate order manager.
    PickingRequestManager pickReqManager = new PickingRequestManager();
    OrderManager orderManager = new FasciaOrderManager(pickReqManager);
    
    // Initialize a set of appropriate bar-code reader manager's.
    BarcodeReaderManagerFactory barManagerFactory = new BarcodeReaderManagerFactory();
    List<BarcodeReaderManager> managers = new ArrayList<>();
    for (String type : WORKER_TYPES) {
      BarcodeReaderManager manager = barManagerFactory.getBarcodeReaderManager(type);
      managers.add(manager);
    }
    
    // Initialize an inventory manager.
    InventoryManager inventoryManager = new InventoryManager(path + INI_FILE, path + TRAVERSAL);
    // Initialize a new warehouse system.
    WarehouseSystem warehouseSystem = new WarehouseSystem();
    // Set the order manager for this warehouse system.
    warehouseSystem.setOrderManager(orderManager); 
    // Set the bar-code reader managers for this warehouse system.
    warehouseSystem.setWorkerManager(WORKER_TYPES, managers);  
    // Set the inventory manager for this warehouse system.
    warehouseSystem.setInventoryManager(inventoryManager);   
    // Setup the warehouse system.
    orderManager.translateTable(path + TRANS_FILE);
    
    // Set the event handler for this system.
    eventHandler = new EventHandler(warehouseSystem, "order");
    
    // Read events from EVENT_FILE.
    String eventFile = path + EVENT_FILE;
    Scanner sc = new Scanner(new File(eventFile));
    while (sc.hasNextLine()) {
      String event = sc.nextLine();
      String eventLine = "[IN][Event]" + event;
      logger.info(eventLine);
      eventHandler.handleEvent(event);
      
    }
    logger.info("Events completed. System turned off.");
    sc.close();
    
    // Produce final.csv containing the final inventory state of locations with stock
    // less than 30.
//    warehouseSystem.produceFinalInventoryFile(path + FINAL_FILE);
    // Produce orders.csv containing all the loaded orders.
//    warehouseSystem.writeOrdersToFile(path + ORDER_FILE);
  }

}
