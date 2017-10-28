
public class RequestBO {


    private int minPrice;
    private int maxPrice;
    private int daysOld;

    public RequestBO(int minPrice, int maxPrice, int daysOld) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.daysOld = daysOld;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getDaysOld() {
        return daysOld;
    }

    public void setDaysOld(int daysOld) {
        this.daysOld = daysOld;
    }
}
