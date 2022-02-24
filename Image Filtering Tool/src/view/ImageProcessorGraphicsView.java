package view;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.ImageProcessorControllerFeatures;
import model.Image;
import utils.ImageUtils;

/**
 * A GUI View that uses the JFrame class to display interface to user. Implements ActionListener to
 * record user clicks, and ImageProcessorGUIView since it is a GUI for the Image Processor.
 */
public class ImageProcessorGraphicsView extends JFrame
        implements ActionListener, ImageProcessorGUIView {

  private final JLabel imageLabel;
  private final JLabel graphLabel;
  private final JComboBox<String> combobox;
  private final JButton visualizeButton;


  private ImageProcessorControllerFeatures controller;

  private Map<String, String> compMap;

  /**
   * Constructs the ImageProcessorGraphicsView with JFrame components.
   */
  public ImageProcessorGraphicsView() {
    super();
    this.createCompMap();

    this.setTitle("Image Processor");
    this.setSize(1280, 800);


    JPanel mainPanel = new JPanel();
    //for elements to be arranged vertically within this panel
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
    //scroll bars around this main panel
    JScrollPane mainScrollPane = new JScrollPane(mainPanel);
    add(mainScrollPane);

    //panel to hold image stuff
    JPanel imagesPanel = new JPanel();
    imagesPanel.setLayout(new BoxLayout(imagesPanel, BoxLayout.X_AXIS));

    JPanel imagePanel = new JPanel();
    imagePanel.setBorder(BorderFactory.createTitledBorder("Image"));
    imagePanel.setLayout(new GridLayout(1, 0, 10, 10));
    imagesPanel.add(imagePanel);

    imageLabel = new JLabel();

    JScrollPane imageScrollPane = new JScrollPane(imageLabel);
    imageLabel.setIcon(new ImageIcon());
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imageLabel.setPreferredSize(new Dimension(600, 450));
    imageScrollPane.setPreferredSize(new Dimension(20, 400));
    imagePanel.add(imageScrollPane);

    //show an image with a scrollbar
    JPanel graphPanel = new JPanel();
    graphPanel.setBorder(BorderFactory.createTitledBorder("Component Histogram"));
    graphPanel.setLayout(new GridLayout(1, 0, 10, 10));
    imagesPanel.add(graphPanel);

    graphLabel = new JLabel();

    JScrollPane graphScrollPane = new JScrollPane(graphLabel);
    graphLabel.setIcon(new ImageIcon());
    graphLabel.setPreferredSize(new Dimension(400, 300));
    graphScrollPane.setPreferredSize(new Dimension(20, 400));
    graphPanel.add(graphScrollPane);

    mainPanel.add(imagesPanel);

    //panel for buttons below image
    JPanel editPanel = new JPanel();
    editPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    editPanel.setLayout(new GridLayout(2, 4));

    //panel to hold visualize stuff
    JPanel componentPanel = new JPanel();
    componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));

    //component panel
    JPanel comboboxPanel = new JPanel();
    comboboxPanel.setLayout(new BoxLayout(comboboxPanel, BoxLayout.X_AXIS));
    componentPanel.add(comboboxPanel);

    String[] options = {"Red Component", "Green Component", "Blue Component",
        "Luma Component", "Value Component", "Intensity Component"};
    combobox = new JComboBox<String>();
    //the event listener when an option is selected

    combobox.addActionListener(this);
    for (int i = 0; i < options.length; i++) {
      combobox.addItem(options[i]);
    }

    combobox.setActionCommand("Component Options");

    comboboxPanel.add(combobox);

    //visualize
    JPanel visualizePanel = new JPanel();
    visualizePanel.setLayout(new FlowLayout());
    componentPanel.add(visualizePanel);
    visualizeButton = new JButton("Visualize Component");
    visualizeButton.setActionCommand("red-component");
    visualizeButton.addActionListener(this);
    visualizePanel.add(visualizeButton);

    editPanel.add(componentPanel);

    //horizontal flip
    JPanel horzflipPanel = new JPanel();
    horzflipPanel.setLayout(new FlowLayout());
    editPanel.add(horzflipPanel);
    JButton horzflipButton = new JButton("Flip Horizontally");
    horzflipButton.setActionCommand("horizontal-flip");
    horzflipButton.addActionListener(this);
    horzflipPanel.add(horzflipButton);

    //blur
    JPanel blurPanel = new JPanel();
    blurPanel.setLayout(new FlowLayout());
    editPanel.add(blurPanel);
    JButton blurButton = new JButton("Blur");
    blurButton.setActionCommand("blur");
    blurButton.addActionListener(this);
    blurPanel.add(blurButton);

    //sharpen
    JPanel sharpenPanel = new JPanel();
    sharpenPanel.setLayout(new FlowLayout());
    editPanel.add(sharpenPanel);
    JButton sharpenButton = new JButton("Sharpen");
    sharpenButton.setActionCommand("sharpen");
    sharpenButton.addActionListener(this);
    sharpenPanel.add(sharpenButton);

    //load image
    JPanel loadPanel = new JPanel();
    loadPanel.setLayout(new FlowLayout());
    editPanel.add(loadPanel);
    JButton loadButton = new JButton("Load image");
    loadButton.setActionCommand("load");
    loadButton.addActionListener(this);
    loadPanel.add(loadButton);



    //JOptionsPane input dialog
    JPanel inputDialogPanel = new JPanel();
    inputDialogPanel.setLayout(new FlowLayout());
    editPanel.add(inputDialogPanel);

    JButton inputButton = new JButton("Change Brightness");
    inputButton.setActionCommand("brighten");
    inputButton.addActionListener(this);
    inputDialogPanel.add(inputButton);

    JButton mosaicButton = new JButton("Mosaic");
    mosaicButton.setActionCommand("mosaic");
    mosaicButton.addActionListener(this);
    inputDialogPanel.add(mosaicButton);

    //vertical flip
    JPanel vertflipPanel = new JPanel();
    vertflipPanel.setLayout(new FlowLayout());
    editPanel.add(vertflipPanel);
    JButton vertflipButton = new JButton("Flip Vertically");
    vertflipButton.setActionCommand("vertical-flip");
    vertflipButton.addActionListener(this);
    vertflipPanel.add(vertflipButton);

    //sepia
    JPanel sepiaPanel = new JPanel();
    sepiaPanel.setLayout(new FlowLayout());
    editPanel.add(sepiaPanel);
    JButton sepiaButton = new JButton("Sepia");
    sepiaButton.setActionCommand("sepia");
    sepiaButton.addActionListener(this);
    sepiaPanel.add(sepiaButton);

    //grayscale
    JPanel grayscalePanel = new JPanel();
    grayscalePanel.setLayout(new FlowLayout());
    editPanel.add(grayscalePanel);
    JButton grayscaleButton = new JButton("Grayscale");
    grayscaleButton.setActionCommand("grayscale");
    grayscaleButton.addActionListener(this);
    grayscalePanel.add(grayscaleButton);

    //save image
    JPanel filesavePanel = new JPanel();
    filesavePanel.setLayout(new FlowLayout());
    editPanel.add(filesavePanel);
    JButton fileSaveButton = new JButton("Save image");
    fileSaveButton.setActionCommand("save");
    fileSaveButton.addActionListener(this);
    filesavePanel.add(fileSaveButton);

    mainPanel.add(editPanel);

    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.setVisible(true);

  }

  private void createCompMap() {
    this.compMap = new HashMap<String, String>();
    this.compMap.put("Red Component", "red-component");
    this.compMap.put("Blue Component", "blue-component");
    this.compMap.put("Green Component", "green-component");
    this.compMap.put("Luma Component", "luma-component");
    this.compMap.put("Intensity Component", "intensity-component");
    this.compMap.put("Value Component", "value-component");
  }

  /**
   * Listens for action events and calls respective controller feature.
   * @param event the given action event.
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    switch (event.getActionCommand()) {
      case "Component Options":
        String selected = (String) this.combobox.getSelectedItem();
        this.visualizeButton.setActionCommand(this.compMap.get(selected));
        break;
      case "red-component":
      case "green-component":
      case "blue-component":
      case "value-component":
      case "intensity-component":
      case "luma-component":
      case "horizontal-flip":
      case "vertical-flip":
      case "blur":
      case "sharpen":
      case "grayscale":
      case "sepia":
        this.controller.runCommand(event.getActionCommand());
        break;
      case "load": {
        final JFileChooser fchooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG GIF PPM BMP PNG Images", "jpg", "gif","ppm","bmp","png");
        fchooser.setFileFilter(filter);
        int retvalue = fchooser.showOpenDialog(ImageProcessorGraphicsView.this);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          this.controller.loadImage(f.getAbsolutePath());
        }
      }
      break;
      case "save": {
        final JFileChooser fchooser = new JFileChooser(".");
        int retvalue = fchooser.showSaveDialog(ImageProcessorGraphicsView.this);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          this.controller.saveImage(f.getAbsolutePath());
        }
      }
      break;
      case "brighten":
        StringBuilder command = new StringBuilder("brighten ");
        boolean intCheck = false;
        int num = 0;
        do {
          intCheck = true;
          try {
            String response = JOptionPane.showInputDialog("Change brightness by: ");
            if (response != null && !response.equals("")) {
              num = Integer.parseInt(response);
            }
          } catch (NumberFormatException nfE) {
            intCheck = false;
          }
        }
        while (!intCheck);

        command.append(num);

        this.controller.runCommand(command.toString());
        break;
      case "mosaic":
        StringBuilder cmd = new StringBuilder("mosaic ");
        boolean ic = false;
        int n = 0;
        do {
          ic = true;
          try {
            String response = JOptionPane.showInputDialog("Number of seeds: ");
            if (response != null && !response.equals("")) {
              n = Integer.parseInt(response);
            }
          } catch (NumberFormatException nfE) {
            ic = false;
          }
        }
        while (!ic);

        cmd.append(n);

        this.controller.runCommand(cmd.toString());
        break;
      default: break;
    }
  }


  /**
   * Sets the current image displayed in our Panel and also sets the histogram image of that image
   * next to the current image.
   * @param image to display.
   */
  @Override
  public void setImage(Image image) {
    this.imageLabel.setIcon(new ImageIcon());
    this.imageLabel.setIcon(smartResize(ImageUtils.makeImageIcon(image),
            imageLabel.getWidth(), imageLabel.getHeight()));
    this.graphLabel.setIcon(resizeIcon(
            new ImageIcon(ImageUtils.makeHistogram(image, 64, 12)),
            graphLabel.getWidth(), graphLabel.getHeight()));
  }

  @Override
  public void passFeatures(ImageProcessorControllerFeatures cont) {
    this.controller = cont;
  }

  /**
   * Displays message in a dialog box.
   * @param message string.
   */
  @Override
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(ImageProcessorGraphicsView.this, message,
            "Message:", JOptionPane.INFORMATION_MESSAGE);
  }

  //fits it exactly into w by h area, may stretch image
  private static ImageIcon resizeIcon(ImageIcon icon, int w, int h) {
    java.awt.Image im = icon.getImage();
    return new ImageIcon(im.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH));
  }

  //fits image into w by h area by scaling each dimension the same amount, so that the bigger
  //  dimension fits exactly into the area, and the other dimension is scaled by the same amount.
  private static ImageIcon smartResize(ImageIcon icon, int w, int h) {
    java.awt.Image im = icon.getImage();
    int imW = im.getWidth(icon.getImageObserver());
    int imH = im.getHeight(icon.getImageObserver());

    double scalingFactorW = (w + 0.0) / (im.getWidth(icon.getImageObserver()) + 0.0);
    double scalingFactorH = (h + 0.0) / (im.getHeight(icon.getImageObserver()) + 0.0);

    double scalingFactor = Math.min(scalingFactorW, scalingFactorH);

    return new ImageIcon(im.getScaledInstance(
            (int) Math.round(imW * scalingFactor),
            (int) Math.round(imH * scalingFactor),
            java.awt.Image.SCALE_SMOOTH));
  }

}
