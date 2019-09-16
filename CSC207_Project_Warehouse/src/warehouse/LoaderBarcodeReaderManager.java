package warehouse;

import java.io.IOException;
import java.io.Serializable;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoaderBarcodeReaderManager extends BarcodeReaderManager {
  
  /** The queue for all unverified picking requests sent from sequencers  */
  private PriorityQueue<PickingRequest> unverifiedQueue;
  /** The queue for all verified picking requests that are pending to be loaded on truck */
  private PriorityQueue<PickingRequest> verifiedQueue;
  /** The id for the next picking request that should be loaded on truck */
  private int currId = 1; 
  /** The Logger */
  private static final Logger LOGGER =
      Logger.getLogger(LoaderBarcodeReaderManager.class.getName());
  
  
  /**
   * Initialize a new LoaderBarcodeReaderManager.
   */
  public LoaderBarcodeReaderManager(){
    super();
    this.unverifiedQueue = new PriorityQueue<>();
    this.verifiedQueue = new PriorityQueue<>();
//    // Set the default console handler level to be false
//    LOGGER.setUseParentHandlers(false);
//    // Set the logger level to all
//    LOGGER.setLevel(Level.ALL);
//    try {
//      FileHandler fh = new FileHandler("log.txt");
//      fh.setFormatter(new SimpleFormatter());
//      // Set the file handler level to all
//      fh.setLevel(Level.ALL);
//      ConsoleHandler ch = new ConsoleHandler();
//      ch.setFormatter(new SimpleFormatter());
//      // Set the file handler level to all
//      ch.setLevel(Level.ALL);
//      LOGGER.addHandler(fh);
//      LOGGER.addHandler(ch);
// 
//    } catch (IOException e) {
//      // print error msg
//    }
  }
  
  @Override
  /**
   * Set the state of this worker to ready and add him to ready workers queue
   * 
   * @param name
   *        the name of this worker
   */
  public void setReady(String name) {
    // If this worker is not in the worker map, create a new worker 
    // and add him into worker map and ready workers
    if (!workerMap.containsKey(name)){
      LoaderBarcodeReader loader = new LoaderBarcodeReader(name);
      workerMap.put(name, loader);
      readyWorkers.add(loader);
    // else directly add this worker into ready workers
    }else{
      readyWorkers.add(workerMap.get(name));
    }
    assignRequest();
  }

  @Override
  /**
   * Assign the first ready worker to load the first picking request in pending on truck 
   * or assign him to verify the first unverified picking request.
   *
   */
  public void assignRequest() {
    if (hasReadyWorker()){
      PickingRequest pr = verifiedQueue.peek();
      if(pr != null && pr.getId() == currId){
        LoaderBarcodeReader loader = (LoaderBarcodeReader) readyWorkers.poll();
        loader.assignRequest(verifiedQueue.poll());
        LOGGER.info("Load picking request with ID " + pr.getId() + " onto truck.");
      }else if(!unverifiedQueue.isEmpty()) {
        readyWorkers.poll().assignRequest(unverifiedQueue.poll());
      }
    }
  }

  @Override
  /**
   * verify whether the sku this worker just scanned is right and if this worker finished 
   * his current request, instruct him what to do next.
   * 
   */
  public boolean verify(String name, String sku) {
    boolean flag = workerMap.get(name).verify(sku);
    // if the sku just scanned is true, check whether this worker finished his current request
    if (flag){
      BarcodeReader loader = workerMap.get(name);
      boolean flag2 = ((PickingRequest) loader.getCurrRequest()).getIsVerified();
      // if he finished current request, check whether this request is the right request that 
      // need to be loaded on truck
      if (flag2){
        int id = ((PickingRequest) loader.getCurrRequest()).getId();
        // instruct this worker if this request need to be sent to pending
        if ( id > currId){
          LOGGER.log(Level.INFO, "Send this to pending");
        // instruct this worker if this request need to be loaded on truck
        }else{
          LOGGER.log(Level.INFO, "Load this on truck");
        }
      }
    }
    return flag;
  }
  
  /**
   * Send this worker's current request to pending  
   *
   * @param name
   *         the name of this worker
   */
  public void sendToPending(String name) {
    boolean flag = ((PickingRequest) workerMap.get(name).getCurrRequest()).getIsVerified();
    if (flag){
      verifiedQueue.add((PickingRequest)workerMap.get(name).getCurrRequest());
      workerMap.get(name).clearCurrRequest();
    }else{
      LOGGER.log(Level.SEVERE, "Unverified");;
    }
  }

  @Override
  public void addRequest(Object request) {
    unverifiedQueue.add((PickingRequest)request);
    this.assignRequest();
  }
  
  /**
   * Load the request on to truck
   */
  @Override
  public boolean sendToNextStage(String name) {
    PickingRequest pr = (PickingRequest) workerMap.get(name).getCurrRequest();
    boolean flag = pr.getId() == currId;
    boolean flag2 = pr.getIsVerified();
    if (flag && flag2){
      currId ++;
      workerMap.get(name).clearCurrRequest();
      this.assignRequest();
      return true;
    }else{
      LOGGER.log(Level.SEVERE, "Invalid Loading!");
      return false;
    }
  }
  
  
  public PriorityQueue<PickingRequest> getVerifiedQueue(){
    return verifiedQueue;
  }
  
  public PriorityQueue<PickingRequest> getUnverifiedQueue(){
    return unverifiedQueue;
  }
  
  public int getCurrId(){
    return currId;
  }
  
  public void setId(int id){
    currId = id;
  }

}
