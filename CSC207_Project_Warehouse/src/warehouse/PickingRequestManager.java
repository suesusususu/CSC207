package warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PickingRequestManager {
  
  /** ArrayList to keep the archived picking requests.*/
  private ArrayList<PickingRequest> historyPr = new ArrayList<PickingRequest>();
  /** Current number of picking request having been created. */
  private int currentId = 0;
  /** Constructor of new picking request managers. 
  */
  
  public PickingRequestManager() {
  }
  
  /** Takes the list of orders and list of skus (translated in
  * the same order how orders are placed) and assigns the unique id to the 
  * picking request to create a new picking request and return it as needed.
  * @param orders
  *        the orders needed to create a new picking. 
  * @param skus
  *        the list of SKU's to be assigned to create the picking request.
  * @return the picking request created.
  */
  public PickingRequest createPickReq(List<String> orders, List<String> skus) {
    currentId++;
    PickingRequest pickReq = new PickingRequest(orders, skus, currentId);
    return pickReq;
  }
  
  /** Adder for historyPr to add completed picking requests as they
   *  have completed being processed. This may be called by the system.
   *  @param pr
   *         the picking request to be added.
   */
  public void addHistoryPickReq(PickingRequest pr) {
    this.historyPr.add(pr);
  }

  /**
   * Getter for historyPr; created for unit test.
   * @return historyPr for the picking request manager.
   */
  public List<PickingRequest> getHistoryPr() {
    return historyPr;
  }
}
