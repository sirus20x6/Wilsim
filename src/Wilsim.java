/***********************************************************************************************
 CLASS:	      Wilsim

 FUNCTION:     Wilsim declares and instantiates objects from the ErosionUI, ErosionSim,
 ErosionCanvas, ErosionIntervals, ErosionHypsometric and SharedParameters classes.
 The class contains:
 -init()
 It creates objects from the classes metioned and calls starts running the
 threads for the ErosionSim and ErosionCanvas objects.
 -start()
 To get the user interface started after all other threads have been started

 DATE CREATED: August 2002
 ***********************************************************************************************/

import java.applet.Applet;

public class Wilsim extends Applet {
    private ErosionUI eui;        // User Interface

    public void init() {
        SharedParameters sparam = new SharedParameters();

        ErosionCanvas ecanv = new ErosionCanvas();
        ecanv.start();

        ErosionCanvas ecanv25p = new ErosionCanvas();
        ecanv25p.start();
        ErosionCanvas ecanv50p = new ErosionCanvas();
        ecanv50p.start();
        ErosionCanvas ecanv75p = new ErosionCanvas();
        ecanv75p.start();
        ErosionCanvas ecanv100p = new ErosionCanvas();
        ecanv100p.start();

        ErosionHypsometric eh = new ErosionHypsometric(sparam);

        int type2 = 2;
        ErosionIntervals eintervals = new ErosionIntervals(sparam, "Average", type2, "Rows");
        int type1 = 1;
        ErosionIntervals eintsediment = new ErosionIntervals(sparam, "Average Sediment", type1, "Rows");
        ErosionIntervals ecolumn = new ErosionIntervals(sparam, "Column", type2, "Rows");
        ErosionIntervals ecolsediment = new ErosionIntervals(sparam, "Column Sediment", type1, "Rows");
        ErosionIntervals erow = new ErosionIntervals(sparam, "Row", type2, "Columns");
        ErosionIntervals erowsediment = new ErosionIntervals(sparam, "Row Sediment", type1, "Columns");

        ErosionSim esim = new ErosionSim(sparam, ecanv, ecanv25p, ecanv50p, ecanv75p, ecanv100p, eintervals, eintsediment, ecolumn, ecolsediment, erow, erowsediment, eh);
        esim.start();

        eui = new ErosionUI(this, esim, sparam, ecanv, ecanv25p, ecanv50p, ecanv75p, ecanv100p, eintervals, eintsediment, ecolumn, ecolsediment, erow, erowsediment, eh);
    }

    public void start() {
        eui.start();  // It would be nice to get rid of this.
    }
}
