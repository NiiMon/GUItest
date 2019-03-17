import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/*
 * LabelDemo.java needs one other file:
 *   images/middle.gif
 */
public class PopWindowTest extends JPanel implements ActionListener{
    public PopWindowTest() {
//        super(new GridLayout(3,1));  //3 rows, 1 column
//        JLabel label1, label2, label3;
//
//        ImageIcon icon = createImageIcon("image/default_product_image.png",
//                "a pretty but meaningless splat");
//
//        //Create the first label.
//        label1 = new JLabel("Image and Text",
//                icon,
//                JLabel.CENTER);
//        //Set the position of its text, relative to its icon:
//        label1.setVerticalTextPosition(JLabel.BOTTOM);
//        label1.setHorizontalTextPosition(JLabel.CENTER);
//
//        //Create the other labels.
//        label2 = new JLabel("Text-Only Label");
//        label3 = new JLabel(icon);
//
//        //Create tool tips, for the heck of it.
//        label1.setToolTipText("A label containing both image and text");
//        label2.setToolTipText("A label containing only text");
//        label3.setToolTipText("A label containing only an image");
//
//        //Add the labels.
//        add(label1);
//        add(label2);
//        add(label3);

        JButton button = new JButton("create new product");
        button.addActionListener(this);
        add(button);
    }

    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new JDialog ();
        dialog.setModal (true);
        dialog.setAlwaysOnTop (true);
        dialog.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
    }

//    /** Returns an ImageIcon, or null if the path was invalid. */
//    protected static ImageIcon createImageIcon(String path,
//                                               String description) {
//        java.net.URL imgURL = PopWindowTest.class.getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL, description);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
//    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("LabelDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new PopWindowTest());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);

                createAndShowGUI();
            }
        });
    }
}