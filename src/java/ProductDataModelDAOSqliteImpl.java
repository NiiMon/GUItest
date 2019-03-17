import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductDataModelDAOSqliteImpl implements ProductDataModelDAO {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        ProductDataModelDAOSqliteImpl obj = new ProductDataModelDAOSqliteImpl();
        obj.add(new ProductDataModel(9, "nine", null, 9.99));
        System.out.println("==== get ===");
        System.out.println(obj.get(1));
        System.out.println(obj.size());
        System.out.println("---------\n");

        System.out.println("==== get all ===");
        System.out.println(obj.getAll());
        System.out.println("---------\n");

        System.out.println("==== update ===");
        obj.update(9, new ProductDataModel(9, "NINEEEEEEE", "", 99.99));
        System.out.println(obj.getAll());
        System.out.println("---------\n");

        System.out.println("==== remove ===");
        obj.remove(1);
        System.out.println(obj.getAll());
        System.out.println("---------\n");

        System.out.println("==== remove all ===");
        obj.removeAll();
        System.out.println(obj.getAll());
        System.out.println("---------\n");
    }

    private static Connection _connection;
    private static boolean _hasData = false;
    static String _url;

    public ProductDataModelDAOSqliteImpl() throws SQLException, ClassNotFoundException {
        String db_name = "product.db";
        _url = "jdbc:sqlite:" + db_name;

        getConnection();
    }

    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        _connection = DriverManager.getConnection(_url);

        // check if there is a product table
        Statement stmt = null;
        try {
            stmt = _connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='product';");
            if (!rs.next()) {
                System.out.println("no product table");
            } else {
                _hasData = true;
                System.out.println("there is a product table");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        initializeDB();
    }

    private void initializeDB() throws SQLException {
        if (!_hasData) {
            _hasData = true;

            System.out.println("creating table...");

            Statement stmt = _connection.createStatement();
            stmt.executeUpdate("DROP table if exists product");
            stmt.executeUpdate("CREATE TABLE product (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "price REAL, " +
                    "image_path TEXT);");
        }
    }

    @Override
    public void add(ProductDataModel product) {
        PreparedStatement prep = null;
        try {
            prep = _connection
                    .prepareStatement("INSERT INTO product values(?,?,?,?);");
            prep.setInt(1, product.get_id());
            prep.setString(2, product.get_name());
            prep.setDouble(3, product.get_price());
            if (product.get_imagePath() != null) {
                prep.setString(4, product.get_imagePath());
            } else {
                prep.setNull(4, 1);
            }
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ProductDataModel get(int id) {
        PreparedStatement prep = null;
        // create a database connection
        try {
            prep = _connection
                    .prepareStatement("SELECT * FROM product " +
                            "WHERE id=?;");
            prep.setInt(1, id);

            ResultSet rs = prep.executeQuery();
            while(rs.next()) {
                return new ProductDataModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("image_path"),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<ProductDataModel> getAll() {
        List<ProductDataModel> result = new ArrayList<>();

        Statement statement = null;
        try {
            statement = _connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from product");
            while(rs.next()) {
                result.add(new ProductDataModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("image_path"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void update(int id, ProductDataModel product) {
        if (product == null) {
            return;
        }

        PreparedStatement prep = null;
        try {
            String sql = "UPDATE product \n" +
                    "SET name=?, price=?, image_path=?\n" +
                    "WHERE id=?;";
            prep = _connection.prepareStatement(sql);

            // set name
            if (product.get_name().equals("")) {
                prep.setNull(1, 1);
            } else {
                prep.setString(1, product.get_name());
            }

            // set price
            prep.setDouble(2, product.get_price());

            // set image_path
            if (product.get_imagePath().equals("")) {
                prep.setNull(3, 1);
            } else {
                prep.setString(3, product.get_imagePath());
            }

            // set id
            prep.setInt(4, product.get_id());

            // execute sql
            prep.execute();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(int id) {
        PreparedStatement prep = null;
        try {
            prep = _connection
                    .prepareStatement("DELETE FROM product WHERE id=?;");
            prep.setInt(1,id);
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAll() {
        Statement stmt = null;
        try {
            stmt = _connection.createStatement();
            stmt.executeUpdate("DELETE FROM product WHERE id>0;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int size() {
        Statement stmt = null;
        try {
            stmt = _connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(id) FROM product;");
            while(rs.next()) {
                return rs.getInt("COUNT(id)");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
