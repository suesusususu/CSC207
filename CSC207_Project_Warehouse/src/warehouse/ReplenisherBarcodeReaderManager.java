package warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReplenisherBarcodeReaderManager extends BarcodeReaderManager{
  
  
  private ConcurrentLinkedQueue<ArrayList<String>> replenishQueue = new ConcurrentLinkedQueue<>();
  
  /**
   * To construct a new replenisher barcode reader manager.
   */
  public ReplenisherBarcodeReaderManager() {
    super();
  }
  
  /**
   * Set a replenisher ready and add the replenisher to ready workers queue.
   */
  @Override
  public void setReady(String name) {
    // Check if the ready picker is in list of replenisher barcode reader,
    // if not, add the picker to both list of replenisher barcode reader queue 
    // and ready picker list.
    if (!workerMap.containsKey(name)) {
      ReplenisherBarcodeReader replenisher = new ReplenisherBarcodeReader(name);
      workerMap.put(name, replenisher);
      readyWorkers.add(replenisher);
    } else { // if already exists, add the replenisher to list of ready picker.
      readyWorkers.add(workerMap.get(name));
    }  
    this.assignRequest();
  }
  
  /**
   * To get the replenish queue.
   * 
   * @return the replenish queue.
   */
  public ConcurrentLinkedQueue<ArrayList<String>> getReplenishQueue() {
    return replenishQueue;
  }

  /**
   * To assign the replenish request to the first ready replenisher.
   */
  @Override
  public void assignRequest() {
    ReplenisherBarcodeReader replenisher = (ReplenisherBarcodeReader) readyWorkers.peek();
    if (replenisher != null && !replenishQueue.isEmpty()) {
      readyWorkers.poll().assignRequest(replenishQueue.poll());
    } 
  }
  
  /**
   * To verify if the replenisher get the correct Sku of replenishing inventory.
   * 
   * @return boolean
   *        if the replenisher picks right.
   */
  @Override
  public boolean verify(String name, String sku) {
    return workerMap.get(name).verify(sku);
  }
  
  /**
   * To add the replenish request to the queue.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void addRequest(Object request) {
    if (request instanceof ArrayList) {
      replenishQueue.add((ArrayList<String>) request);
      this.assignRequest();
    }
    
  }

  /**
   * Inherit method from super class but do nothing.
   */
  @Override
  public void sendToPending(String name) {
    
  }

  /**
   * Inherit method from super class but do nothing.
   * 
   * @return false
   */
  @Override
  public boolean sendToNextStage(String name) {
    return false;
  }
  
  
}
