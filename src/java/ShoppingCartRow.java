import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ShoppingCartRow extends JPanel{

    JLabel product_name, product_price, jLabel_not_number, product_total_price;
    JButton jButton_remove;
    JTextField num_of_items;
    double total_price;

    int _id;

    Test parent;
    Product product;

    public ShoppingCartRow(Test p, int id) {
        parent = p;
        _id = id;
        product = (Product) p.grid_products.get(_id);
        initUI();
    }

    private void initUI() {
        // product name
        product_name = new JLabel(product.product_name + product._id);
        this.add(product_name);

        // product price
        product_price = new JLabel("$ " +
                String.format("%.2f", product.product_price));
        this.add(product_price);

        // number of itmes
        num_of_items = new JTextField("" + parent.shopping_cart.get(_id));
        jLabel_not_number = new JLabel("");
        jLabel_not_number.setForeground(Color.RED);
        num_of_items.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int num = Integer.parseInt(num_of_items.getText());
//                    System.out.println(num_of_product.getText());
                    jLabel_not_number.setText("");
                    parent.shopping_cart.put(_id, num);

                    product_total_price.setText(
                            String.format("$ %.2f",
                                    parent.shopping_cart.get(_id) * parent.PDM_db.get(_id).get_price())
                    );

                } catch (NumberFormatException ex) {
//                    System.out.println(num_of_product.getText());
                    jLabel_not_number.setText("请输入整数");
                }
            }
        });
        num_of_items.setColumns(5);
        this.add(num_of_items);
        this.add(jLabel_not_number);


        // remove button
        jButton_remove = new JButton("remove");
        jButton_remove.addActionListener(parent);
        jButton_remove.setActionCommand("shopping_remove_" + _id);
        this.add(jButton_remove);

        // total price
        total_price = product.product_price * parent.shopping_cart.get(_id);
        product_total_price = new JLabel("$ " +
                String.format("%.2f", total_price));
//        product_total_price.setBorder(BorderFactory.createLineBorder(Color.orange));
        this.add(product_total_price);

        this.setPreferredSize(new Dimension(parent.card2.getWidth(), jButton_remove.getHeight() + 10));
//        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setLayout( new FlowLayout(FlowLayout.LEFT, 20, 0) );


        parent.shopping_cart_component.put(_id, this);
    }
}
