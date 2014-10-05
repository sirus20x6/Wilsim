/***********************************************************************************************
 CLASS:	      ErosionHypsometric

 FUNCTION:     This class creates a canvas that will show the hypsometric curve.
 The class contains:
 -Constructor:
 *public ErosionHypsometric(SharedParameters sparams)
 -Helping Functions:
 *public void startHypsometric(double rh[], double ra[])
 *public void run()
 *public void clearHypsometric()
 *public void refreshHypsometric()
 *public void paint(Graphics g)
 *public void update(Graphics g)

 DATE CREATED: May 2003
 ***********************************************************************************************/

import java.awt.*;

class ErosionHypsometric extends Canvas implements Runnable {
    private static final int HINTERVAL = 11;
    private static final int marginLeft = 25;
    private static final int marginBottom = 25;
    private final Font f = new Font("Times Roman", Font.BOLD, 12);
    private final Font f1 = new Font("Times Roman", Font.BOLD, 10);
    private final double[] relativeHeight;
    private final double[] relativeArea;
    private Dimension size;
    private Thread runHypsometric = null;
    //to work on the offscreen
    private Image osImage = null;
    private Graphics osGraph = null;
    private int minY;
    private int maxY;
    private int minX;
    private int maxX;

    /**
     * ********************************************************************************************
     * Constructor
     * *********************************************************************************************
     */
    public ErosionHypsometric() {
        super();
        setBackground(Color.white);
        relativeHeight = new double[HINTERVAL];
        relativeArea = new double[HINTERVAL];
        for (int i = 0; i < HINTERVAL; i++) {
            relativeHeight[i] = -1;
            relativeArea[i] = -1;
        }
    }


    /**
     * ********************************************************************************************
     * to get the curve drawn
     * *********************************************************************************************
     */
    public void startHypsometric(double rh[], double ra[]) {
        if (runHypsometric == null) {
            for (int i = 0; i < HINTERVAL; i++) {
                relativeHeight[i] = rh[i];
                relativeArea[i] = ra[i];
            }
            runHypsometric = new Thread(this);
            runHypsometric.setPriority(runHypsometric.getPriority() + 1);
            runHypsometric.start();
            repaint();
        }
    }

    /**
     * ********************************************************************************************
     * where the action takes place
     * *********************************************************************************************
     */
    public void run() {
        for (int i = 0; i < HINTERVAL - 1; i++) {
            osGraph.setColor(new Color(255, 0, 255));
            osGraph.drawLine((int) (marginLeft + ((maxX - minX) * relativeArea[i])),
                    (size.height - marginBottom - ((maxY - minY) / (HINTERVAL - 1)) * i),
                    (int) (marginLeft + ((maxX - minX) * relativeArea[i + 1])),
                    (size.height - marginBottom - ((maxY - minY) / (HINTERVAL - 1)) * (i + 1)));
        }
        runHypsometric = null;
    }//end run

    /**
     * ********************************************************************************************
     * to clean the canvas
     * *********************************************************************************************
     */
    public void clearHypsometric() {
        osImage = null;
    }

    /**
     * ********************************************************************************************
     * to get the canvas redrawn
     * *********************************************************************************************
     */
    public void refreshHypsometric() {
        repaint();
    }

    /**
     * ********************************************************************************************
     * to draw stuff in the canvas
     * *********************************************************************************************
     */
    public void paint(Graphics g) {
        //this is run the first time
        if (osImage == null) {
            size = getSize();
            //variables to work on the offscreen
            osImage = createImage(size.width, size.height);
            osGraph = osImage.getGraphics();
            osGraph.clearRect(0, 0, size.width, size.height);
            osGraph.setColor(Color.black);
            osGraph.setFont(f);
            FontMetrics fm = osGraph.getFontMetrics();
            osGraph.drawString("Hypsometric Curve",
                    (size.width - fm.stringWidth("Hypsometric Curve")) / 2, 10);
            osGraph.setColor(new Color(0, 0, 255));
            osGraph.setColor(Color.black);
            osGraph.setFont(f1);
            osGraph.setColor(Color.gray);
            int marginRight = 25;
            osGraph.drawLine(marginLeft, size.height - marginBottom, size.width - marginRight, size.height - marginBottom);
            int marginTop = 25;
            osGraph.drawLine(marginLeft, marginTop, marginLeft, size.height - marginBottom);
            osGraph.drawString("Relative Area",
                    (size.width - fm.stringWidth("Relative Area")) / 2, size.height - 5);
            osGraph.drawString("R",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2);
            osGraph.drawString("e",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 10);
            osGraph.drawString("l",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 20);
            osGraph.drawString("a",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 30);
            osGraph.drawString("t",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 40);
            osGraph.drawString("i",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 50);
            osGraph.drawString("v",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 60);
            osGraph.drawString("e",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 70);
            osGraph.drawString("H",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 90);
            osGraph.drawString("e",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 100);
            osGraph.drawString("i",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 110);
            osGraph.drawString("g",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 120);
            osGraph.drawString("h",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 130);
            osGraph.drawString("t",
                    2, (size.height - fm.stringWidth("Relative Height")) / 2 + 140);
            int segmentHorizontal = (size.width - marginLeft) / HINTERVAL;
            int segmentVertical = (size.height - marginBottom) / HINTERVAL;
            int nseg = 0;
            osGraph.drawString("0.0", marginLeft + (nseg * segmentHorizontal) - 2, size.height - marginBottom + 10);
            osGraph.drawString("0.0", 10, size.height - marginBottom - (nseg * segmentVertical));
            minX = marginLeft;
            minY = marginBottom;
            for (nseg = 0; nseg < HINTERVAL; nseg++) {
                if (nseg == 5) {
                    osGraph.drawString("0.5", marginLeft + (nseg * segmentHorizontal) - 2, size.height - marginBottom + 10);
                    osGraph.drawString("0.5", 10, size.height - marginBottom - (nseg * segmentVertical));
                }
                osGraph.drawLine(marginLeft + (nseg * segmentHorizontal), size.height - marginBottom - 2,
                        marginLeft + (nseg * segmentHorizontal), size.height - marginBottom + 2);
                osGraph.drawLine(marginLeft - 2, size.height - marginBottom - (nseg * segmentVertical),
                        marginLeft + 2, size.height - marginBottom - (nseg * segmentVertical));
            }
            osGraph.drawString("1.0", marginLeft + ((nseg - 1) * segmentHorizontal) - 2, size.height - marginBottom + 10);
            osGraph.drawString("1.0", 10, size.height - marginBottom - ((nseg - 1) * segmentVertical));
            maxX = marginLeft + ((nseg - 1) * segmentHorizontal);
            maxY = size.height - (size.height - marginBottom - ((nseg - 1) * segmentVertical));
            osGraph.setFont(f);
        }
        //finally draw on the canvas
        g.drawImage(osImage, 0, 0, this);
    }//end paint

    /**
     * ********************************************************************************************
     * to prevent image from flickering
     * *********************************************************************************************
     */
    public void update(Graphics g) {
        paint(g);
    }

}// end of Erosion Hypsometric