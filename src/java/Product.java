import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Product extends JPanel {
    Test parent;

    private ProductDataModel _pdm;

    public ProductDataModel get_pdm() {
        return _pdm;
    }

    int _id;
    String product_name;
    ImageIcon productImage;
    String productImagePath;

    double product_price;

    JLabel text;
    JButton delete, addToCart;
    JPanel jPNorth, jPCenter, jPSouth;
    JLabel jLabel_product_price;


    public Product(Test parent, int id) {
        this.parent = parent;

        _id = id;
        product_name = "商品名称";   //"product name";
        Random random = new Random();
        int low = 10;
        int high = 100;
        product_price = 1.0 * (random.nextInt(high-low) + low);
        productImagePath = parent.default_image_path;

        _pdm = new ProductDataModel(id, product_name, productImagePath, product_price);

        initUI();
    }

    public Product(Test parent, int id, String name, String imagePath, double price) {
        _id = id;
        this.parent = parent;
        product_name = name;
        productImagePath = imagePath == null ? parent.default_image_path : imagePath;
        product_price = price;

        _pdm = new ProductDataModel(id, name, imagePath, price);

        initUI();
    }

    public Product(Test parent, ProductDataModel pdm) {
        this.parent = parent;
        _id = pdm.get_id();
        _pdm = pdm;
        product_name = pdm.get_name();
        productImagePath = _pdm.get_imagePath() == null ? parent.default_image_path : _pdm.get_imagePath();

        product_price = pdm.get_price();

        initUI();
    }

    private void initUI() {
        text = new JLabel(product_name);
        ImageIcon delete_icon = new ImageIcon(
                new ImageIcon("image/icon_delete.png")
                .getImage()
                .getScaledInstance(20, 20, Image.SCALE_DEFAULT)
        );
        delete = new JButton("删除", delete_icon);
        delete.addActionListener(parent);
        delete.setActionCommand("product_remove_" + _id);

        jPNorth = new JPanel();
        jPNorth.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPNorth.add(delete);

        productImage = new ImageIcon(
                new ImageIcon(productImagePath)
                        .getImage()
                        .getScaledInstance(150, 150,
                                Image.SCALE_DEFAULT)
        );

        jPCenter = new JPanel();
        jPCenter.setLayout(new BorderLayout());
        jPCenter.add(new JLabel(productImage), BorderLayout.CENTER);
        jPCenter.add(text, BorderLayout.SOUTH);

        jLabel_product_price = new JLabel("$ " +
                String.format("%.2f", product_price));
        jLabel_product_price.setForeground(Color.red);

        ImageIcon addToCart_icon = new ImageIcon(
                new ImageIcon("image/icon_add.png")
                        .getImage()
                        .getScaledInstance(20, 20, Image.SCALE_DEFAULT)
        );
        addToCart = new JButton("购物车", addToCart_icon);
        addToCart.addActionListener(parent);
        addToCart.setActionCommand("product_addToCart_" + _id);
        jPSouth = new JPanel();
        jPSouth.add(jLabel_product_price);
        jPSouth.add(addToCart);


        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(jPNorth);
        this.add(jPCenter);
        this.add(jPSouth);

        this.setBorder(BorderFactory.createLineBorder(Color.black));

    }
}