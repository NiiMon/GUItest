import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDataModelDAOHashMapImpl implements ProductDataModelDAO {
    private Map<Integer, ProductDataModel> _db;

    public ProductDataModelDAOHashMapImpl() {
        _db = new HashMap<>();
    }

    @Override
    public void add(ProductDataModel product) {
        _db.put(product.get_id(), product);
    }

    @Override
    public ProductDataModel get(int id) {
        if (_db.containsKey(id)) {
            return _db.get(id);
        }
        return null;
    }

    @Override
    public List<ProductDataModel> getAll() {
        List<ProductDataModel> result = new ArrayList<>();
        for (int id : _db.keySet()) {
            result.add(_db.get(id));
        }
        return result;
    }

    @Override
    public void update(int id, ProductDataModel product) {
        if (product != null && id == product.get_id()) {
            _db.put(id, product);
        }
    }

    @Override
    public void remove(int id) {
        if (_db.containsKey(id)) {
            _db.remove(id);
        }
    }

    @Override
    public void removeAll() {
        _db = new HashMap<>();
    }

    @Override
    public int size() {
        return _db.size();
    }
}
