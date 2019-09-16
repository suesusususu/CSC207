package warehouse;

import java.util.Set;
import java.util.logging.Logger;

public class EventHandler {
  
  /** The system this event handler works for. */
  private WarehouseSystem warehouseSystem;
  /** The set of worker types this event handler handles. */
  private Set<String> workerTypes;
  /** The order type this event handler handles. */
  private String orderType;
  /** A logger to log messages. */
  private Logger logger = Logger.getLogger(EventHandler.class.getName());
  
  /**
   * Instantiate a new event handler.
   * 
   * @param system
   *            the system this event handler works for.
   * @param orderType
   *            the order type this event handler handles.
   */
  public EventHandler(WarehouseSystem system, String orderType) {
    this.warehouseSystem = system;
    this.workerTypes = system.getWorkerTypes();
    this.orderType = orderType;
  }
  
  /**
   * Return whether this event is of a valid type.
   * 
   * @param event
   *            the event message string.
   * @return whether this event is of a valid type.
   */
  public boolean isValidType(String event) {
    if (event != null && event.length() > 0) {
      String[] eventArray = event.trim().split(" ");
      String type = eventArray[0].toLowerCase();
      if (workerTypes.contains(type) || type.equalsIgnoreCase(orderType)) {
        return true;
      }
    }
    this.invalidEvent();
    return false;
  }
  
  /** 
   * Report invalid event message.
   */
  private void invalidEvent() {
    logger.severe("Event not recognized, enter a valid event.");
  }
  
  /**
   * Handles events of various types.
   * 
   * @param event
   *            the event message string.
   */
  public void handleEvent(String event) {
    // Check whether this event is of valid type.
    if (this.isValidType(event)) {
      String[] eventArray = event.trim().split(" ");
      String type = eventArray[0];
      // Handles order event.
      if (type.equalsIgnoreCase(orderType)) {
        this.handleOrderEvent(eventArray);
        // Handles picker event.
      } else if (type.equalsIgnoreCase("PICKER")) {
        this.handlePickerEvent(eventArray);
        // Handles sequencer event.
      } else if (type.equalsIgnoreCase("SEQUENCER")) {
        this.handleSeqEvent(eventArray);
        // Handles loader event.
      } else if (type.equalsIgnoreCase("LOADER")) {
        this.handleLoaderEvent(eventArray);
        // Handles replenisher event.
      } else if (type.equalsIgnoreCase("REPLENISHER")) {
        this.handleRepleEvent(eventArray);
      }
    }
  }
  
  /**
   * Handles order events.
   * 
   * @param event
   *            the array of each event word strings.
   */
  private void handleOrderEvent(String[] event) {
    String order = event[1] + " " + event[2];
    logger.info("[IN][To System]New order: " + order);
    warehouseSystem.processOrder(order);
  }
  
  /**
   * Handles picker event.
   * 
   * @param event
   *            the array of each event word strings.
   */
  private void handlePickerEvent(String[] event) {
    String type = "picker";
    // Get the picker's name.
    String pickerName = event[1];
    // The message header to this type of event.
    String msgHeader = "[IN][Barcode Reader][To System]Picker ";
      // The picker is ready to work.
    if (event[2].equalsIgnoreCase("ready")) {
      logger.info(msgHeader + pickerName + " is Ready.");
      warehouseSystem.setReady(type, pickerName);
      // The picker picks a product.
    } else if (event[2].equalsIgnoreCase("pick")) {      
      String sku = event[3];
      logger.info(msgHeader + pickerName + " scans " + sku);
      warehouseSystem.verify(type, pickerName, sku);
      // The picker sent current picking request to Marshaling.
    } else if (event[3].equalsIgnoreCase("marshaling")) {
      logger.info(msgHeader + pickerName + " send to Marshaling.");
      warehouseSystem.sendToNextStage(type, pickerName);
    }
  }
  
  /**
   * Handles sequencer event.
   * 
   * @param event
   *            the array of each event word strings.
   */
  private void handleSeqEvent(String[] event) {
    String type = "sequencer";
    // Get the sequencer's name.
    String seqName = event[1];
    // The message header to this type of event.
    String msgHeader = "[IN][Barcode Reader][To System]Sequencer ";
      // The sequencer is ready to work.
    if (event[2].equalsIgnoreCase("ready")) {
      logger.info(msgHeader + seqName + " is Ready.");
      warehouseSystem.setReady(type, seqName);
      // The sequencer scans a product.
    } else if (event[2].equalsIgnoreCase("scan")) {      
      String sku = event[3];
      logger.info(msgHeader + seqName + " scans " + sku);
      warehouseSystem.verify(type, seqName, sku);
      // The sequencer rescans.
    } else if (event[2].equalsIgnoreCase("rescan")) {
      logger.info(msgHeader + seqName + " rescans.");
      warehouseSystem.rescan(type, seqName);
      // The sequencer send current picking request to loader.
    } else if (event[2].equalsIgnoreCase("send") && event[4].equalsIgnoreCase("loader")) {
      logger.info(msgHeader + seqName + " send to loader.");
      warehouseSystem.sendToNextStage(type, seqName); 
      // The sequencer discards picking current request.
    } else if (event[2].equalsIgnoreCase("retrieve")) {
      logger.info(msgHeader + seqName + " retrieves.");
      warehouseSystem.retrievePickReq(type, seqName);
    }
  }
  
  /**
   * Handles loader event.
   * 
   * @param event
   *            the array of each event word strings.
   */
  private void handleLoaderEvent(String[] event) {
    String type = "loader";
    // Get the loader's name.
    String loaderName = event[1];
    // The message header to this type of event.
    String msgHeader = "[IN][Barcode Reader][To System]Loader ";
      // The loader is ready to work.
    if (event[2].equalsIgnoreCase("ready")) {
      logger.info(msgHeader + loaderName + " is Ready.");
      warehouseSystem.setReady(type, loaderName);
      // The loader scans a product.
    } else if (event[2].equalsIgnoreCase("scan")) {      
      String sku = event[3];
      logger.info(msgHeader + loaderName + " scans " + sku);
      warehouseSystem.verify(type, loaderName, sku);
      // The loader rescans.
    } else if (event[2].equalsIgnoreCase("rescan")) {
      logger.info(msgHeader + loaderName + " rescans.");
      warehouseSystem.rescan(type, loaderName);
      // The loader send current picking request to pending.
    } else if (event[2].equalsIgnoreCase("send") && event[4].equalsIgnoreCase("pending")) {
      logger.info(msgHeader + loaderName + " send to pending.");
      warehouseSystem.sendToPending(type, loaderName);
      // The loader loads his current request onto truck.
    } else if (event[2].equalsIgnoreCase("load")) {
      logger.info(msgHeader + loaderName + " loads.");
      warehouseSystem.sendToNextStage(type, loaderName);
      // The loader discards picking current request.
    } else if (event[2].equalsIgnoreCase("retrieve")) {
      logger.info(msgHeader + loaderName + " retrieves.");
      warehouseSystem.retrievePickReq(type, loaderName);
    }
  }
  
  /**
   * Handles replenisher event.
   * 
   * @param event
   *            the array of each event word strings.
   */
  private void handleRepleEvent(String[] event) {
    String type = "replenisher";
    // Get the replenisher's name.
    String repleName = event[1];
    // The message header to this type of event.
    String msgHeader = "[IN][Barcode Reader][To System]Replenisher ";
      // The replenisher is ready to work.
    if (event[2].equalsIgnoreCase("ready")) {
      logger.info(msgHeader + repleName + " is Ready.");
      warehouseSystem.setReady(type, repleName);
      // The replenisher scans a product.
    } else if (event[2].equalsIgnoreCase("scan")) {      
      String sku = event[3];
      logger.info(msgHeader + repleName + " scans " + sku);
      warehouseSystem.verify(type, repleName, sku);
      // The replenisher replenishes a location.
    } else if (event[2].equalsIgnoreCase("replenish")) {
      // Get the location that is replenished.
      String location = "";
      for (int i = 3; i < event.length; i++) {
        location += event[i] + " ";
      }
      location = location.toUpperCase().trim();
      logger.info(msgHeader + repleName + " replenished " + location);
      // Replenish stock at the location.
      warehouseSystem.replenish(location);
    }
  }

}
