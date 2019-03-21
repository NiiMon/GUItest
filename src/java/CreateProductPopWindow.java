import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CreateProductPopWindow extends JFrame implements ActionListener {
    Test _parent;

    JPanel panel;
    JLabel image_lable, name_label, price_label;
    JTextField name_textField, price_textField;
    JButton change_pic_button, creat_button;
    JFileChooser fc;

    String absImagePath;

    public CreateProductPopWindow(Test parent) {
        _parent = parent;

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));



        setAlwaysOnTop (true);
        setSize(400, 400);
        setVisible(true);


        // add component
        image_lable = new JLabel(new ImageIcon(
                new ImageIcon(_parent.default_image_path)
                        .getImage()
                        .getScaledInstance(150, 150,
                                Image.SCALE_DEFAULT)
        ));
        image_lable.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(image_lable);

        change_pic_button = new JButton("更改图片");  //"change pic");
        change_pic_button.setAlignmentX(Component.LEFT_ALIGNMENT);
        change_pic_button.addActionListener(this);
        change_pic_button.setActionCommand("popup_change_pic");


        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(
                "Image Files", "jpg", "png", "gif", "jpeg"
        ));
        panel.add(change_pic_button);

        JPanel name_panel = new JPanel();
        name_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        name_label = new JLabel("商品名称");    //"name");
        name_panel.add(name_label);
        name_textField = new JTextField();
        name_textField.setColumns(20);
        name_panel.add(name_textField);
        panel.add(name_panel);

        JPanel price_panel = new JPanel();
        price_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        price_label = new JLabel("商品单价");   //"price");
        price_panel.add(price_label);
        price_textField = new JTextField();
        price_textField.setColumns(20);
        price_panel.add(price_textField);
        panel.add(price_panel);

        creat_button = new JButton("创建商品"); //"create product");
        creat_button.addActionListener(this);
        panel.add(creat_button);
        add(panel);
        pack();
    }

    void copyFile(String srcPath, String dstPath) {
        File src = new File(srcPath);
        File dst = new File(dstPath);

        try {
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == change_pic_button) {
            if (_parent.lastVisitedPath != null) {
                fc = new JFileChooser(_parent.lastVisitedPath);
//                fc = new JFileChooser();
//
//                //Add a custom file filter and disable the default
//                //(Accept All) file filter.
//                fc.addChoosableFileFilter(new ImageFilter());
//                fc.setAcceptAllFileFilterUsed(false);
//
//                //Add custom icons for file types.
//                fc.setFileView(new ImageFileView());
//
//                //Add the preview pane.
//                fc.setAccessory(new ImagePreview(fc));
            }
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                absImagePath = file.getAbsolutePath();
                _parent.lastVisitedPath = absImagePath;

                image_lable.setIcon(
                        new ImageIcon(
                                new ImageIcon(absImagePath)
                                        .getImage()
                                        .getScaledInstance(150, 150,
                                                Image.SCALE_DEFAULT))
                );

                System.out.println("Opening: " + file.getName());
                System.out.println(absImagePath);
            } else {
                System.out.println("Open command cancelled by user.");
            }
        } else if (e.getSource() == creat_button) {
            Double price = null;
            try {
                price = Double.parseDouble(price_textField.getText());
            } catch (NumberFormatException ex) {
//                ex.printStackTrace();
            }

            if (price == null || name_textField.getText().equals("")) {
                JOptionPane.showMessageDialog(this,
                        "请检查以下：\n" +
                                "1. 商品名称不能为空\n" +
                                "2. 商品单价必须是数字（英文小数点）"
//                        "please check following:\n" +
//                                "1. name cannot be empty\n" +
//                                "2. price must be a valid number"
                );
            } else {
                ProductDataModel pdm = new ProductDataModel(
                        ++_parent.product_id,
                        name_textField.getText(),
                        absImagePath,
                        price
                );

                if (absImagePath != null) {
                    String dstPath = String.format("image/product/product_%d.jpg", pdm.get_id());
                    pdm.set_imagePath(dstPath);

                    copyFile(absImagePath, dstPath);
                }

                _parent.PDM_db.add(pdm);

                Product product = new Product(_parent, pdm);
                _parent.grid_products.put(pdm.get_id(), product);
                _parent.card1.add(product);

                _parent.repaintScrollPane1();
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }

            System.out.println(
                    "image path: " + absImagePath + "\n" +
                    "name: " + name_textField.getText() + "\n" +
                    "price: " + price_textField.getText()
            );

        }
    }
}
