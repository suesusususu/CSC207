package warehouse;

/**
 * A worker bar-code reader.
 */
public abstract class BarcodeReader {
  
  /** The current request assigned to this worker. */
  protected Object currRequest;
  /** The name of this worker. */
  protected String name;
  
  /**
   * Instantiate a new worker bar-code reader with no request assigned.
   * 
   * @param name
   *            the name of this worker.
   */
  public BarcodeReader(String name) {
    this.name = name;
  }
  
  /**
   * Return the name of the worker using this bar-code reader.
   * 
   * @return the name of the worker using this bar-code reader.
   */
  public String getName() {
    return name;
  }

  /**
   * Assign a request to this worker.
   * 
   * @param request
   *            the request being assigned to this worker.
   */
  public void assignRequest(Object request) {
    this.currRequest = request;
    if (request instanceof PickingRequest) {
      System.out.println("Picking Request ID " + ((PickingRequest) request).getId() 
              + " is assigned to " + name);
    }
  }
  
  /**
  * Return true if and only if the SKU number this worker scanned is 
  * correct.
  * 
  * @param sku
  *         the SKU that this worker just scanned.
  * @return true if the SKU number this worker scanned is correct.
  */
  public abstract boolean verify(String sku);
  
  /**
   * Return the current request that is assigned to this worker.
   * 
   * @return the request assigned to this worker.
   */
  public Object getCurrRequest() {
    return this.currRequest;
  }
  
  /** 
   * Clear the current request assigned to this worker.
   */
  public void clearCurrRequest() {
    this.currRequest = null;
  }
  
  /**
   * Update the worker's state when he asks to rescan his current set of 
   * products.
   */
  public abstract void rescan();

}
