import java.awt.*;

public class ProductDataModel {
    /**
     * ProductModel:
     *      int     _id;
     *      String  _name;
     *      String  _imagePath;
     *      double  _price;
     */
    private int     _id;
    private String  _name;
    private String   _imagePath;
    private double  _price;

    public ProductDataModel(int id, String name, String imagePath, double price) {
        _id = id;
        _name = name;
        _imagePath = imagePath;
        _price = price;
    }


    public int get_id() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public String get_imagePath() {
        return _imagePath;
    }

    public double get_price() {
        return _price;
    }

    @Override
    public String toString() {
        return String.format("ProductDataMode=[id=%d, name=%s, price=$%.2f, imagePath=%s]",
                _id, _name, _price, _imagePath);
    }

    public void set_imagePath(String _imagePath) {
        this._imagePath = _imagePath;
    }
}
