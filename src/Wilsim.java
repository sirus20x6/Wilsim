/***********************************************************************************************
 CLASS:	      Wilsim

 FUNCTION:     Wilsim declares and instantiates objects from the ErosionUI, ErosionSim,
 EROSIONCANVAS, ErosionIntervals, ErosionHypsometric and SharedParameters classes.
 The class contains:
 -init()
 It creates objects from the classes metioned and calls starts running the
 threads for the ErosionSim and EROSIONCANVAS objects.
 -start()
 To get the user interface started after all other threads have been started

 DATE CREATED: August 2002
 ***********************************************************************************************/

import java.applet.Applet;

public class Wilsim extends Applet {
    private ErosionUI eui;        // User Interface

    public void init() {
        SharedParameters sparam = new SharedParameters();

        EROSIONCANVAS ecanv = new EROSIONCANVAS();
        ecanv.start();

        EROSIONCANVAS ecanv25p = new EROSIONCANVAS();
        ecanv25p.start();
        EROSIONCANVAS ecanv50p = new EROSIONCANVAS();
        ecanv50p.start();
        EROSIONCANVAS ecanv75p = new EROSIONCANVAS();
        ecanv75p.start();
        EROSIONCANVAS ecanv100p = new EROSIONCANVAS();
        ecanv100p.start();

        ErosionHypsometric eh = new ErosionHypsometric();

        int type2 = 2;
        ErosionIntervals eintervals = new ErosionIntervals("Average", type2, "Rows");
        int type1 = 1;
        ErosionIntervals eintsediment = new ErosionIntervals("Average Sediment", type1, "Rows");
        ErosionIntervals ecolumn = new ErosionIntervals("Column", type2, "Rows");
        ErosionIntervals ecolsediment = new ErosionIntervals("Column Sediment", type1, "Rows");
        ErosionIntervals erow = new ErosionIntervals("Row", type2, "Columns");
        ErosionIntervals erowsediment = new ErosionIntervals("Row Sediment", type1, "Columns");

        ErosionSim esim = new ErosionSim(ecanv, ecanv25p, ecanv50p, ecanv75p, ecanv100p, eintervals, eintsediment, ecolumn, ecolsediment, erow, erowsediment, eh);
        esim.start();

        eui = new ErosionUI(this, esim, sparam, ecanv, ecanv25p, ecanv50p, ecanv75p, ecanv100p, eintervals, eintsediment, ecolumn, ecolsediment, erow, erowsediment, eh);
    }

    public void start() {
        eui.start();  // It would be nice to get rid of this.
    }
}
