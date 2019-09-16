package warehouse;

import java.io.FileNotFoundException;
import java.util.List;

public interface OrderManager {
  
  /** Takes the name of the translation file and convert it into a variable.
   * 
   * @param fileName
   *        The name of the file to be used in translation.
   * @throws FileNotFoundException
   *         The file may not exist or be entered wrong.
   */
  public abstract void translateTable(String fileName) throws FileNotFoundException;
  
  /** Takes an order and process it. */
  public abstract PickingRequest processOrder(String order);
  
  /** Add the picking request into the history picking request of corresponding
   * picking request manager.
   * 
   * @param picReq
   *        The picking request to be added into history.
   */
  public abstract void addHistoryPickReq(PickingRequest pickReq);

  }
