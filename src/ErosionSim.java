/***********************************************************************************************
 CLASS:	      ErosionSim

 FUNCTION:     ErosionSim is a class where the cellular automata algorithm is implemented.
 The class contains:
 -Constructor:
 *ErosionSim(SharedParameters s, ErosionCanvas e, ErosionCanvas e25p, ErosionCanvas e50p, ErosionCanvas e75p, ErosionCanvas e100p,
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
 *private void resetCanvasSlope(int j, int i, float height)
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
 *private void resetIntervals(ErosionIntervals eicopy, boolean rowflag)
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

class ErosionSim implements Runnable {
    private final ErosionCanvas ecanv;
    private final ErosionCanvas ecanv25p;
    private final ErosionCanvas ecanv50p;
    private final ErosionCanvas ecanv75p;
    private final ErosionCanvas ecanv100p;

    private final Thread thisthread;  // The simulation thread
    // variables for the surface object creation
    private static final int BARMINHEIGHT = 20;
    private static final int BARMINWIDTH = 2;
    private final int[] newcolor = new int[3];
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
    private final ErosionIntervals ei;
    private final ErosionIntervals eis;
    private final ErosionIntervals er;
    private final ErosionIntervals ers;
    private final ErosionIntervals ec;
    private final ErosionIntervals ecs;
    private boolean rowflag = true;
    //flags needed troughout the simulation
    private boolean keepgoing;
    private boolean firstTime;
    private final boolean uiSetup;
    private boolean running;
    private static boolean startNeeded = true;
    private final int[] xcoordArray = new int[9];
    private final int[] ycoordArray = new int[9];
    private final int[] xcoordDiffusionArray = new int[4];
    private final int[] ycoordDiffusionArray = new int[4];
    private int diffusesedimentx = 0;
    private int diffusesedimenty = 0;
    private int irand;
    private int jrand;
    private int steps = 0;
    //coord variables to be used later
    private int newx;
    private int newy;
    //double targetBar = 0;
    //double lowestBar = 0;
    //for messages
    private static Label values2 = new Label("");
    private static Label values4 = new Label("");
    private double randxleft;
    private double randxright;
    private double randybottom;
    private double randytop;
    private double basicErosion = 0;
    private final ErosionColors colors = new ErosionColors();
    private int incrIndex = 0;
    private int incrDiffusionIndex = 0;
    private static int intervalStep = 1;
    private static int intervalCounter = 0;
    private static final int nIntervals = 10;
    //for the hypsometric curve graph
    private final ErosionHypsometric eh;
    private static double[] relativeHeight;
    private static double[] relativeArea;
    private static final int HYPSOINTERVAL = 11;
    private static boolean initialize = true;

    private final TextArea msg;

    /**
     * ********************************************************************************************
     * Constructor
     * *********************************************************************************************
     */
    ErosionSim(SharedParameters s, ErosionCanvas e,
               ErosionCanvas e25p, ErosionCanvas e50p, ErosionCanvas e75p, ErosionCanvas e100p,
               ErosionIntervals i, ErosionIntervals is,
               ErosionIntervals c, ErosionIntervals cs,
               ErosionIntervals r, ErosionIntervals rs,
               ErosionHypsometric h) {
        ecanv = e;
        ecanv25p = e25p;
        ecanv50p = e50p;
        ecanv75p = e75p;
        ecanv100p = e100p;
        ei = i;
        eis = is;
        er = r;
        ers = rs;
        ec = c;
        ecs = cs;
        eh = h;
        uiSetup = false;
        thisthread = new Thread(this);
        // Set up the debugging frame
        Frame errs = new Frame("Debugging info");
        errs.setSize(500, 500);
        msg = new TextArea("");
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
        thisthread.setPriority(current.getPriority() - 1);
        thisthread.start();
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
            if (SharedParameters.ITERATIONCOUNTER >= SharedParameters.TOTALYEARS) {
                // Time to stop -- all done
                running = false;
                SharedParameters.ROUTINESTARTED = false;
                continue;
            }

            // To get it started
            if (firstTime) {
                SharedParameters.ITERATIONCOUNTER += 1;
                intervalStep++;
                // Call getStartingCell to get the first random bar
                getStartingCell();
            }
            // To re-calculate erosion values every time a change occurs
            if (SharedParameters.EROSIONNEEDED) {
                getErosionValue();
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
            if (firstTime && keepgoing) {
                applyDiffusion();
                firstTime = false;
            }

            //only apply these if getSurroundingCells was successful
            if (keepgoing) {
                //search for the lowest cell in the 3x3 grid
                searchlowestCell();

                //apply erosion according to erodibility parameters
                calculateErosion();

                //check capacity
                checkcarryingCapacity();

                //always calculate for intervals - it doesn't hurt
                if (intervalStep == (SharedParameters.ENDTIME / nIntervals) || initialize) {
                    saveInterval();
                    intervalStep = 1;
                    initialize = false;
                    calculateHypsometric();
                    intervalCounter++;
                }

                //set lowest bar as target
                jrand = newx;
                irand = newy;
            }
            // to make sure colors are set right for the snapshots as well as for the animation
            if ((SharedParameters.BARSPROCESSED % 7500) == 0 || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME || SharedParameters.ITERATIONCOUNTER == 0
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 2
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 3
                    || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME - 5) {
                setColors();
                ecanv.setWallColor();
            }
            ecanv.redraw();

            //work with the snapshots at their corresponding times
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4) {
                setSnapshot(ecanv25p);
            }
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 2) {
                setSnapshot(ecanv50p);
            }
            if (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 3) {
                setSnapshot(ecanv75p);
            }
            if (SharedParameters.ITERATIONCOUNTER == (SharedParameters.ENDTIME - 5)) {
                setSnapshot(ecanv100p);
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
            reset(); // Maybe break this out later to separate button?
            SharedParameters.STARTALLOVER = false;
        }
        if (SharedParameters.OLDSLOPE != SharedParameters.SLOPE) {
            resetSlope();
        }
        if (SharedParameters.RESETINTERVALSLEGEND) {
            resetIntervals(ei, rowflag);
            resetIntervals(eis, rowflag);
            resetIntervals(ec, rowflag);
            resetIntervals(ecs, rowflag);
            rowflag = false;
            resetIntervals(er, rowflag);
            resetIntervals(ers, rowflag);
            rowflag = true;
            resetIntervalArrays();
            SharedParameters.RESETINTERVALSLEGEND = false;
        }
        SharedParameters.ROUTINESTARTED = true;
        keepgoing = true;
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
        if (!uiSetup) {
            values2 = SharedParameters.values2;
            values4 = SharedParameters.values4;
        }
        values4.setText("           ");
        //temporary change the cell size to width = 1 impact on erosion value
        SharedParameters.BARWIDTH = 1;
        surfaceArray = new SurfaceBar[SharedParameters.ROWS][SharedParameters.COLUMNS];
        ecanv.setGridSize(SharedParameters.ROWS, SharedParameters.COLUMNS);
        ecanv.setViewHeight();
        resetSnapshots(ecanv25p);
        resetSnapshots(ecanv50p);
        resetSnapshots(ecanv75p);
        resetSnapshots(ecanv100p);

        double y1 = 0;
        double x1;
        double y;
        double x = y = x1 = y1 = 0;
        double incrhorizontal = 0;
        double incrvertical = incrhorizontal = 0;
        newcolor[0] = 220;
        newcolor[1] = 180;
        newcolor[2] = 0;

        firstTime = true;
        startNeeded = true;
        SharedParameters.CARRYINGCAPACITY = 0;
        SharedParameters.STEPCOUNTER = 0;
        steps = 0;
        intervalStep = 1;
        intervalCounter = 0;
        initialize = true;
        running = false;

        //set iteration counter to zero to start
        SharedParameters.ITERATIONCOUNTER = 0;
        SharedParameters.BARSPROCESSED = 0;
        SharedParameters.COLORCHANGE = 0;

        // create the surface
        for (int j = 0; j < SharedParameters.ROWS; j++) {
            for (int i = 0; i < SharedParameters.COLUMNS; i++) {
                incrhorizontal = (BARMINWIDTH) * i;
                x1 = BARMINWIDTH;
                y1 = BARMINHEIGHT;
                double slope = BARMINHEIGHT * SharedParameters.SLOPE * j / SharedParameters.ROWS;
                //first row only
                if (j == 0) {
                    surfaceArray[j][i] = new SurfaceBar(x, y, 1, y1 - 1, slope, newcolor[0], newcolor[1], newcolor[2]);
                } else {
                    surfaceArray[j][i] = new SurfaceBar(x, y, x1, y1, slope, newcolor[0], newcolor[1], newcolor[2]);
                }
                surfaceArray[j][i].setfinalHeight();
                resetCanvasSlope(j, i, (float) surfaceArray[j][i].getsurfacefinalHeight());
            }//end for i
            incrvertical = incrvertical - 1;
        }//end for j
        setColors();
        resetIntervals(ei, rowflag);
        resetIntervals(eis, rowflag);
        resetIntervals(ec, rowflag);
        resetIntervals(ecs, rowflag);
        rowflag = false;
        resetIntervals(er, rowflag);
        resetIntervals(ers, rowflag);
        rowflag = true;
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
                resetCanvasSlope(j, i, (float) surfaceArray[j][i].getsurfacefinalHeight());
            }
        }
    }//end of resetSlope()

    /**
     * ********************************************************************************************
     * to reset slope in canvas
     * *********************************************************************************************
     */
    private void resetCanvasSlope(int j, int i, float height) {
        ecanv.setDataHeight(j, i, height);
        ecanv25p.setDataHeight(j, i, height);
        ecanv50p.setDataHeight(j, i, height);
        ecanv75p.setDataHeight(j, i, height);
        ecanv100p.setDataHeight(j, i, height);
    }//end of reset canvas slope

    /**
     * ********************************************************************************************
     * this function gets an x,y random location in the array
     * *********************************************************************************************
     */
    private void getStartingCell() {
        SharedParameters.ROUTINESTARTED = true;
        //get random values for first target at the beginnig of an iteration
        //set index to 1+ off because of the walls
        jrand = (int) (0 + Math.random() * (SharedParameters.ROWS - 1));
        irand = (int) (0 + Math.random() * (SharedParameters.COLUMNS - 1));
        SharedParameters.STEPCOUNTER += 1;
    }//end getstarting Cell

    /**
     * ********************************************************************************************
     * get erosion according to parameters from applet
     * *********************************************************************************************
     */
    void getErosionValue() {
        double rand = 0;
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
            randxleft = randxright = randybottom = randytop = -1;
        } else {
            SharedParameters.EROSION = 0;
            randxleft = SharedParameters.XRANDLEFT;
            randxright = SharedParameters.XRANDRIGHT;
            randybottom = SharedParameters.YRANDBOTTOM;
            randytop = SharedParameters.YRANDTOP;
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
    private void getSurroundingCells() {

        SharedParameters.BARSPROCESSED += 1;
        keepgoing = true;
        cleanup();

        if (SharedParameters.ITERATIONCOUNTER > 0) {
            if ((SharedParameters.BARSPROCESSED % 7500) == 0 || SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME) {
                values4.setText("" + SharedParameters.ITERATIONCOUNTER);
                steps = 0;
            }
            newx = newy = 0;
            incrIndex = 0;
            incrDiffusionIndex = 0;
            //get 3x3 grid and closest neighbors
            //get 3x3 grid in vectors for later analysis
            try {
                if (jrand - 1 >= 0 && jrand - 1 < SharedParameters.ROWS && irand - 1 >= 0 && irand - 1 < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand - 1;
                    ycoordArray[incrIndex] = irand - 1;
                    incrIndex++;
                }
                if (jrand - 1 >= 0 && jrand - 1 < SharedParameters.ROWS && irand >= 0 && irand < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand - 1;
                    ycoordArray[incrIndex] = irand;
                    xcoordDiffusionArray[incrDiffusionIndex] = jrand - 1;
                    ycoordDiffusionArray[incrDiffusionIndex] = irand;
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (jrand - 1 >= 0 && jrand - 1 < SharedParameters.ROWS && irand + 1 >= 0 && irand + 1 < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand - 1;
                    ycoordArray[incrIndex] = irand + 1;
                    incrIndex++;
                }
                if (jrand >= 0 && jrand < SharedParameters.ROWS && irand - 1 >= 0 && irand - 1 < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand;
                    ycoordArray[incrIndex] = irand - 1;
                    xcoordDiffusionArray[incrDiffusionIndex] = jrand;
                    ycoordDiffusionArray[incrDiffusionIndex] = irand - 1;
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (jrand >= 0 && jrand < SharedParameters.ROWS && irand >= 0 && irand < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand;
                    ycoordArray[incrIndex] = irand;
                    incrIndex++;
                }
                if (jrand >= 0 && jrand < SharedParameters.ROWS && irand + 1 >= 0 && irand + 1 < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand;
                    ycoordArray[incrIndex] = irand + 1;
                    xcoordDiffusionArray[incrDiffusionIndex] = jrand;
                    ycoordDiffusionArray[incrDiffusionIndex] = irand + 1;
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (jrand + 1 >= 0 && jrand + 1 < SharedParameters.ROWS && irand - 1 >= 0 && irand - 1 < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand + 1;
                    ycoordArray[incrIndex] = irand - 1;
                    incrIndex++;
                }
                if (jrand + 1 >= 0 && jrand + 1 < SharedParameters.ROWS && irand >= 0 && irand < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand + 1;
                    ycoordArray[incrIndex] = irand;
                    xcoordDiffusionArray[incrDiffusionIndex] = jrand + 1;
                    ycoordDiffusionArray[incrDiffusionIndex] = irand;
                    incrIndex++;
                    incrDiffusionIndex++;
                }
                if (jrand + 1 >= 0 && jrand + 1 < SharedParameters.ROWS && irand + 1 >= 0 && irand + 1 < SharedParameters.COLUMNS) {
                    xcoordArray[incrIndex] = jrand + 1;
                    ycoordArray[incrIndex] = irand + 1;
                    incrIndex++;
                }//end of getting 3x3 grid in vectors
            } catch (ArrayIndexOutOfBoundsException aioobe) {
            }
        }//end of iteration and thread checking
    }//end of getsurroundingCells

    /**
     * ********************************************************************************************
     * apply diffusion when first precipiton falls
     * *********************************************************************************************
     */
    void applyDiffusion() {
        //get target coordinates in local variables
        int randrow1 = jrand;
        int randcolumn1 = irand;
        int getsedimentx = -1;
        int getsedimenty = -1;
        double sedimentTaken = 0;

        for (int t = 0; t < incrDiffusionIndex; t++) {
            //extract each one of the closest neighbors from vector (4 bars)
            int tempx = xcoordDiffusionArray[t];
            int tempy = ycoordDiffusionArray[t];
            double diffusionPower = 0;
            double possibleDiffusion = 1;
            double currentDiffusion = 0;
            if (diffusesedimentx > -1 && diffusesedimenty > -1) {
                //compare it to with target cell - decide which cells dump and get sediment
                if (surfaceArray[tempx][tempy].getsurfacefinalHeight() > surfaceArray[randrow1][randcolumn1].getsurfacefinalHeight()) {
                    diffusesedimentx = tempx;
                    diffusesedimenty = tempy;
                    getsedimentx = randrow1;
                    getsedimenty = randcolumn1;
                } else {
                    diffusesedimentx = randrow1;
                    diffusesedimenty = randcolumn1;
                    getsedimentx = tempx;
                    getsedimenty = tempy;
                }
                //determine the height difference between the two cells
                double heightDifference = surfaceArray[diffusesedimentx][diffusesedimenty].getsurfacefinalHeight() - surfaceArray[getsedimentx][getsedimenty].getsurfacefinalHeight();
                //if neighbor has sediment
                if (surfaceArray[diffusesedimentx][diffusesedimenty].getSediment() > 0) {
                    diffusionPower = heightDifference / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                    //check for basic erosion rate
                    if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                        possibleDiffusion = basicErosion * 2 * diffusionPower;
                    }
                    //check for erosion rate with break at x
                    if (SharedParameters.XPOINT >= 0 && randxleft >= 0 && randcolumn1 <= SharedParameters.XPOINT) {
                        possibleDiffusion = randxleft * 2 * diffusionPower;
                    }
                    if (SharedParameters.XPOINT >= 0 && randxright >= 0 && randcolumn1 > SharedParameters.XPOINT) {
                        possibleDiffusion = randxright * 2 * diffusionPower;
                    }
                    //check for erosion rate with break at y
                    if (SharedParameters.YPOINT >= 0 && randybottom >= 0 && randrow1 <= SharedParameters.YPOINT) {
                        possibleDiffusion = randybottom * 2 * diffusionPower;
                    }
                    if (SharedParameters.YPOINT >= 0 && randytop >= 0 && randrow1 > SharedParameters.YPOINT) {
                        possibleDiffusion = randytop * 2 * diffusionPower;
                    }
                    //if sediment is not enough
                    if (possibleDiffusion > surfaceArray[diffusesedimentx][diffusesedimenty].getSediment()) {
                        currentDiffusion = surfaceArray[diffusesedimentx][diffusesedimenty].getSediment();
                        possibleDiffusion -= currentDiffusion;
                        sedimentTaken = surfaceArray[diffusesedimentx][diffusesedimenty].getSediment();
                        surfaceArray[diffusesedimentx][diffusesedimenty].setSediment(-sedimentTaken);
                    } else {
                        currentDiffusion = possibleDiffusion;
                    }
                }
                //if neighbor does not have sediment
                if ((surfaceArray[diffusesedimentx][diffusesedimenty].getSediment() == 0) && (currentDiffusion != possibleDiffusion)) {
                    diffusionPower = (heightDifference - currentDiffusion) / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                    //check for basic erosion rate
                    if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                        possibleDiffusion = basicErosion * diffusionPower;
                    }
                    //check for erosion rate with break at x
                    if (SharedParameters.XPOINT >= 0 && randxleft >= 0 && randcolumn1 <= SharedParameters.XPOINT) {
                        possibleDiffusion = randxleft * diffusionPower;
                    }
                    if (SharedParameters.XPOINT >= 0 && randxright >= 0 && randcolumn1 > SharedParameters.XPOINT) {
                        possibleDiffusion = randxright * diffusionPower;
                    }
                    //check for erosion rate with break at y
                    if (SharedParameters.YPOINT >= 0 && randybottom >= 0 && randrow1 <= SharedParameters.YPOINT) {
                        possibleDiffusion = randybottom * diffusionPower;
                    }
                    if (SharedParameters.YPOINT >= 0 && randytop >= 0 && randrow1 > SharedParameters.YPOINT) {
                        possibleDiffusion = randytop * diffusionPower;
                    }
                    currentDiffusion = currentDiffusion + possibleDiffusion;
                }
                //only for front row - apply only ten percent
                surfaceArray[diffusesedimentx][diffusesedimenty].setErosion(currentDiffusion - sedimentTaken);
                if (randrow1 == 0) {
                    currentDiffusion = currentDiffusion * 0.10;
                }
                surfaceArray[getsedimentx][getsedimenty].setSediment(currentDiffusion);
            }
        }//end of for loop
        for (int t = 0; t < incrIndex; t++) {
            //fix height after diffusion
            surfaceArray[xcoordArray[t]][ycoordArray[t]].setfinalHeight();
        }
    }//end of applyDiffusion


    /**
     * ********************************************************************************************
     * search for lowest cell in 3x3 grid
     * *********************************************************************************************
     */
    void searchlowestCell() {
        //to do certain number of iterations
        int randx1 = jrand;
        int randy1 = irand;
        int current = 0;
        int minHeight = current;
        int xcoord1, ycoord1, xcoord2, ycoord2;

        //find lowest height in array
        int nextHeight;
        for (nextHeight = current + 1; nextHeight < incrIndex; nextHeight++) {
            double bar2height, bar1height;
            xcoord2 = xcoordArray[nextHeight];
            ycoord2 = ycoordArray[nextHeight];
            xcoord1 = xcoordArray[minHeight];
            ycoord1 = ycoordArray[minHeight];
            bar2height = surfaceArray[xcoord2][ycoord2].getsurfacefinalHeight();
            bar1height = surfaceArray[xcoord1][ycoord1].getsurfacefinalHeight();

            //if next bar is lower than current, change value of index
            if (bar2height < bar1height) {
                minHeight = nextHeight;
            }
        }//end of for loop

        //this is to check if there is any lowest cell
        if (xcoordArray[minHeight] >= 0 && ycoordArray[minHeight] >= 0) {
            newx = xcoordArray[minHeight];
            newy = ycoordArray[minHeight];
            //this is to check if the lowest now was the lowest before in order to avoid an endless loop
            if (newx == SharedParameters.OLDX && newy == SharedParameters.OLDY) {
                cleanup();
                firstTime = true;
                keepgoing = false;
                return;
            } else {
                SharedParameters.OLDX = randx1;
                SharedParameters.OLDY = randy1;
            }
            //this is to check if the lowest now and lowest before are the same height
            if (surfaceArray[newx][newy].getsurfacefinalHeight() == surfaceArray[randx1][randy1].getsurfacefinalHeight()) {
                firstTime = true;
                keepgoing = false;
                return;
            }
        } else {
            firstTime = true;
            keepgoing = false;
        }//end if anybars
    }//end of searchlowestCell()

    /**
     * ********************************************************************************************
     * apply the erosion according to erodibility parameters
     * *********************************************************************************************
     */
    void calculateErosion() {
        //to do certain number of iterations
        if (keepgoing) {
            int randx2 = jrand;
            int randy2 = irand;
            //get the heights of both bars and calculate height difference
            double heightDiff = 0;
            SharedParameters.HEIGHTDIFFERENCE = heightDiff = surfaceArray[randx2][randy2].getsurfacefinalHeight() - surfaceArray[newx][newy].getsurfacefinalHeight();
            double erosionPower = 0;
            double possibleErosion = 1;
            double currentErosion = 0;
            SharedParameters.SEDIMENT = 0;
            double sedimentTaken = 0;
            if (surfaceArray[randx2][randy2].getSediment() > 0) {
                erosionPower = heightDiff / (SharedParameters.BARWIDTH * SharedParameters.BARWIDTH);
                //calculate erosion of bar based on basic erosion (uniform or random)
                if (SharedParameters.XPOINT < 0 && SharedParameters.YPOINT < 0) {
                    possibleErosion = (basicErosion * 2) * erosionPower;
                }
                //check y break point
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 > SharedParameters.YPOINT) {
                    possibleErosion = (randytop * 2) * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = (randybottom * 2) * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = (randxright * 2) * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = (randxleft * 2) * erosionPower;
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
                    possibleErosion = randytop * erosionPower;
                }
                if (SharedParameters.YPOINT > -1 && SharedParameters.YPOINT <= SharedParameters.ROWS && randx2 <= SharedParameters.YPOINT) {
                    possibleErosion = randybottom * erosionPower;
                }
                //check x break point
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 > SharedParameters.XPOINT) {
                    possibleErosion = randxright * erosionPower;
                }
                if (SharedParameters.XPOINT > -1 && SharedParameters.XPOINT <= SharedParameters.COLUMNS && randy2 <= SharedParameters.XPOINT) {
                    possibleErosion = randxleft * erosionPower;
                }
                currentErosion = currentErosion + possibleErosion;
            }
            SharedParameters.SEDIMENT = currentErosion;
            //only for front row
            surfaceArray[randx2][randy2].setErosion(currentErosion - sedimentTaken);
            if (newx == 0) {
                currentErosion = currentErosion * 0.10;
            }
            surfaceArray[newx][newy].setSediment(currentErosion);
            surfaceArray[randx2][randy2].setfinalHeight();
            surfaceArray[newx][newy].setfinalHeight();
        }
    }//end of applyErosion

    /**
     * ********************************************************************************************
     * to see if erosion continues
     * *********************************************************************************************
     */
    void checkcarryingCapacity() {
        if (keepgoing) {
            double changeperStep;
            //check default without changes in climate
            if (SharedParameters.RAINFALLRATEDEFAULT > 0) {
                SharedParameters.CARRYINGCAPACITY = SharedParameters.RAINFALLRATEDEFAULT * SharedParameters.HEIGHTDIFFERENCE;
                if (SharedParameters.SEDIMENT > SharedParameters.CARRYINGCAPACITY) {
                    firstTime = true;
                    keepgoing = false;
                    return;
                }
            }
            //check when climate is increasing
            if (SharedParameters.RAININCREASELOW != 0 && SharedParameters.RAININCREASEHIGH != 0) {
                double highlowdifference = SharedParameters.RAININCREASEHIGH - SharedParameters.RAININCREASELOW;
                changeperStep = highlowdifference / (SharedParameters.ENDTIME / SharedParameters.TIMESTEP);

                //this is just to reset the carrying capacity
                if (startNeeded) {
                    SharedParameters.CARRYINGCAPACITY = SharedParameters.RAININCREASELOW;
                    startNeeded = false;
                }

                if (SharedParameters.STEPCOUNTER >= (int) SharedParameters.TIMESTEP) {
                    SharedParameters.CARRYINGCAPACITY += changeperStep;
                    SharedParameters.STEPCOUNTER = 1;
                }
                if (SharedParameters.SEDIMENT > (SharedParameters.CARRYINGCAPACITY * SharedParameters.HEIGHTDIFFERENCE)) {
                    firstTime = true;
                    keepgoing = false;
                    return;
                }
            }//end increase
            //check when climate is decreasing
            if (SharedParameters.RAINDECREASELOW != 0 && SharedParameters.RAINDECREASEHIGH != 0) {
                double highlowdifference = SharedParameters.RAINDECREASEHIGH - SharedParameters.RAINDECREASELOW;
                if (startNeeded) {
                    SharedParameters.CARRYINGCAPACITY = SharedParameters.RAINDECREASEHIGH;
                    startNeeded = false;
                }
                changeperStep = highlowdifference / (SharedParameters.ENDTIME / SharedParameters.TIMESTEP);

                if (SharedParameters.STEPCOUNTER > (int) SharedParameters.TIMESTEP) {
                    SharedParameters.CARRYINGCAPACITY -= changeperStep;
                    SharedParameters.STEPCOUNTER = 1;
                }

                if (SharedParameters.SEDIMENT > (SharedParameters.CARRYINGCAPACITY * SharedParameters.HEIGHTDIFFERENCE)) {
                    firstTime = true;
                    keepgoing = false;
                }
            }
        }//end of keepgoing
    }//end of check carrying Capacity

    /**
     * ********************************************************************************************
     * to reset interval arrays
     * *********************************************************************************************
     */
    private void resetIntervalArrays() {
        sumIntervalsSediment = new double[SharedParameters.ROWS];
        sumColumns = new double[SharedParameters.ROWS];
        sumColumnsBedrock = new double[SharedParameters.ROWS];
        double[] sumRows = new double[SharedParameters.ROWS];
        averageIntervals = new double[SharedParameters.ROWS];
        averageIntervalsBedrock = new double[SharedParameters.ROWS];
        columnIntervals = new double[SharedParameters.ROWS];
        columnIntervalsBedrock = new double[SharedParameters.ROWS];
        columnSediment = new double[SharedParameters.ROWS];
        for (int i = 0; i < SharedParameters.ROWS; i++) {
            sumRows[i] = 0;
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
        SharedParameters.FIRSTINTERVAL = true;
    }// end of reset intervals

    /**
     * ********************************************************************************************
     * to reset intervals parameters
     * *********************************************************************************************
     */
    private void resetIntervalsParms(ErosionIntervals eicopy) {
        double min = surfaceArray[0][0].getsurfacefinalHeight();
        double max = surfaceArray[SharedParameters.ROWS - 1][SharedParameters.COLUMNS - 1].getsurfacefinalHeight()
                + (SharedParameters.ENDTIME * SharedParameters.TECTONICSPERCENTAGE);
        eicopy.passParms(min, max);
        eicopy.passMsg(msg);
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


    /**
     * ********************************************************************************************
     * if thread gets lazy
     * *********************************************************************************************
     */
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
        int colorIndex = 0;
        for (int x = 0; x < SharedParameters.ROWS; x++) {
            for (int y = 0; y < SharedParameters.COLUMNS; y++) {
                colorIndex = (int) ((surfaceArray[x][y].getsurfacefinalHeight() - lowestBar) / interpolation);
                if (colorIndex < 0) {
                    colorIndex = 0;
                }
                if (colorIndex > 1022) {
                    colorIndex = 1022;
                }
                ecanv.setDataHeight(x, y, (float) surfaceArray[x][y].getsurfacefinalHeight());
                ecanv.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));

                //set the snapshots at their corresponding times
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4)) {
                    ecanv25p.setDataHeight(x, y, (float) surfaceArray[x][y].getsurfacefinalHeight());
                    ecanv25p.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 2)) {
                    ecanv50p.setDataHeight(x, y, (float) surfaceArray[x][y].getsurfacefinalHeight());
                    ecanv50p.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER == SharedParameters.ENDTIME / 4 * 3)) {
                    ecanv75p.setDataHeight(x, y, (float) surfaceArray[x][y].getsurfacefinalHeight());
                    ecanv75p.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
                if ((SharedParameters.ITERATIONCOUNTER == 0) || (SharedParameters.ITERATIONCOUNTER < SharedParameters.ENDTIME - 5)) {
                    ecanv100p.setDataHeight(x, y, (float) surfaceArray[x][y].getsurfacefinalHeight());
                    ecanv100p.setDataColor(x, y, colors.getColor1(colorIndex), colors.getColor2(colorIndex), colors.getColor3(colorIndex));
                }
            }
        }
    }//end setColors

    /**
     * ********************************************************************************************
     * to reset the snapshot images everytime an iteration starts
     * *********************************************************************************************
     */
    private void resetSnapshots(ErosionCanvas ecsnapshot) {
        ecsnapshot.setGridSize(SharedParameters.ROWS, SharedParameters.COLUMNS);
        ecsnapshot.setViewHeight();
        ecsnapshot.setVisible(false);
    }//end of reset snapshots

    /**
     * ********************************************************************************************
     * to draw corresponding snapshot
     * *********************************************************************************************
     */
    private void setSnapshot(ErosionCanvas ecsnapshot) {
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
            xcoordArray[i] = -1;
            ycoordArray[i] = -1;
        }
        for (int i = 0; i < 4; i++) {
            xcoordDiffusionArray[i] = -1;
            ycoordDiffusionArray[i] = -1;
        }
        SharedParameters.SEDIMENT = 0.0;
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
 * void sety(double newy) = sets the width of the bar
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
    SurfaceBar(double x, double y, double x1, double y1, double slope,
               int color1, int color2, int color3) {
        this.roughness = calculateRoughness();
        this.slope = slope;
        this.y1 = y1;
        int[] barColor = new int[3];
        if (color1 > 255) {
            barColor[0] = 245;
        } else {
            barColor[0] = color1;
        }
        if (color2 > 255) {
            barColor[1] = 150;
        } else {
            barColor[1] = color2;
        }
        if (color3 > 255) {
            barColor[2] = 0;
        } else {
            barColor[2] = color3;
        }
        double width = SharedParameters.BARWIDTH;
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

    //sets the sediment
    void setSediment(double sediment) {
        this.sediment += sediment;
    }

    //returns the sediment
    double getSediment() {
        return sediment;
    }


    //sets the erosion
    void setErosion(double erosion) {
        this.erosion += erosion;
    }

    //returns the erosion
    double getErosion() {
        return erosion;
    }

    //sets the tectonic value
    void setTectonics(double tectonicvalue) {
        this.tectonicvalue += tectonicvalue;
    }

    //gets the tectonic value
    double getTectonics() {
        return tectonicvalue;
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

    //returns value after erosion and sediment are applied
    double getsurfacefinalHeight() {
        return finalHeight;
    }

    //returns the ybasePosition of the bar (difference between y and y1 which gives the actual height)
    double gety1() {
        return y1;
    }

}//end SurfaceBar class
