package warehouse;

import java.awt.List;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Initialize a replenisher in the warehouse.
 */
public class ReplenisherBarcodeReader extends BarcodeReader {
  
  /** Set a logger. */
  private static final Logger LOGGER =
      Logger.getLogger(ReplenisherBarcodeReader.class.getName());
  
  /**
   * Initialize a replenisher's barcode reader with name. Constructor a
   * logger and handler.
   * 
   * @param name
   */
  public ReplenisherBarcodeReader(String name) {
    super(name);
  }
  
  /**
   * To verify if the sku is the corresponded to the location that
   * need to replenish.
   * 
   * @param Sku
   *        Sku of the replenisher scans.
   *        
   * @return boolean
   *         if the replenish scans correct the sku.
   */
  @Override
  public boolean verify(String Sku) {
    System.out.println(this.currRequest);
    System.out.println(this.currRequest instanceof ArrayList);
    // Check if the sku are correct
    if (Sku.equals(((ArrayList<?>) currRequest).get(1))) {
      // If correct, return true.
      LOGGER.log(Level.FINE, "Correct, ready to replenish");
      return true;
      // If not, return false and log a message to tell the replenisher
    } else {
      LOGGER.log(Level.SEVERE, "Not correct.");
      return false;
  }
  }

  /**
   * To replenish a location's stock.
   * 
   * @param location
   *        the location replenisher replenished.
   */
  public void replenish(String location) {
    LOGGER.log(Level.FINE, location + "is replenished.");
    // After replenished the location, the current request should clear up.
    ((ArrayList<?>) this.currRequest).clear();
  }

  
  /**
   * Inherit method from super class but do nothing.
   */
  @Override
  public void rescan() {
    
  }
  

}
