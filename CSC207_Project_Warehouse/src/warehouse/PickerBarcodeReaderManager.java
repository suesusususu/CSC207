package warehouse;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * A picker barcode manager in the warehouse.
 */
public class PickerBarcodeReaderManager extends BarcodeReaderManager {
  
  /** List of all pickers in the warehouse. */
//  private Map<String, PickerBarcodeReader> listPickerBarcodeReader = new HashMap<String, PickerBarcodeReader>();
  /** List of ready pickers. */
//  private ConcurrentLinkedQueue<PickerBarcodeReader> readyPicker = new ConcurrentLinkedQueue<>();
  private ConcurrentLinkedQueue<PickingRequest> pickingRequestQueue = new ConcurrentLinkedQueue<>();
  private PriorityQueue<PickingRequest> retrievedQueue = new PriorityQueue<>();
  
  /**
   * To initialize a picker barcode manager.
   */
  public PickerBarcodeReaderManager() {
    super();
  }
  
  
  /**
   * To get the queue of picking request.
   * 
   * @return the queue of picking request.
   */
  public ConcurrentLinkedQueue<PickingRequest> getPickingRequestQueue() {
    return pickingRequestQueue;
  }
  
  /**
   * To get the queue of retrieved picking request.
   * 
   * @return the queue of retrieved picking request.
   */
  public PriorityQueue<PickingRequest> getRetrievedQueue() {
    return retrievedQueue;
  }
  
  /** 
   * Add the ready picker to ready picker list. 
   * 
   * @param name
   *        the name of a picker.
   */
  public void setReady(String name) {
    // Check if the ready picker is in list of picker barcode reader,
    // if not, add the picker to both list of picker barcode reader queue 
    // and ready picker list.
    if (! workerMap.containsKey(name)) {
      PickerBarcodeReader picker = new PickerBarcodeReader(name);
      workerMap.put(name, picker);
      readyWorkers.add(picker);
    } else { // if already exists, add the picker to list of ready picker.
      readyWorkers.add(workerMap.get(name));
    }
    this.assignRequest();
  }
  
  /**
   * To assign a picking request to a ready picker.
   * 
   * @param name
   *        the name of a picker.
   */
  @Override
  public void assignRequest() {
    PickerBarcodeReader picker = (PickerBarcodeReader) readyWorkers.peek();
    // Check if there is picker ready.
    if (picker != null) {
      // Check if there is picking request in retrieved queue.
      if(! retrievedQueue.isEmpty()) {
        // assign the retrieved picking request first.        
        picker.assignPickingRequest(retrievedQueue.poll());
        readyWorkers.poll();
      // if there is not retrieved picking request, 
      //check if there is regular picking request.
      } else if (! pickingRequestQueue.isEmpty())  {
        picker.assignPickingRequest(pickingRequestQueue.poll());
        readyWorkers.poll();
      }
    }
  }
  
  
  /**
   * To verify if a picker picks correct.
   * 
   * @param name
   *        name of a picker.
   * @param Sku
   *        current inventory's Sku the picker picks and verifies.
   * @return the picker picks correct or not.
   */
  public boolean verify(String name, String Sku) {
    // Verify if the picker picks correctly.
    return workerMap.get(name).verify(Sku);
  }
  
  /**
   * Send the picking request the picker has finished picking to marshaling.
   * 
   * @param name
   */
  public boolean sendToNextStage(String name) {
    return ((PickerBarcodeReader) workerMap.get(name)).sendToNextStage();
  }

  /**
   * Add the picking request in the picking request queue.
   */
  @Override
  public void addRequest(Object request) {
    pickingRequestQueue.add((PickingRequest) request);
    System.out.println("Picking Request ID " + ((PickingRequest) request).getId() + " added to Pick queue.");
    this.assignRequest();
  }
  
  /**
   * Add the retrieved picking request to retrieved queue.
   * 
   * @param request
   *        the picking request need to retrieve.
   */
  public void addRetrievedReq(PickingRequest request) {
    retrievedQueue.add(request);
    this.assignRequest();
  }


  /**
   * Inherit method from super class but do nothing.
   */
  @Override
  public void sendToPending(String name) {
    
  }
}

