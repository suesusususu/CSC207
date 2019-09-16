package warehouse;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The warehouse system.
 */
public class WarehouseSystem {
  
  /** The order manager that manages and process every order comes into system. */
  private OrderManager orderManager;
  /** The worker type to bar-code reader manager map that manages and dispatch all 
   * workers in the warehouse. */
  private Map<String, BarcodeReaderManager> workerManager;
  /** The inventory of the warehouse .*/ 
  private InventoryManager inventoryManager;
  private static final Logger LOGGER =
      Logger.getLogger(WarehouseSystem.class.getName());
  
  /**
   * Create a new warehouse system.
   */
  public WarehouseSystem() {
    this.workerManager = new HashMap<>();
  }
  
  /**
   * Set the order manager when initialize this system.
   * 
   * @param orderManager
   *            the order manager that the warehouse system needs.
   */
  public void setOrderManager(OrderManager orderManager) {
    this.orderManager = orderManager;
  }
  
  /**
   * Translate the file containing the products and subjected SKU numbers.
   * 
   * @param fileName
   *            the file with products and SKU numbers.
   * @throws FileNotFoundException
   *            throws an exception if the file is not found.
   */
  public void translateTable(String fileName) throws FileNotFoundException {
    this.orderManager.translateTable(fileName);
  }
  
  /**
   * Set all bar-code reader managers when initialize this system.
   * 
   * @param types
   *          the list of all worker types in this warehouse.
   * @param managers
   *          the list of the bar-code reader managers of each worker type.
   */
  public void setWorkerManager(String[] types, List<BarcodeReaderManager> managers) {
    if (!(types.length == managers.size())){
      LOGGER.log(Level.SEVERE, "No!");
    }else{
      for (int i = 0; i < types.length; i++) {
         String type = types[i];
         BarcodeReaderManager manager = managers.get(i);
         this.workerManager.put(type, manager);
      }
    }
  }
  
  public Set<String> getWorkerTypes() {
    return this.workerManager.keySet();
  }
  
  /**
   * Process each incoming order to the warehouse system to create picking request,
   * and add each picking request into the picker bar-code reader manager to wait 
   * for picking.
   * 
   * @param order
   *          the order information.
   */
  public void processOrder(String order) {
    PickingRequest pickReq = this.orderManager.processOrder(order);
    // Whenever there generates a picking request, add it into picker bar-code reader manager 
    // and assign a picking request to a ready picker.
    if (!(pickReq == null)) {
      System.out.println("Picking Request ID " + pickReq.getId() + " assigned to Picker manager.");
      this.addRequest("picker", pickReq);
    }
  }
  
  /**
   * Set the state of this worker to be ready to accept requests, and add 
   * him into the ready worker queue, then assign him an appropriate request
   * if possible.
   * 
   * @param type
   *         the type of this worker.
   * @param name
   *         the name of this worker.
   */  
  public void setReady(String type, String name) {
    this.workerManager.get(type).setReady(name);
  }
  
  /**
   * Check whether the SKU number this worker scanned is correct.
   * 
   * @param type
   *         the type of this worker.
   * @param name
   *         the name of this worker.
   * @param sku
   *         the SKU that this worker just scanned.
   */
  public void verify(String type, String name, String sku) {
    // Verify the scanned SKU to the correct SKU.
    boolean verified = this.workerManager.get(type).verify(name, sku);
    // If a picker scans a correct item, update the stock of that location.
    if (verified && type.equalsIgnoreCase("PICKER")) {
      List<String> repleReq = this.inventoryManager.updateStock(sku);
      if (repleReq != null) {
        this.workerManager.get("replenisher").addRequest(repleReq);
      }
    }
  }
  
  /**
   * Set the inventory manager of the system.
   * 
   * @param inventory
   *           the inventory manager this warehouse system uses.
   */
  public void setInventoryManager(InventoryManager inventory) {
    this.inventoryManager = inventory;
  }
  
  /**
   * Send the worker's current request to the next processing stage if it's
   * finished. For instance, when a picker finished his current picking request, 
   * he will send it to marshaling.
   * 
   * @param type
   *          the type of this worker.
   * @param name
   *          the name of this worker.
   */
  public void sendToNextStage(String type, String name) {
    Object request = this.workerManager.get(type).getCurrRequest(name);
    System.out.println(request.toString());
    System.out.println("Picking request " + (request instanceof PickingRequest));
    // Check whether this request should be sent to the next stage.
    if (this.workerManager.get(type).sendToNextStage(name)) {
      // Add a picking request to the marshaling area.
      if (type.equalsIgnoreCase("PICKER")) {
        this.addRequest("sequencer", request);
        // Send the picking request to loader.
      } else if (type.equalsIgnoreCase("SEQUENCER")) {
        this.addRequest("loader", request);
        // Load the picking request onto truck.
      } else if (type.equalsIgnoreCase("LOADER")) {
        this.orderManager.addHistoryPickReq((PickingRequest) request);
      }
    }    
  }
  
  public void sendToPending(String type, String name) {
    this.workerManager.get(type).sendToPending(name);
  }
  
  /**
   * Add a request to a worker manager's request queue of a certain type,
   * and assign it to a ready worker if possible.
   * 
   * @param type
   *            the type of the worker manager to whom to add the request.
   * @param request
   *            the request to be added and assigned to a worker manager.
   */
  public void addRequest(String type, Object request) {
    this.workerManager.get(type).addRequest(request);
  }
  
  /**
   * Retrieve a picking request to be waiting for picking again, and give it
   * higher priority to be picked than other non-retrieved picking requests.
   * 
   * @param type
   *            the type of the worker from whom the picking request is 
   *            retrieved.
   * @param name
   *            the name of the worker from whom the picking request is 
   *            retrieved.
   */
  public void retrievePickReq(String type, String name) {
    // Check whether the retrieved request is a picking request.
    Object request = workerManager.get(type).getCurrRequest(name);
    if (request instanceof PickingRequest) {
      PickingRequest pickReq = (PickingRequest) request;
      // Add this request to the retrieved picking request queue of picker
      // bar-code reader manager.
      BarcodeReaderManager manager = this.workerManager.get("picker");
      if (manager instanceof PickerBarcodeReaderManager) {
        PickerBarcodeReaderManager pickManager = (PickerBarcodeReaderManager) manager;
        pickManager.addRetrievedReq(pickReq);
        // Reset this picking request's status to be unverified.
        pickReq.setIsVerified(false);
        // Remove this worker's current assigned request.
        workerManager.get(type).clearCurrRequest(name);
      }
    }
  }
  
  /**
   * Update a worker's state when he asks to rescan his current set of 
   * products.
   * 
   * @param type
   *            the worker type of this worker.
   * @param name
   *            the name of this worker.
   */
  public void rescan(String type, String name) {
    this.workerManager.get(type).rescan(name);
  }
  
  /** 
   * Update the stock of a location when a replenish request is finished.
   * 
   * @param location
   *            the location that get replenished.
   */
  public void replenish(String location) {
    this.inventoryManager.replenish(location);
  }
  
  /**
   * Return an optimized ordered list of location and SKU pairs, based on
   * the shortest traversal distance.
   * 
   * @param skus
   *            the list of SKUs whose corresponding locations are being
   *            optimized.
   * @return an optimized ordered list of location and SKU pairs.
   */
  public List<ArrayList<String>> optimize(List<String> skus) {
    try {
      return WarehousePicking.optimize(skus);
    } catch (FileNotFoundException exception) {
      return null;
    }
  }
  
  public Map<String, BarcodeReaderManager> getWorkerManager() {
    return workerManager;
  }
  
  public OrderManager getOrderManager() {
    return orderManager;
  }

}
