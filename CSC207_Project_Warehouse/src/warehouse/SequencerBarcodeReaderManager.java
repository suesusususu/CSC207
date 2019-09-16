package warehouse;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The manager for sequencer bar-code readers.
 */
public class SequencerBarcodeReaderManager extends BarcodeReaderManager {
  
  /** The queue of picking requests in the marshaling area. */
  private ConcurrentLinkedQueue<PickingRequest> marshalingQueue;
  
  /**
   * Instantiate a new sequencer bar-code reader manager with no workers
   * and no picking requests.
   */
  public SequencerBarcodeReaderManager() {
    super();
    this.marshalingQueue = new ConcurrentLinkedQueue<>();
  }
  
  /**
   * Set the state of a sequencer to be ready to accept picking requests, 
   * add him into the ready worker queue, then assign him an appropriate 
   * picking request if possible.
   * 
   * @param name
   *         the name of this sequencer.
   */
  @Override
  public void setReady(String name) {
    // Add the sequencer to the manager if not already exist.
    if (!this.workerMap.containsKey(name)) {
      SequencerBarcodeReader worker = new SequencerBarcodeReader(name);
      this.workerMap.put(name, worker);
    }
    // Add the sequencer to the ready worker queue.
    this.readyWorkers.add(this.workerMap.get(name));
    // Assign the sequencer a picking request if there is one in the 
    // marshaling area.
    this.assignRequest();

  }
  
  /**
   * If the first order in the pending request queue should be sent to
   * loader, assign it to the first ready sequencer if exists, and ask
   * him to send it to loader.
   * 
   * Otherwise assign the first picking request in the marshaling area 
   * to the first ready sequencer, if they exist.
   */
  @Override
  public void assignRequest() {
    // Check if there is a ready sequencer.
    if (this.hasReadyWorker() && !this.marshalingQueue.isEmpty()) {
      //if there is a picking request in the marshaling queue,
      // assign the first picking request to the first ready sequencer, and
      // remove them from their queues.
      BarcodeReader worker = this.readyWorkers.poll();
      worker.assignRequest(this.marshalingQueue.poll());
    }
  }
  
  /**
   * Return and report if the SKU number the sequencer scanned is correct, and 
   * check whether the whole picking request has been verified, if so, 
   * instruct the sequencer to send his request to loader or to pending.
   * 
   * @return true if the SKU number the sequencer scanned is correct.
   */
  @Override
  public boolean verify(String name, String sku) {
    // Verify whether the just scanned SKU is correct.
    BarcodeReader worker = this.workerMap.get(name);
    boolean flag = worker.verify(sku);
    if (flag) {
      System.out.println("Correct.");
      // Check if the whole picking request has been verified.
      PickingRequest pickReq = (PickingRequest) worker.getCurrRequest();
      boolean isVerified = pickReq.getIsVerified();
      if (isVerified) {
        System.out.println("Picking Request " + pickReq.getId() + " Verified. Send to loader.");
      }     
    } else {
      System.out.println("Item Wrong!");
    }
    return flag;
  }
  
  /**
   * Add a request to the marshaling queue, and assign it to a ready sequencer 
   * if there is one.
   * 
   * @param request
   *            the picking request to be added to the marshaling queue.
   */
  @Override
  public void addRequest(Object request) {
    // Add the picking request into the marshaling queue.
    if (request instanceof PickingRequest) {
      this.marshalingQueue.add((PickingRequest) request);
    }
    // Assign the picking request to a ready sequencer if there exists one.
    this.assignRequest();

  }
  
  /**
   * Send the sequencer's current assigned picking request to pending queue 
   * that wait to be sent to loader, if this request has been verified.
   * 
   * @param name
   *            the name of this sequencer.
   */
  @Override
  public void sendToPending(String name) {

  }
  
  /**
   * Return and report if it is valid for this sequencer to send his current 
   * picking request to loader. If it is, grant the action and record corresponding
   * changes. If it is not, instruct the sequencer with correct actions.
   * 
   * It is a valid action if this picking request has been verified and its 
   * ID is no greater than the current ID requirement.
   * 
   * @return true if and only if it is valid for this sequencer to send his current 
   *            picking request to loader.
   */
  @Override
  public boolean sendToNextStage(String name) {
    // Get the sequencer with this name and his current picking request.
    BarcodeReader worker = this.workerMap.get(name);
    PickingRequest pickReq = (PickingRequest) worker.getCurrRequest();
    // If this request has not been verified, ask the worker to verify it.
    if (!pickReq.getIsVerified()) {
      System.out.println("Verify the picking request first!");
      // Otherwise check the ID requirement.
    } else {
        // If this action is valid, reset the request's status to be unverified and
        // remove it from this worker's current assigned request.
        pickReq.setIsVerified(false);
        worker.clearCurrRequest();
        System.out.println("Picking Request " + pickReq.getId() + " sent to loader.");
        return true;
      } 
    return false;
  }
  
  public ConcurrentLinkedQueue<PickingRequest> getMarshalingQueue(){
    return marshalingQueue;
  }
  
  


}
