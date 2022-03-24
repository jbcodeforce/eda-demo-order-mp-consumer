package ibm.eda.demo.ordermgr.consumer;

public class OrderMetric {
    public String orderState;
    public long count;

    public OrderMetric(){}

    public OrderMetric(String orderState, long count) {
        this.orderState = orderState;
        this.count = count;
    }

    public void updateCount(long n) {
        count = count + n;
    }

    public String toString(){
        return "Metric: k:" + orderState + " v=" + count;
    }
}
