/***********************************************************************************************
 CLASS:	      ErosionIntervals

 FUNCTION:     This class creates a canvas that will show the interval values for the columns,
 rows and average profile of the grid.
 The class contains:
 -Constructor:
 *public ErosionIntervals(SharedParameters sparams, String name, int type, String bt)
 -Helping Functions:
 *public void passParms(double min, double max)
 *public void startIntervals(double values[])
 *public void startIntervals(double values[], double values1[])
 *public void settings(int aindex)
 *public void run()
 *public void clearIntervals()
 *public void refreshGraph()
 *public void paint(Graphics g)
 *public void update(Graphics g)

 DATE CREATED: April 2003
 ***********************************************************************************************/

import java.awt.*;

class ErosionIntervals extends Canvas implements Runnable {
    private static final int marginLeft = 25;
    private static final int marginBottom = 25;
    private static final int insignificantHeight = 17;
    private final Font f = new Font("Times Roman", Font.PLAIN, 9);
    private final double[] heights;
    private final double[] bedrock;
    private Dimension size;
    private Thread runIntervals = null;
    //to work on the offscreen
    private Image osImage = null;
    private Graphics osGraph = null;
    private int horizontalSegmentSize = 0;
    private double amplifyHorizontalSize = 1;
    private double verticalSegmentSize = 0;
    private double maxValue = 20;
    private int verticalCellHeightSegment = 1;
    private int verticalSedimentSegment = 1;
    private String strProfile = "";
    private String bottomTitle = "";
    private int arrayIndex = 200;
    private int numberOfValues = 0;
    private int type = 0;


    /**
     * ********************************************************************************************
     * Constructor
     * *********************************************************************************************
     */
    public ErosionIntervals(String name, int type, String bt) {
        super();
        setBackground(Color.white);
        strProfile = name;
        this.type = type;
        int maxrows = 200;
        heights = new double[maxrows];
        bedrock = new double[maxrows];
        bottomTitle = bt;
        for (int i = 0; i < maxrows; i++) {
            heights[i] = -1;
            bedrock[i] = -1;
        }
    }

    public void passParms(double max) {
        amplifyHorizontalSize = 1;
        maxValue = max;
    }


    /**
     * ********************************************************************************************
     * to get the intervals drawn
     * *********************************************************************************************
     */
    public void startIntervals(double values[]) {
        numberOfValues = 1;
        if (runIntervals == null) {
            System.arraycopy(values, 0, heights, 0, arrayIndex);
            runIntervals = new Thread(this);
            runIntervals.setPriority(runIntervals.getPriority() + 1);
            runIntervals.start();
            repaint();
        }
    }

    /**
     * ********************************************************************************************
     * to get the intervals drawn
     * *********************************************************************************************
     */
    public void startIntervals(double values[], double values1[]) {
        numberOfValues = 2;
        if (runIntervals == null) {
            for (int i = 0; i < arrayIndex; i++) {
                heights[i] = values[i];
                bedrock[i] = values1[i];
            }
            runIntervals = new Thread(this);
            runIntervals.setPriority(runIntervals.getPriority() + 1);
            runIntervals.start();
            repaint();
        }
    }

    /**
     * ********************************************************************************************
     * to get change the settings when the size of the grid changes
     * *********************************************************************************************
     */
    public void settings(int aindex) {
        arrayIndex = aindex;
        repaint();
    }

    /**
     * ********************************************************************************************
     * where the action takes place
     * *********************************************************************************************
     */
    public void run() {
        if (numberOfValues == 2) {
            for (int i = 0; i < arrayIndex - 1; i++) {
                osGraph.setColor(new Color(0, 0, 255));
                osGraph.drawLine(marginLeft + (int) (i * horizontalSegmentSize * amplifyHorizontalSize),
                        size.height - marginBottom - (int) ((heights[i] - insignificantHeight) * (verticalSegmentSize / verticalCellHeightSegment)),
                        marginLeft + (int) ((i + 1) * horizontalSegmentSize * amplifyHorizontalSize),
                        size.height - marginBottom - (int) ((heights[i + 1] - insignificantHeight) * (verticalSegmentSize / verticalCellHeightSegment)));
//			msg.append("height " + ((heights[i + 1] - insignificantHeight) * (verticalSegmentSize / verticalCellHeightSegment))+ "\n");
                osGraph.setColor(new Color(255, 0, 0));
                osGraph.drawLine(marginLeft + (int) (i * horizontalSegmentSize * amplifyHorizontalSize),
                        size.height - marginBottom - (int) ((bedrock[i] - insignificantHeight) * (verticalSegmentSize / verticalCellHeightSegment)),
                        marginLeft + (int) ((i + 1) * horizontalSegmentSize * amplifyHorizontalSize),
                        size.height - marginBottom - (int) ((bedrock[i + 1] - insignificantHeight) * (verticalSegmentSize / verticalCellHeightSegment)));
            }
        }
        if (numberOfValues == 1) {
            for (int i = 0; i < arrayIndex - 1; i++) {
//			msg.append("Height " + heights[i] + "\n");
                osGraph.setColor(new Color(0, 155, 0));
                osGraph.drawLine(marginLeft + (int) (i * horizontalSegmentSize * amplifyHorizontalSize),
                        size.height - marginBottom - (int) (heights[i] * (verticalSegmentSize / verticalSedimentSegment)),
                        marginLeft + (int) ((i + 1) * horizontalSegmentSize * amplifyHorizontalSize),
                        size.height - marginBottom - (int) (heights[i + 1] * (verticalSegmentSize / verticalSedimentSegment)));
            }
        }
        repaint();
        runIntervals = null;
    }//end run

    /**
     * ********************************************************************************************
     * to clean the canvas
     * *********************************************************************************************
     */
    public void clearIntervals() {
        osImage = null;
    }

    /**
     * ********************************************************************************************
     * to get the canvas redrawn
     * *********************************************************************************************
     */
    public void refreshGraph() {
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
            //graph lines and legends
            osGraph.setColor(Color.black);
            osGraph.setFont(f);
            FontMetrics fm = osGraph.getFontMetrics();
            int htyposition = 9;
            int htxposition = 15;
            osGraph.drawString("  Ht", htxposition, htyposition);
            int bottomTitleMargin = 50;
            osGraph.drawString(bottomTitle, size.width - bottomTitleMargin, size.height - 5);
            osGraph.drawString(strProfile + " Profile - Every 10% of total iterations",
                    (size.width - fm.stringWidth(strProfile + " Profile - Every 10% of total iterations")) / 2, htyposition);
            osGraph.setColor(Color.gray);
            osGraph.drawLine(marginLeft, size.height - marginBottom, size.width - marginLeft + 20, size.height - marginBottom);
            int marginTop = 10;
            osGraph.drawLine(marginLeft, marginTop, marginLeft, size.height - marginBottom);
            //graph references - same scaling will be used for drawing line
            //horizontal references
            horizontalSegmentSize = (size.width - marginLeft) / arrayIndex;

            //this is to scale the horizontal references
            if (horizontalSegmentSize > 2) {
                amplifyHorizontalSize = 1;
            } else if (horizontalSegmentSize == 2) {
                amplifyHorizontalSize = 0.97;
            } else {
                amplifyHorizontalSize = 1.6;
            }

            int ruler = 0;
            for (int i = 0; i < arrayIndex + 1; i++) {
                if ((i % 10) == 0) {
                    osGraph.drawLine(marginLeft + (int) (i * horizontalSegmentSize * amplifyHorizontalSize), size.height - marginBottom - 2,
                            marginLeft + (int) (i * horizontalSegmentSize * amplifyHorizontalSize), size.height - marginBottom + 2);
                    osGraph.drawString("" + ruler, marginLeft + (int) (i * horizontalSegmentSize * amplifyHorizontalSize) - 2, size.height - marginBottom + 10);
                    ruler += 10;
                }
            }// end of for loop

            //vertical references
            int verticalNumberOfSegments = 10;
            verticalSegmentSize = (size.height - marginTop - marginBottom) / verticalNumberOfSegments;
            maxValue = maxValue + (SharedParameters.ENDTIME * SharedParameters.TECTONICSPERCENTAGE);
            verticalCellHeightSegment = (int) ((maxValue - insignificantHeight) / verticalNumberOfSegments);
            verticalSedimentSegment = (int) (maxValue / verticalNumberOfSegments / 2);
            if (verticalCellHeightSegment == 0) {
                verticalCellHeightSegment = 1;
            }
            if (verticalSedimentSegment == 0) {
                verticalSedimentSegment = 1;
            }
//		msg.append("maxValue: " + maxValue + "\n");
//		msg.append("mv / #segs " + verticalSedimentSegment + "\n");
//		msg.append("multiplier " + (verticalSegmentSize / verticalSedimentSegment) + "\n");
            if (type == 2) {
                int cellHeight = insignificantHeight;
                for (int i = 0; i < verticalNumberOfSegments + 1; i++) {
                    osGraph.setColor(new Color(0, 0, 255));
                    osGraph.drawLine(size.width - 80, 20, size.width - 70, 20);
                    osGraph.drawString("Total Height", size.width - 65, 20);
                    osGraph.setColor(new Color(255, 0, 0));
                    osGraph.drawLine(size.width - 80, 30, size.width - 70, 30);
                    osGraph.drawString("Bedrock", size.width - 65, 30);
                    osGraph.setColor(Color.gray);
                    osGraph.drawLine(marginLeft - 2, size.height - marginBottom - ((int) (i * verticalSegmentSize)),
                            marginLeft + 2, size.height - marginBottom - ((int) (i * verticalSegmentSize)));
                    osGraph.drawString("" + cellHeight, 1, size.height - marginBottom - ((int) (i * verticalSegmentSize)));
                    cellHeight += verticalCellHeightSegment;
                }
            }
            if (type == 1) {
                int sedimentAmount = 0;
                for (int i = 0; i < verticalNumberOfSegments + 1; i++) {
                    osGraph.setColor(new Color(0, 155, 0));
                    osGraph.drawLine(size.width - 80, 20, size.width - 70, 20);
                    osGraph.drawString("Total Sediment", size.width - 65, 20);
                    osGraph.setColor(Color.gray);
                    osGraph.drawLine(marginLeft - 2, size.height - marginBottom - ((int) (i * verticalSegmentSize)),
                            marginLeft + 2, size.height - marginBottom - ((int) (i * verticalSegmentSize)));
                    osGraph.drawString("" + sedimentAmount, 1, size.height - marginBottom - ((int) (i * verticalSegmentSize)));
                    sedimentAmount += verticalSedimentSegment;
                }
            }

        }//end if osImage == null
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
}// end of Erosion Intervals