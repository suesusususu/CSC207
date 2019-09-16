package warehouse;

/**
 * A BarcodeReaderManager factory to create new BarcodeReaderManager
 * of a specific worker type.
 */
public class BarcodeReaderManagerFactory {
  
  /**
   * Instantiate a new BarcodeReaderManagerFactory.
   */
  public BarcodeReaderManagerFactory() {
    
  }
  
  /**
   * Create and get relevant BarcodeReaderManager based on worker type.
   * 
   * @param type
   *            the worker type of the BarcodeReaderManager to create.
   * @return
   *            the BarcodeReaderManager of the worker type.
   */
  public BarcodeReaderManager getBarcodeReaderManager(String type) {
    if (type == null) {
      return null;
    }
    if (type.equalsIgnoreCase("PICKER")) {
      return new PickerBarcodeReaderManager();
    } else if (type.equalsIgnoreCase("SEQUENCER")) {
      return new SequencerBarcodeReaderManager();
    } else if (type.equalsIgnoreCase("LOADER")) {
      return new LoaderBarcodeReaderManager();
    } else if (type.equalsIgnoreCase("REPLENISHER")) {
      return new ReplenisherBarcodeReaderManager();
    }
    
    return null;
  }

}
