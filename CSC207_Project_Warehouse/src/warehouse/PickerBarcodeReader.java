package warehouse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A picker in the warehouse.
 */
public class PickerBarcodeReader extends BarcodeReader {
  

  // private static final long serialVersionUID = 1L;
  private static final Logger LOGGER =
      Logger.getLogger(PickerBarcodeReader.class.getName());
//  private static final Handler consoleHandler = new ConsoleHandler();
  /** The current picking request the picker is holding now. */
//  private PickingRequest currentPickingRequest;
  /** The name of the picker holding this PickerBarcodeReader. */
//  private String name;
  /** A queue of picking inventory with location and Sku. */ 
  private ConcurrentLinkedQueue<ArrayList<String>> pickingOrderQueue = new ConcurrentLinkedQueue<>();
  
  /** 
   * Initialize a new picker. 
   */
  public PickerBarcodeReader(String name) {
    super(name);
//    LOGGER.setUseParentHandlers(false);
//    LOGGER.setLevel(Level.ALL);
//    try {
//      FileHandler fh = new FileHandler("log.txt");
//      fh.setFormatter(new SimpleFormatter());
//      fh.setLevel(Level.ALL);
//      ConsoleHandler ch = new ConsoleHandler();
//      ch.setFormatter(new SimpleFormatter());
//      // after setting the level, only log messages with level >= the level set
//      // could be displayed.
//      ch.setLevel(Level.ALL);
//      LOGGER.addHandler(fh);
//      LOGGER.addHandler(ch);
//    } catch (IOException e) {
//      
//    }
  }
  
  /**
   * To get the current picking request the picker assigned.
   * 
   * @return current picking request the picker holds.
   */
//  public PickingRequest getCurrentPickingRequest() {
//    return this.currRequest;
//  }
  
  /**
   * Get the picking queue with location and Sku.
   * 
   * @return queue of picking inventories.
   */
  public ConcurrentLinkedQueue<ArrayList<String>> getPickingOrderQueue() {
    return pickingOrderQueue;
  }
  
  /**
   * To verify if the current inventory's Sku is correct.
   * 
   * @param Sku
   * @return the picker picks Sku is correct or not.
   */
  public boolean verify(String Sku) {
    // if the Sku the picker scan is match the first one 
    // in the picking order queue, return true.
    if (Sku.equals(pickingOrderQueue.peek().get(1))) {
      pickingOrderQueue.poll();
      ArrayList<String> next = pickingOrderQueue.peek();
      if (next != null) {  // Check if there still exist next picking location and sku.
        LOGGER.log(Level.FINE, "Picks correct. Pick SKU " + next.get(1) + " at location " + next.get(0));
        return true;
      } else if (next == null) {
        // if the picker has finished picking
        LOGGER.log(Level.FINE, "All correct. Send to marshaling.");
        return true;
      }
    }
    // If scanned sku is not correct.
    ArrayList<String> correct = pickingOrderQueue.peek();
    LOGGER.log(Level.SEVERE, "Picks wrong, put back!" + " Pick SKU " + correct.get(1) + " at location " + 
    correct.get(0));
     return false;
    
  }
  
  /** 
   * Get new picking request from system, and change the current picking request to
   * the assigned picking request.
   * 
   * @param pr
   *        a valid picking request that will be assigned to the picker.
   */
  public void assignPickingRequest(PickingRequest pr) {
    // Change the current picking request to the assigned one.
    this.currRequest = pr;
    // Add optimized sku to picking order queue
    for (ArrayList<String> item : optimize(pr.getSkus())){
      pickingOrderQueue.add(item);
    }
    // Tell the picker the first location and sku to pick.
    ArrayList<String> first = pickingOrderQueue.peek(); 
    LOGGER.log(Level.FINE, "Pick Sku " + first.get(1) + " at " + first.get(0));
    
    System.out.println("Picking request ID " + pr.getId() + " assigned to picker " + name + " with "
        + "picking order queue " + pickingOrderQueue);
  }
  
  /**
   * After the picker finished current picking request, send to to marshaling to sequence.
   */
  public boolean sendToNextStage() {
    if (pickingOrderQueue.isEmpty()) {
    LOGGER.log(Level.FINE, "Picker " + name + " sent to marshaling.");
    System.out.println(this.getCurrRequest() instanceof PickingRequest);
    // After send to sequencer, clear the current picking request.
    this.currRequest = null;
    return true;
  } else {
    // If the picker didn't finish picking
    LOGGER.log(Level.SEVERE, "Can't send to marshaling!");
    return false;
  }
  }

  /**
   * Optimize the shortest route to pick inventories.
   * 
   * @param skus
   * @return optimized list of location and sku pairs.
   */
  private List<ArrayList<String>> optimize(List<String> skus) {
    try {
      return WarehousePicking.optimize(skus);
    } catch (FileNotFoundException e) {
      return null;
    }
  }
  
  /**
   * Inherit the method from super class but do nothing.
   */
  @Override
  public void rescan() {
    
  }
  
//  public PickingRequest getCurrRequest() {
//    return this.currentPickingRequest;
//  }
}
