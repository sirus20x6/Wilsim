/***********************************************************************************************
 CLASS:	      SharedParameters

 FUNCTION:     This class holds the values shared by the user interface and the running
 simulation. These values should be changed by the UI and drive the simulation.
 The simulation reads these values but does not change them.
 The class contains:
 -Constructor:
 *public SharedParameters() = all variables are initialized to default values.

 INPUT:	      User will change parameter values.

 DATE CREATED: August 2002
 ***********************************************************************************************/

import java.awt.*;

class SharedParameters {
    // These are set by the UI for use by the simulation
    static final Label values1 = new Label("Total Iterations: ");
    static final Label values2 = new Label("                                ");
    static final Label values3 = new Label("# iterations processed:");
    static final Label values4 = new Label("                                ");
    // Shared parameters
    static int COLUMNS, OLDCOLUMNS;
    static int ROWS, OLDROWS;
    static double BARWIDTH;
    static double SLOPE, OLDSLOPE;
    static int ENDTIME;
    static double EROSION, RANDVALUE;
    static boolean RANDEROSION;
    static boolean ROUTINESTARTED;
    static int ITERATIONCOUNTER;
    static int reverseIterationCounter;
    static int YPOINT;
    static int XPOINT;
    static double YRANDTOP, YRANDBOTTOM;
    static double XRANDLEFT, XRANDRIGHT;
    static double RAINFALLRATEDEFAULT;
    static double RAININCREASELOW, RAININCREASEHIGH;
    static double RAINDECREASELOW, RAINDECREASEHIGH;
    static double CARRYINGCAPACITY;
    static double TIMESTEP;
    static int BARSPROCESSED;
    static int TECTONICSYPOINT;
    static int TECTONICSXPOINT;
    static double TECTONICSYTOP, TECTONICSYBOTTOM;
    static double TECTONICSXLEFT, TECTONICSXRIGHT;
    static double TECTONICSPERCENTAGE;
    static boolean APPLYTECTONICS, EROSIONNEEDED, RESETINTERVALSLEGEND;
    static int STEPCOUNTER;
    static boolean STARTALLOVER;
    static int OLDX, OLDY;
    static int TOTALYEARS;
    static int COLINTERVALS;
    static int ROWINTERVALS;
    static double HEIGHTDIFFERENCE;
    static boolean AVGVISIBLE;
    static boolean COLVISIBLE;
    static boolean ROWVISIBLE;
    static boolean HYPSOMETRIC;
    static Button SHOWSURFACEBUTTON;

    /**
     * ********************************************************************************************
     * Constructor
     * *********************************************************************************************
     */
    public SharedParameters() {
        COLUMNS = 60;
        OLDCOLUMNS = 60;
        ROWS = 100;
        OLDROWS = 100;
        BARWIDTH = 1;
        ENDTIME = 10000;
        OLDSLOPE = SLOPE = 0.01;
        EROSION = 0.05;
        RANDEROSION = false;
        ITERATIONCOUNTER = 0;
        YPOINT = 0;
        YRANDTOP = 0.0;
        YRANDBOTTOM = 0.0;
        XPOINT = 0;
        XRANDLEFT = 0.0;
        XRANDRIGHT = 0.0;
        TECTONICSYPOINT = 0;
        TECTONICSYTOP = 0;
        TECTONICSYBOTTOM = 0;
        TECTONICSXPOINT = 0;
        TECTONICSXLEFT = 0;
        TECTONICSXRIGHT = 0;
        TECTONICSPERCENTAGE = 0;
        RAINFALLRATEDEFAULT = 0.1;
        RAININCREASELOW = 0;
        RAINDECREASELOW = 0;
        RAININCREASEHIGH = 0;
        RAINDECREASEHIGH = 0;
        ROUTINESTARTED = false;
        TIMESTEP = 1;
        BARSPROCESSED = 0;
        STEPCOUNTER = 1;
        APPLYTECTONICS = false;
        EROSIONNEEDED = true;
        CARRYINGCAPACITY = 0.1;
        OLDX = OLDY = 0;
        TOTALYEARS = 0;
        RANDVALUE = 0;
        HEIGHTDIFFERENCE = 0;
        //FIRSTINTERVAL = RESETINTERVALSLEGEND = false;
        AVGVISIBLE = COLVISIBLE = ROWVISIBLE = HYPSOMETRIC = false;
        COLINTERVALS = 1;
        ROWINTERVALS = 1;
    }
}//end class SharedParameters

