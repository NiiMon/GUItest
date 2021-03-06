import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test extends JFrame implements ActionListener {

    public static void main(String[] args) {
        ProductDataModelDAO PDM_db = null;
        try {
            PDM_db = new ProductDataModelDAOSqliteImpl();
        } catch (SQLException e) {
            System.out.println("getting SQLException when initializing DAO...");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("failed to initialize DAO...");
            e.printStackTrace();
        }

        Test obj = new Test(PDM_db);
    }

    Map<Integer, JComponent> grid_products;
    Map<Integer, Integer> shopping_cart;
    Map<Integer, JComponent> shopping_cart_component;

    String SHOPPINGPANEL;
    String TEXTPANEL;
    String default_image_path;
    String lastVisitedPath;

    int num_of_cols;
    int product_id ;
    int windowLength;
    int extraWindowWidth;

    JPanel card1, card2;
    JScrollPane scrollPane1, scrollPane2;
    JTabbedPane tabbedPane;

    JLabel summary_total_price, summary_customer_name_display;
    JButton summary_refresh, summary_writeToFile, summary_clearCart;
    JTextField summary_customer_name_input;

    ProductDataModelDAO PDM_db;

    private void initializations() {

        grid_products = new HashMap<>();
        shopping_cart = new HashMap<>();
        shopping_cart_component = new HashMap<>();

        SHOPPINGPANEL = "商品"; //"Products";
        TEXTPANEL = "购物车"; //"Summary";
        default_image_path = "image/product/default_product_image.png";   // default image

        extraWindowWidth = 100;
        num_of_cols = 4;
        product_id = 0;
        windowLength = 1000;


        // read from product_data_model_db
        for (ProductDataModel pdm : PDM_db.getAll()) {
            grid_products.put(pdm.get_id(), new Product(this, pdm));
        }



        card1 = new JPanel() {
            //Make the panel wider than it really needs, so
            //the window's wide enough for the tabs to stay
            //in one row.
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += extraWindowWidth;
                return size;
            }
        };

        card2 = new JPanel();

        tabbedPane = new JTabbedPane();
        scrollPane1 = new JScrollPane(card1);
        scrollPane2 = new JScrollPane(card2);

    }

    public Test(ProductDataModelDAO PDM_db) {
        this.PDM_db = PDM_db;
        initializations();

        fillCard1();

        fillCard2();

        createAndShowGUI();
    }


    void fillCard1() {
        JButton new_product = new JButton("创建新商品");  //"new product");
        new_product.addActionListener(this);
        new_product.setActionCommand("product_new");
        grid_products.put(-2, new_product);
        card1.add(new_product);

        // load all products
        List<ProductDataModel> allProductDM = PDM_db.getAll();
        for (ProductDataModel pdm : allProductDM) {
            product_id = Math.max(product_id, pdm.get_id());
            Product product = new Product(this, pdm);
            grid_products.put(pdm.get_id(), product);
            card1.add(product);
        }

        repaintScrollPane1();
    }

    private void fillCard2() {
        // summary panel
        JPanel summary = new JPanel();

        // summary customer name
        summary_customer_name_display = new JLabel("顾客：");   //"Customer: ");
        summary_customer_name_input = new JTextField();
        summary_customer_name_input.setColumns(10);

        // summary refresh button
        ImageIcon refresh_icon = new ImageIcon(
                new ImageIcon("image/icon/icon_refresh.png")
                        .getImage()
                        .getScaledInstance(20, 20, Image.SCALE_DEFAULT)
        );
        summary_refresh = new JButton("刷新", refresh_icon);  //"refresh");
        summary_refresh.addActionListener(this);
        summary_refresh.setActionCommand("summary_refresh");

        ImageIcon delete_icon = new ImageIcon(
                new ImageIcon("image/icon/icon_delete.png")
                        .getImage()
                        .getScaledInstance(20, 20, Image.SCALE_DEFAULT)
        );
        summary_clearCart = new JButton("清空购物车", delete_icon);
        summary_clearCart.addActionListener(this);


        // summary total price
        summary_total_price = new JLabel("总价：$ 0.00");     //"Total: $ 0.00");
        summary_total_price.setFont(new Font(summary_total_price.getFont().getName(),
                Font.PLAIN,
                summary_total_price.getFont().getSize() * 2));
        summary_total_price.setForeground(Color.RED);

        // write to file button
        summary_writeToFile = new JButton("生成订单");  //"write to file");
        summary_writeToFile.addActionListener(this);
        summary_writeToFile.setActionCommand("summary_write");

        // add to summary panel
        summary.add(summary_customer_name_display);
        summary.add(summary_customer_name_input);
        summary.add(summary_refresh);
        summary.add(summary_clearCart);
        summary.add(summary_total_price);
        summary.add(summary_writeToFile);

        summary.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        summary.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("购物车详情"), //"Order summary"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));

        // add to card2
        card2.add(summary);
        card2.setLayout(new GridLayout(0,1,5,5));

        repaintScrollPane2();
    }

    private void createAndShowGUI() {

        tabbedPane.add(SHOPPINGPANEL, scrollPane1);
        tabbedPane.addTab(TEXTPANEL, scrollPane2);
        this.add(tabbedPane, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Size and display the window.
        this.setVisible(true);
        this.setTitle("订单生成器");
        this.setSize(windowLength, windowLength);
        this.setResizable(false);
    }

    void repaintScrollPane1() {
        card1.setLayout(new GridLayout((grid_products.size() + num_of_cols - 1)/num_of_cols,
                num_of_cols, 5, 5));

        card1.revalidate();
        card1.repaint();
        scrollPane1.revalidate();
        scrollPane1.repaint();
    }

    private void repaintScrollPane2() {
        card2.setLayout(new GridLayout(shopping_cart.size() + 1, 1,5,5));

        card2.revalidate();
        card2.repaint();
        scrollPane2.revalidate();
        scrollPane2.repaint();
    }

    private void addNewRowToShoppingCart(int id) {
        ShoppingCartRow row = new ShoppingCartRow(this, id);
        card2.add(row);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String[] command_split = command.split("_");
        if (command_split[0].equals("product")) {
            if (command_split[1].equals("create")) {
                // command: product_create
                Product new_product = new Product(this, ++product_id);

                // add new_product to HashMap
                grid_products.put(product_id, new_product);

                // add newly created product data model to database
                PDM_db.add(new_product.get_pdm());

                // add new_product to UI
                card1.add(new_product);

                // refresh UI
                repaintScrollPane1();

                // log
                System.out.println("add product " + new_product._id +
                        "; grid_products.size: " + grid_products.size());

            } else if (command_split[1].equals("remove")) {
                // command: product_remove_id
                int id = Integer.valueOf(command_split[2]);
                if (grid_products.containsKey(id)) {
                    // remove product from UI
                    card1.remove(grid_products.get(id));

                    // remove product from HashMap
                    grid_products.remove(id);

                    // remove product data model from database
                    PDM_db.remove(id);
                }

                // refresh UI
                repaintScrollPane1();

                // log
                System.out.println("remove product " + id +
                        "; grid_products.size: " + grid_products.size());
            } else if (command_split[1].equals("addToCart")) {
                // command: product_addToCart_id
                System.out.print(command + "; ");
                int id = Integer.parseInt(command_split[2]);
                if (!shopping_cart.containsKey(id)) {
                    shopping_cart.put(id, 1);

                    addNewRowToShoppingCart(id);

                    repaintScrollPane2();

                    System.out.println("product " + id + " put to cart; ");
                } else {
                    System.out.println("already exist; ");
                    JOptionPane.showMessageDialog(this,
                            "已经添加到购物车", //"already added!",
                            "警告", //"warning",
                            JOptionPane.WARNING_MESSAGE);
                }

            } else if (command_split[1].equals("new")) {
                // command: product_new
                System.out.println(command + "; ");

                createProductFrame();

            } else {
                System.out.println("unknown command in product");
            }
        } else if (command_split[0].equals("shopping")) {
            if (command_split[1].equals("remove")) {
                // command: shopping_remove_id
                System.out.print(command + "; ");
                int id = Integer.parseInt(command_split[2]);

                if (shopping_cart.containsKey(id)) {
                    card2.remove(shopping_cart_component.get(id));
                    shopping_cart_component.remove(id);
                    shopping_cart.remove(id);
                }

                repaintScrollPane2();
                System.out.println("shopping cart item " + id + " removed;");
            } else {
                System.out.println("unknown command in shopping");
            }
        } else if (command_split[0].equals("summary")) {
            if (command_split[1].equals("refresh")) {
                // command: summary_refresh
                System.out.println(command);

                double sum = 0;
                for (int id : shopping_cart.keySet()) {
                    Product product = (Product) grid_products.get(id);
                    sum += product.product_price * shopping_cart.get(id);
                }

                summary_total_price.setText("总价： $ " +    //"Total: $ " +
                        String.format("%,.2f", sum));



            } else if (command_split[1].equals("write")) {
                // command: summary_write
                System.out.println(command);

                if (shopping_cart.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "购物车是空的！", //"shopping cart is empty",
                            "失败", //"failed!",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // get current time
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                DateTimeFormatter folder = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDateTime now = LocalDateTime.now();

                String file_name = dtf.format(now) + "_" + summary_customer_name_input.getText();
                String file_ext = ".txt";
                String file_path = "receipts/" + folder.format(now) + "/";

                File file = new File(file_path);
                if (!file.exists()) {
                    file.mkdirs();
                }

                try {
                    // text length for alignment
                    int nameLength = 35;
                    int countLength = 10;
                    int priceLenth = 15;
                    int totalLenth = nameLength + countLength + priceLenth + 3;

                    // get writer
                    PrintWriter writer = new PrintWriter(
                            file_path + file_name + file_ext,
                            "UTF-8"
                    );

                    // write header information
                    writer.println(stringSpliter("-", totalLenth));
                    writer.println(
                            summary_customer_name_display.getText() +
                            summary_customer_name_input.getText()
                    );
                    writer.println(stringSpliter("-", totalLenth));
                    writer.println(
                            print3Rows(
                                    padRight("商品名称",
                                            nameLength - getChinaNum("商品名称")),
                                    padLeft("数量",
                                            countLength - getChinaNum("数量")),
                                    padLeft("价值",
                                            priceLenth - getChinaNum("价值"))
                            )
                    );
                    writer.println(stringSpliter("-", totalLenth));

                    // write rows
                    for (int id : shopping_cart.keySet()) {
                        ProductDataModel pdm = PDM_db.get(id);
                        String name = pdm.get_name();
                        int count = shopping_cart.get(id);
                        double price = pdm.get_price() * count;

                        writer.println(
                                print3Rows(
                                padRight(name,
                                        nameLength - getChinaNum(name)),
                                padLeft("" + count,
                                        countLength),
                                padLeft(String.format("$ " + "%,.2f", price),
                                        priceLenth)
                        ));
                    }

                    // write footer
                    writer.println(stringSpliter("-", totalLenth));
                    writer.println(
                            padLeft(summary_total_price.getText(),
                                    totalLenth - getChinaNum(summary_total_price.getText()))
                    );
                    writer.println(stringSpliter("-", totalLenth));

                    writer.close();
                    System.out.println("file written: "+file_name + file_ext);
                    JOptionPane.showMessageDialog(
                            this,
                            "订单输出在：" + file_path + file_name + file_ext, //file_path + file_name + file_ext + " has been written.",
                            "成功！",//"success!",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }


            } else {
                System.out.println("unknown command in shopping");
            }
        } else if (e.getSource() == summary_clearCart) {
            for (int id : shopping_cart.keySet()) {
                card2.remove(shopping_cart_component.get(id));
                shopping_cart_component.remove(id);
            }
            shopping_cart = new HashMap<>();

            repaintScrollPane2();
            System.out.println("shopping cart has been cleared;");
        } else {
            System.out.println("unknown command");
        }
    }

    private void createProductFrame() {
        CreateProductPopWindow popup = new CreateProductPopWindow(this);


    }

    private static String print3Rows(String s1, String s2, String s3) {
        return String.format(
                "%s\t%s\t%s",
                s1,
                s2,
                s3
        );
    }


    private static int getChinaNum(String str) {
        int amount = 0;
        String exp="^[\u4E00-\u9FA5|\\！|\\,|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]$";
        Pattern pattern= Pattern.compile(exp);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Matcher matcher=pattern.matcher(c + "");
            if(matcher.matches()) {
                amount++;
            }
        }
        return amount;
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    private static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    private static String stringSpliter(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

}
