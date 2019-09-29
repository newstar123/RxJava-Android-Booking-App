package app.delivering.mvp.tab.summary.model;

public class SortedBillItem {
    private int quantity;
    private String billName;
    private String billSum;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getBillSum() {
        return billSum;
    }

    public void setBillSum(String billSum) {
        this.billSum = billSum;
    }

}
