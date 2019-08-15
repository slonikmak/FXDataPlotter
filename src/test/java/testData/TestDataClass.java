package testData;

/**
 * @autor slonikmak on 12.08.2019.
 */
public class TestDataClass {
    double data1;
    double data2;

    public TestDataClass(double data1, double data2) {
        this.data1 = data1;
        this.data2 = data2;
    }

    public TestDataClass(){

    }

    public double getData1() {
        return data1;
    }

    public void setData1(double data1) {
        this.data1 = data1;
    }

    public double getData2() {
        return data2;
    }

    public void setData2(double data2) {
        this.data2 = data2;
    }
}
