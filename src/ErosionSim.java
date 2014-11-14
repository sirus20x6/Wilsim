/***********************************************************************************************
 CLASS:	      ErosionSim

 FUNCTION:     ErosionSim is a class where the cellular automata algorithm is implemented.
 The class contains:
 -Constructor:
 *ErosionSim(SharedParameters s, EROSIONCANVAS e, EROSIONCANVAS e25p, EROSIONCANVAS e50p, EROSIONCANVAS e75p, EROSIONCANVAS e100p,
 ErosionIntervals i, ErosionIntervals is,
 ErosionIntervals c, ErosionIntervals cs, ErosionIntervals r, ErosionIntervals rs,
 ErosionHypsometric h)
 it initilializes needed variables to be able to communicate with other parts of the applet.
 -Helping functions:
 *public void start()
 it sets up the thread that will run the algorithm
 *public void run()
 what the thread will do while it is true. It will first
 reset the parameters and conditions, then it will check for ending
 conditions and if everything is alright, it will proceed to execute
 the algorithm.
 Algorithm inside run() = if the precipiton has just fallen, it starts
 the counter, and it gets a random cell in the grid. If it is first
 time, it will calculate the erosion percentages for the rest of the
 simulation.
 It will also check if tectonics is enabled and will proceed to apply
 it.
 Once those functions are executed, it calls a function to get the 8
 cells surrounding the randomly chosen one and calls a function to
 apply diffusion.
 After checking that a wall was not hit, we continue in the while loop
 by calling the functions to search the lowest cell in the group, to
 calculate the corresponding erosion, to check the carrying capacity and
 to draw the grid with the changes.
 The target coordinates move from the randomly chosen cell to the lowest
 cell and the loop continues.
 It takes care of interval saving and snapshot calculations when required
 *private boolean bored()
 to check the status of the thread
 *public synchronized void resume()
 to get the thread going
 *public void suspend()
 to supend the thread
 *private void reset()
 to do the first set up of parameters and values
 *private void setColors()
 to check for colors in the rainbow
 *private void resetSlope()
 to check for slope in graph
 *private void resetCanvasSlope(int j, int i, double height)
 to reset the canvas
 *private void getstartingCell()
 this functions gets an x,y random location in the topographic grid.
 *private void getSurroundingCells()
 this function gets the cells surrounding the randomly selected one.
 *public void getErosionValue()
 get erosion according to parameters from applet.
 *public void applyDiffusion()
 apply diffusion when first precipiton falls.
 *public void searchlowestCell()
 search for lowest cell in 3x3 grid
 *public void calculateErosion()
 apply the erosion according to erodibility parameters
 *public void checkcarryingCapacity()
 check the carrying capacity
 *public void applyTectonics()
 apply tectonics if selected
 *public void saveInterval()
 to save intervals
 *private void resetIntervalArrays()
 to reset arrays for interval values
 *private void resetIntervals(ErosionIntervals eicopy, boolean rowFlag)
 to reset the canvas
 *public void cleanup()
 set variables before each iteration
 *public void printMessages(int messageNumber)
 function called to print messages
 *void resetHypsometric()
 to reset hypsometric values and image
 *void calculateHypsometric()
 to calculate and get the curve drawn

 INPUT:	      User will select parameters.

 DATE CREATED: August 2002
 ***********************************************************************************************/

import java.awt.*;
import java.util.ArrayList;

class ErosionSim implements Runnable {
    // variables for the surface object creation
    private static final int BARMINHEIGHT = 20;
    private static final int nIntervals = 10;
    private static final int HYPSOINTERVAL = 11;
    private static Label values2 = new Label("");
    private static Label values4 = new Label("");
    private static int intervalStep = 1;
    private static int intervalCounter = 0;
    private static double[] relativeHeight;
    private static double[] relativeArea;
    private static boolean initialize = true;
    private final EROSIONCANVAS eCanv;
    private final EROSIONCANVAS eCanv25P;
    private final EROSIONCANVAS eCanv50P;
    private final EROSIONCANVAS eCanv75P;
    private final EROSIONCANVAS eCanv100P;
    private final Thread THISTHREAD;  // The simulation thread
    private final ErosionIntervals ei;
    private final ErosionIntervals eis;
    private final ErosionIntervals er;
    private final ErosionIntervals ers;
    private final ErosionIntervals ec;
    private final ErosionIntervals ecs;
    private final boolean UISETUP;
    private final int[] YCOORDARRAY = new int[9];
    private final int[] XCOORDARRAY = new int[9];
    private final int[] XCOORDDIFFUSIONARRAY = new int[4];
    private final int[] YCOORDDIFFUSIONARRAY = new int[4];
    private final ErosionColors colors = new ErosionColors();
    //for the hypsometric curve graph
    private final ErosionHypsometric eh;
    //to hold the grid objects
    private SurfaceBar[][] surfaceArray;
    //to save interval information
    private double[] sumColumns;
    private double[] sumColumnsBedrock;
    private double[] sumIntervalsSediment;
    private double[] averageIntervals;
    private double[] averageIntervalsBedrock;
    private double[] columnIntervals;
    private double[] columnIntervalsBedrock;
    private double[] columnSediment;
    private double[] rowIntervals;
    private double[] rowIntervalsBedrock;
    private double[] rowSediment;
    private boolean rowFlag = true;
    //flags needed troughout the simulation
    private boolean keepGoing;
    private boolean firstTime;
    private boolean running;
    private int diffusesedImentX = 0;
    private int diffusesedImentY = 0;
    private int cellX;
    private int cellY;
    //    private int steps = 0;
    //coord variables to be used later
    private int newX;
    private int newY;
    private double randXLeft;
    private double randXRight;
    private double randYBottom;
    private double randYTop;
    private double basicErosion = 0;
    private int incrIndex = 0;
    private int incrDiffusionIndex = 0;
    private ArrayList<Integer> sourceY = new ArrayList<Integer>();
    private ArrayList<Integer> sourceX = new ArrayList<Integer>();
    ArrayList<Integer> lowerCellsX = new ArrayList<Integer>();
    ArrayList<Integer> lowerCellsY = new ArrayList<Integer>();
    private ArrayList<Double> distanceAL = new ArrayList<Double>();
    private ArrayList<Double> gradientSigmaAL = new ArrayList<Double>();
    private ArrayList<Double> gradientAL = new ArrayList<Double>();
    private ArrayList<Double> forceAL = new ArrayList<Double>();

    /**
     * ********************************************************************************************
     * Constructor
     * *********************************************************************************************
     */
    ErosionSim(EROSIONCANVAS e,
               EROSIONCANVAS e25p, EROSIONCANVAS e50p, EROSIONCANVAS e75p, EROSIONCANVAS e100p,
               ErosionIntervals i, ErosionIntervals is,
               ErosionIntervals c, ErosionIntervals cs,
               ErosionIntervals r, ErosionIntervals rs,
               ErosionHypsometric h) {
        eCanv = e;
        eCanv25P = e25p;
        eCanv50P = e50p;
        eCanv75P = e75p;
        eCanv100P = e100p;
        ei = i;
        eis = is;
        er = r;
        ers = rs;
        ec = c;
        ecs = cs;
        eh = h;
        UISETUP = false;
        THISTHREAD = new Thread(this);
        // Set up the debugging frame
        Frame errs = new Frame("Debugging info");
        errs.setSize(500, 500);
        TextArea msg = new TextArea("");
        errs.add(msg);
//	errs.show();
    }

    /**
     * ********************************************************************************************
     * to get the simulation started
     * *********************************************************************************************
     */
    public void start() {
        // Reduce priority of renderer so that UI takes precedence
        Thread current = Thread.currentThread();
        THISTHREAD.setPriority(current.getPriority() - 1);
        THISTHREAD.start();
    }

    /**
     * ********************************************************************************************
     * this function drives the simulation
     * *********************************************************************************************
     */
    public void run() {
        // Initialize all the data values
        reset();
        while (true) {
            if (bored()) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {
                    }
                }
                continue;  // Recheck conditions
            }

            // Let's do something!
            if (SharedParameters.ITERATIONCOUNTER >= SharedParameters.TOTALYEARS && SharedParameters.reverseIterationCounter == 0) {
                // Time to stop -- all done
                //running = false;
                //SharedParameters.ROUTINESTARTED = false;
                SharedParameters.reverseIterationCounter = SharedParameters.ITERATIONCOUNTER;
                int reverseIntervalStep = intervalStep;
                SharedParameters.ITERATIONCOUNTER =
                        intervalStep = 1;
                // continue;
            }

            if (!(SharedParameters.ITERATIONCOUNTER >= 0)) {
                // Time to stop -- all done
                running = false;
                SharedParameters.ROUTINESTARTED = false;
                continue;
            }

            if (SharedParameters.reverseIterationCounter >= 200000) {
                // Time to stop -- all done
                running = false;
                SharedParameters.ROUTINESTARTED = false;
                continue;
            }

            // To get it started
            if (firstTime) {
                SharedParameters.ITERATIONCOUNTER++;
                if (SharedParameters.reverseIterationCounter >= 100000) {
                    SharedParameters.reverseIterationCounter++;
                }
                intervalStep++;
                // Call getStartingCell to get the first random bar
                getStartingCell();
            }
            // To re-calculate erosion values every time a change occurs
            if (SharedParameters.EROSIONNEEDED) {
                if (SharedParameters.reverseIterationCounter > 0) {
                    //reverseGetErosionValue();
                    //getErosionValue();
                    SharedParameters.reverseIterationCounter++;
                } else {
                    getErosionValue();
                }
                SharedParameters.EROSIONNEEDED = false;
            }

            // To apply tectonics if selected
            if (SharedParameters.APPLYTECTONICS && firstTime) {
                applyTectonics();
            }

            // To look for the eight target surrounding cells every time it
            // goes through this loop
            getSurroundingCells();

            //get erosion value according to applet parameters and apply diffusion
            if (firstTime && keepGoing) {
                applyDiffusion();
                firstTime = false;
            }

            //only apply these if getSurroundingCells was successful
            if (keepGoing) {
                //search for the lowest cell in the 3x3 grid
                if (SharedParameters.reverseIterationCounter > 0) {
                    reverseSearchLowestCell();
                    reverseCalculateErosion();
                    //calculateErosion();
                } else {
                    searchlowestCell();
                    while(lowerCellsX.size() > 0) {
                        newX = sourceY.get(0);
                        newY = sourceX.get(0);
                        cellX = lowerCellsX.get(0);
                        cellY = lowerCellsY.get(0);
                        calculateErosion();


                        lowerCellsX.remove(0);
                        lowerCellsY.remove(0);
                    }
                    sourceY.remove(0);
                    sourceX.remove(0);

                    //calculateErosion(sourceY.get(0), sourceX.get(0), lowerCellsX.get(0),lowerCellsY.get(0));
                }

                //apply erosion according to erodibility parameters


                //check capacity
                //checkcarryingCapacity();

                //always calculate for intervals - it doesn't hurt
                if (intervalStep == (SharedParameters.ENDTIME / nIntervals) || initialize) {
                    saveInterval();
                    intervalStep = 1;
                    initialize = false;
                    calculateHypsometric();
                    intervalCounter++;
                }

                //set lowest bar as target
                cellY = newX;
                cellX = newY;
            }

            // to make sure colors are set right for the snapshots as well as for the animation
            if ((SharedParameters.BARSPROCESSED % 7500) == 0 || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME || SharedParameters.ITERATIONCOUNTER == 0
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 2
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 3
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME - 5) {
                setColors();
                eCanv.setWallColor();
            }
            eCanv.redraw();

            //work with the snapshots at their corresponding times
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4) {
                setSnapshot(eCanv25P);
            }
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 2) {
                setSnapshot(eCanv50P);
            }
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 3) {
                setSnapshot(eCanv75P);
            }
            if (SharedParameters.ITERATIONCOUNTER == (SharedParameters.ENDTIME - 5)) {
                setSnapshot(eCanv100P);

                SharedParameters.SHOWSURFACEBUTTON.setLabel("Run");
            }
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME) {
                SharedParameters.STARTALLOVER = true;
            }
            Thread.yield();  // Play nice -- let someone else have some time
        }
    }//end of run()


    /**
     * ********************************************************************************************
     * if thread gets lazy
     * *********************************************************************************************
     */
    private boolean bored() {
        // When this returns true, there is nothing constructive to do.
        return !running;

    }// end of bored

    /**
     * ********************************************************************************************
     * to get thread going from where it was paused there is checking if stuff needs to be reset
     * *********************************************************************************************
     */
    public synchronized void resume() {
        // This is called to start the simulation up (again)
        if (SharedParameters.OLDCOLUMNS != SharedParameters.COLUMNS || SharedParameters.OLDROWS != SharedParameters.ROWS) {
            SharedParameters.OLDCOLUMNS = SharedParameters.COLUMNS;
            SharedParameters.OLDROWS = SharedParameters.ROWS;
            SharedParameters.STARTALLOVER = true;
        }
        if ((int) (SharedParameters.ENDTIME / SharedParameters.TIMESTEP) != SharedParameters.TOTALYEARS) {
            //make sure the ending time is updated
            values2.setText("" + SharedParameters.ENDTIME);
            SharedParameters.STARTALLOVER = true;
        }
        if (SharedParameters.EROSIONNEEDED) {
            getErosionValue();
            SharedParameters.EROSIONNEEDED = false;
        }
        if (SharedParameters.STARTALLOVER) {
            reset();
            SharedParameters.STARTALLOVER = false;
        }
        if (SharedParameters.OLDSLOPE != SharedParameters.SLOPE) {
            resetSlope();
        }
        if (SharedParameters.RESETINTERVALSLEGEND) {
            resetIntervals(ei, rowFlag);
            resetIntervals(eis, rowFlag);
            resetIntervals(ec, rowFlag);
            resetIntervals(ecs, rowFlag);
            rowFlag = false;
            resetIntervals(er, false);
            resetIntervals(ers, rowFlag);
            rowFlag = true;
            resetIntervalArrays();
            SharedParameters.RESETINTERVALSLEGEND = false;
        }
        SharedParameters.ROUTINESTARTED = true;
        keepGoing = true;
        running = true;
        notify();
    }// end of resume

    /**
     * ********************************************************************************************
     * hold on
     * *********************************************************************************************
     */
    public void suspend() {
        SharedParameters.ROUTINESTARTED = false;
        running = false;
    }

    /**
     * ********************************************************************************************
     * reset values
     * *********************************************************************************************
     */
    private void reset() {
        // When this truly becomes a reset function, the next few lines
        // should go somewhere more appropriate
        SharedParameters.EROSIONNEEDED = true;
        SharedParameters.ROUTINESTARTED = false;
        SharedParameters.TOTALYEARS = (int) (SharedParameters.ENDTIME / SharedParameters.TIMESTEP);
        SharedParameters.OLDCOLUMNS = SharedParameters.COLUMNS;
        SharedParameters.OLDROWS = SharedParameters.ROWS;
        if (!UISETUP) {
            values2 = SharedParameters.values2;
            values4 = SharedParameters.values4;
        }
        values4.setText("           ");
        //temporary change the cell size to width = 1 impact on erosion value
        SharedParameters.BARWIDTH = 1;
        surfaceArray = new SurfaceBar[SharedParameters.ROWS][SharedParameters.COLUMNS];
        eCanv.setGridSize(SharedParameters.ROWS, SharedParameters.COLUMNS);
        resetSnapshots(eCanv25P);
        resetSnapshots(eCanv50P);
        resetSnapshots(eCanv75P);
        resetSnapshots(eCanv100P);


        double y1;
        double incrvertical = 0;

        firstTime = true;
        SharedParameters.CARRYINGCAPACITY = 0;
        SharedParameters.STEPCOUNTER = 0;
        //steps = 0;
        intervalStep = 1;
        intervalCounter = 0;
        initialize = true;
        running = false;

        //set iteration counter to zero to start
        SharedParameters.ITERATIONCOUNTER = 0;
        SharedParameters.BARSPROCESSED = 0;

        // create the surface
        for (int j = 0; j < SharedParameters.ROWS; j++) {
            for (int i = 0; i < SharedParameters.COLUMNS; i++) {
                y1 = BARMINHEIGHT;
                double slope = BARMINHEIGHT * SharedParameters.SLOPE * j / SharedParameters.ROWS;
                //first row only
                if (j == 0) {
                    surfaceArray[j][i] = new SurfaceBar(y1 - 1, slope);
                } else {
                    surfaceArray[j][i] = new SurfaceBar(y1, slope);
                }
                surfaceArray[j][i].setfinalHeight();
                resetCanvasSlope(j, i, surfaceArray[j][i].getsurfacefinalHeight());
            }//end for i
            incrvertical = incrvertical - 1;
        }//end for j
        setColors();
        resetIntervals(ei, rowFlag);
        resetIntervals(eis, rowFlag);
        resetIntervals(ec, rowFlag);
        resetIntervals(ecs, rowFlag);
        rowFlag = false;
        resetIntervals(er, false);
        resetIntervals(ers, rowFlag);
        rowFlag = true;
        resetIntervalArrays();
        resetHypsometric();
    }//end reset

    /**
     * ********************************************************************************************
     * when slope is resetted from applet
     * *********************************************************************************************
     */
    private void resetSlope() {
        SharedParameters.OLDSLOPE = SharedParameters.SLOPE;
        for (int j = 0; j < SharedParameters.ROWS; j++) {
            double newSlope = BARMINHEIGHT * SharedParameters.SLOPE * j / SharedParameters.ROWS;
            for (int i = 0; i < SharedParameters.COLUMNS; i++) {
                surfaceArray[j][i].setSlope(newSlope);
                surfaceArray[j][i].setfinalHeight();
                resetCanvasSlope(j, i, surfaceArray[j][i].getsurfacefinalHeight());
            }
        }
    }//end of resetSlope()

    /**
     * ********************************************************************************************
     * to reset slope in canvas
     * *********************************************************************************************
     */
    private void resetCanvasSlope(int j, int i, double height) {
        eCanv.setDataHeight(j, i, height);
        eCanv25P.setDataHeight(j, i, height);
        eCanv50P.setDataHeight(j, i, height);
        eCanv75P.setDataHeight(j, i, height);
        eCanv100P.setDataHeight(j, i, height);
    }//end of reset canvas slope

    /**
     * ********************************************************************************************
     * this function gets an x,y random location in the array
     * *********************************************************************************************
     */
    private void getStartingCell() {
        SharedParameters.ROUTINESTARTED = true;

        double currentHeight = 0;
        for (int x = 0; x < SharedParameters.COLUMNS; x++){
            for (int y = 0; y < SharedParameters.ROWS; y++){
                if (surfaceArray[y][x].getsurfacefinalHeight() > currentHeight){
                    cellY = y;
                    cellX = x;
                    currentHeight = surfaceArray[y][x].getsurfacefinalHeight();
                }

            }
        }
        SharedParameters.STEPCOUNTER += 1;

    }//end getstarting Cell

    /**
     * ********************************************************************************************
     * get erosion according to parameters from applet
     * *********************************************************************************************
     */
    void getErosionValue() {
        basicErosion = 0;
        //to get erosion value if no break point has been selected
        if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
            //if random erosion over basic is selected
            if (SharedParameters.RANDEROSION) {
                //get a the random value
                basicErosion = SharedParameters.RANDVALUE;
            } else {
                basicErosion = SharedParameters.EROSION;
            }
            randXLeft = randXRight = randYBottom = randYTop = -1;
        } else {
            SharedParameters.EROSION = 0;
            randXLeft = SharedParameters.XRANDLEFT;
            randXRight = SharedParameters.XRANDRIGHT;
            randYBottom = SharedParameters.YRANDBOTTOM;
            randYTop = SharedParameters.YRANDTOP;
        }//end of erosion checking
    }//end of getErosionValue()

    /**
     * ********************************************************************************************
     * get erosion according to parameters from applet
     * *********************************************************************************************
     */
    void reverseGetErosionValue() {
        basicErosion = 0;
        //to get erosion value if no break point has been selected
        if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
            //if random erosion over basic is selected
            if (SharedParameters.RANDEROSION) {
                //get a the random value
                basicErosion = SharedParameters.RANDVALUE * -1;
            } else {
                basicErosion = SharedParameters.EROSION * -1;
            }
            randXLeft = randXRight = randYBottom = randYTop = -1;
        } else {
            SharedParameters.EROSION = 0;
            randXLeft = SharedParameters.XRANDLEFT;
            randXRight = SharedParameters.XRANDRIGHT;
            randYBottom = SharedParameters.YRANDBOTTOM;
            randYTop = SharedParameters.YRANDTOP;
        }//end of erosion checking
    }//end of getErosionValue()

    /**
     * ********************************************************************************************
     * to apply tectonics
     * *********************************************************************************************
     */
    void applyTectonics() {
        double tectbottom, tecttop, tectleft, tectright;
        //calculate erosion of bar based on basic erosion (uniform or random)
        //check y break point
        if (SharedParameters.TECTONICSYPOINT > -1 && SharedParameters.TECTONICSYPOINT <= SharedParameters.ROWS) {
            for (int trows = 0; trows < SharedParameters.ROWS; trows++) {
                for (int tcolumns = 0; tcolumns < SharedParameters.COLUMNS; tcolumns++) {
                    if (trows < SharedParameters.TECTONICSYPOINT) {
                        tectbottom = SharedParameters.TECTONICSYBOTTOM;
                        surfaceArray[trows][tcolumns].setTectonics(tectbottom);
                    }
                    if (trows >= SharedParameters.TECTONICSYPOINT) {
                        tecttop = SharedParameters.TECTONICSYTOP;
                        surfaceArray[trows][tcolumns].setTectonics(tecttop);
                    }
                    surfaceArray[trows][tcolumns].setfinalHeight();
                }
            }
        }//end for break point at y
        //check x break point
        if (SharedParameters.TECTONICSXPOINT > -1 && SharedParameters.TECTONICSXPOINT < SharedParameters.COLUMNS) {
            for (int trows = 0; trows < SharedParameters.ROWS; trows++) {
                for (int tcolumns = 0; tcolumns < SharedParameters.COLUMNS; tcolumns++) {
                    if (tcolumns < SharedParameters.TECTONICSXPOINT) {
                        tectleft = SharedParameters.TECTONICSXLEFT;
                        surfaceArray[trows][tcolumns].setTectonics(tectleft);
                    }
                    if (tcolumns >= SharedParameters.TECTONICSXPOINT) {
                        tectright = SharedParameters.TECTONICSXRIGHT;
                        surfaceArray[trows][tcolumns].setTectonics(tectright);
                    }
                    surfaceArray[trows][tcolumns].setfinalHeight();
                }
            }
        }//end for break point at y
    }//end of applyTectonics

    /**
     * ********************************************************************************************
     * this function gets the cells surrounding the randomly selected one
     * *********************************************************************************************
     */
    double gradientSigma = 0;
    int thisGeneration = 0;
    double squareRootTwo = Math.sqrt(2.00);
    private void getSurroundingCells() {

        SharedParameters.BARSPROCESSED += 1;
        keepGoing = true;
        cleanup();

        if (SharedParameters.ITERATIONCOUNTER > 0) {
            if ((SharedParameters.BARSPROCESSED % 7500) == 0 || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME) {
                values4.setText("" + SharedParameters.ITERATIONCOUNTER);
//                steps = 0;
            }
            newX = newY = 0;
            incrIndex = 0;
            incrDiffusionIndex = 0;
            //get 3x3 grid and closest neighbors
            //get 3x3 grid in vectors for later analysis
            double currentHeight = surfaceArray[cellY][cellX].getsurfacefinalHeight();

            try {
                if (cellY - 1 >= 0 && cellY - 1 < SharedParameters.ROWS && cellX - 1 >= 0 && cellX - 1 < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY - 1;
                    XCOORDARRAY[incrIndex] = cellX - 1;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add(squareRootTwo);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                }
                if (cellY - 1 >= 0 && cellY - 1 < SharedParameters.ROWS && cellX >= 0 && cellX < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY - 1;
                    XCOORDARRAY[incrIndex] = cellX;
                    XCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellY - 1;
                    YCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellX;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                       distanceAL.add((double) 1);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (cellY - 1 >= 0 && cellY - 1 < SharedParameters.ROWS && cellX + 1 >= 0 && cellX + 1 < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY - 1;
                    XCOORDARRAY[incrIndex] = cellX + 1;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add(squareRootTwo);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                }
                if (cellY >= 0 && cellY < SharedParameters.ROWS && cellX - 1 >= 0 && cellX - 1 < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY;
                    XCOORDARRAY[incrIndex] = cellX - 1;
                    XCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellY;
                    YCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellX - 1;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add((double) 1);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (cellY >= 0 && cellY < SharedParameters.ROWS && cellX >= 0 && cellX < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY;
                    XCOORDARRAY[incrIndex] = cellX;
                    incrIndex++;
                }
                if (cellY >= 0 && cellY < SharedParameters.ROWS && cellX + 1 >= 0 && cellX + 1 < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY;
                    XCOORDARRAY[incrIndex] = cellX + 1;
                    XCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellY;
                    YCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellX + 1;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add((double) 1);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (cellY + 1 >= 0 && cellY + 1 < SharedParameters.ROWS && cellX - 1 >= 0 && cellX - 1 < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY + 1;
                    XCOORDARRAY[incrIndex] = cellX - 1;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add(squareRootTwo);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                }
                if (cellY + 1 >= 0 && cellY + 1 < SharedParameters.ROWS && cellX >= 0 && cellX < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY + 1;
                    XCOORDARRAY[incrIndex] = cellX;
                    XCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellY + 1;
                    YCOORDDIFFUSIONARRAY[incrDiffusionIndex] = cellX;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add((double) 1);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (cellY + 1 >= 0 && cellY + 1 < SharedParameters.ROWS && cellX + 1 >= 0 && cellX + 1 < SharedParameters.COLUMNS) {
                    YCOORDARRAY[incrIndex] = cellY + 1;
                    XCOORDARRAY[incrIndex] = cellX + 1;
                    if(surfaceArray[YCOORDARRAY[incrIndex]][XCOORDARRAY[incrIndex]].getsurfacefinalHeight() < currentHeight) {
                        lowerCellsX.add(XCOORDARRAY[incrIndex]);
                        lowerCellsY.add(YCOORDARRAY[incrIndex]);
                        distanceAL.add(squareRootTwo);
/*                        sourceY.add(cellY);
                        sourceX.add(cellX);*/
                        thisGeneration++;
                    }
                    incrIndex++;
                }//end of getting 3x3 grid in vectors

            } catch (ArrayIndexOutOfBoundsException aioobe) {
            }
            double gradient;
            for (int x = 0; x < thisGeneration; x++) {
                gradientAL.add((surfaceArray[lowerCellsY.get(lowerCellsY.size() - 1)]
                        [lowerCellsX.get(lowerCellsX.size() - 1)].getsurfacefinalHeight() - currentHeight) / distanceAL.get(0));
                distanceAL.remove(0);
            }

            for (int x = 0; x < thisGeneration; x++) {
                gradientSigma += gradientAL.get(x);
            }
            for (int x = 0; x < thisGeneration; x++) {
                forceAL.add(gradientAL.get(x) / gradientSigma);
            }
            distanceAL.clear();
            gradientSigmaAL.clear();
            gradientAL.clear();
            thisGeneration = 0;
            gradientSigma = 0;


        }//end of iteration and thread checking
    }//end of getsurroundingCells

    /**
     * ********************************************************************************************
     * apply diffusion when first precipiton falls
     * *********************************************************************************************
     */
    void applyDiffusion() {
        //get target coordinates in local variables
        int randrow1 = cellY;
        int randcolumn1 = cellX;
        int getsedimentx;
        int getsedimenty;
        double sedimentTaken = 0;

        for (int t = 0; t < incrDiffusionIndex; t++) {
            //extract each one of the closest neighbors from vector (4 bars)
            int tempx = XCOORDDIFFUSIONARRAY[t];
            int tempy = YCOORDDIFFUSIONARRAY[t];
            double diffusionPower;
            double possibleDiffusion = 1;
            double currentDiffusion = 0;
            if (diffusesedImentX > -1 && diffusesedImentY > -1) {
                //compare it to with target cell - decide which cells dump and get sediment
                if (surfaceArray[tempx][tempy].getsurfacefinalHeight() > surfaceArray[randrow1][randcolumn1].getsurfacefinalHeight()) {
                    diffusesedImentX = tempx;
                    diffusesedImentY = tempy;
                    getsedimentx = randrow1;
                    getsedimenty = randcolumn1;
                } else {
                    diffusesedImentX = randrow1;
                    diffusesedImentY = randcolumn1;
                    getsedimentx = tempx;
                    getsedimenty = tempy;
                }
                //determine the height difference between the two cells
                double heightDifference = surfaceArray[diffusesedImentX][diffusesedImentY].getsurfacefinalHeight() - surfaceArray[getsedimentx][getsedimenty].getsurfacefinalHeight();
                //if neighbor has sediment
                if (surfaceArray[diffusesedImentX][diffusesedImentY].getSediment() > 0) {
                    diffusionPower = heightDifference / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                    //check for basic erosion rate
                    if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                        possibleDiffusion = basicErosion * 2 * diffusionPower;
                    }
                    //check for erosion rate with break at x
                    if (SharedParameters.XPOINT >= 0 && randXLeft >= 0 && randcolumn1 <= SharedParameters.XPOINT) {
                        possibleDiffusion = randXLeft * 2 * diffusionPower;
                    }
                    if (SharedParameters.XPOINT >= 0 && randXRight >= 0 && randcolumn1 > SharedParameters.XPOINT) {
                        possibleDiffusion = randXRight * 2 * diffusionPower;
                    }
                    //check for erosion rate with break at y
                    if (SharedParameters.YPOINT >= 0 && randYBottom >= 0 && randrow1 <= SharedParameters.YPOINT) {
                        possibleDiffusion = randYBottom * 2 * diffusionPower;
                    }
                    if (SharedParameters.YPOINT >= 0 && randYTop >= 0 && randrow1 > SharedParameters.YPOINT) {
                        possibleDiffusion = randYTop * 2 * diffusionPower;
                    }
                    //if sediment is not enough
                    if (possibleDiffusion > surfaceArray[diffusesedImentX][diffusesedImentY].getSediment()) {
                        currentDiffusion = surfaceArray[diffusesedImentX][diffusesedImentY].getSediment();
                        possibleDiffusion -= currentDiffusion;
                        sedimentTaken = surfaceArray[diffusesedImentX][diffusesedImentY].getSediment();
                        surfaceArray[diffusesedImentX][diffusesedImentY].setSediment(-sedimentTaken);
                    } else {
                        currentDiffusion = possibleDiffusion;
                    }
                }
                //if neighbor does not have sediment
                if ((surfaceArray[diffusesedImentX][diffusesedImentY].getSediment() == 0) && (currentDiffusion != possibleDiffusion)) {
                    diffusionPower = (heightDifference - currentDiffusion) / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                    //check for basic erosion rate
                    if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                        possibleDiffusion = basicErosion * diffusionPower;
                    }
                    //check for erosion rate with break at x
                    if (SharedParameters.XPOINT >= 0 && randXLeft >= 0 && randcolumn1 <= SharedParameters.XPOINT) {
                        possibleDiffusion = randXLeft * diffusionPower;
                    }
                    if (SharedParameters.XPOINT >= 0 && randXRight >= 0 && randcolumn1 > SharedParameters.XPOINT) {
                        possibleDiffusion = randXRight * diffusionPower;
                    }
                    //check for erosion rate with break at y
                    if (SharedParameters.YPOINT >= 0 && randYBottom >= 0 && randrow1 <= SharedParameters.YPOINT) {
                        possibleDiffusion = randYBottom * diffusionPower;
                    }
                    if (SharedParameters.YPOINT >= 0 && randYTop >= 0 && randrow1 > SharedParameters.YPOINT) {
                        possibleDiffusion = randYTop * diffusionPower;
                    }
                    currentDiffusion = currentDiffusion + possibleDiffusion;
                }
                //only for front row - apply only ten percent
                surfaceArray[diffusesedImentX][diffusesedImentY].setErosion(currentDiffusion - sedimentTaken);
                if (randrow1 == 0) {
                    currentDiffusion = currentDiffusion * 0.10;
                }
                surfaceArray[getsedimentx][getsedimenty].setSediment(currentDiffusion);
            }
        }//end of for loop
        for (int t = 0; t < incrIndex; t++) {
            //fix height after diffusion
            surfaceArray[YCOORDARRAY[t]][XCOORDARRAY[t]].setfinalHeight();
        }
    }//end of applyDiffusion

    /**
     * ********************************************************************************************
     * search for lowest cell in 3x3 grid
     * *********************************************************************************************
     */

    void searchlowestCell() {
        //to do certain number of iterations
        int tempx = -1;
        int tempy = -1;

        int currentMinHeight = 0;
        int xcoord1, ycoord1, xcoord2, ycoord2;

        //find lowest height in array
        int nextHeight;
        for (nextHeight = currentMinHeight + 1; nextHeight < incrIndex; nextHeight++) {
            double bar2height, bar1height;
            xcoord2 = YCOORDARRAY[nextHeight];
            ycoord2 = XCOORDARRAY[nextHeight];
            xcoord1 = YCOORDARRAY[currentMinHeight];
            ycoord1 = XCOORDARRAY[currentMinHeight];
            bar2height = surfaceArray[xcoord2][ycoord2].getsurfacefinalHeight();
            bar1height = surfaceArray[xcoord1][ycoord1].getsurfacefinalHeight();

            //if next bar is lower than current, change value of index
            if (bar2height < bar1height) {
                //gradiant.add(currentMinHeight);
                currentMinHeight = nextHeight;
            }

        }//end of for loop
        //this is to check if there is any lowest cell
        if (YCOORDARRAY[currentMinHeight] >= 0 && XCOORDARRAY[currentMinHeight] >= 0) {
            newX = YCOORDARRAY[currentMinHeight];
            newY = XCOORDARRAY[currentMinHeight];
            sourceY.add(newX);
            sourceX.add(newY);
            //this is to check if the lowest now was the lowest before in order to avoid an endless loop
            if (newX == SharedParameters.OLDX && newY == SharedParameters.OLDY) {
                cleanup();
                firstTime = true;
                keepGoing = false;
                return;
            } else {
                SharedParameters.OLDX = cellY;
                SharedParameters.OLDY = cellX;
            }
            //this is to check if the lowest now and lowest before are the same height
            if (surfaceArray[newX][newY].getsurfacefinalHeight() == surfaceArray[cellY][cellX].getsurfacefinalHeight()) {
                firstTime = true;
                keepGoing = false;
            }
        } else {
            firstTime = true;
            keepGoing = false;
        }//end if anybars
    }//end of searchlowestCell()

    void reverseSearchLowestCell() {
        //to do certain number of iterations
        int currentMinHeight = 0;
        int xcoord1, ycoord1, xcoord2, ycoord2;

        //find lowest height in array
        int nextHeight;
        for (nextHeight = currentMinHeight + 1; nextHeight < incrIndex; nextHeight++) {
            double bar2height, bar1height;
            xcoord2 = YCOORDARRAY[nextHeight];
            ycoord2 = XCOORDARRAY[nextHeight];
            xcoord1 = YCOORDARRAY[currentMinHeight];
            ycoord1 = XCOORDARRAY[currentMinHeight];
            bar2height = surfaceArray[xcoord2][ycoord2].getsurfacefinalHeight();
            bar1height = surfaceArray[xcoord1][ycoord1].getsurfacefinalHeight();

            //if next bar is lower than current, change value of index
            if (bar2height > bar1height) {     /// flipped
                currentMinHeight = nextHeight;
            }
        }//end of for loop

        //this is to check if there is any lowest cell
        if (YCOORDARRAY[currentMinHeight] >= 0 && XCOORDARRAY[currentMinHeight] >= 0) {
            newX = YCOORDARRAY[currentMinHeight];
            newY = XCOORDARRAY[currentMinHeight];
            //this is to check if the lowest now was the lowest before in order to avoid an endless loop
            if (newX == SharedParameters.OLDX && newY == SharedParameters.OLDY) {
                cleanup();
                firstTime = true;
                keepGoing = false;
                return;
            } else {
                SharedParameters.OLDX = cellY;
                SharedParameters.OLDY = cellX;
            }
            //this is to check if the lowest now and lowest before are the same height
            if (surfaceArray[newX][newY].getsurfacefinalHeight() == surfaceArray[cellY][cellX].getsurfacefinalHeight()) {
                firstTime = true;
                keepGoing = false;
            }
        } else {
            firstTime = true;
            keepGoing = false;
        }//end if anybars
    }//end of searchlowestCell()

    /**
     * ********************************************************************************************
     * apply the erosion according to erodibility parameters
     * *********************************************************************************************
     */
    void calculateErosion() {
        //to do certain number of iterations
        if (keepGoing) {
            int randx2 = cellY;
            int randy2 = cellX;
            //get the heights of both bars and calculate height difference
            double heightDiff;
            SharedParameters.HEIGHTDIFFERENCE = heightDiff = surfaceArray[randx2][randy2].getsurfacefinalHeight() - surfaceArray[newX][newY].getsurfacefinalHeight();
            double erosionPower;
            double possibleErosion = 1 * forceAL.get(0);
            double currentErosion = 0;
            double sedimentTaken = 0;
            if (surfaceArray[randx2][randy2].getSediment() > 0) {
                erosionPower = heightDiff / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = (basicErosion * 2) * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = (randYTop * 2) * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = (randYBottom * 2) * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = (randXRight * 2) * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = (randXLeft * 2) * erosionPower;
                }
                if (possibleErosion > surfaceArray[randx2][randy2].getSediment()) {
                    currentErosion = surfaceArray[randx2][randy2].getSediment();
                    possibleErosion -= currentErosion;
                    sedimentTaken = surfaceArray[randx2][randy2].getSediment();
                    surfaceArray[randx2][randy2].setSediment(-sedimentTaken);
                } else {
                    currentErosion = possibleErosion;
                }
            }
            if ((surfaceArray[randx2][randy2].getSediment() == 0) && (currentErosion != possibleErosion)) {
                erosionPower = (heightDiff - currentErosion) / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = basicErosion * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = randYTop * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = randYBottom * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = randXRight * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = randXLeft * erosionPower;
                }
                currentErosion = currentErosion + possibleErosion;
            }
            //only for front row
            surfaceArray[randx2][randy2].setErosion(currentErosion - sedimentTaken);
            if (newX == 0) {
                currentErosion = currentErosion * 0.10;
            }
            surfaceArray[newX][newY].setSediment(currentErosion);
            surfaceArray[randx2][randy2].setfinalHeight();
            surfaceArray[newX][newY].setfinalHeight();
        }
    }//end of applyErosion


    void calculateErosion(int randx2, int randy2, int newY, int newX) {
        //to do certain number of iterations
        if (keepGoing) {
/*            int randx2 = cellY;
            int randy2 = cellX;*/
            //get the heights of both bars and calculate height difference
            double heightDiff;
            SharedParameters.HEIGHTDIFFERENCE = heightDiff = surfaceArray[randx2][randy2].getsurfacefinalHeight() - surfaceArray[newX][newY].getsurfacefinalHeight();
            double erosionPower;
            double possibleErosion = 1;
            double currentErosion = 0;
            double sedimentTaken = 0;
            if (surfaceArray[randx2][randy2].getSediment() > 0) {
                erosionPower = heightDiff / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = (basicErosion * 2) * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = (randYTop * 2) * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = (randYBottom * 2) * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = (randXRight * 2) * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = (randXLeft * 2) * erosionPower;
                }
                if (possibleErosion > surfaceArray[randx2][randy2].getSediment()) {
                    currentErosion = surfaceArray[randx2][randy2].getSediment();
                    possibleErosion -= currentErosion;
                    sedimentTaken = surfaceArray[randx2][randy2].getSediment();
                    surfaceArray[randx2][randy2].setSediment(-sedimentTaken);
                } else {
                    currentErosion = possibleErosion;
                }
            }
            if ((surfaceArray[randx2][randy2].getSediment() == 0) && (currentErosion != possibleErosion)) {
                erosionPower = (heightDiff - currentErosion) / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = basicErosion * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = randYTop * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = randYBottom * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = randXRight * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = randXLeft * erosionPower;
                }
                currentErosion = currentErosion + possibleErosion;
            }
            //only for front row
            surfaceArray[randx2][randy2].setErosion(currentErosion - sedimentTaken);
            if (newX == 0) {
                currentErosion = currentErosion * 0.10;
            }
            surfaceArray[newX][newY].setSediment(currentErosion);
            surfaceArray[randx2][randy2].setfinalHeight();
            surfaceArray[newX][newY].setfinalHeight();
            sourceX.remove(0);
            sourceY.remove(0);
            lowerCellsX.remove(0);
            lowerCellsY.remove(0);
        }
    }//end of applyErosion
    /**
     * ********************************************************************************************
     * apply the erosion according to erodibility parameters
     * *********************************************************************************************
     */
    void reverseCalculateErosion() {
        //to do certain number of iterations
        if (keepGoing) {
            int randx2 = cellY;
            int randy2 = cellX;
            //get the heights of both bars and calculate height difference
            double heightDiff;
            SharedParameters.HEIGHTDIFFERENCE = heightDiff = surfaceArray[randx2][randy2].getsurfacefinalHeight() - surfaceArray[newX][newY].getsurfacefinalHeight();
            double erosionPower;
            double possibleErosion = 1;
            double currentErosion = 0;
            double sedimentTaken = 0;
            if (surfaceArray[randx2][randy2].getSediment() > 0) {
                erosionPower = heightDiff / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = (basicErosion * 2) * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = (randYTop * 2) * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = (randYBottom * 2) * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = (randXRight * 2) * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = (randXLeft * 2) * erosionPower;
                }

                if (possibleErosion > surfaceArray[randx2][randy2].getSediment()) {
                    currentErosion = surfaceArray[randx2][randy2].getSediment();
                    possibleErosion -= currentErosion;
                    sedimentTaken = surfaceArray[randx2][randy2].getSediment();
                    surfaceArray[randx2][randy2].setSediment(+sedimentTaken);
                } else {
                    currentErosion = possibleErosion;
                }
            }
            if ((surfaceArray[randx2][randy2].getSediment() == 0) && (currentErosion != possibleErosion)) {
                erosionPower = (heightDiff - currentErosion) / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = basicErosion * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = randYTop * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = randYBottom * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = randXRight * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = randXLeft * erosionPower;
                }
                currentErosion = currentErosion + possibleErosion;
            }
            //only for front row
            surfaceArray[randx2][randy2].setErosion(currentErosion + sedimentTaken);
            if (newX == 0) {
                currentErosion = currentErosion * 0.10;
            }
            surfaceArray[newX][newY].setSediment(currentErosion);
            surfaceArray[randx2][randy2].reverseSetFinalHeight();
            surfaceArray[newX][newY].reverseSetFinalHeight();
        }
    }//end of applyErosion

    /**
     * ********************************************************************************************
     * to reset interval arrays
     * *********************************************************************************************
     */
    private void resetIntervalArrays() {
        sumIntervalsSediment = new double[SharedParameters.ROWS];
        sumColumns = new double[SharedParameters.ROWS];
        sumColumnsBedrock = new double[SharedParameters.ROWS];
        averageIntervals = new double[SharedParameters.ROWS];
        averageIntervalsBedrock = new double[SharedParameters.ROWS];
        columnIntervals = new double[SharedParameters.ROWS];
        columnIntervalsBedrock = new double[SharedParameters.ROWS];
        columnSediment = new double[SharedParameters.ROWS];
        for (int i = 0; i < SharedParameters.ROWS; i++) {
            sumIntervalsSediment[i] = 0;
            sumColumns[i] = 0;
            sumColumnsBedrock[i] = 0;
            averageIntervals[i] = 0;
            averageIntervalsBedrock[i] = 0;
            columnIntervals[i] = 0;
            columnIntervalsBedrock[i] = 0;
            columnSediment[i] = 0;
        }
        rowIntervals = new double[SharedParameters.COLUMNS];
        rowIntervalsBedrock = new double[SharedParameters.COLUMNS];
        rowSediment = new double[SharedParameters.COLUMNS];
        for (int i = 0; i < SharedParameters.COLUMNS; i++) {
            rowIntervals[i] = 0;
            rowIntervalsBedrock[i] = 0;
            rowSediment[i] = 0;
        }
    }// end of reset interval arrays

    /**
     * ********************************************************************************************
     * to reset intervals
     * *********************************************************************************************
     */
    private void resetIntervals(ErosionIntervals eicopy, boolean rowflag) {
        eicopy.clearIntervals();
        if (rowflag) {
            eicopy.settings(SharedParameters.ROWS);
        } else {
            eicopy.settings(SharedParameters.COLUMNS);
        }
        resetIntervalsParms(eicopy);
    }// end of reset intervals

    /**
     * ********************************************************************************************
     * to reset intervals parameters
     * *********************************************************************************************
     */
    private void resetIntervalsParms(ErosionIntervals eicopy) {
//        double min = surfaceArray[0][0].getsurfacefinalHeight();
        double max = surfaceArray[SharedParameters.ROWS - 1][SharedParameters.COLUMNS - 1].getsurfacefinalHeight()
                + (SharedParameters.ENDTIME * SharedParameters.TECTONICSPERCENTAGE);
        eicopy.passParms(max);
        //eicopy.passMsg(msg);
    }// to reset graph parameters

    /**
     * ********************************************************************************************
     * to save intervals
     * *********************************************************************************************
     */
    void saveInterval() {
        int ndx = 0;
        for (int i = 0; i < SharedParameters.ROWS; i++) {
            for (int j = 0; j < SharedParameters.COLUMNS; j++) {
                sumColumns[ndx] += surfaceArray[i][j].getsurfacefinalHeight();
                sumColumnsBedrock[ndx] += surfaceArray[i][j].getBedrock();
                sumIntervalsSediment[ndx] += (surfaceArray[i][j].getsurfacefinalHeight() - surfaceArray[i][j].getBedrock());
            }
            sumColumns[ndx] = sumColumns[ndx] / SharedParameters.COLUMNS;
            sumColumnsBedrock[ndx] = sumColumnsBedrock[ndx] / SharedParameters.COLUMNS;
            sumIntervalsSediment[ndx] = sumIntervalsSediment[ndx] / SharedParameters.COLUMNS;
            ndx += 1;
        }
        if (intervalCounter > 0) {
            if (SharedParameters.AVGVISIBLE) {
                for (int i = 0; i < SharedParameters.ROWS; i++) {
                    averageIntervals[i] = sumColumns[i];
                    averageIntervalsBedrock[i] = sumColumnsBedrock[i];
                }
                drawIntervals(ei, averageIntervals, averageIntervalsBedrock);
                drawIntervals(eis, sumIntervalsSediment);
            }
            if (SharedParameters.COLVISIBLE) {
                for (int i = 0; i < SharedParameters.ROWS; i++) {
                    columnIntervals[i] = surfaceArray[i][SharedParameters.COLINTERVALS - 1].getsurfacefinalHeight();
                    columnIntervalsBedrock[i] = surfaceArray[i][SharedParameters.COLINTERVALS - 1].getBedrock();
                    columnSediment[i] = (surfaceArray[i][SharedParameters.COLINTERVALS - 1].getsurfacefinalHeight() - surfaceArray[i][SharedParameters.COLINTERVALS - 1].getBedrock());
                }
                drawIntervals(ec, columnIntervals, columnIntervalsBedrock);
                drawIntervals(ecs, columnSediment);
            }
            if (SharedParameters.ROWVISIBLE) {
                for (int i = 0; i < SharedParameters.COLUMNS; i++) {
                    rowIntervals[i] = surfaceArray[SharedParameters.ROWINTERVALS - 1][i].getsurfacefinalHeight();
                    rowIntervalsBedrock[i] = surfaceArray[SharedParameters.ROWINTERVALS - 1][i].getBedrock();
                    rowSediment[i] = (surfaceArray[SharedParameters.ROWINTERVALS - 1][i].getsurfacefinalHeight() - surfaceArray[SharedParameters.ROWINTERVALS - 1][i].getBedrock());
                }
                drawIntervals(er, rowIntervals, rowIntervalsBedrock);
                drawIntervals(ers, rowSediment);
            }
        }
        Thread.yield();
    }//end of save intervals

    /**
     * ********************************************************************************************
     * to draw intervals with two values
     * *********************************************************************************************
     */
    void drawIntervals(ErosionIntervals eicopy, double arrayValues1[], double arrayValues2[]) {
        eicopy.startIntervals(arrayValues1, arrayValues2);
        eicopy.refreshGraph();
    }//end of draw intervals


    /**
     * ********************************************************************************************
     * to draw intervals with one value
     * *********************************************************************************************
     */
    void drawIntervals(ErosionIntervals eicopy, double arrayValues1[]) {
        eicopy.startIntervals(arrayValues1);
        eicopy.refreshGraph();
    }//end of draw intervals

    /**
     * ********************************************************************************************
     * to reset hypsometric curve graph
     * *********************************************************************************************
     */
    void resetHypsometric() {
        relativeHeight = new double[HYPSOINTERVAL];
        relativeArea = new double[HYPSOINTERVAL];
        for (int i = 0; i < HYPSOINTERVAL; i++) {
            relativeHeight[i] = 1;
            relativeArea[i] = 1;
        }
        eh.clearHypsometric();
        eh.refreshHypsometric();
    }

    /**
     * ********************************************************************************************
     * to calculate the hypsometric curve
     * *********************************************************************************************
     */
    void calculateHypsometric() {
        if (SharedParameters.HYPSOMETRIC && intervalCounter > 0) {
            double h = surfaceArray[0][0].getsurfacefinalHeight();
            double H = 0;
            int aCounter = 0;

            //calculate little h
            for (int i = 0; i < SharedParameters.ROWS; i++) {
                for (int j = 0; j < SharedParameters.COLUMNS; j++) {
                    double htemp = surfaceArray[i][j].getsurfacefinalHeight();
                    if (htemp < h) {
                        h = htemp;
                    }
                }
            }//end of for loop
            //calculate big H
            for (int i = 0; i < SharedParameters.ROWS; i++) {
                for (int j = 0; j < SharedParameters.COLUMNS; j++) {
                    double Htemp = surfaceArray[i][j].getsurfacefinalHeight();
                    if (Htemp > H) {
                        H = Htemp;
                    }
                }//end of for loop
            }
            //calculate little a
            int ndx1 = 0;
            for (double ndx = 0.0; ndx <= 1.0; ndx += 0.1) {
                relativeHeight[ndx1] = ndx;
                for (int i = 0; i < SharedParameters.ROWS; i++) {
                    for (int j = 0; j < SharedParameters.COLUMNS; j++) {
                        double atemp = surfaceArray[i][j].getsurfacefinalHeight();
                        if (atemp > (ndx * (H - h)) + h) {
                            aCounter++;
                        }
                    }
                }
                relativeArea[ndx1] = (double) aCounter / (SharedParameters.COLUMNS * SharedParameters.ROWS);
                ndx1++;
                aCounter = 0;
            }//end of for loop
            eh.startHypsometric(relativeHeight, relativeArea);
            Thread.yield();
        }//end of calculation conditions
        eh.refreshHypsometric();
    }//end of calculate Hypsometric


    private void setColors() {
        //scale of colors ranging from blue to orange
        //blue meaning the lowest and orange meaning the highest
        //implementation of 4 color areas
        double lowestBar = BARMINHEIGHT;
        double highestBar = 0;
        for (int i = 0; i < SharedParameters.ROWS; i++) {
            for (int j = 0; j < SharedParameters.COLUMNS; j++) {
                double bar1 = surfaceArray[i][j].getsurfacefinalHeight();
                if (bar1 > highestBar) {
                    highestBar = bar1;
                }
            }//end of for loop
        }
        double difference = highestBar - lowestBar;
        double interpolation = difference / 1023;
        int colorIndex;
        for (int x = 0; x < SharedParameters.ROWS; x++) {
            for (int y = 0; y < SharedParameters.COLUMNS; y++) {
                colorIndex = (int) ((surfaceArray[x][y].getsurfacefinalHeight() - lowestBar) / interpolation);
                if (colorIndex < 0) {
                    colorIndex = 0;
                }
                if (colorIndex > 1022) {
                    colorIndex = 1022;
                }
                eCanv.setDataHeight(x, y, surfaceArray[x][y].getsurfacefinalHeight());
                eCanv.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));

                //set the snapshots at their corresponding times
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4)) {
                    eCanv25P.setDataHeight(x, y, surfaceArray[x][y].getsurfacefinalHeight());
                    eCanv25P.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 2)) {
                    eCanv50P.setDataHeight(x, y, surfaceArray[x][y].getsurfacefinalHeight());
                    eCanv50P.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 3)) {
                    eCanv75P.setDataHeight(x, y, surfaceArray[x][y].getsurfacefinalHeight());
                    eCanv75P.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER < SharedParameters.ENDTIME - 5)) {
                    eCanv100P.setDataHeight(x, y, surfaceArray[x][y].getsurfacefinalHeight());
                    eCanv100P.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
            }
        }
    }//end setColors

    /**
     * ********************************************************************************************
     * to reset the snapshot images everytime an iteration starts
     * *********************************************************************************************
     */
    private void resetSnapshots(EROSIONCANVAS ecsnapshot) {
        ecsnapshot.setGridSize(SharedParameters.ROWS, SharedParameters.COLUMNS);
        ecsnapshot.setVisible(false);
    }//end of reset snapshots

    /**
     * ********************************************************************************************
     * to draw corresponding snapshot
     * *********************************************************************************************
     */
    private void setSnapshot(EROSIONCANVAS ecsnapshot) {
        ecsnapshot.setVisible(true);
        ecsnapshot.setWallColor();
        ecsnapshot.redraw();
    }// end of set snapshot


    /**
     * ********************************************************************************************
     * to start fresh
     * *********************************************************************************************
     */
    void cleanup() {
        for (int i = 0; i < 9; i++) {
            YCOORDARRAY[i] = -1;
            XCOORDARRAY[i] = -1;
        }
        for (int i = 0; i < 4; i++) {
            XCOORDDIFFUSIONARRAY[i] = -1;
            YCOORDDIFFUSIONARRAY[i] = -1;
        }
    }
}//end of ErosionSim

/**
 * ********************************************************************************************
 * CLASS:	      SurfaceBar
 * <p/>
 * FUNCTION:     This class represents the single unit that makes the topographic grid.
 * The class contains:
 * -Constructor:
 * SurfaceBar() = default constructor
 * SurfaceBar(double x, double y, double x1, double y1, double slope,
 * int color1, int color2, int color3) = constructor usually called
 * -Helping functions:
 * double calculateRoughness() = calculate bar roughness
 * double getRoughness() = get bar roughness
 * double getSlope() = returns the slope of the bar
 * void setSlope(double newSlope) = sets the height of the bar
 * void setSediment(double sediment) = sets the sediment
 * double getSediment() = returns the sediment
 * void setErosion(double erosion) = sets the erosion
 * double getErosion() = returns the erosion
 * void setTectonics(double tectonicvalue) = sets the tectonic value
 * double getTectonics() = gets the tectonic value
 * double getsurfacefinalHeight() = returns value after erosion and
 * sediment are applied
 * double gety() = returns the width of the bar
 * void sety(double newY) = sets the width of the bar
 * double getx1() = returns the xbasePosition of the bar
 * void setx1(double newx1) = sets the xbasePosition of the bar
 * double gety1() = returns the ybasePosition of the bar
 * (difference between y and y1 which gives the actual height)
 * void sety1(double newy1) = sets the ybasePosition of the bar
 * void setColor(int color1, int color2, int color3) = allows caller to set the color of the bar
 * int getColor1() = allows caller to get color1
 * int getColor2() = allows caller to get color2
 * int getColor3() = allows caller to get color3
 * <p/>
 * INPUT:        Nothing.
 * <p/>
 * OUTPUT:       It allows for the creation of a rectangular object that will be used to create a
 * graph.
 * *********************************************************************************************
 */
//Begin SurfaceBar
class SurfaceBar {
    private final double y1;
    private final double roughness;
    private double slope;
    private double erosion;
    private double sediment;
    private double tectonicvalue;
    private double finalHeight = 0;

    //default constructor

    //will be the constructor used most often, allows the caller to
    //set the height, width, and color of the bar
    SurfaceBar(double y1, double slope) {
        this.roughness = calculateRoughness();
        this.slope = slope;
        this.y1 = y1;

//        double width = SharedParameters.BARWIDTH;
        erosion = sediment = tectonicvalue = 0;
        finalHeight = y1;
    }

    //calculate bar roughness
    double calculateRoughness() {
        return -0.000005 + Math.random() * 0.000005;
    }

    //get bar roughness
    double getRoughness() {
        return roughness;
    }

    //returns the slope of the bar
    double getSlope() {
        return slope;
    }

    //sets the height of the bar
    void setSlope(double newSlope) {
        slope = newSlope;
    }

    //returns the sediment
    double getSediment() {
        return sediment;
    }

    //sets the sediment
    void setSediment(double sediment) {
        this.sediment += sediment;
    }

    //returns the erosion
    double getErosion() {
        return erosion;
    }

    //sets the erosion
    void setErosion(double erosion) {
        this.erosion += erosion;
    }

    //gets the tectonic value
    double getTectonics() {
        return tectonicvalue;
    }

    //sets the tectonic value
    void setTectonics(double tectonicvalue) {
        this.tectonicvalue += tectonicvalue;
    }

    //returns basic height
    double getbasicHeight() {
        return gety1() + getSlope() + getRoughness();
    }

    //returns basic height
    double getBedrock() {
        if (getErosion() > getSediment()) {
            return getsurfacefinalHeight() - getErosion() + getSediment();
        } else {
            return getbasicHeight();
        }
    }

    //set final height
    void setfinalHeight() {
        finalHeight = gety1() + getSlope() + getRoughness() + getSediment() - getErosion() + getTectonics();
    }

    //set final height
    void reverseSetFinalHeight() {
        finalHeight = gety1() + getSlope() + getRoughness() + getSediment() - getErosion() + getTectonics();
    }

    //returns value after erosion and sediment are applied
    double getsurfacefinalHeight() {
        return finalHeight;
    }

    //returns the ybasePosition of the bar (difference between y and y1 which gives the actual height)
    double gety1() {
        return y1;
    }

}//end SurfaceBar class