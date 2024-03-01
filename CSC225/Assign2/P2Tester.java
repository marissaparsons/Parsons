/* P2Tester.java
   CSC 225 - Summer 2023
   
   YOU DO NOT NEED TO MODIFY THIS CODE IN ANY WAY
   
   Addie Jordon - 06/26/2023
   Anthony Estey - 07/18/20
   Thanks to Dr. Bill Bird for providing code related to the GUI. 
   Some of the code for keyboard bindings is based on code from B. Jiao
*/

import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

import javax.swing.border.EtchedBorder;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class P2Tester {

	private JFrame viewerWindow;
	private final JPanel lowerPanel = new JPanel();
	private final JPanel lowerLeftPanel = new JPanel();
  private List<Integer> xsInteger;
	
  private int magnitude = 200;
  private int initialScale = 2;
	private Color currentColour;
	private ImageCanvas imageCanvas;
	private BufferedImage currentImage;
	private JLabel swatchLabel;
	private boolean samplingColour;
	private JLabel regionCountLabel;
	private JCheckBox animateCheckBox;
	private AnimateHelper animator;
	
	private enum ImageAlgorithm{
    QUICKSORT,
    MERGESORT,
    INSERTIONSORT,
    RADIXSORT,
		DFS,
		BFS,
	}
	
	ImageAlgorithm selectedAlgorithm;
	
	
	private P2Tester() {
		currentColour = new Color(0,0,0);
		samplingColour = false;
		animator = null;
		initialize();
		changeColour(new Color(0,0,0));
		reloadImage();
	}

	private class ImagePixelWriter implements PixelWriter{
		public void setPixel(int x, int y, Color colour){
			if (x < 0 || x >= magnitude)
				ErrorAbort("Invalid x coordinate %d",x);
			if (y < 0 || y >= magnitude)
				ErrorAbort("Invalid y coordinate %d",y);
			currentImage.setRGB(x, y, colour.getRGB());
			
			
			if (animator != null){
				animator.setPixel(x, y); //In animation mode, use the AnimationHelper to schedule refreshes
			}else{
				imageCanvas.refreshImage(); //Otherwise, use refreshImage (which schedules a refresh at an idle moment).
			}
		}

    public Color getColor(int x, int y) {
      if (x < 0 || x >= magnitude)
        ErrorAbort("Invalid x coordinate %d",x);
			if (y < 0 || y >= magnitude)
				ErrorAbort("Invalid y coordinate %d",y);
			
      return new Color(currentImage.getRGB(x, y));
    }

	}
	
		
	private void setSelectedAlgorithm(ImageAlgorithm alg){
		selectedAlgorithm = alg;
	}
	
  private void runSortAlgorithm() {

    if (animateEnabled()) {
      boolean insertionSort = (selectedAlgorithm == ImageAlgorithm.INSERTIONSORT);
      animator = new AnimateHelper(magnitude, magnitude, insertionSort);
    } else {
      animator = null;
    }

    if (selectedAlgorithm == ImageAlgorithm.QUICKSORT) {
      System.out.println("Running quicksort");
	    GraphAlgorithms.QuickSort(xsInteger, new ImagePixelWriter());
    } else if (selectedAlgorithm == ImageAlgorithm.MERGESORT) {
      System.out.println("Running mergesort");
      GraphAlgorithms.MergeSort(xsInteger, new ImagePixelWriter());
    } else if (selectedAlgorithm == ImageAlgorithm.INSERTIONSORT) {
      System.out.println("Running insertionsort");
      GraphAlgorithms.InsertionSort(xsInteger, new ImagePixelWriter());
    } else if (selectedAlgorithm == ImageAlgorithm.RADIXSORT) {
      System.out.println("Running radix sort");
      GraphAlgorithms.RadixSort(xsInteger, new ImagePixelWriter());
    }

		if (animator != null) {
			imageCanvas.refreshImage();
		}
    System.out.println("Done\n");
  }

	private void runGraphAlgorithms(int X, int Y){
		int width = currentImage.getWidth();
		int height = currentImage.getHeight();
		
		if (animateEnabled()) {
			animator = new AnimateHelper(width,height,false);
		} else {
			animator = null;
		}

		Color[][] imagePixels = new Color[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				imagePixels[y][x] = new Color(currentImage.getRGB(x,y));
			}
		}
		
		PixelGraph G = new PixelGraph(imagePixels);
		PixelVertex v = G.getPixelVertex(X,Y);
		if (selectedAlgorithm == ImageAlgorithm.DFS){
			System.out.printf("Filling using DFS\n");
			GraphAlgorithms.FloodFillDFS(v, new ImagePixelWriter(), currentColour);
			System.out.println("Finished filling\n");
		}else if (selectedAlgorithm == ImageAlgorithm.BFS){
			System.out.printf("Filling using BFS\n");
			GraphAlgorithms.FloodFillBFS(v, new ImagePixelWriter(), currentColour);
		}

		if (animator != null) {
			imageCanvas.refreshImage();
		}
	}

	
	private void shufflePixels(){
    int[] xs = IntStream.range(0, magnitude).toArray();
    xsInteger  = Arrays.stream(xs).boxed().collect(Collectors.toList());
    Collections.shuffle(xsInteger);

		currentImage = new BufferedImage(magnitude,magnitude,BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < magnitude; x++) {
			for(int y = 0; y <magnitude; y++) {
          currentImage.setRGB(x,y,Color.WHITE.getRGB());
      }
    }

    for (int x=0; x < magnitude; x++)
      currentImage.setRGB(x,xsInteger.get(x),Color.BLACK.getRGB());
		System.out.printf("Shuffled a %d by %d line\n\n",magnitude,magnitude);
	}
	
	private void reloadImage(){
		shufflePixels();
		imageCanvas.refreshImage();
	}

	
	private void setSwatch(){
		swatchLabel.setIcon(new ImageIcon(new ColourSwatch(25,25,currentColour)));
	}
	
	private void changeColour(Color newColour){
		currentColour = newColour;
		setSwatch();
	}
	
	private void chooseColour(){
		Color newColour = JColorChooser.showDialog(null, "Choose a colour.", currentColour);
		if (newColour != null)
			changeColour(newColour);
	}
	
	private void clickPoint(int x, int y, int button_number){
		if (x >= currentImage.getWidth() || y >= currentImage.getHeight()) {
			return;
    }
		if (samplingColour){
			int iCol = currentImage.getRGB(x,y);
			changeColour(new Color(iCol));
			samplingColour = false;
			return;
		}
		runGraphAlgorithms(x,y);
		
	}
	
	private boolean animateEnabled(){
		return animateCheckBox.isSelected();
	}
	

	private void initialize() {
		viewerWindow = new JFrame();
		viewerWindow.setTitle("CSC 225 - Image Visualization");
		viewerWindow.setBounds(magnitude, magnitude,1000, 600);
		viewerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		viewerWindow.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		imageCanvas = new ImageCanvas();
		scrollPane.setViewportView(imageCanvas);
		viewerWindow.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
		lowerPanel.setLayout(new BorderLayout(0, 0));
		lowerPanel.add(lowerLeftPanel, BorderLayout.WEST);

		
		JPanel swatchPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) swatchPanel.getLayout();
		flowLayout.setVgap(1);
		flowLayout.setHgap(1);
		swatchPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lowerLeftPanel.add(swatchPanel);
		
		swatchLabel = new JLabel("");
		swatchLabel.setHorizontalAlignment(SwingConstants.CENTER);
		swatchPanel.add(swatchLabel);
		
		JButton chooseColourButton = new JButton("Choose Colour");
		chooseColourButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseColour();
			}
		});
		lowerLeftPanel.add(chooseColourButton);
		chooseColourButton.setHorizontalAlignment(SwingConstants.LEFT);
		
		JButton sampleColourButton = new JButton("Sample Colour");
		sampleColourButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				samplingColour = true;
			}
		});
		lowerLeftPanel.add(sampleColourButton);
		
		JButton reloadButton = new JButton("Shuffle sequence");
		reloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reloadImage();
			}
		});
		lowerLeftPanel.add(reloadButton);

		JButton quicksortButton = new JButton("Run sort");
		quicksortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
        runSortAlgorithm();
			}
		});
		lowerLeftPanel.add(quicksortButton);

		SpinnerModel MagnitudeModel = new SpinnerNumberModel(magnitude, //initial value
                                        1, 2000, 1);
    JSpinner magnitudeSpinner = new JSpinner(MagnitudeModel);
		magnitudeSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
        Object magobj = magnitudeSpinner.getValue();
        if (magobj instanceof Integer) {
          System.out.println("Setting magnitude to: " + Integer.toString((Integer)magobj)); 
          magnitude = (Integer) magobj;
          reloadImage();
        }
			}
		});
		lowerLeftPanel.add(magnitudeSpinner);
		
		animateCheckBox = new JCheckBox("Animate?");
		lowerLeftPanel.add(animateCheckBox);
		
		JPanel lowerRightPanel = new JPanel();
		lowerPanel.add(lowerRightPanel, BorderLayout.EAST);
		
		JLabel algorithmSelectLabel = new JLabel("Algorithm:");
		lowerRightPanel.add(algorithmSelectLabel);
		
		final String[] algorithmList = {"Quick sort", "Merge sort", "Insertion sort", "Radix sort", "Flood fill (DFS)", "Flood fill (BFS)"};
		final ImageAlgorithm[] algorithmIndices = {ImageAlgorithm.QUICKSORT, ImageAlgorithm.MERGESORT, ImageAlgorithm.INSERTIONSORT, ImageAlgorithm.RADIXSORT, ImageAlgorithm.DFS, ImageAlgorithm.BFS};
		final JComboBox algorithmSelectionBox = new JComboBox<String>(algorithmList);
		algorithmSelectionBox.setSelectedIndex(0);
		setSelectedAlgorithm(ImageAlgorithm.QUICKSORT);
		algorithmSelectionBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSelectedAlgorithm(algorithmIndices[algorithmSelectionBox.getSelectedIndex()]);
			}
		});
		lowerRightPanel.add(algorithmSelectionBox);
		
	}
	
	class ImageCanvas extends JComponent{
	
		private static final long serialVersionUID = 1L;
		
		private double scaleFactor;

		public ImageCanvas(){
			setSize(magnitude, magnitude);
			scaleFactor = initialScale;
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					translateClick(x,y,e.getButton());
				}
			});
			addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					int z = e.getWheelRotation();
					if (z < 0)
						zoomIn(-z);
					else
						zoomOut(z);
				}
			});
			
			//Bind the UP and DOWN keys to zooming in and out
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0), "zoomIn");
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0), "zoomOut");
			
			getActionMap().put("zoomIn", new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					zoomIn(1);
				} 
			});

			getActionMap().put("zoomOut", new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					zoomOut(1);
				} 
			});
			
		}

		private int imageWidth(){
			return magnitude;
		}
		private int imageHeight(){
			return magnitude;
		}
		private int canvasWidth(){
			return (int)(imageWidth()*scaleFactor);
		}
		private int canvasHeight(){
			return (int)(imageHeight()*scaleFactor);
		}
		
		private void translateClick(int x, int y, int button_number){
		  imageCanvas.requestFocus();	
			//Reject the point if it's out of bounds
			if (x < 0 || x >= canvasWidth())
				return;
			if (y < 0 || y >= canvasHeight())
				return;
			//Determine the image coordinates of the (x,y) point
			
			int tx = (int)(x/scaleFactor);
			int ty = (int)(y/scaleFactor);
			
			clickPoint(tx,ty, button_number);
		}
		
		private void zoomIn(int z){
			setScale((int)Math.ceil(scaleFactor/Math.pow(0.9, z)));
		}
		private void zoomOut(int z){
/*      double updatedSF = scaleFactor*Math.pow(0.9, z);
      DecimalFormat dfmt = new DecimalFormat("#.#####");
      dfmt.setRoundingMode(RoundingMode.DOWN);
      double trunc = Double.parseDouble(dfmt.format(updatedSF)); */
      setScale((int)Math.ceil(scaleFactor*Math.pow(0.9, z)));
		}
		
		public void refreshImage(){
			int width = canvasWidth();
			int height = canvasHeight();
			Dimension d = new Dimension(magnitude,magnitude);
			setSize(d);
			setPreferredSize(d);
			repaint();
		}
		public void refreshRectangle(Rectangle r){
			//Scale the rectangle to accommodate the current scale factor
			int x = (int)(r.x*scaleFactor);
			int y = (int)(r.y*scaleFactor);
			int w = (int)(r.width*scaleFactor);
			int h = (int)(r.height*scaleFactor);
			Rectangle scaledRectangle = new Rectangle(x,y,w,h);
			paintImmediately(scaledRectangle);
		}
		public void setScale(double s){
			scaleFactor = s;
			refreshImage();
		}

		@Override
		public void paintComponent(Graphics g){
			if (currentImage == null)
				return;
			int width = canvasWidth();
			int height = canvasHeight();
			g.drawImage(currentImage, 0, 0, width,height,null);
		}
		
	}
	
	class AnimateHelper{
		Rectangle currentBounds = null;
		int pixelCount = 0;
		double pixelsPerMillisecond;

		AnimateHelper(int w, int h, boolean insertionSort){
      //insertion sort runs so slowly, we will speed it up
			double totalPixels = w*h;
			//Aim for 1% of all pixels per second
      // unless it's insertion sort, then 8% pixels/s
      if (insertionSort) {
        pixelsPerMillisecond = ((totalPixels*0.08)/1000);
      } else {
			  pixelsPerMillisecond = ((totalPixels*0.01)/1000);
      }
		}
		void setPixel(int x, int y){
			pixelCount++;
			if (currentBounds == null)
				currentBounds = new java.awt.Rectangle(x, y, 1,1);
			else
				currentBounds.add(new Rectangle(x,y,1,1));
			if (pixelCount >= pixelsPerMillisecond*10){
				pixelCount = 0;
				imageCanvas.refreshRectangle(currentBounds);
				currentBounds = null;
				//Sleep for 10 milliseconds.
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					return;
				} 
				
			}
		}
	}
	
	static class ColourSwatch extends BufferedImage{
		public ColourSwatch(int width, int height, Color colour){
			super(width,height,BufferedImage.TYPE_INT_RGB);
			int iCol = (colour.getRed()<<16) + (colour.getGreen()<<8) + colour.getBlue();
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++)
					setRGB(x, y, iCol);
					
		}
	}
	
	
	public static void spawn() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					P2Tester window = new P2Tester();
					window.viewerWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args){
		spawn();
	}

	/* Prints an error message and exits (intended for user errors) */
	private static void ErrorExit(String errorMessage, Object... formatArgs){
		System.err.printf("ERROR: " + errorMessage + "\n",formatArgs);
		System.exit(0);
	}
	/* Throws a runtime error (intended for logic errors) */
	private static void ErrorAbort(String errorMessage, Object... formatArgs){
		throw new Error(String.format(errorMessage,formatArgs));
	}

}

