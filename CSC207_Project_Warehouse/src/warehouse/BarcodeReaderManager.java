package warehouse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The manager for bar-code readers.
 */
public abstract class BarcodeReaderManager {
  
  /** A map of worker name to worker bar-code reader. */
  protected Map<String, BarcodeReader> workerMap;
  /** The queue of all ready workers, in the order they declare ready. */
  protected ConcurrentLinkedQueue<BarcodeReader> readyWorkers;
  
  /**
   * Instantiate a new bar-code reader manager with empty workers.
   */
  public BarcodeReaderManager() {
    this.workerMap = new HashMap<String, BarcodeReader>();
    this.readyWorkers = new ConcurrentLinkedQueue<BarcodeReader>();
  }
  
  /**
   * Return the worker name to worker bar-code reader map this manager manages.
   * 
   * @return the worker name to worker bar-code reader map.
   */
  public Map<String, BarcodeReader> getWorkerMap() {
    return workerMap;
  }

  /**
   * Return a queue of all the ready workers this manager manages.
   * 
   * @return a queue of all the ready workers this manager manages.
   */
  public ConcurrentLinkedQueue<BarcodeReader> getReadyWorkers() {
    return readyWorkers;
  }

  /**
   * Set the state of this worker to be ready to accept requests, add him
   * into the ready worker queue, then assign him an appropriate request
   * if possible.
   * 
   * @param name
   *         the name of this worker.
   */
  public abstract void setReady(String name);
  
  /**
   * Assign the request comes first into the system or has the highest
   * priority to the first ready worker, if both of them exist.
   */
  public abstract void assignRequest();
  
  /**
   * Return true if and only if the SKU number this worker scanned is 
   * correct.
   * 
   * @param name
   *         the name of this worker.
   * @param sku
   *         the SKU that this worker just scanned.
   * @return true if the SKU number this worker scanned is correct.
   */
  public abstract boolean verify(String name, String sku);
  
  /**
   * Return whether this bar-code reader manager has any ready workers.
   * 
   * @return true if this worker manager has ready workers.
   */
  public boolean hasReadyWorker() {
    return !this.readyWorkers.isEmpty();
  }
  
  /**
   * Add request to the bar-code reader manager's request queue that waiting
   * to be processed.
   * 
   * @param request
   *            the request that need to be added into the manager's
   *            request queue.
   */
  public abstract void addRequest(Object request);
  
  /**
   * Return the current request assigned to this worker.
   * 
   * @param name
   *            the name of this worker.
   * @return the current request assigned to this worker.
   */
  public Object getCurrRequest(String name) {
    return this.workerMap.get(name).getCurrRequest();
  }
  
  /**
   * Clear the current request assigned to this worker.
   * 
   * @param name
   *            the name of this worker.
   */
  public void clearCurrRequest(String name) {
    this.workerMap.get(name).clearCurrRequest();
  }
  
  /**
   * Send the worker's current assigned request to pending queue that
   * waiting to be sent to the next stage.
   * 
   * @param name
   *            the name of this worker.
   */
  public abstract void sendToPending(String name);
  
  /**
   * Retrieve a picking request that is being picked wrongly to be 
   * waiting for picking again.
   * 
   * @param pickReq
   *            the picking request that is being retrieved.
   */
//  public abstract void addRetrievedReq(PickingRequest pickReq);
  
  /**
   * Update a worker's state when he asks to rescan his current set of 
   * products.
   * 
   * @param name
   *            the name of this worker.
   */
  public void rescan(String name) {
    this.workerMap.get(name).rescan();
  }
  
  public abstract boolean sendToNextStage(String name);

}
