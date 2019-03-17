import java.util.List;

public interface ProductDataModelDAO {
    void add(ProductDataModel product);
    ProductDataModel get(int id);
    List<ProductDataModel> getAll();
    void update(int id, ProductDataModel product);
    void remove(int id);
    void removeAll();
    int size();
}
