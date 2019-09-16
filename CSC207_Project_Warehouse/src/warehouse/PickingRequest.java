package warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class creates picking requests that keeps track of each picking request.
 */
public class PickingRequest implements Comparable<PickingRequest> {

  /** This is the unique id of this PickingRequest given by PickingRequestManager. */
  private int id;
  /**
   * This is the corresponding SKU numbers of the orders; they are arranged by
   * front and back in the order of the orders.
   */  
  private List<String> skus;
  /** This is the list of the orders in this picking request. */
  private List<String> orders;
  
  private boolean isVerified;

  /**
   * The class needs to be initialized with the list of 4 orders.
   * 
   * @param orders
   *            A list of strings, each string is formated as orders are.
   * @param skus
   *            A list of skus, in the order of orders.
   * @param id
   *            The integer the PickingRequest should keep as its id.
   */
  
  public PickingRequest(List<String> orders, List<String> skus, int id) {
    this.orders = orders;
    this.skus = skus;
    this.id = id;
    this.isVerified = false;
  }
  
  /** This is the getter for orders. */
  public List<String> getOders() {
    return this.orders;
  }
  
  /** This is the getter for SKU's. */
  public List<String> getSkus() {
    return this.skus;
  }
  
  /** This is the getter for id. */
  public int getId() {
    return this.id;
  }

  
  /**
  * This method indicates whether the id of the first object is greater than
  * the object, i.e. if the id of the first object is greater than the second
  * (the one in the bracket), it returns a positive integer; if they are the
  * same id, it returns 0; if the first is smaller than the second, return a
  * negative integer.
  * 
  * @param comparedPr
  *        This is the picking request to be compared to.
  * @return difference
  */
  @Override
  public int compareTo(PickingRequest comparedPr) {
    int secondid = comparedPr.getId();
    int difference = this.getId() - secondid;
    return difference;
  }
  
  public boolean getIsVerified(){
    return isVerified;
  }
  
  public void setIsVerified(boolean state){
    isVerified = state;
  }

}
