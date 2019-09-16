package warehouse;

import java.util.ArrayList;
import java.util.List;

public class SequencerBarcodeReader extends BarcodeReader {
  
  /** The list of SKUs this sequencer has scanned. */
  private List<String> scannedSkus;

  public SequencerBarcodeReader(String name) {
    super(name);
    this.scannedSkus = new ArrayList<>();
  }
  
  /**
   * Return true if the SKU number the sequencer just scanned is correct, and
   * check whether all items in this picking request have been verified and if so, 
   * change the request's status to be verified, otherwise report any item that 
   * is wrong.
   * 
   * @return true if the SKU number the sequencer scanned is correct.
   */
  @Override
  public boolean verify(String sku) {
    PickingRequest pickReq = (PickingRequest) this.currRequest;
    // Get the correct SKU list of the sequencer's picking request.
    List<String> skus = pickReq.getSkus();
    // Add the scanned SKU to the scanned SKU list.
    this.scannedSkus.add(sku);
    // Compare if the last scanned SKU is the same as the one in the picking
    // request's SKU list with the same location.
    int num = this.scannedSkus.size();
    boolean flag = num <= skus.size() && sku.equals(skus.get(num - 1));
    // Check whether all items in this picking request has been verified, and
    // if so, set the picking request's status to be verified.
    this.verifyRequest();
    // Return whether the just scanned item is correct.
    return flag;
  }
  
  /**
   * Update the sequencer's state when he asks to rescan his current set of 
   * products.
   */
  @Override
  public void rescan() {
    this.scannedSkus.clear();
  }
  
  /**
   * When the same number of items as in the sequencer's picking request have 
   * been scanned, change the picking request's status to be verified if all
   * items are correct, otherwise report any item that is wrong.
   */
  public void verifyRequest() {
    PickingRequest pickReq = (PickingRequest) this.currRequest;
    // Get the correct SKU list of the sequencer's picking request.
    List<String> skus = pickReq.getSkus();
    // Only verify this request if the scanned list has the same number of items
    // as the request's SKUs.
    int num = this.scannedSkus.size();
    if (num == skus.size()) {
      List<Integer> wrongItems = new ArrayList<>();
      // Check and record if any scanned item is wrong.
      for (int i = 0; i < num; i++) {
        if (!this.scannedSkus.get(i).equals(skus.get(i))) {
          wrongItems.add(i + 1);
        }
      }
      // If there all items are correct, change the request's status to be verified.
      if (wrongItems.isEmpty()) {
        pickReq.setIsVerified(true);
        // Report if there are any wrong items scanned.
      } else {
        System.out.println("Item(s) " + wrongItems.toString() + " wrong!");
      }
    }  
  }
  
  @Override
  public void clearCurrRequest() {
    super.clearCurrRequest();
    this.scannedSkus.clear();
  }

  public PickingRequest getCurrRequest(){
    return (PickingRequest)currRequest;
  }
  
  public List<String> getScannedSkus() {
    return scannedSkus;
  }

}
