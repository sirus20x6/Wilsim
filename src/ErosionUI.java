/********************************************************************************************
 CLASS:	      ErosionUI

 FUNCTION:     ErosionUI declares and defines all the components for the user interface on the 
 applet. It initializes static variables to be used for the initialization of the
 components and for resetting their values.	      
 The class contains:
 -Constructor: 
 *ErosionUI(Panel thisPanel, ErosionSim e1, SharedParameters sparams, 
 ErosionCanvas e2, ErosionCanvas e25p, ErosionCanvas e50p, ErosionCanvas e75p, ErosionCanvas e100p,
 ErosionIntervals ei, ErosionIntervals eis,
 ErosionIntervals ec, ErosionIntervals ecs, ErosionIntervals er, ErosionIntervals ers,
 ErosionHypsometric ehrec)
 it creates and initializes all GUI components
 -Helping functions:
 *public void start()
 to make advanced options invisible when the applet starts
 *public void stop()
 what to do when the user moves to another page
 *public void itemStateChanged(ItemEvent e)
 to listen to changes in radio buttons
 *public void adjustmentValueChanged(AdjustmentEvent e)
 to listen to sliders
 *public static double getDouble(String numStr)
 to convert numbers in labels to double
 *void constraint(Container cont, Component comp, 
 int gridx, int gridy,
 int gridwidth, int gridheight,
 int fill, int anchor,
 double weightx, double weighty,
 int top, int left, int bottom, int right)
 to be able to handle the component location

 INPUT:	      User will select parameters.

 DATE CREATED: August 2002
 ***********************************************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

class ErosionUI implements ItemListener, AdjustmentListener, ActionListener {

    //global parameters
    private final SharedParameters params;

    //for the intervals
    private final ErosionIntervals eintervals;
    private final ErosionIntervals eintsediment;
    private final ErosionIntervals ecolumn;
    private final ErosionIntervals ecolsediment;
    private final ErosionIntervals erow;
    private final ErosionIntervals erowsediment;

    //for the hypsometric curve
    private final ErosionHypsometric eh;

    private static CustomPanel optionGeneralPanel;
    private static CustomPanel optionCardManagerPanel;
    private static CustomPanel optionintervals1down;
    private static CustomPanel imagesPanel;

    //to show the topographic grid
    private static SurfacePanel sPanel;

    //cardlayout managers
    private static CardLayout cardSimulationManager;
    private static CardLayout cardManagerGeneral;
    private static CardLayout cardManagerUp;
    private static CardLayout cardManagerIntervals;

    //labels
    private static Label cardLabelSimulation;
    private static Label cardLabelSnapshot;
    private static Label optionTabLabel;
    private static Label intervalTabLabel;
    private static Label hypsometricTabLabel;
    private static Label cardLabelIntervals1;
    private static Label cardLabelIntervals2;
    private static Label cardLabelIntervals3;
    private static Label gridxarrow;
    private static Label gridyarrow;
    private static Label endtimearrow;
    private static Label topographyarrow;
    private static Label erodibilitybreakxarrow;
    private static Label erodibilitybreakyarrow;
    private static Label erodibilityuniformarrow;
    private static Label erodibilitybreakxleftarrow;
    private static Label erodibilitybreakxrightarrow;
    private static Label climatedefaultarrow;
    private static Label increasingclimatearrow;
    private static Label lowclimatearrow;
    private static Label decreasingclimatearrow;
    private static Label climateseparatorLabel;
    private static Label highclimatearrow;
    private static Label tectonicsbreakxarrow;
    private static Label tectonicsbreakyarrow;
    private static Label tectonicsbreaktoparrow;
    private static Label tectonicsbreakbottomarrow;
    private static Label xmaxLabel;
    private static Label xmaxsliderLabel;
    private static Label ymaxLabel;
    private static Label ymaxsliderLabel;
    private static Label endtimeyearLabel;
    private static Label endtimesliderLabel;
    private static Label topographydefaultLabel;
    private static Label randomsliderLabel;
    private static Label randomsliderLabel1;
    private static Label erodibilitybpointxLabel;
    private static Label erodibilitybpointyLabel;
    private static Label erodibilityylabelnorth;
    private static Label erodibilityylabelsouth;
    private static Label climatenote;
    private static Label tectonicsbpointxLabel;
    private static Label tectonicsbpointyLabel;
    private static Label interchangeablelabelnorth;
    private static Label interchangeablelabelsouth;
    private static Label erodibilitylabelnorth;
    private static Label erodibilitylabelsouth;
    private static Label tectonicsnorth;
    private static Label tectonicssouth;
    private static Label tectonicslabelCheck1;
    private static Label rowintervalvalueLabel;
    private static Label columnintervalvalueLabel;

    //customized types of labels
    private final decimalLabel topographyslopeLabel;
    private final decimalLabel erodibilityuniformvalueLabel;
    private final decimalLabel erodibilityxleftvalueLabel;
    private final decimalLabel erodibilityxrightvalueLabel;
    private final decimalLabel erodibilityytopvalueLabel;
    private final decimalLabel erodibilityybottomvalueLabel;
    private final decimalLabel climatedefaultvalueLabel;
    private final decimalLabel climateincreasingvaluelowLabel;
    private final decimalLabel climatedecreasingvaluelowLabel;
    private final decimalLabel climateincreasingvaluehighLabel;
    private final decimalLabel climatedecreasingvaluehighLabel;
    private final decimalLabel tectonicsxleftvalueLabel;
    private final decimalLabel tectonicsxrightvalueLabel;
    private final decimalLabel tectonicsytopvalueLabel;
    private final decimalLabel tectonicsybottomvalueLabel;


    //sliders
    private static Scrollbar interchangeableSlider;
    private static Scrollbar erodibilitySlider;
    private static Scrollbar erodibilityadvancedSlider;
    private static Scrollbar climateSlider;
    private static Scrollbar tectonicsSlider;
    private static Scrollbar tectonicsadvancedSlider;
    private static Scrollbar columnintervalSlider;
    private static Scrollbar rowintervalSlider;

    //checkboxes
    private static Checkbox initialconditionCheck;
    private static Checkbox hypsometricCheck;
    private static Checkbox optionsCheck;
    private static Checkbox saveIntCheck;
    private static Checkbox initadvancedgridx;
    private static Checkbox initadvancedgridy;
    private static Checkbox initadvancedendtime;
    private static Checkbox initadvancedtopography;
    private static Checkbox initadvancedfake;
    private static Checkbox erodibilityCheck;
    private static Checkbox erodibilityleft;
    private static Checkbox erodibilitybottom;
    private static Checkbox erodibilityfake;
    private static Checkbox erodibilityuniformCheck;
    private static Checkbox erodibilityrandomCheck;
    private static Checkbox erodibilitybpointxCheck;
    private static Checkbox erodibilitybpointyCheck;
    private static Checkbox climateCheck;
    private static Checkbox climatefake;
    private static Checkbox climatedefaultCheck;
    private static Checkbox climatedecreasingCheck;
    private static Checkbox climateincreasingCheck;
    private static Checkbox climateincreasinghigh;
    private static Checkbox climateincreasinglow;
    private static Checkbox tectonicCheck;
    private static Checkbox tectonicsfake;
    private static Checkbox tectonicsleft;
    private static Checkbox tectonicsbottom;
    private static Checkbox tectonicsdefaultCheck;
    private static Checkbox tectonicsbpointxCheck;
    private static Checkbox tectonicsbpointyCheck;
    private static Checkbox averageprofileCheck;
    private static Checkbox selectedcolumnCheck;
    private static Checkbox selectedrowCheck;
    private static Checkbox simulationCheck;
    private static Checkbox snapshotCheck;

    //help buttons
    private final Button initialConditionsHelpButton;
    private final Button erodibilityHelpButton;
    private final Button climateHelpButton;
    private final Button tectonicsHelpButton;

    //textareas for help text
    private final TextArea initialConditionsHelpText;
    private final TextArea erodibilityHelpText;
    private final TextArea climateHelpText;
    private final TextArea tectonicsHelpText;

    //static variables used by components
    //grid variables for components
    private static final int xminimum = 60;
    private static final int xmaximum = 110;
    private static final int yminimum = 100;
    private static final int ymaximum = 210;
    private static final int xyblock = 5;
    //time variables
    private static final int timeminimum = 1000;
    private static final int timemaximum = 10001;
    private static final int timeblock = 10;
    //erodibility variables
    private static final double erodibilityuniform = 0.05;
    private static final int uniformminimum = 10;
    private static final int uniformmaximum = 60;
    private static final int uniformstep = 10;
    private static final int xleftminimum = 10;
    private static final int xrightmaximum = 60;
    private static final int minimumxpoint = 0;
    private static final int minimumypoint = 0;
    //slope variables
    private static final int slopeminimum = 1;
    private static final int slopemaximum = 40;
    private static final int slopeblock = 1;
    //climate variables
    private static final int climateminimum = 50;
    private static final int climatemaximum = 160;
    private static final int climatelow = 50;
    private static final int climatehigh = 160;
    private static final int climateblock = 10;
    private static final int climatedefaultblock = 10;
    //tectonics
    private static final int tectonicsxleftminimum = 0;
    private static final int tectonicsxleftmaximum = 40;
    private static final int tectonicsblock = 10;
    private static final int tectonicsminimumxpoint = 0;
    private static final int tectonicsminimumypoint = 0;

    private static boolean xgrid = false;
    private static boolean ygrid = false;
    private static boolean endt = false;
    private static boolean topo = false;
    private static boolean uniformErosion = false;
    private static boolean breakx = false;
    private static boolean breaky = false;
    private static boolean breakxleft = false;
    private static boolean breakxright = false;
    private static boolean breakytop = false;
    private static boolean breakybottom = false;
    private static boolean climatedefault1 = false;
    private static boolean climateincrease = false;
    private static boolean climatedecrease = false;
    private static boolean climateincreaselow = false;
    private static boolean climateincreasehigh = false;
    private static boolean climatedecreaselow = false;
    private static boolean climatedecreasehigh = false;
    private static boolean tectonicsx = false;
    private static boolean tectonicsy = false;
    private static boolean tectonicsxleft = false;
    private static boolean tectonicsxright = false;
    private static boolean tectonicsytop = false;
    private static boolean tectonicsybottom = false;

    //different colors used
    private static final Color highlights = Color.blue;
    private static final Color outsidepanelColor = new Color(149, 167, 191);
    private static final Color snapshotpanelColor = new Color(159, 167, 201);
    private static final Color optionsColor = new Color(149, 157, 191);
    private static final Color intervalColor = new Color(164, 172, 206);
    private static final Color hypsometricColor = new Color(179, 197, 221);
    private static final Color initialconditionsColor = new Color(174, 182, 206);

    /**
     * ********************************************************************************************
     * Constructor
     * *********************************************************************************************
     */
    ErosionUI(Panel thisPanel, ErosionSim e1, SharedParameters sparams,
              ErosionCanvas e2,
              ErosionCanvas e25p, ErosionCanvas e50p, ErosionCanvas e75p, ErosionCanvas e100p,
              ErosionIntervals ei, ErosionIntervals eis,
              ErosionIntervals ec, ErosionIntervals ecs, ErosionIntervals er, ErosionIntervals ers,
              ErosionHypsometric ehrec) {
        // Get a link to the simulation, shared parameters, and image
        params = sparams;
        eintervals = ei;
        eintsediment = eis;
        ecolumn = ec;
        ecolsediment = ecs;
        erow = er;
        erowsediment = ers;
        eh = ehrec;

        // Set up shared simulation parameters
        SharedParameters.COLUMNS = SharedParameters.OLDCOLUMNS = 60;
        SharedParameters.ROWS = SharedParameters.OLDROWS = 100;
        SharedParameters.BARWIDTH = 100;
        SharedParameters.ENDTIME = timeminimum * 100;
        SharedParameters.OLDSLOPE = SharedParameters.SLOPE = (double) slopeminimum / 100;
        SharedParameters.YPOINT = -1;
        SharedParameters.XPOINT = -1;
        SharedParameters.YRANDTOP = 0.0;
        SharedParameters.YRANDBOTTOM = 0.0;
        SharedParameters.XRANDLEFT = 0.0;
        SharedParameters.XRANDRIGHT = 0.0;
        SharedParameters.RAINFALLRATEDEFAULT = 0.1;
        SharedParameters.RAININCREASELOW = 0;
        SharedParameters.RAINDECREASELOW = 0;
        SharedParameters.RAININCREASEHIGH = 0;
        SharedParameters.RAINDECREASEHIGH = 0;
        SharedParameters.STEPCOUNTER = 0;
        SharedParameters.CLIMATEDEFAULT = true;

        //user interface layout
        //panel that has all the subpanels and components
        thisPanel.setLayout(new BorderLayout());
        thisPanel.setBackground(outsidepanelColor);

        //CardLayout manager that controls the simulation and snapshots panels
        cardSimulationManager = new CardLayout();
        CustomPanel imagesChoicePanel = new CustomPanel(950, 30, 2, 2, 0, 2, 120, 130, 170);
        imagesChoicePanel.setLayout(new GridLayout(1, 5));
        imagesPanel = new CustomPanel(950, 600, 0, 2, 2, 2, 120, 130, 170);
        imagesPanel.setLayout(cardSimulationManager);
        cardLabelSimulation = new Label("Simulation");
        cardLabelSnapshot = new Label("Snapshot");
        CustomPanel simulationPanel = new CustomPanel(950, 600, 4, 2, 2, 2, 149, 157, 191);
        simulationPanel.setBackground(optionsColor);
        simulationPanel.setLayout(new BorderLayout());
        CustomPanel snapshotPanel = new CustomPanel(950, 600, 0, 0, 0, 0);
        snapshotPanel.setBackground(snapshotpanelColor);
        snapshotPanel.setLayout(new GridLayout(2, 2));

        //top panel with titles
        CheckboxGroup imageButtons = new CheckboxGroup();
        simulationCheck = new Checkbox("SIMULATION", true, imageButtons);
        simulationCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        simulationCheck.setBackground(new Color(129, 137, 171));
        simulationCheck.addItemListener(this);
        new ToolTip("Display animated landform evolution over time", simulationCheck);
        snapshotCheck = new Checkbox("SNAPSHOTS", false, imageButtons);
        snapshotCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        snapshotCheck.setBackground(snapshotpanelColor);
        snapshotCheck.addItemListener(this);
        new ToolTip("Display snapshots of landforms at different intervals", snapshotCheck);
        Label fillerLabel1 = new Label("");
        fillerLabel1.setBackground(snapshotpanelColor);
        Label fillerLabel2 = new Label("");
        fillerLabel2.setBackground(snapshotpanelColor);
        Label fillerLabel3 = new Label("");
        fillerLabel3.setBackground(snapshotpanelColor);
        imagesChoicePanel.add(simulationCheck);
        imagesChoicePanel.add(snapshotCheck);
        imagesChoicePanel.add(fillerLabel1);
        imagesChoicePanel.add(fillerLabel2);
        imagesChoicePanel.add(fillerLabel3);

        //panels on the right of the simulation
        CheckboxGroup optionRadioButtons = new CheckboxGroup();
        optionsCheck = new Checkbox("OPTIONS", true, optionRadioButtons);
        optionsCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        optionsCheck.setBackground(optionsColor);
        optionsCheck.addItemListener(this);
        new ToolTip("Set different parameters for the simulation", optionsCheck);
        saveIntCheck = new Checkbox("PROFILES", false, optionRadioButtons);
        saveIntCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        saveIntCheck.setBackground(intervalColor);
        saveIntCheck.addItemListener(this);
        new ToolTip("Display profiles at different intervals", saveIntCheck);
        hypsometricCheck = new Checkbox("HYPSOMETRIC", false, optionRadioButtons);
        hypsometricCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        hypsometricCheck.setBackground(hypsometricColor);
        hypsometricCheck.addItemListener(this);
        new ToolTip("Display hypsometric curves at different intervals", hypsometricCheck);

        //add radio buttons that control the subpanels on the right
        CustomPanel optionGeneralChoicePanel = new CustomPanel(400, 20, 0, 0, 0, 0);
        optionGeneralChoicePanel.setLayout(new GridLayout(1, 4));
        optionGeneralChoicePanel.add(optionsCheck);
        optionGeneralChoicePanel.add(saveIntCheck);
        optionGeneralChoicePanel.add(hypsometricCheck);

        cardManagerGeneral = new CardLayout();
        optionGeneralPanel = new CustomPanel(400, 550, 0, 0, 1, 1);
        optionGeneralPanel.setLayout(cardManagerGeneral);
        optionGeneralPanel.setBackground(optionsColor);
        optionTabLabel = new Label("Options");
        intervalTabLabel = new Label("Save Intervals");
        hypsometricTabLabel = new Label("Hypsometric");

        //big option panel
        CustomPanel optionPanel = new CustomPanel(400, 550, 0, 2, 2, 2, 149, 157, 191);
        optionPanel.setLayout(new BorderLayout());
        optionPanel.setBackground(optionsColor);
        CustomPanel optionTabPanel = new CustomPanel(400, 550, 5, 2, 0, 5);
        optionTabPanel.setLayout(new BorderLayout());
        optionTabPanel.setBackground(optionsColor);
        CustomPanel intervalTabPanel = new CustomPanel(400, 550, 5, 0, 0, 0);
        intervalTabPanel.setLayout(new BorderLayout());
        intervalTabPanel.setBackground(intervalColor);
        CustomPanel hypsometricTabPanel = new CustomPanel(400, 550, 10, 20, 10, 20);
        hypsometricTabPanel.setLayout(new BorderLayout());
        hypsometricTabPanel.setBackground(hypsometricColor);

        //all the options for the Options Panel
        CheckboxGroup setupradioButtons = new CheckboxGroup();
        initialconditionCheck = new Checkbox("Initial    ", true, setupradioButtons);
        initialconditionCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        initialconditionCheck.setBackground(initialconditionsColor);
        initialconditionCheck.addItemListener(this);
        Label initiallabel = new Label("     Conditions    ");
        initiallabel.setFont(new Font("Times Roman", Font.BOLD, 11));
        initiallabel.setBackground(initialconditionsColor);
        erodibilityCheck = new Checkbox("Erodibility    ", false, setupradioButtons);
        erodibilityCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        erodibilityCheck.setBackground(new Color(189, 197, 221));
        erodibilityCheck.addItemListener(this);
        climateCheck = new Checkbox("Climate    ", false, setupradioButtons);
        climateCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        climateCheck.setBackground(new Color(204, 212, 236));
        climateCheck.addItemListener(this);
        tectonicCheck = new Checkbox("Tectonics    ", false, setupradioButtons);
        tectonicCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        tectonicCheck.setBackground(new Color(219, 227, 251));
        tectonicCheck.addItemListener(this);
        Label fillerLabel4 = new Label("");
        fillerLabel4.setBackground(new Color(189, 197, 221));
        Label fillerLabel5 = new Label("");
        fillerLabel5.setBackground(new Color(204, 212, 236));
        Label fillerLabel6 = new Label("");
        fillerLabel6.setBackground(new Color(219, 227, 251));
        //top of option panel with the three options: Initial Conditions, Climate and Tectonics headers.
        CustomPanel optionChoicePanel = new CustomPanel(400, 30, 0, 0, 0, 0);
        optionChoicePanel.setLayout(new GridLayout(2, 4));
        optionChoicePanel.add(initialconditionCheck);
        optionChoicePanel.add(erodibilityCheck);
        optionChoicePanel.add(climateCheck);
        optionChoicePanel.add(tectonicCheck);
        optionChoicePanel.add(initiallabel);
        optionChoicePanel.add(fillerLabel4);
        optionChoicePanel.add(fillerLabel5);
        optionChoicePanel.add(fillerLabel6);

        //panels within Options
        CustomPanel optionCardPanel = new CustomPanel(400, 510, 0, 0, 0, 0);
        optionCardPanel.setLayout(new BorderLayout());
        CustomPanel intervalCardPanel = new CustomPanel(400, 510, 0, 0, 0, 0);
        intervalCardPanel.setLayout(new BorderLayout());
        CustomPanel hypsometricCardPanel = new CustomPanel(380, 510, 0, 0, 0, 0);
        hypsometricCardPanel.setLayout(new BorderLayout());


        //panels that belong to INITIAL CONDITIONS
        //top part
        cardManagerUp = new CardLayout();
        optionCardManagerPanel = new CustomPanel(400, 510, 0, 0, 0, 0);
        optionCardManagerPanel.setLayout(cardManagerUp);
        CustomPanel initialConditionsTabPanel = new CustomPanel(400, 400, 0, 0, 0, 0);
        initialConditionsTabPanel.setLayout(new BorderLayout());
        initialConditionsTabPanel.setBackground(initialconditionsColor);
        CustomPanel initialConditionsTabPanelupwest = new CustomPanel(270, 200, 0, 0, 8, 0);
        initialConditionsTabPanelupwest.setLayout(new GridBagLayout());
        CustomPanel initialConditionsTabPanelupeast = new CustomPanel(130, 200, 0, 0, 0, 0);
        initialConditionsTabPanelupeast.setLayout(new BorderLayout());
        CustomPanel initialConditionsTabPanelupchoicearrows = new CustomPanel(100, 200, 4, 4, 8, 0);
        initialConditionsTabPanelupchoicearrows.setLayout(new BorderLayout());
        CustomPanel initialConditionsTabPanelupchoice = new CustomPanel(20, 200, 0, 4, 0, 0);
        initialConditionsTabPanelupchoice.setLayout(new GridBagLayout());
        CustomPanel initialConditionsTabPaneluparrows = new CustomPanel(80, 200, 0, 0, 0, 0);
        initialConditionsTabPaneluparrows.setLayout(new GridBagLayout());
        CustomPanel initialConditionsTabPanelupslider = new CustomPanel(30, 200, 4, 0, 4, 0);
        initialConditionsTabPanelupslider.setLayout(new BorderLayout());
        CustomPanel initialConditionsTabPanelup = new CustomPanel(400, 170, 0, 0, 0, 0);
        initialConditionsTabPanelup.setLayout(new BorderLayout());
        CustomPanel initialConditionsTabPaneldown = new CustomPanel(400, 330, 0, 5, 5, 5);
        initialConditionsTabPaneldown.setLayout(new BorderLayout());
        CustomPanel initialConditionsTabPaneldownbutton = new CustomPanel(300, 20, 0, 0, 0, 0);
        initialConditionsTabPaneldownbutton.setLayout(new GridLayout(1, 3));
        Label initialConditionsTabLabel = new Label("Initial Conditions");

        //panels that belong to ERODIBILITY
        CustomPanel erodibilityTabPanel = new CustomPanel(400, 400, 0, 0, 0, 0);
        erodibilityTabPanel.setLayout(new BorderLayout());
        erodibilityTabPanel.setBackground(new Color(189, 197, 221));
        Label erodibilityTabLabel = new Label("Erodibility");
        //bottom part
        CustomPanel erodibilityTabPanelup = new CustomPanel(400, 170, 0, 0, 0, 0);
        erodibilityTabPanelup.setLayout(new BorderLayout());
        CustomPanel erodibilityTabPanelupwest = new CustomPanel(220, 170, 0, 28, 0, 0);
        erodibilityTabPanelupwest.setLayout(new GridBagLayout());
        CustomPanel erodibilityTabPanelupeast = new CustomPanel(180, 170, 0, 0, 0, 0);
        erodibilityTabPanelupeast.setLayout(new BorderLayout());
        CustomPanel erodibilityTabPanelupeasttop = new CustomPanel(500, 40, 0, 0, 0, 0);
        erodibilityTabPanelupeasttop.setLayout(new GridBagLayout());
        CustomPanel erodibilityTabPanelupchoicearrows = new CustomPanel(150, 170, 0, 4, 0, 0);
        erodibilityTabPanelupchoicearrows.setLayout(new BorderLayout());
        CustomPanel erodibilityTabPanelupchoice = new CustomPanel(110, 130, 0, 4, 0, 0);
        erodibilityTabPanelupchoice.setLayout(new GridBagLayout());
        CustomPanel erodibilityTabPaneluparrows = new CustomPanel(40, 130, 0, 0, 0, 0);
        erodibilityTabPaneluparrows.setLayout(new GridBagLayout());
        CustomPanel erodibilityTabPanelupslider = new CustomPanel(30, 170, 4, 0, 4, 0);
        erodibilityTabPanelupslider.setLayout(new BorderLayout());
        CustomPanel erodibilityTabPaneldown = new CustomPanel(400, 330, 0, 5, 5, 5);
        erodibilityTabPaneldown.setLayout(new BorderLayout());
        CustomPanel erodibilityTabPaneldownbutton = new CustomPanel(300, 20, 0, 0, 0, 0);
        erodibilityTabPaneldownbutton.setLayout(new GridLayout(1, 3));

        //panels that belong to CLIMATE
        CustomPanel climateTabPanel = new CustomPanel(400, 400, 0, 0, 0, 0);
        climateTabPanel.setLayout(new BorderLayout());
        climateTabPanel.setBackground(new Color(204, 212, 236));
        Label climateTabLabel = new Label("Climate");
        CustomPanel climateTabPanelup = new CustomPanel(400, 170, 0, 0, 0, 0);
        climateTabPanelup.setLayout(new BorderLayout());
        CustomPanel climateTabPanelupwest = new CustomPanel(330, 170, 0, 0, 0, 0);
        climateTabPanelupwest.setLayout(new GridBagLayout());
        CustomPanel climateTabPanelupeast = new CustomPanel(70, 170, 0, 0, 0, 0);
        climateTabPanelupeast.setLayout(new BorderLayout());
        CustomPanel climateTabPanelupeasttop = new CustomPanel(70, 30, 0, 0, 0, 0);
        CustomPanel climateTabPanelupchoicearrows = new CustomPanel(40, 140, 20, 0, 25, 0);
        climateTabPanelupchoicearrows.setLayout(new GridBagLayout());
        CustomPanel climateTabPanelupslider = new CustomPanel(30, 140, 20, 0, 20, 0);
        climateTabPanelupslider.setLayout(new BorderLayout());
        CustomPanel climateTabPaneldown = new CustomPanel(400, 330, 0, 5, 5, 5);
        climateTabPaneldown.setLayout(new BorderLayout());
        CustomPanel climateTabPaneldownbutton = new CustomPanel(300, 20, 0, 0, 0, 0);
        climateTabPaneldownbutton.setLayout(new GridLayout(1, 3));

        //panels that belong to TECTONICS
        CustomPanel tectonicsTabPanel = new CustomPanel(400, 400, 0, 0, 0, 0);
        tectonicsTabPanel.setLayout(new BorderLayout());
        tectonicsTabPanel.setBackground(new Color(219, 227, 251));
        Label tectonicsTabLabel = new Label("Tectonics");
        CustomPanel tectonicsTabPanelup = new CustomPanel(400, 170, 0, 0, 0, 0);
        tectonicsTabPanelup.setLayout(new BorderLayout());
        CustomPanel tectonicsTabPanelupwest = new CustomPanel(225, 170, 0, 0, 0, 0);
        tectonicsTabPanelupwest.setLayout(new GridBagLayout());
        CustomPanel tectonicsTabPanelupeast = new CustomPanel(175, 170, 0, 0, 0, 0);
        tectonicsTabPanelupeast.setLayout(new BorderLayout());
        CustomPanel tectonicsTabPanelupeasttop = new CustomPanel(180, 45, 0, 0, 0, 0);
        CustomPanel tectonicsTabPanelupchoicearrows = new CustomPanel(152, 125, 0, 4, 0, 0);
        tectonicsTabPanelupchoicearrows.setLayout(new BorderLayout());
        CustomPanel tectonicsTabPanelupchoice = new CustomPanel(130, 125, 5, 4, 0, 0);
        tectonicsTabPanelupchoice.setLayout(new GridBagLayout());
        CustomPanel tectonicsTabPaneluparrows = new CustomPanel(22, 125, 2, 0, 0, 0);
        tectonicsTabPaneluparrows.setLayout(new GridBagLayout());
        CustomPanel tectonicsTabPanelupslider = new CustomPanel(28, 125, 4, 0, 0, 0);
        tectonicsTabPanelupslider.setLayout(new BorderLayout());
        CustomPanel tectonicsTabPaneldown = new CustomPanel(400, 330, 0, 5, 5, 5);
        tectonicsTabPaneldown.setLayout(new BorderLayout());
        CustomPanel tectonicsTabPaneldownbutton = new CustomPanel(300, 20, 0, 0, 0, 0);
        tectonicsTabPaneldownbutton.setLayout(new GridLayout(1, 3));


        //big left panel that contains the animation and the buttons
        CustomPanel erosionPanel = new CustomPanel(540, 550, 2, 2, 2, 2, 149, 157, 191);
        erosionPanel.setLayout(new BorderLayout());

        //West panel of the upper part of initial conditions
        //GRID SIZE
        //all concerning x
        Label gridsizeLabel = new Label("             Grid Size: ");
        gridsizeLabel.setFont(new Font("Times", Font.BOLD, 11));
        constraint(initialConditionsTabPanelupwest, gridsizeLabel, 2, 1, 1, 2,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        xmaxLabel = new Label("x max = ");
        xmaxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, xmaxLabel, 4, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        xmaxsliderLabel = new Label("60");
        xmaxsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, xmaxsliderLabel, 5, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        //all concerning y
        ymaxLabel = new Label("y max = ");
        ymaxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, ymaxLabel, 4, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        ymaxsliderLabel = new Label("100");
        ymaxsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, ymaxsliderLabel, 5, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        //END TIME
        Label endtimeLabel = new Label("            End Time: ");
        endtimeLabel.setFont(new Font("Times", Font.BOLD, 11));
        constraint(initialConditionsTabPanelupwest, endtimeLabel, 2, 11, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        endtimeyearLabel = new Label("# of iterations =");
        endtimeyearLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, endtimeyearLabel, 4, 11, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        endtimesliderLabel = new Label("" + (timeminimum * 100));
        endtimesliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, endtimesliderLabel, 5, 11, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        //TOPOGRAPHY
        Label topographyLabel = new Label("       Topography: ");
        topographyLabel.setFont(new Font("Times", Font.BOLD, 11));
        constraint(initialConditionsTabPanelupwest, topographyLabel, 2, 17, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        topographydefaultLabel = new Label("slope = ");
        topographydefaultLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, topographydefaultLabel, 4, 17, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        topographyslopeLabel = new decimalLabel();
        topographyslopeLabel.set2decimalText(SharedParameters.SLOPE);
        topographyslopeLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(initialConditionsTabPanelupwest, topographyslopeLabel, 5, 17, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        //East panel of the upper part of initial conditions
        //INITIAL CONDITIONS components
        Label advancedoptions1Label = new Label("                                                                                               Advanced Options");
        advancedoptions1Label.setFont(new Font("Times Roman", Font.BOLD, 11));
        initialConditionsTabPanelup.add(advancedoptions1Label, BorderLayout.NORTH);
        CheckboxGroup initadvancedconditions = new CheckboxGroup();
        initadvancedgridx = new Checkbox("", false, initadvancedconditions);
        initadvancedgridx.setFont(new Font("Times Roman", Font.BOLD, 10));
        initadvancedgridx.addItemListener(this);
        constraint(initialConditionsTabPanelupchoice, initadvancedgridx, 2, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        gridxarrow = new Label("                           ");
        gridxarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(initialConditionsTabPaneluparrows, gridxarrow, 2, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST
        );
        initadvancedgridy = new Checkbox("", false, initadvancedconditions);
        initadvancedgridy.setFont(new Font("Times Roman", Font.BOLD, 10));
        initadvancedgridy.addItemListener(this);
        constraint(initialConditionsTabPanelupchoice, initadvancedgridy, 2, 2, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        gridyarrow = new Label("                           ");
        gridyarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(initialConditionsTabPaneluparrows, gridyarrow, 2, 4, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST
        );

        initadvancedendtime = new Checkbox("", false, initadvancedconditions);
        initadvancedendtime.setFont(new Font("Times Roman", Font.BOLD, 10));
        initadvancedendtime.addItemListener(this);
        constraint(initialConditionsTabPanelupchoice, initadvancedendtime, 2, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        endtimearrow = new Label("                           ");
        endtimearrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(initialConditionsTabPaneluparrows, endtimearrow, 2, 8, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST
        );
        initadvancedtopography = new Checkbox("", false, initadvancedconditions);
        initadvancedtopography.setFont(new Font("Times Roman", Font.BOLD, 10));
        initadvancedtopography.addItemListener(this);
        constraint(initialConditionsTabPanelupchoice, initadvancedtopography, 2, 4, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        topographyarrow = new Label("                           ");
        topographyarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(initialConditionsTabPaneluparrows, topographyarrow, 2, 10, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST
        );
        //this is a fake button in order to get the page started without any selection
        initadvancedfake = new Checkbox("", false, initadvancedconditions);
        initadvancedfake.addItemListener(this);
        constraint(initialConditionsTabPanelupwest, initadvancedfake, 2, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        Label label5 = new Label("");
        initialConditionsTabPanelupslider.add(label5, BorderLayout.EAST);
        interchangeablelabelnorth = new Label("      ");
        interchangeablelabelnorth.setFont(new Font("Times Roman", Font.BOLD, 8));
        initialConditionsTabPanelupslider.add(interchangeablelabelnorth, BorderLayout.NORTH);
        interchangeableSlider = new Scrollbar(Scrollbar.VERTICAL);
        interchangeableSlider.setMinimum(xminimum);
        interchangeableSlider.setMaximum(xmaximum);
        interchangeableSlider.setUnitIncrement(xyblock);
        interchangeableSlider.addAdjustmentListener(this);
        initialConditionsTabPanelupslider.add(interchangeableSlider, BorderLayout.CENTER);
        interchangeablelabelsouth = new Label("      ");
        interchangeablelabelsouth.setFont(new Font("Times Roman", Font.BOLD, 8));
        initialConditionsTabPanelupslider.add(interchangeablelabelsouth, BorderLayout.SOUTH);

        initialConditionsHelpButton = new Button("Show Help");
        initialConditionsHelpButton.addActionListener(this);
        Label label1 = new Label("");
        Label label2 = new Label("");
        initialConditionsTabPaneldownbutton.add(label1);
        initialConditionsTabPaneldownbutton.add(initialConditionsHelpButton);
        initialConditionsTabPaneldownbutton.add(label2);
        initialConditionsTabPaneldown.add(initialConditionsTabPaneldownbutton, BorderLayout.NORTH);
        initialConditionsHelpText = new TextArea("INITIAL CONDITIONS PARAMETERS\n\n" +
                "Select the change you want to make and you will see a horizontal line\n" +
                "joining the radio button and the slider on the right. Use the slider\n" +
                "to choose new values: \n\n" +
                "1. Grid Size,	\n" +
                "      This option is used to change the size of the topographic grid\n" +
                "   	a. select xmax for changing the width of the grid (number \n" +
                "   	   of columns)\n" +
                "      	default : 60 " +
                "	 minimum : 60" +
                "	    maximum : 100\n\n" +
                "   	b. select ymax for changing the length of the grid (number \n" +
                "   	   of rows)\n" +
                "      	default : 100" +
                "	 minimum : 100" +
                "	    maximum : 200\n\n" +
                "2. End Time, \n" +
                "      This option is used to choose number of iterations desired\n" +
                "      	default : 100,000" +
                "  minimum : 100,000" +
                "  maximum : 1,000,000\n\n" +
                "3. Topography, \n" +
                "      This option is used to select how much slope you want for the \n" +
                "      simulation\n" +
                "      	default : 0.01" +
                "  	 minimum : 0.01" +
                "  	    maximum : 0.30\n\n" +
                "");
        initialConditionsTabPaneldown.add(initialConditionsHelpText, BorderLayout.CENTER);

        //ERODIBILITY
        //uniform default values
        Label erodibilityadvancedoptions = new Label("                                                                                               Advanced Options");
        erodibilityadvancedoptions.setFont(new Font("Times Roman", Font.BOLD, 11));
        erodibilityTabPanelup.add(erodibilityadvancedoptions, BorderLayout.NORTH);
        Label label4 = new Label("");
        constraint(erodibilityTabPanelupwest, label4, 3, 0, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        CheckboxGroup erodibilityradioButtons = new CheckboxGroup();
        erodibilityuniformCheck = new Checkbox("uniform =             ", true, erodibilityradioButtons);
        erodibilityuniformCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        erodibilityuniformCheck.addItemListener(this);
        constraint(erodibilityTabPanelupwest, erodibilityuniformCheck, 3, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        erodibilityuniformvalueLabel = new decimalLabel();
        erodibilityuniformvalueLabel.set2decimalText(erodibilityuniform);
        erodibilityuniformvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupwest, erodibilityuniformvalueLabel, 4, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        Label labelc = new Label("           ");
        constraint(erodibilityTabPanelupwest, labelc, 5, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        //random values
        erodibilityrandomCheck = new Checkbox("random value =", false, erodibilityradioButtons);
        erodibilityrandomCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        erodibilityrandomCheck.addItemListener(this);
//	constraint(erodibilityTabPanelupwest, erodibilityrandomCheck, 3, 3, 1, 1,
//		   GridBagConstraints.NONE, GridBagConstraints.WEST,
//		   1.0, 1.0, 1, 1, 1, 1);
        randomsliderLabel = new Label("              ");
        randomsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(erodibilityTabPanelupwest, randomsliderLabel, 3, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        randomsliderLabel1 = new Label("             ");
        randomsliderLabel1.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(erodibilityTabPanelupwest, randomsliderLabel1, 4, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        //break at x
        erodibilitybpointxCheck = new Checkbox("break at x =", false, erodibilityradioButtons);
        erodibilitybpointxCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        erodibilitybpointxCheck.addItemListener(this);
        constraint(erodibilityTabPanelupwest, erodibilitybpointxCheck, 3, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        erodibilitybpointxLabel = new Label("  " + minimumxpoint);
        erodibilitybpointxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(erodibilityTabPanelupwest, erodibilitybpointxLabel, 3, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );
        erodibilitybreakxarrow = new Label("           ");
        erodibilitybreakxarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(erodibilityTabPanelupwest, erodibilitybreakxarrow, 4, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );

        Label label7 = new Label("");
        constraint(erodibilityTabPanelupwest, label7, 4, 8, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        erodibilitylabelnorth = new Label("  ");
        erodibilitylabelnorth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupwest, erodibilitylabelnorth, 5, 6, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        erodibilitySlider = new Scrollbar(Scrollbar.VERTICAL);
        erodibilitySlider.setMinimum(xminimum);
        erodibilitySlider.setMaximum(xmaximum);
        erodibilitySlider.setUnitIncrement(xyblock);
        erodibilitySlider.addAdjustmentListener(this);
        constraint(erodibilityTabPanelupwest, erodibilitySlider, 5, 7, 1, 7,
                GridBagConstraints.VERTICAL, GridBagConstraints.WEST
        );

        // break at y
        erodibilitybpointyCheck = new Checkbox("break at y =", false, erodibilityradioButtons);
        erodibilitybpointyCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        erodibilitybpointyCheck.addItemListener(this);
        constraint(erodibilityTabPanelupwest, erodibilitybpointyCheck, 3, 13, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        erodibilitybpointyLabel = new Label("  " + minimumypoint);
        erodibilitybpointyLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(erodibilityTabPanelupwest, erodibilitybpointyLabel, 3, 13, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );
        erodibilitybreakyarrow = new Label("           ");
        erodibilitybreakyarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(erodibilityTabPanelupwest, erodibilitybreakyarrow, 4, 13, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );
        Label label6 = new Label("");
        constraint(erodibilityTabPanelupwest, label6, 4, 14, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        erodibilitylabelsouth = new Label(" ");
        erodibilitylabelsouth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupwest, erodibilitylabelsouth, 5, 14, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        // advanced conditions for erodibility
        CheckboxGroup erodibilityadvancedconditions = new CheckboxGroup();
        erodibilityleft = new Checkbox("         ", false, erodibilityadvancedconditions);
        erodibilityleft.setFont(new Font("Times Roman", Font.BOLD, 9));
        erodibilityleft.addItemListener(this);
        constraint(erodibilityTabPanelupchoice, erodibilityleft, 1, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        erodibilityxleftvalueLabel = new decimalLabel();
        erodibilityxleftvalueLabel.set2decimalText(0.01);
        erodibilityxleftvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupchoice, erodibilityxleftvalueLabel, 2, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );

        erodibilityxrightvalueLabel = new decimalLabel();
        erodibilityxrightvalueLabel.set2decimalText(0.01);
        erodibilityxrightvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupchoice, erodibilityxrightvalueLabel, 2, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );

        erodibilityytopvalueLabel = new decimalLabel();
        erodibilityytopvalueLabel.set2decimalText(0.01);
        erodibilityytopvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupchoice, erodibilityytopvalueLabel, 2, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );
        erodibilityfake = new Checkbox("       ", false, erodibilityadvancedconditions);
        erodibilityfake.addItemListener(this);
        constraint(erodibilityTabPanelupwest, erodibilityfake, 4, 10, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        erodibilitybottom = new Checkbox("          ", false, erodibilityadvancedconditions);
        erodibilitybottom.setFont(new Font("Times Roman", Font.BOLD, 9));
        erodibilitybottom.addItemListener(this);
        constraint(erodibilityTabPanelupchoice, erodibilitybottom, 1, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        erodibilityybottomvalueLabel = new decimalLabel();
        erodibilityybottomvalueLabel.set2decimalText(0.01);
        erodibilityybottomvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(erodibilityTabPanelupchoice, erodibilityybottomvalueLabel, 2, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );

        erodibilityuniformarrow = new Label("                              ");
        erodibilityuniformarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(erodibilityTabPanelupeasttop, erodibilityuniformarrow, 1, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST
        );

        erodibilitybreakxleftarrow = new Label("                ");
        erodibilitybreakxleftarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(erodibilityTabPaneluparrows, erodibilitybreakxleftarrow, 1, 4, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );
        erodibilitybreakxrightarrow = new Label("                ");
        erodibilitybreakxrightarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(erodibilityTabPaneluparrows, erodibilitybreakxrightarrow, 1, 7, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        Label label12 = new Label("");
        erodibilityTabPanelupslider.add(label12, BorderLayout.EAST);
        erodibilityylabelnorth = new Label("0.01  ");
        erodibilityylabelnorth.setFont(new Font("Times Roman", Font.BOLD, 8));
        erodibilityTabPanelupslider.add(erodibilityylabelnorth, BorderLayout.NORTH);
        erodibilityadvancedSlider = new Scrollbar(Scrollbar.VERTICAL);
        erodibilityadvancedSlider.setMinimum(uniformminimum);
        erodibilityadvancedSlider.setMaximum(uniformmaximum);
        erodibilityadvancedSlider.setUnitIncrement(uniformstep);
        erodibilityadvancedSlider.addAdjustmentListener(this);
        erodibilityTabPanelupslider.add(erodibilityadvancedSlider, BorderLayout.CENTER);
        erodibilityylabelsouth = new Label("0.05  ");
        erodibilityylabelsouth.setFont(new Font("Times Roman", Font.BOLD, 8));
        erodibilityTabPanelupslider.add(erodibilityylabelsouth, BorderLayout.SOUTH);

        erodibilityHelpButton = new Button("Show Help");
        erodibilityHelpButton.addActionListener(this);
        Label label8 = new Label("");
        Label label9 = new Label("");
        erodibilityTabPaneldownbutton.add(label8);
        erodibilityTabPaneldownbutton.add(erodibilityHelpButton);
        erodibilityTabPaneldownbutton.add(label9);
        erodibilityTabPaneldown.add(erodibilityTabPaneldownbutton, BorderLayout.NORTH);
        erodibilityHelpText = new TextArea("ERODIBILITY PARAMETERS\n\n" +
                "Select the change you want to make and you will see a horizontal line\n" +
                "joining the radio button and the slider on the right. Use the slider\n" +
                "to choose new values: \n\n" +
                "1. Uniform,  	\n" +
                "      This option is used to set a uniform erosion value\n" +
                "      for the whole topographic grid\n" +
                "      	default : 0.05" +
                "		minimum : 0.01" +
                "		maximum : 0.05\n\n" +
                "2. Break at x, \n" +
                "      This option is used to choose a break point at a certain column.\n" +
                "      	default : 0" +
                "            minimum : 0" +
                "            maximum : max # of columns\n\n" +
                "      Then, choose the erodibility value for the grid cells on the left\n" +
                "      and right side of the break point.                              \n" +
                "      	default : 0.05" +
                "		minimum : 0.01" +
                "		maximum : 0.05\n\n" +
                "3. Break at y, \n" +
                "      This option is used to choose a break point at a certain row.\n" +
                "      	default : 0" +
                "            minimum : 0" +
                "            maximum : max # of rows\n\n" +
                "      Then, choose the erodibility value for the grid cells on the top\n" +
                "      and the bottom side of the break point.                         \n" +
                "      	default : 0.05" +
                "		minimum : 0.01" +
                "		maximum : 0.05\n\n" +
                "");
        erodibilityTabPaneldown.add(erodibilityHelpText, BorderLayout.CENTER);


        //CLIMATE
        Label climateadvancedoptions = new Label("                                                                                               Advanced Options");
        climateadvancedoptions.setFont(new Font("Times Roman", Font.BOLD, 11));
        climateTabPanelup.add(climateadvancedoptions, BorderLayout.NORTH);
        Label rainfallLabel = new Label("Rainfall");
        rainfallLabel.setFont(new Font("Times", Font.BOLD, 11));
        constraint(climateTabPanelupwest, rainfallLabel, 1, 0, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );
        Label rainfallLabel1 = new Label("Rate");
        rainfallLabel1.setFont(new Font("Times", Font.BOLD, 11));
        constraint(climateTabPanelupwest, rainfallLabel1, 2, 0, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        CheckboxGroup climatechoiceradioButtons = new CheckboxGroup();
        climatedefaultCheck = new Checkbox("Constant =", true, climatechoiceradioButtons);
        climatedefaultCheck.addItemListener(this);
        climatedefaultCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(climateTabPanelupwest, climatedefaultCheck, 2, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        climatedefaultvalueLabel = new decimalLabel();
        climatedefaultvalueLabel.set2decimalText(0.1);
        climatedefaultvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(climateTabPanelupwest, climatedefaultvalueLabel, 4, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        climateincreasingCheck = new Checkbox("Increasing", false, climatechoiceradioButtons);
        climateincreasingCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        climateincreasingCheck.addItemListener(this);
        constraint(climateTabPanelupwest, climateincreasingCheck, 2, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        increasingclimatearrow = new Label("        ");
        increasingclimatearrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(climateTabPanelupwest, increasingclimatearrow, 3, 3, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTHEAST
        );
        CheckboxGroup climateadvancedButtons = new CheckboxGroup();
        climateincreasinglow = new Checkbox("Low  = ", false, climateadvancedButtons);
        climateincreasinglow.setFont(new Font("Times Roman", Font.BOLD, 9));
        climateincreasinglow.addItemListener(this);
        constraint(climateTabPanelupwest, climateincreasinglow, 4, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        climatefake = new Checkbox("  ", false, climateadvancedButtons);
        climatefake.addItemListener(this);
        constraint(climateTabPanelupwest, climatefake, 5, 4, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        climateincreasingvaluelowLabel = new decimalLabel();
        climateincreasingvaluelowLabel.set2decimalText(0.05);
        climateincreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(climateTabPanelupwest, climateincreasingvaluelowLabel, 5, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        climatedecreasingvaluelowLabel = new decimalLabel();
        climatedecreasingvaluelowLabel.set2decimalText(0.05);
        climatedecreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(climateTabPanelupwest, climatedecreasingvaluelowLabel, 5, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        climateseparatorLabel = new Label("|");
        constraint(climateTabPanelupwest, climateseparatorLabel, 4, 4, 1, 1,
                GridBagConstraints.VERTICAL, GridBagConstraints.WEST
        );

        climatedecreasingCheck = new Checkbox("Decreasing", false, climatechoiceradioButtons);
        climatedecreasingCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        climatedecreasingCheck.addItemListener(this);
        constraint(climateTabPanelupwest, climatedecreasingCheck, 2, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        decreasingclimatearrow = new Label("        ");
        decreasingclimatearrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(climateTabPanelupwest, decreasingclimatearrow, 3, 5, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST
        );
        climateincreasinghigh = new Checkbox("High = ", false, climateadvancedButtons);
        climateincreasinghigh.setFont(new Font("Times Roman", Font.BOLD, 9));
        climateincreasinghigh.addItemListener(this);
        constraint(climateTabPanelupwest, climateincreasinghigh, 4, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        climateincreasingvaluehighLabel = new decimalLabel();
        climateincreasingvaluehighLabel.set2decimalText(0.05);
        climateincreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(climateTabPanelupwest, climateincreasingvaluehighLabel, 5, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        climatedecreasingvaluehighLabel = new decimalLabel();
        climatedecreasingvaluehighLabel.set2decimalText(0.01);
        climatedecreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(climateTabPanelupwest, climatedecreasingvaluehighLabel, 5, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );

        climatenote = new Label("***HIGH value should be greater than LOW value");
        climatenote.setFont(new Font("Times Roman", Font.ITALIC, 10));
        constraint(climateTabPanelupwest, climatenote, 2, 6, 4, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );


        climatedefaultarrow = new Label("___________________________");
        climatedefaultarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(climateTabPanelupchoicearrows, climatedefaultarrow, 1, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );

        lowclimatearrow = new Label("___________________________");
        lowclimatearrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(climateTabPanelupchoicearrows, lowclimatearrow, 1, 2, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );

        Label label0 = new Label("");
        constraint(climateTabPanelupchoicearrows, label0, 1, 3, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );

        highclimatearrow = new Label("___________________________");
        highclimatearrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(climateTabPanelupchoicearrows, highclimatearrow, 1, 4, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );

        Label label13 = new Label("");
        climateTabPanelupslider.add(label13, BorderLayout.EAST);
        Label climatenorth = new Label("0.05");
        climatenorth.setFont(new Font("Times Roman", Font.BOLD, 9));
        climateTabPanelupslider.add(climatenorth, BorderLayout.NORTH);
        climateSlider = new Scrollbar(Scrollbar.VERTICAL);
        climateSlider.setMinimum(climateminimum);
        climateSlider.setMaximum(climatemaximum);
        climateSlider.setUnitIncrement(climatedefaultblock);
        climateSlider.addAdjustmentListener(this);
        climateTabPanelupslider.add(climateSlider, BorderLayout.CENTER);
        Label climatesouth = new Label("0.15");
        climatesouth.setFont(new Font("Times Roman", Font.BOLD, 9));
        climateTabPanelupslider.add(climatesouth, BorderLayout.SOUTH);

        climateHelpButton = new Button("Show Help");
        climateHelpButton.addActionListener(this);
        Label label10 = new Label("");
        Label label11 = new Label("");
        climateTabPaneldownbutton.add(label10);
        climateTabPaneldownbutton.add(climateHelpButton);
        climateTabPaneldownbutton.add(label11);
        climateTabPaneldown.add(climateTabPaneldownbutton, BorderLayout.NORTH);
        climateHelpText = new TextArea("CLIMATE PARAMETERS\n\n" +
                "Select the change you want to make and you will see a horizontal line\n" +
                "joining the radio button and the slider on the right. Use the slider\n" +
                "to choose new values: \n\n" +
                "1. Constant,  	\n" +
                "      This option is used to set a constant rainfall rate\n" +
                "      for the whole duration of the simulation\n" +
                "      	default : 0.10	minimum : 0.05	maximum : 0.15\n\n" +
                "2. Increasing,  	\n" +
                "      This option is used to set the rainfall rate to increase\n" +
                "      linearly (from minimum to maximum) with time (iterations)\n" +
                "      A low value should be set:                           \n" +
                "      	default : 0.05	minimum : 0.05	maximum : 0.14\n" +
                "      A high value should be set:                           \n" +
                "      	default : 0.06	minimum : 0.06	maximum : 0.15\n\n" +
                "2. Decreasing,  	\n" +
                "      This option is used to set the rainfall rate to decrease\n" +
                "      linearly (from maximum to minimum) with time (iterations)\n" +
                "      A low value should be set:                         \n" +
                "      	default : 0.05	minimum : 0.05	maximum : 0.14\n\n" +
                "      A high value should be set:                        \n" +
                "      	default : 0.06	minimum : 0.06	maximum : 0.15\n\n" +
                "The slider values are connected in such a way that the low\n" +
                "value is always smaller than the high value.             \n" +
                "");
        climateTabPaneldown.add(climateHelpText, BorderLayout.CENTER);


        //TECTONICS
        Label tectonicsadvancedoptions = new Label("                                                                                               Advanced Options");
        tectonicsadvancedoptions.setFont(new Font("Times Roman", Font.BOLD, 11));
        tectonicsTabPanelup.add(tectonicsadvancedoptions, BorderLayout.NORTH);
        Label upliftrateLabel = new Label("Uplift");
        upliftrateLabel.setFont(new Font("Times", Font.BOLD, 11));
        constraint(tectonicsTabPanelupwest, upliftrateLabel, 2, 0, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );
        Label upliftrateLabel1 = new Label("Rate:");
        upliftrateLabel1.setFont(new Font("Times", Font.BOLD, 11));
        constraint(tectonicsTabPanelupwest, upliftrateLabel1, 3, 0, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        CheckboxGroup tectonicschoiceradioButtons = new CheckboxGroup();
        tectonicsdefaultCheck = new Checkbox("Fixed at 0", true, tectonicschoiceradioButtons);
        tectonicsdefaultCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        tectonicsdefaultCheck.addItemListener(this);
        constraint(tectonicsTabPanelupwest, tectonicsdefaultCheck, 3, 5, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        tectonicslabelCheck1 = new Label("(no uplift)");
        constraint(tectonicsTabPanelupwest, tectonicslabelCheck1, 4, 5, 3, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );

        tectonicsnorth = new Label("    ");
        tectonicsnorth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(tectonicsTabPanelupwest, tectonicsnorth, 6, 6, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        tectonicsbpointxCheck = new Checkbox("break at x =", false, tectonicschoiceradioButtons);
        tectonicsbpointxCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        tectonicsbpointxCheck.addItemListener(this);
        constraint(tectonicsTabPanelupwest, tectonicsbpointxCheck, 3, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        tectonicsbpointxLabel = new Label("" + minimumxpoint);
        tectonicsbpointxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(tectonicsTabPanelupwest, tectonicsbpointxLabel, 4, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );
        tectonicsSlider = new Scrollbar(Scrollbar.VERTICAL);
        tectonicsSlider.setMinimum(minimumxpoint);
        tectonicsSlider.setMaximum(SharedParameters.COLUMNS + 10);
        tectonicsSlider.setUnitIncrement(1);
        tectonicsSlider.addAdjustmentListener(this);
        constraint(tectonicsTabPanelupwest, tectonicsSlider, 6, 7, 1, 6,
                GridBagConstraints.VERTICAL, GridBagConstraints.WEST
        );
        Label label3 = new Label(" ");
        constraint(tectonicsTabPanelupwest, label3, 5, 9, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );

        tectonicsbreakxarrow = new Label("      ");
        tectonicsbreakxarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(tectonicsTabPanelupwest, tectonicsbreakxarrow, 5, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );
        tectonicsbpointyCheck = new Checkbox("break at y =", false, tectonicschoiceradioButtons);
        tectonicsbpointyCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
        tectonicsbpointyCheck.addItemListener(this);
        constraint(tectonicsTabPanelupwest, tectonicsbpointyCheck, 3, 12, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );
        tectonicsbpointyLabel = new Label("" + minimumypoint);
        tectonicsbpointyLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
        constraint(tectonicsTabPanelupwest, tectonicsbpointyLabel, 4, 12, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST
        );

        tectonicsbreakyarrow = new Label("      ");
        tectonicsbreakyarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(tectonicsTabPanelupwest, tectonicsbreakyarrow, 5, 12, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHEAST
        );

        tectonicssouth = new Label("   ");
        tectonicssouth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(tectonicsTabPanelupwest, tectonicssouth, 6, 13, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.NORTHWEST
        );


        CheckboxGroup tectonicsadvancedconditions = new CheckboxGroup();
        tectonicsleft = new Checkbox("          ", false, tectonicsadvancedconditions);
        tectonicsleft.setFont(new Font("Times Roman", Font.BOLD, 9));
        tectonicsleft.addItemListener(this);
        constraint(tectonicsTabPanelupchoice, tectonicsleft, 1, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        tectonicsxleftvalueLabel = new decimalLabel();
        tectonicsxleftvalueLabel.set4decimalText(0);
        tectonicsxleftvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(tectonicsTabPanelupchoice, tectonicsxleftvalueLabel, 3, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );


        tectonicsytopvalueLabel = new decimalLabel();
        tectonicsytopvalueLabel.set4decimalText(0);
        tectonicsytopvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(tectonicsTabPanelupchoice, tectonicsytopvalueLabel, 3, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        tectonicsbreaktoparrow = new Label("      ");
        tectonicsbreaktoparrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(tectonicsTabPaneluparrows, tectonicsbreaktoparrow, 1, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        tectonicsfake = new Checkbox("          ", false, tectonicsadvancedconditions);
        tectonicsfake.addItemListener(this);
        constraint(tectonicsTabPanelupwest, tectonicsfake, 5, 11, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        tectonicsbottom = new Checkbox("          ", false, tectonicsadvancedconditions);
        tectonicsbottom.setFont(new Font("Times Roman", Font.BOLD, 9));
        tectonicsbottom.addItemListener(this);
        constraint(tectonicsTabPanelupchoice, tectonicsbottom, 1, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );
        Label label18 = new Label("");
        constraint(tectonicsTabPanelupchoice, label18, 2, 7, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );
        tectonicsxrightvalueLabel = new decimalLabel();
        tectonicsxrightvalueLabel.set4decimalText(0);
        tectonicsxrightvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(tectonicsTabPanelupchoice, tectonicsxrightvalueLabel, 3, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        Label label14 = new Label("");
        constraint(tectonicsTabPanelupchoice, label14, 4, 7, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        tectonicsybottomvalueLabel = new decimalLabel();
        tectonicsybottomvalueLabel.set4decimalText(0);
        tectonicsybottomvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(tectonicsTabPanelupchoice, tectonicsybottomvalueLabel, 3, 7, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        tectonicsbreakbottomarrow = new Label("      ");
        tectonicsbreakbottomarrow.setFont(new Font("Times Roman", Font.BOLD, 12));
        constraint(tectonicsTabPaneluparrows, tectonicsbreakbottomarrow, 1, 7, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        label12 = new Label("");
        tectonicsTabPanelupslider.add(label12, BorderLayout.EAST);
        Label tectonicsbreaknorth = new Label("0.0000      ");
        tectonicsbreaknorth.setFont(new Font("Times Roman", Font.BOLD, 8));
        tectonicsTabPanelupslider.add(tectonicsbreaknorth, BorderLayout.NORTH);
        tectonicsadvancedSlider = new Scrollbar(Scrollbar.VERTICAL);
        tectonicsadvancedSlider.setMinimum(tectonicsxleftminimum);
        tectonicsadvancedSlider.setMaximum(tectonicsxleftmaximum);
        tectonicsadvancedSlider.setUnitIncrement(tectonicsblock);
        tectonicsadvancedSlider.addAdjustmentListener(this);
        tectonicsTabPanelupslider.add(tectonicsadvancedSlider, BorderLayout.CENTER);
        Label tectonicsbreaksouth = new Label("0.0003      ");
        tectonicsbreaksouth.setFont(new Font("Times Roman", Font.BOLD, 8));
        tectonicsTabPanelupslider.add(tectonicsbreaksouth, BorderLayout.SOUTH);

        tectonicsHelpButton = new Button("Show Help");
        tectonicsHelpButton.addActionListener(this);
        Label label15 = new Label("");
        Label label16 = new Label("");
        tectonicsTabPaneldownbutton.add(label15);
        tectonicsTabPaneldownbutton.add(tectonicsHelpButton);
        tectonicsTabPaneldownbutton.add(label16);
        tectonicsTabPaneldown.add(tectonicsTabPaneldownbutton, BorderLayout.NORTH);
        tectonicsHelpText = new TextArea("TECTONICS PARAMETERS\n\n" +
                "Select the change you want to make and you will see a horizontal line\n" +
                "joining the radio button and the slider on the right. Use the slider\n" +
                "to choose new values: \n\n" +
                "1. Fixed at 0,  	\n" +
                "      This option is used to set uplift rate as 0, i.e., no uplift\n" +
                "2. Break at x, \n" +
                "      This option is used to choose a break point at a certain column.\n" +
                "      	default : 0" +
                "    	minimum : 0" +
                "            maximum : max # of columns\n\n" +
                "      Then, choose an uplift rate for grid cells either on the left\n" +
                "      side or the right side of the break point.            \n" +
                "      	default : 0.0000" +
                "	minimum : 0.0000" +
                "	maximum : 0.0003\n\n" +
                "3. Break at y, \n" +
                "      This option is used to choose a break point at a certain row.\n" +
                "      	default : 0" +
                "            minimum : 0" +
                "            maximum : max # of rows\n\n" +
                "      Then, choose the uplift rate for grid cells either on the top\n" +
                "      side or the bottom side of the break point.            \n" +
                "      	default : 0.0000" +
                "	minimum : 0.0000" +
                "	maximum : 0.0003\n\n" +
                "");
        tectonicsTabPaneldown.add(tectonicsHelpText, BorderLayout.CENTER);


        //down part
        //panels that belong to SAVE INTERVALS
        CustomPanel optionintervals = new CustomPanel(400, 550, 2, 0, 0, 0);
        optionintervals.setLayout(new BorderLayout());
        optionintervals.setBackground(intervalColor);
        CustomPanel optionintervals1north = new CustomPanel(400, 30, 0, 0, 0, 0);
        optionintervals1north.setLayout(new GridLayout(1, 3));
        optionintervals1north.setBackground(new Color(169, 187, 211));
        CheckboxGroup intervalradioButtons = new CheckboxGroup();
        averageprofileCheck = new Checkbox("Average profile", true, intervalradioButtons);
        averageprofileCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        averageprofileCheck.addItemListener(this);
        averageprofileCheck.setBackground(new Color(179, 197, 221));
        new ToolTip("Display average profiles along y (column) direction", averageprofileCheck);
        selectedcolumnCheck = new Checkbox("Column profile", false, intervalradioButtons);
        selectedcolumnCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        selectedcolumnCheck.addItemListener(this);
        selectedcolumnCheck.setBackground(new Color(189, 207, 231));
        new ToolTip("Display profiles along a selected column (use slider to select the column value)", selectedcolumnCheck);
        selectedrowCheck = new Checkbox("Row profile", false, intervalradioButtons);
        selectedrowCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
        selectedrowCheck.addItemListener(this);
        selectedrowCheck.setBackground(new Color(199, 217, 241));
        new ToolTip("Display profiles along a selected row (use slider to select the row value)", selectedrowCheck);
        optionintervals1north.add(averageprofileCheck);
        optionintervals1north.add(selectedcolumnCheck);
        optionintervals1north.add(selectedrowCheck);
        CustomPanel hypsometriccurve = new CustomPanel(400, 330, 2, 0, 0, 0);
        hypsometriccurve.setLayout(new BorderLayout());
        hypsometriccurve.setBackground(hypsometricColor);
        hypsometriccurve.add(eh, BorderLayout.CENTER);
        CustomPanel hypsometricdown = new CustomPanel(400, 100, 2, 0, 0, 0);
        hypsometricdown.setLayout(new BorderLayout());
        hypsometricdown.setBackground(hypsometricColor);

        CustomPanel optionintervals1 = new CustomPanel(400, 500, 0, 0, 0, 0);
        optionintervals1.setBackground(new Color(169, 187, 211));
        optionintervals1.setLayout(new BorderLayout());
        CustomPanel averageprofileIntervals = new CustomPanel(380, 500, 10, 10, 10, 10);
        averageprofileIntervals.setLayout(new BorderLayout());
        CustomPanel eintervalspanel = new CustomPanel(360, 240, 3, 3, 0, 3);
        eintervalspanel.setLayout(new BorderLayout());
        CustomPanel eintsedimentpanel = new CustomPanel(360, 240, 3, 3, 0, 3);
        eintsedimentpanel.setLayout(new BorderLayout());
        eintervalspanel.add(eintervals, BorderLayout.CENTER);
        eintsedimentpanel.add(eintsediment, BorderLayout.CENTER);
        averageprofileIntervals.setBackground(hypsometricColor);
        averageprofileIntervals.add(eintervalspanel, BorderLayout.NORTH);
        averageprofileIntervals.add(eintsedimentpanel, BorderLayout.SOUTH);

        CustomPanel columnprofileIntervals = new CustomPanel(380, 550, 10, 10, 10, 10);
        columnprofileIntervals.setLayout(new BorderLayout());
        columnprofileIntervals.setBackground(new Color(189, 207, 231));
        CustomPanel columnintervalcontrols = new CustomPanel(350, 40, 0, 0, 0, 0);
        columnintervalcontrols.setLayout(new GridBagLayout());

        Label columnintervalLabel = new Label("Column =");
        columnintervalLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(columnintervalcontrols, columnintervalLabel, 0, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );
        columnintervalvalueLabel = new Label("" + 1);
        columnintervalvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(columnintervalcontrols, columnintervalvalueLabel, 1, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        Label columnintervalLabelNorth = new Label("" + 1);
        columnintervalLabelNorth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(columnintervalcontrols, columnintervalLabelNorth, 4, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.EAST
        );
        columnintervalSlider = new Scrollbar(Scrollbar.HORIZONTAL);
        columnintervalSlider.setMinimum(0);
        columnintervalSlider.setMaximum(SharedParameters.COLUMNS + 10);
        columnintervalSlider.setValue(0);
        columnintervalSlider.setUnitIncrement(xyblock);
        columnintervalSlider.addAdjustmentListener(this);
        constraint(columnintervalcontrols, columnintervalSlider, 5, 1, 5, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );
        Label columnintervalLabelSouth = new Label("" + SharedParameters.COLUMNS);
        columnintervalLabelSouth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(columnintervalcontrols, columnintervalLabelSouth, 10, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.WEST
        );

        CustomPanel ecolumnspanel = new CustomPanel(360, 260, 3, 3, 0, 3);
        ecolumnspanel.setLayout(new BorderLayout());
        CustomPanel ecolsedimentpanel = new CustomPanel(360, 220, 3, 3, 0, 3);
        ecolsedimentpanel.setLayout(new BorderLayout());
        ecolumnspanel.add(columnintervalcontrols, BorderLayout.NORTH);
        ecolumnspanel.add(ecolumn, BorderLayout.CENTER);
        ecolsedimentpanel.add(ecolsediment, BorderLayout.CENTER);

        columnprofileIntervals.add(ecolumnspanel, BorderLayout.NORTH);
        columnprofileIntervals.add(ecolsedimentpanel, BorderLayout.SOUTH);


        CustomPanel rowprofileIntervals = new CustomPanel(380, 145, 10, 10, 10, 10);
        rowprofileIntervals.setLayout(new BorderLayout());
        rowprofileIntervals.setBackground(new Color(199, 217, 241));
        CustomPanel rowintervalcontrols = new CustomPanel(360, 40, 0, 0, 0, 0);
        rowintervalcontrols.setLayout(new GridBagLayout());
        Label rowintervalLabel = new Label("Row =       ");
        rowintervalLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(rowintervalcontrols, rowintervalLabel, 0, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );
        rowintervalvalueLabel = new Label("" + 1);
        rowintervalvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(rowintervalcontrols, rowintervalvalueLabel, 1, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        Label rowintervalLabelNorth = new Label("    " + 1);
        rowintervalLabelNorth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(rowintervalcontrols, rowintervalLabelNorth, 4, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST
        );
        rowintervalSlider = new Scrollbar(Scrollbar.HORIZONTAL);
        rowintervalSlider.setMinimum(0);
        rowintervalSlider.setMaximum(SharedParameters.ROWS + 10);
        rowintervalSlider.setValue(0);
        rowintervalSlider.setUnitIncrement(xyblock);
        rowintervalSlider.addAdjustmentListener(this);
        constraint(rowintervalcontrols, rowintervalSlider, 5, 1, 5, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );
        Label rowintervalLabelSouth = new Label("" + SharedParameters.ROWS);
        rowintervalLabelSouth.setFont(new Font("Times Roman", Font.BOLD, 9));
        constraint(rowintervalcontrols, rowintervalLabelSouth, 10, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST
        );

        CustomPanel erowpanel = new CustomPanel(360, 260, 3, 3, 0, 3);
        erowpanel.setLayout(new BorderLayout());
        CustomPanel erowsedimentpanel = new CustomPanel(360, 220, 3, 3, 0, 3);
        erowsedimentpanel.setLayout(new BorderLayout());
        erowpanel.add(rowintervalcontrols, BorderLayout.NORTH);
        erowpanel.add(erow, BorderLayout.CENTER);
        erowsedimentpanel.add(erowsediment, BorderLayout.CENTER);

        rowprofileIntervals.add(erowpanel, BorderLayout.NORTH);
        rowprofileIntervals.add(erowsedimentpanel, BorderLayout.SOUTH);

        cardLabelIntervals1 = new Label("Average Profile");
        cardLabelIntervals2 = new Label("Column Profile");
        cardLabelIntervals3 = new Label("Row Profile");
        optionintervals1down = new CustomPanel(398, 500, 0, 0, 0, 0);
        optionintervals1down.setBackground(new Color(169, 187, 211));
        cardManagerIntervals = new CardLayout();
        optionintervals1down.setLayout(cardManagerIntervals);
        optionintervals1down.add(averageprofileIntervals, cardLabelIntervals1.getText());
        optionintervals1down.add(columnprofileIntervals, cardLabelIntervals2.getText());
        optionintervals1down.add(rowprofileIntervals, cardLabelIntervals3.getText());
        optionintervals1.add(optionintervals1down, BorderLayout.CENTER);
        optionintervals1.add(optionintervals1north, BorderLayout.NORTH);
        optionintervals.add(optionintervals1, BorderLayout.CENTER);


        CustomPanel snapshot25Panel = new CustomPanel(425, 300, 0, 0, 10, 0);
        snapshot25Panel.setLayout(new BorderLayout());
        CustomPanel ecanv25TextPanel = new CustomPanel(125, 300, 120, 0, 110, 5);
        ecanv25TextPanel.setLayout(new BorderLayout());
        ecanv25TextPanel.setBackground(snapshotpanelColor);
        Label ecanv25Label1 = new Label("Image will display");
        ecanv25Label1.setFont(new Font("Times Roman", Font.BOLD, 11));
        Label ecanv25Label2 = new Label("after 25% iterations");
        ecanv25Label2.setFont(new Font("Times Roman", Font.BOLD, 11));
        ecanv25TextPanel.add(ecanv25Label1, BorderLayout.NORTH);
        ecanv25TextPanel.add(ecanv25Label2, BorderLayout.SOUTH);
        snapshot25Panel.add(ecanv25TextPanel, BorderLayout.WEST);
        snapshot25Panel.add(e25p, BorderLayout.CENTER);
        e25p.setBackgroundColor(snapshotpanelColor);
        CustomPanel snapshot50Panel = new CustomPanel(425, 300, 0, 0, 10, 0);
        snapshot50Panel.setLayout(new BorderLayout());
        CustomPanel ecanv50TextPanel = new CustomPanel(125, 300, 120, 0, 110, 5);
        ecanv50TextPanel.setLayout(new BorderLayout());
        ecanv50TextPanel.setBackground(snapshotpanelColor);
        Label ecanv50Label1 = new Label("Image will display");
        ecanv50Label1.setFont(new Font("Times Roman", Font.BOLD, 11));
        Label ecanv50Label2 = new Label("after 50% iterations");
        ecanv50Label2.setFont(new Font("Times Roman", Font.BOLD, 11));
        ecanv50TextPanel.add(ecanv50Label1, BorderLayout.NORTH);
        ecanv50TextPanel.add(ecanv50Label2, BorderLayout.SOUTH);
        snapshot50Panel.add(ecanv50TextPanel, BorderLayout.WEST);
        snapshot50Panel.add(e50p, BorderLayout.CENTER);
        e50p.setBackgroundColor(snapshotpanelColor);
        CustomPanel snapshot75Panel = new CustomPanel(425, 300, 0, 0, 10, 0);
        snapshot75Panel.setLayout(new BorderLayout());
        CustomPanel ecanv75TextPanel = new CustomPanel(125, 300, 120, 0, 110, 5);
        ecanv75TextPanel.setLayout(new BorderLayout());
        ecanv75TextPanel.setBackground(snapshotpanelColor);
        Label ecanv75Label1 = new Label("Image will display");
        ecanv75Label1.setFont(new Font("Times Roman", Font.BOLD, 11));
        Label ecanv75Label2 = new Label("after 75% iterations");
        ecanv75Label2.setFont(new Font("Times Roman", Font.BOLD, 11));
        ecanv75TextPanel.add(ecanv75Label1, BorderLayout.NORTH);
        ecanv75TextPanel.add(ecanv75Label2, BorderLayout.SOUTH);
        snapshot75Panel.add(ecanv75TextPanel, BorderLayout.WEST);
        snapshot75Panel.add(e75p, BorderLayout.CENTER);
        e75p.setBackgroundColor(snapshotpanelColor);
        CustomPanel snapshot100Panel = new CustomPanel(425, 300, 0, 0, 10, 0);
        snapshot100Panel.setLayout(new BorderLayout());
        CustomPanel ecanv100TextPanel = new CustomPanel(125, 300, 120, 0, 110, 5);
        ecanv100TextPanel.setLayout(new BorderLayout());
        ecanv100TextPanel.setBackground(snapshotpanelColor);
        Label ecanv100Label1 = new Label("Image will display");
        ecanv100Label1.setFont(new Font("Times Roman", Font.BOLD, 11));
        Label ecanv100Label2 = new Label("after 100% iterations");
        ecanv100Label2.setFont(new Font("Times Roman", Font.BOLD, 11));
        ecanv100TextPanel.add(ecanv100Label1, BorderLayout.NORTH);
        ecanv100TextPanel.add(ecanv100Label2, BorderLayout.SOUTH);
        snapshot100Panel.add(ecanv100TextPanel, BorderLayout.WEST);
        snapshot100Panel.add(e100p, BorderLayout.CENTER);
        e100p.setBackgroundColor(snapshotpanelColor);

        //add panels
        sPanel = new SurfacePanel(sparams, e2, e1);
        erosionPanel.add(sPanel, BorderLayout.CENTER);
        initialConditionsTabPanelup.add(initialConditionsTabPanelupwest, BorderLayout.WEST);
        initialConditionsTabPanelupchoicearrows.add(initialConditionsTabPanelupchoice, BorderLayout.WEST);
        initialConditionsTabPanelupchoicearrows.add(initialConditionsTabPaneluparrows, BorderLayout.EAST);
        initialConditionsTabPanelupeast.add(initialConditionsTabPanelupchoicearrows, BorderLayout.WEST);
        initialConditionsTabPanelupeast.add(initialConditionsTabPanelupslider, BorderLayout.EAST);
        initialConditionsTabPanelup.add(initialConditionsTabPanelupeast, BorderLayout.EAST);
        initialConditionsTabPanel.add(initialConditionsTabPanelup, BorderLayout.NORTH);
        initialConditionsTabPanel.add(initialConditionsTabPaneldown, BorderLayout.SOUTH);
        climateTabPanelupeast.add(climateTabPanelupchoicearrows, BorderLayout.WEST);
        climateTabPanelupeast.add(climateTabPanelupslider, BorderLayout.EAST);
        climateTabPanelup.add(climateTabPanelupeast, BorderLayout.EAST);
        climateTabPanelup.add(climateTabPanelupwest, BorderLayout.WEST);
        climateTabPanel.add(climateTabPanelup, BorderLayout.NORTH);
        climateTabPanel.add(climateTabPaneldown, BorderLayout.SOUTH);
        tectonicsTabPanelupchoicearrows.add(tectonicsTabPaneluparrows, BorderLayout.EAST);
        tectonicsTabPanelupchoicearrows.add(tectonicsTabPanelupchoice, BorderLayout.WEST);
        tectonicsTabPanelupeast.add(tectonicsTabPanelupeasttop, BorderLayout.NORTH);
        tectonicsTabPanelupeast.add(tectonicsTabPanelupslider, BorderLayout.EAST);
        tectonicsTabPanelupeast.add(tectonicsTabPanelupchoicearrows, BorderLayout.WEST);
        tectonicsTabPanelup.add(tectonicsTabPanelupeast, BorderLayout.EAST);
        tectonicsTabPanelup.add(tectonicsTabPanelupwest, BorderLayout.WEST);
        tectonicsTabPanel.add(tectonicsTabPanelup, BorderLayout.NORTH);
        tectonicsTabPanel.add(tectonicsTabPaneldown, BorderLayout.SOUTH);
        erodibilityTabPanelupchoicearrows.add(erodibilityTabPanelupeasttop, BorderLayout.NORTH);
        erodibilityTabPanelupchoicearrows.add(erodibilityTabPanelupchoice, BorderLayout.WEST);
        erodibilityTabPanelupchoicearrows.add(erodibilityTabPaneluparrows, BorderLayout.EAST);
        erodibilityTabPanelupeast.add(erodibilityTabPanelupchoicearrows, BorderLayout.WEST);
        erodibilityTabPanelupeast.add(erodibilityTabPanelupslider, BorderLayout.EAST);
        erodibilityTabPanelup.add(erodibilityTabPanelupeast, BorderLayout.EAST);
        erodibilityTabPanelup.add(erodibilityTabPanelupwest, BorderLayout.WEST);
        erodibilityTabPanel.add(erodibilityTabPanelup, BorderLayout.NORTH);
        erodibilityTabPanel.add(erodibilityTabPaneldown, BorderLayout.SOUTH);
        optionCardManagerPanel.add(initialConditionsTabPanel, initialConditionsTabLabel.getText());
        optionCardManagerPanel.add(erodibilityTabPanel, erodibilityTabLabel.getText());
        optionCardManagerPanel.add(climateTabPanel, climateTabLabel.getText());
        optionCardManagerPanel.add(tectonicsTabPanel, tectonicsTabLabel.getText());
        optionCardPanel.add(optionCardManagerPanel, BorderLayout.NORTH);
        intervalCardPanel.add(optionintervals, BorderLayout.CENTER);
        hypsometricCardPanel.add(hypsometriccurve, BorderLayout.NORTH);
        hypsometricCardPanel.add(hypsometricdown, BorderLayout.SOUTH);
        optionTabPanel.add(optionChoicePanel, BorderLayout.NORTH);
        optionTabPanel.add(optionCardPanel, BorderLayout.CENTER);
        intervalTabPanel.add(intervalCardPanel, BorderLayout.CENTER);
        hypsometricTabPanel.add(hypsometricCardPanel, BorderLayout.CENTER);
        optionGeneralPanel.add(optionTabPanel, optionTabLabel.getText());
        optionGeneralPanel.add(intervalTabPanel, intervalTabLabel.getText());
        optionGeneralPanel.add(hypsometricTabPanel, hypsometricTabLabel.getText());
        optionPanel.add(optionGeneralChoicePanel, BorderLayout.NORTH);
        optionPanel.add(optionGeneralPanel, BorderLayout.CENTER);
        simulationPanel.add(erosionPanel, BorderLayout.WEST);
        simulationPanel.add(optionPanel, BorderLayout.EAST);
        snapshotPanel.add(snapshot25Panel);
        snapshotPanel.add(snapshot50Panel);
        snapshotPanel.add(snapshot75Panel);
        snapshotPanel.add(snapshot100Panel);
        imagesPanel.add(simulationPanel, cardLabelSimulation.getText());
        imagesPanel.add(snapshotPanel, cardLabelSnapshot.getText());
        thisPanel.add(imagesChoicePanel, BorderLayout.NORTH);
        thisPanel.add(imagesPanel, BorderLayout.CENTER);
    }//end constructor

    /**
     * ********************************************************************************************
     * to get the applet started
     * *********************************************************************************************
     */
    public void start() {
        // This is unfortunate.  When widgets are made invisible before 
        // UI actually appears, subsequent visibility updates are flaky.		    
        sPanel.repaint();
        initadvancedfake.setVisible(false);
        setComponentColor(1);
        setArrow(1);
        setLabelValue(12);
        setLabelValue(13);
        setLabelValue(14);
        erodibilityfake.setVisible(false);
        climatefake.setVisible(false);
        tectonicsfake.setVisible(false);
        initialConditionsHelpText.setVisible(false);
    }//end start

    /**
     * ********************************************************************************************
     * when the applet loses focus
     * *********************************************************************************************
     */

    /**
     * ********************************************************************************************
     * implementation of ActionListener
     * *********************************************************************************************
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == initialConditionsHelpButton) {
            if (initialConditionsHelpButton.getLabel().equals("Show Help")) {
                initialConditionsHelpButton.setLabel("Hide Help");
                initialConditionsHelpText.setVisible(true);
            } else {
                initialConditionsHelpButton.setLabel("Show Help");
                initialConditionsHelpText.setVisible(false);
            }
        }
        if (e.getSource() == erodibilityHelpButton) {
            if (erodibilityHelpButton.getLabel().equals("Show Help")) {
                erodibilityHelpText.setVisible(true);
                erodibilityHelpButton.setLabel("Hide Help");
            } else {
                erodibilityHelpText.setVisible(false);
                erodibilityHelpButton.setLabel("Show Help");
            }
        }
        if (e.getSource() == climateHelpButton) {
            if (climateHelpButton.getLabel().equals("Show Help")) {
                climateHelpButton.setLabel("Hide Help");
                climateHelpText.setVisible(true);
            } else {
                climateHelpButton.setLabel("Show Help");
                climateHelpText.setVisible(false);
            }
        }
        if (e.getSource() == tectonicsHelpButton) {
            if (tectonicsHelpButton.getLabel().equals("Show Help")) {
                tectonicsHelpButton.setLabel("Hide Help");
                tectonicsHelpText.setVisible(true);
            } else {
                tectonicsHelpButton.setLabel("Show Help");
                tectonicsHelpText.setVisible(false);
            }
        }
    }//end of action Performed	

    /**
     * ********************************************************************************************
     * implementation of ItemListener
     * *********************************************************************************************
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == simulationCheck) {
            cardSimulationManager.show(imagesPanel, cardLabelSimulation.getText());
        }
        if (e.getSource() == snapshotCheck) {
            cardSimulationManager.show(imagesPanel, cardLabelSnapshot.getText());
        }
        if (e.getSource() == optionsCheck) {
            cardManagerGeneral.show(optionGeneralPanel, optionTabLabel.getText());
        }
        if (e.getSource() == saveIntCheck) {
            cardManagerGeneral.show(optionGeneralPanel, intervalTabLabel.getText());
            SharedParameters.AVGVISIBLE = true;
            eintervals.refreshGraph();
            eintsediment.refreshGraph();
        }
        if (e.getSource() == hypsometricCheck) {
            cardManagerGeneral.show(optionGeneralPanel, hypsometricTabLabel.getText());
            SharedParameters.HYPSOMETRIC = true;
            eh.refreshHypsometric();
        }
        if (e.getSource() == initialconditionCheck) {
            cardManagerUp.show(optionCardManagerPanel, "Initial Conditions");
            initialConditionsHelpText.setVisible(false);
            initialConditionsHelpButton.setLabel("Show Help");
            SharedParameters.PREVIOUSTAB = false;
            SharedParameters.NEXTTAB = true;
            initadvancedfake.setState(true);
            if ((SharedParameters.RAININCREASEHIGH < SharedParameters.RAININCREASELOW) || (SharedParameters.RAINDECREASEHIGH < SharedParameters.RAINDECREASELOW)) {
                cardManagerUp.show(optionCardManagerPanel, "Climate");
                climatenote.setText("Check LOW and HIGH values");
            }
            setArrow(1);
            setComponentColor(1);
        }
        if (e.getSource() == erodibilityCheck) {
            cardManagerUp.show(optionCardManagerPanel, "Erodibility");
            erodibilityHelpText.setVisible(false);
            erodibilityHelpButton.setLabel("Show Help");
            erodibilityfake.setState(true);
            erodibilityadvancedSlider.setMinimum(uniformminimum);
            erodibilityadvancedSlider.setMaximum(uniformmaximum);
            erodibilityadvancedSlider.setUnitIncrement(uniformstep);
            erodibilitySlider.setValue(0);
            setArrow(1);
            setComponentColor(1);
            setComponentColor(13);
            if ((SharedParameters.RAININCREASEHIGH < SharedParameters.RAININCREASELOW) || (SharedParameters.RAINDECREASEHIGH < SharedParameters.RAINDECREASELOW)) {
                cardManagerUp.show(optionCardManagerPanel, "Climate");
                climatenote.setText("Check LOW and HIGH values!!!");
            }
        }
        if (e.getSource() == climateCheck) {
            cardManagerUp.show(optionCardManagerPanel, "Climate");
            climateHelpText.setVisible(false);
            climateHelpButton.setLabel("Show Help");
            climatenote.setText("***HIGH value should be greater than LOW value");
            climatefake.setState(true);
            setComponentColor(1);
        }
        if (e.getSource() == tectonicCheck) {
            cardManagerUp.show(optionCardManagerPanel, "Tectonics");
            tectonicsHelpText.setVisible(false);
            tectonicsHelpButton.setLabel("Show Help");
            SharedParameters.PREVIOUSTAB = true;
            SharedParameters.NEXTTAB = false;
            tectonicsfake.setState(true);
            tectonicsSlider.setValue(minimumxpoint);
            tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
            setArrow(1);
            setComponentColor(1);
            setLabelValue(9);
            if ((SharedParameters.RAININCREASEHIGH < SharedParameters.RAININCREASELOW) || (SharedParameters.RAINDECREASEHIGH < SharedParameters.RAINDECREASELOW)) {
                cardManagerUp.show(optionCardManagerPanel, "Climate");
                climatenote.setText("Check LOW and HIGH values");
            }
        }

        //to disable parameter input while process is running
        if (!SharedParameters.ROUTINESTARTED) {
            if (e.getSource() == averageprofileCheck) {
                cardManagerIntervals.show(optionintervals1down, cardLabelIntervals1.getText());
                eintervals.refreshGraph();
                eintsediment.refreshGraph();
                SharedParameters.AVGVISIBLE = true;
                SharedParameters.COLVISIBLE = false;
                SharedParameters.COLVISIBLE = false;
            }
            if (e.getSource() == selectedcolumnCheck) {
                cardManagerIntervals.show(optionintervals1down, cardLabelIntervals2.getText());
                SharedParameters.AVGVISIBLE = false;
                SharedParameters.COLVISIBLE = true;
                SharedParameters.ROWVISIBLE = false;
                ecolumn.refreshGraph();
                ecolsediment.refreshGraph();
            }
            if (e.getSource() == selectedrowCheck) {
                cardManagerIntervals.show(optionintervals1down, cardLabelIntervals3.getText());
                SharedParameters.AVGVISIBLE = false;
                SharedParameters.COLVISIBLE = false;
                SharedParameters.ROWVISIBLE = true;
                erow.refreshGraph();
                erowsediment.refreshGraph();
            }
            if (e.getSource() == initadvancedgridx) {
                xgrid = true;
                ygrid = false;
                endt = false;
                topo = false;
                setComponentColor(1);
                setComponentColor(2);
                setArrow(1);
                setArrow(2);
                interchangeablelabelnorth.setText("" + xminimum);
                interchangeablelabelsouth.setText("" + (xmaximum - 10));
                interchangeableSlider.setValue(0);
                interchangeableSlider.setMinimum(xminimum);
                interchangeableSlider.setMaximum(xmaximum);
                interchangeableSlider.setUnitIncrement(xyblock);
                interchangeableSlider.setValue(xminimum);
            }
            if (e.getSource() == initadvancedgridy) {
                ygrid = true;
                xgrid = false;
                endt = false;
                topo = false;
                setComponentColor(1);
                setComponentColor(3);
                setArrow(1);
                setArrow(3);
                interchangeablelabelnorth.setText("" + yminimum);
                interchangeablelabelsouth.setText("" + (ymaximum - 10));
                interchangeableSlider.setValue(0);
                interchangeableSlider.setMinimum(yminimum);
                interchangeableSlider.setMaximum(ymaximum);
                interchangeableSlider.setUnitIncrement(xyblock);
                interchangeableSlider.setValue(yminimum);
            }
            if (e.getSource() == initadvancedendtime) {
                ygrid = false;
                xgrid = false;
                endt = true;
                topo = false;
                setComponentColor(1);
                setComponentColor(4);
                setArrow(1);
                setArrow(4);
                interchangeableSlider.setValue(0);
                interchangeablelabelnorth.setText("100000");
                interchangeablelabelsouth.setText("1000000");
                interchangeableSlider.setMinimum(timeminimum);
                interchangeableSlider.setMaximum(timemaximum);
                interchangeableSlider.setUnitIncrement(timeblock);
                interchangeableSlider.setValue(timeminimum);
            }
            if (e.getSource() == initadvancedtopography) {
                ygrid = false;
                xgrid = false;
                endt = false;
                topo = true;
                setComponentColor(1);
                setComponentColor(5);
                setArrow(1);
                setArrow(5);
                interchangeablelabelnorth.setText("0.01");
                interchangeablelabelsouth.setText("0.30");
                interchangeableSlider.setValue(0);
                interchangeableSlider.setMinimum(slopeminimum);
                interchangeableSlider.setMaximum(slopemaximum);
                interchangeableSlider.setUnitIncrement(slopeblock);
                interchangeableSlider.setValue(slopeminimum);
            }
            //uniform default value for erodibility
            if (e.getSource() == erodibilityuniformCheck) {
                breakx = false;
                breaky = false;
                uniformErosion = true;
                SharedParameters.EROSIONNEEDED = true;
                //set default values for other options
                SharedParameters.RANDEROSION = false;
                SharedParameters.XPOINT = -1;
                SharedParameters.YPOINT = -1;
                SharedParameters.XRANDLEFT = 0;
                SharedParameters.XRANDRIGHT = 0;
                SharedParameters.YRANDTOP = 0;
                SharedParameters.YRANDBOTTOM = 0;
                setComponentColor(1);
                setComponentColor(6);
                setArrow(1);
                setArrow(19);
                erodibilityadvancedSlider.setMinimum(uniformminimum);
                erodibilityadvancedSlider.setMaximum(uniformmaximum);
                erodibilityadvancedSlider.setUnitIncrement(uniformstep);
                setLabelValue(2);
                erodibilityfake.setState(true);
                erodibilityylabelnorth.setText("0.01");
                erodibilityylabelsouth.setText("0.05");
            }//end of uniform default value for erodibility
            //random value for erodibility
            if (e.getSource() == erodibilityrandomCheck) {
                breakx = false;
                breaky = false;
                SharedParameters.EROSIONNEEDED = true;
                SharedParameters.RANDEROSION = true;
                SharedParameters.RANDVALUE = (0.03 + Math.random() * 0.04);
                //set default 
                SharedParameters.XPOINT = -1;
                SharedParameters.YPOINT = -1;
                SharedParameters.XRANDLEFT = 0;
                SharedParameters.XRANDRIGHT = 0;
                SharedParameters.YRANDTOP = 0;
                SharedParameters.YRANDBOTTOM = 0;
                setComponentColor(1);
                setComponentColor(7);
                setArrow(1);
                setLabelValue(2);
                erodibilityfake.setState(true);
            }
            //breaking point at x for erodibility
            if (e.getSource() == erodibilitybpointxCheck) {
                uniformErosion = false;
                SharedParameters.EROSIONNEEDED = true;
                SharedParameters.XRANDLEFT = 0.01;
                SharedParameters.XRANDRIGHT = 0.01;
                SharedParameters.XPOINT = 0;
                //set default values for other options
                SharedParameters.YPOINT = -1;
                SharedParameters.YRANDTOP = 0;
                SharedParameters.YRANDBOTTOM = 0;
                setComponentColor(1);
                setComponentColor(8);
                setLabelValue(3);
                setArrow(1);
                setArrow(6);
                erodibilitylabelnorth.setText("  0");
                erodibilitylabelsouth.setText("" + SharedParameters.COLUMNS);
                erodibilitySlider.setMinimum(0);
                erodibilitySlider.setMaximum(SharedParameters.COLUMNS + 10);
                erodibilitySlider.setValue(0);
                breakx = true;
                breaky = false;
                SharedParameters.RANDEROSION = false;
                erodibilityadvancedSlider.setMinimum(xleftminimum);
                erodibilityadvancedSlider.setMaximum(xrightmaximum);
                erodibilityadvancedSlider.setUnitIncrement(uniformstep);
                erodibilityuniformvalueLabel.set2decimalText(erodibilityuniform);
            }
            //breaking point at y for erodibility
            if (e.getSource() == erodibilitybpointyCheck) {
                uniformErosion = false;
                SharedParameters.EROSIONNEEDED = true;
                SharedParameters.YRANDTOP = 0.01;
                SharedParameters.YRANDBOTTOM = 0.01;
                SharedParameters.YPOINT = 0;
                //set default for other options
                SharedParameters.XPOINT = -1;
                setComponentColor(1);
                setComponentColor(9);
                setArrow(1);
                setArrow(7);
                setLabelValue(4);
                erodibilitylabelnorth.setText("  0");
                erodibilitylabelsouth.setText("" + SharedParameters.ROWS);
                erodibilitySlider.setMinimum(0);
                erodibilitySlider.setMaximum(SharedParameters.ROWS + 10);
                erodibilitySlider.setValue(0);
                SharedParameters.XRANDLEFT = 0;
                SharedParameters.XRANDRIGHT = 0;
                breakx = false;
                breaky = true;
                SharedParameters.RANDEROSION = false;
                erodibilityadvancedSlider.setMinimum(xleftminimum);
                erodibilityadvancedSlider.setMaximum(xrightmaximum);
                erodibilityadvancedSlider.setUnitIncrement(uniformstep);
                erodibilityuniformvalueLabel.set2decimalText(erodibilityuniform);
            }
            if ((e.getSource() == erodibilityleft) && breakx) {
                setArrow(8);
                erodibilityadvancedSlider.setValue(xleftminimum);
                breakxleft = true;
                breakxright = false;
                breakytop = false;
                breakybottom = false;
            }
            if ((e.getSource() == erodibilitybottom) && breakx) {
                setArrow(9);
                erodibilityadvancedSlider.setValue(xleftminimum);
                breakxleft = false;
                breakxright = true;
                breakytop = false;
                breakybottom = false;
            }
            if ((e.getSource() == erodibilityleft) && breaky) {
                setArrow(8);
                erodibilityadvancedSlider.setValue(xleftminimum);
                breakxleft = false;
                breakxright = false;
                breakytop = true;
                breakybottom = false;
            }
            if ((e.getSource() == erodibilitybottom) && breaky) {
                setArrow(9);
                erodibilityadvancedSlider.setValue(xleftminimum);
                breakxleft = false;
                breakxright = false;
                breakytop = false;
                breakybottom = true;
            }
            //default climate
            if (e.getSource() == climatedefaultCheck) {
                climatedefault1 = true;
                climateincrease = false;
                climatedecrease = false;
                climateincreaselow = false;
                climateincreasehigh = false;
                climatedecreaselow = false;
                climatedecreasehigh = false;
                SharedParameters.CLIMATEDEFAULT = true;
                SharedParameters.INCREASEON = false;
                SharedParameters.DECREASEON = false;
                SharedParameters.RAININCREASELOW = 0.0;
                SharedParameters.RAININCREASEHIGH = 0.0;
                SharedParameters.RAINDECREASELOW = 0.0;
                SharedParameters.RAINDECREASEHIGH = 0.0;
                SharedParameters.RAINFALLRATEDEFAULT = 0.1;
                climatedefaultvalueLabel.set2decimalText(0.10);
                climateSlider.setMinimum(climateminimum);
                climateSlider.setMaximum(climatemaximum);
                climateSlider.setValue(climateminimum);
                climateSlider.setUnitIncrement(climatedefaultblock);
                setArrow(1);
                setArrow(10);
                setLabelValue(13);
                setComponentColor(1);
                setComponentColor(10);
            }
            //climate increasing choice
            if (e.getSource() == climateincreasingCheck) {
                climatedefault1 = false;
                climateincrease = true;
                climatedecrease = false;
                climateincreaselow = false;
                climateincreasehigh = false;
                climatedecreaselow = false;
                climatedecreasehigh = false;
                SharedParameters.CLIMATEDEFAULT = false;
                SharedParameters.DECREASEON = false;
                SharedParameters.INCREASEON = true;
                SharedParameters.RAININCREASEHIGH = 0.10;
                SharedParameters.RAININCREASELOW = 0.05;
                SharedParameters.RAINDECREASEHIGH = 0.0;
                SharedParameters.RAINDECREASELOW = 0.0;
                SharedParameters.RAINFALLRATEDEFAULT = 0.0;
                climateSlider.setMinimum(climatelow);
                climateSlider.setMaximum(climatehigh);
                climateSlider.setValue(climatelow);
                climateSlider.setBlockIncrement(climateblock);
                setComponentColor(1);
                setComponentColor(11);
                setLabelValue(5);
                setArrow(1);
                setArrow(11);
            }
            //climate decreasing choice
            if (e.getSource() == climatedecreasingCheck) {
                climatedefault1 = false;
                climateincrease = false;
                climatedecrease = true;
                climateincreaselow = false;
                climateincreasehigh = false;
                climatedecreaselow = false;
                climatedecreasehigh = false;
                SharedParameters.CLIMATEDEFAULT = false;
                SharedParameters.INCREASEON = false;
                SharedParameters.DECREASEON = true;
                SharedParameters.RAINDECREASEHIGH = 0.10;
                SharedParameters.RAINDECREASELOW = 0.05;
                SharedParameters.RAININCREASEHIGH = 0.0;
                SharedParameters.RAININCREASELOW = 0.0;
                SharedParameters.RAINFALLRATEDEFAULT = 0.0;
                climatedefaultvalueLabel.set1decimalText();
                climateSlider.setMinimum(climatelow);
                climateSlider.setMaximum(climatehigh);
                climateSlider.setValue(climatelow);
                climateSlider.setBlockIncrement(climateblock);
                setComponentColor(1);
                setComponentColor(12);
                setLabelValue(6);
                setArrow(1);
                setArrow(12);
            }
            if ((e.getSource() == climateincreasinglow) && climatedecrease) {
                climateincreaselow = false;
                climateincreasehigh = false;
                climatedecreaselow = true;
                climatedecreasehigh = false;
                setArrow(13);
            }
            if ((e.getSource() == climateincreasinghigh) && climatedecrease) {
                climateincreaselow = false;
                climateincreasehigh = false;
                climatedecreaselow = false;
                climatedecreasehigh = true;
                setArrow(14);
            }
            if ((e.getSource() == climateincreasinglow) && climateincrease) {
                climateincreaselow = true;
                climateincreasehigh = false;
                climatedecreaselow = false;
                climatedecreasehigh = false;
                setArrow(13);
            }
            if ((e.getSource() == climateincreasinghigh) && climateincrease) {
                climateincreaselow = false;
                climateincreasehigh = true;
                climatedecreaselow = false;
                climatedecreasehigh = false;
                setArrow(14);
            }

            //default rate fixed for tectonics
            if (e.getSource() == tectonicsdefaultCheck) {
                SharedParameters.APPLYTECTONICS = false;
                SharedParameters.TECTONICSXPOINT = -1;
                SharedParameters.TECTONICSYPOINT = -1;
                SharedParameters.TECTONICSPERCENTAGE = 0;
                tectonicsnorth.setText("   ");
                tectonicssouth.setText("   ");
                tectonicsbpointxLabel.setText("" + minimumxpoint);
                tectonicsbpointyLabel.setText("" + minimumypoint);
                tectonicsSlider.setValue(minimumxpoint);
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsfake.setState(true);
                tectonicsx = false;
                tectonicsy = false;
                setComponentColor(1);
                setComponentColor(14);
                setLabelValue(12);
                setLabelValue(10);
                setLabelValue(14);
                setArrow(1);
            }
            //breaking point at x for tectonics
            if (e.getSource() == tectonicsbpointxCheck) {
                tectonicsx = true;
                tectonicsy = false;
                setComponentColor(1);
                setComponentColor(15);
                setLabelValue(7);
                setLabelValue(14);
                setArrow(1);
                setArrow(15);
                SharedParameters.TECTONICSXPOINT = 0;
                SharedParameters.TECTONICSYPOINT = -1;
                SharedParameters.APPLYTECTONICS = true;
                tectonicsnorth.setText("  0");
                tectonicssouth.setText("" + SharedParameters.COLUMNS);
                tectonicsSlider.setMaximum(SharedParameters.COLUMNS + 10);
                tectonicsbpointyLabel.setText("" + minimumypoint);
                tectonicsSlider.setValue(minimumxpoint);
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsbottom.setEnabled(true);
                tectonicsleft.setEnabled(true);
                tectonicsxleftvalueLabel.setForeground(highlights);
                tectonicsxrightvalueLabel.setForeground(highlights);
                tectonicsytopvalueLabel.setForeground(highlights);
                tectonicsybottomvalueLabel.setForeground(highlights);
            }
            //breaking point at y for tectonics
            if (e.getSource() == tectonicsbpointyCheck) {
                tectonicsx = false;
                tectonicsy = true;
                setComponentColor(1);
                setComponentColor(16);
                setLabelValue(8);
                setArrow(1);
                setArrow(16);
                SharedParameters.TECTONICSXPOINT = -1;
                SharedParameters.TECTONICSYPOINT = 0;
                SharedParameters.APPLYTECTONICS = true;
                tectonicsbpointxLabel.setText("" + minimumxpoint);
                tectonicsnorth.setText("  0");
                tectonicssouth.setText("" + SharedParameters.ROWS);
                tectonicsSlider.setMaximum(SharedParameters.ROWS + 10);
                tectonicsSlider.setValue(minimumxpoint);
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsbottom.setEnabled(true);
                tectonicsleft.setEnabled(true);
                tectonicsxleftvalueLabel.setForeground(highlights);
                tectonicsxrightvalueLabel.setForeground(highlights);
                tectonicsytopvalueLabel.setForeground(highlights);
                tectonicsybottomvalueLabel.setForeground(highlights);
            }
            if ((e.getSource() == tectonicsleft) && tectonicsx) {
                setArrow(17);
                tectonicsxleft = true;
                tectonicsxright = false;
                tectonicsytop = false;
                tectonicsybottom = false;
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsleft.setForeground(highlights);
                tectonicsxleftvalueLabel.setForeground(highlights);
                tectonicsbottom.setForeground(Color.gray);
                tectonicsxrightvalueLabel.setForeground(Color.gray);
                tectonicsxrightvalueLabel.set4decimalText(0.0000);
            }
            if ((e.getSource() == tectonicsbottom) && tectonicsx) {
                setArrow(18);
                tectonicsxleft = false;
                tectonicsxright = true;
                tectonicsytop = false;
                tectonicsybottom = false;
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsleft.setForeground(Color.gray);
                tectonicsxleftvalueLabel.setForeground(Color.gray);
                tectonicsxleftvalueLabel.set4decimalText(0.0000);
                tectonicsbottom.setForeground(highlights);
                tectonicsxrightvalueLabel.setForeground(highlights);
            }
            if ((e.getSource() == tectonicsleft) && tectonicsy) {
                setArrow(17);
                tectonicsxleft = false;
                tectonicsxright = false;
                tectonicsytop = true;
                tectonicsybottom = false;
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsleft.setForeground(highlights);
                tectonicsytopvalueLabel.setForeground(highlights);
                tectonicsbottom.setForeground(Color.gray);
                tectonicsybottomvalueLabel.setForeground(Color.gray);
                tectonicsybottomvalueLabel.set4decimalText(0.0000);
            }
            if ((e.getSource() == tectonicsbottom) && tectonicsy) {
                setArrow(18);
                tectonicsxleft = false;
                tectonicsxright = false;
                tectonicsytop = false;
                tectonicsybottom = true;
                tectonicsadvancedSlider.setValue(tectonicsxleftminimum);
                tectonicsleft.setForeground(Color.gray);
                tectonicsytopvalueLabel.setForeground(Color.gray);
                tectonicsytopvalueLabel.set4decimalText(0.0000);
                tectonicsbottom.setForeground(highlights);
                tectonicsybottomvalueLabel.setForeground(highlights);
            }
        }//end of routinestarted check		
    }//end of itemStateChanged

    /**
     * ********************************************************************************************
     * implementation of AdjustmentListener
     * *********************************************************************************************
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        //to prevent from changes when process started
        if (!SharedParameters.ROUTINESTARTED) {
            //GRID SIZE		
            if ((e.getSource() == interchangeableSlider) && xgrid) {
                if (e.getValue() > 100) {
                    xmaxsliderLabel.setText("100");
                    SharedParameters.COLUMNS = 100;
                } else {
                    xmaxsliderLabel.setText(String.valueOf(e.getValue()));
                    SharedParameters.COLUMNS = Integer.parseInt(String.valueOf(e.getValue()));
                }
            }
            if ((e.getSource() == interchangeableSlider) && ygrid) {
                if (e.getValue() > 200) {
                    ymaxsliderLabel.setText("200");
                    SharedParameters.ROWS = 200;
                } else {
                    ymaxsliderLabel.setText(String.valueOf(e.getValue()));
                    SharedParameters.ROWS = Integer.parseInt(String.valueOf(e.getValue()));
                }
            }
            if ((e.getSource() == interchangeableSlider) && endt) {
                try {
                    SharedParameters.ENDTIME = Integer.parseInt(String.valueOf(e.getValue())) * 100;
                } catch (NumberFormatException nfe) {
                }
                endtimesliderLabel.setText("" + SharedParameters.ENDTIME);
            } else if ((e.getSource() == interchangeableSlider) && topo) {
                try {
                    int slope = Integer.parseInt(String.valueOf(e.getValue()));
                    SharedParameters.SLOPE = (double) slope / 100;
                    if (SharedParameters.SLOPE > 0.30) {
                        topographyslopeLabel.setText("0.30");
                    } else {
                        topographyslopeLabel.set2decimalText(SharedParameters.SLOPE);
                    }
                    SharedParameters.RESETINTERVALSLEGEND = true;
                } catch (NumberFormatException nfe) {
                }
            }
            //ERODIBILITY
            else if ((e.getSource() == erodibilitySlider) && breakx) {
                try {
                    int xpoint = Integer.parseInt(String.valueOf(e.getValue()));
                    SharedParameters.XPOINT = xpoint;
                    erodibilitybpointyLabel.setText("  0");
                    erodibilitybpointxLabel.setText("  " + xpoint + " ");
                    SharedParameters.YRANDTOP = 0.0;
                    SharedParameters.YRANDBOTTOM = 0.0;
                    SharedParameters.EROSIONNEEDED = true;
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == erodibilitySlider) && breaky) {
                try {
                    int ypoint = Integer.parseInt(String.valueOf(e.getValue()));
                    SharedParameters.YPOINT = ypoint;
                    erodibilitybpointxLabel.setText("  0");
                    erodibilitybpointyLabel.setText("  " + ypoint + " ");
                    SharedParameters.XRANDLEFT = 0.0;
                    SharedParameters.XRANDRIGHT = 0.0;
                    SharedParameters.EROSIONNEEDED = true;
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == erodibilityadvancedSlider) && uniformErosion) {
                int uniform = Integer.parseInt(String.valueOf(e.getValue()));
                double uniformvalue = (double) uniform / 1000;
                SharedParameters.EROSION = uniformvalue;
                erodibilityuniformvalueLabel.set2decimalText(uniformvalue);
                SharedParameters.EROSIONNEEDED = true;
            } else if ((e.getSource() == erodibilityadvancedSlider) && breakxleft) {
                if (SharedParameters.XPOINT > -1) {
                    int xleft = Integer.parseInt(String.valueOf(e.getValue()));
                    double leftvalue = (double) xleft / 1000;
                    erodibilityxleftvalueLabel.set2decimalText(leftvalue);
                    SharedParameters.XRANDLEFT = leftvalue;
                    SharedParameters.EROSIONNEEDED = true;
                }
            } else if ((e.getSource() == erodibilityadvancedSlider) && breakxright) {
                if (SharedParameters.XPOINT > -1) {
                    int xright = Integer.parseInt(String.valueOf(e.getValue()));
                    double rightvalue = (double) xright / 1000;
                    erodibilityxrightvalueLabel.set2decimalText(rightvalue);
                    SharedParameters.XRANDRIGHT = rightvalue;
                    SharedParameters.EROSIONNEEDED = true;
                }
            } else if ((e.getSource() == erodibilityadvancedSlider) && breakytop) {
                if (SharedParameters.YPOINT > -1) {
                    int ytop = Integer.parseInt(String.valueOf(e.getValue()));
                    double topvalue = (double) ytop / 1000;
                    erodibilityytopvalueLabel.set2decimalText(topvalue);
                    SharedParameters.YRANDTOP = topvalue;
                    SharedParameters.EROSIONNEEDED = true;
                }
            } else if ((e.getSource() == erodibilityadvancedSlider) && breakybottom) {
                if (SharedParameters.YPOINT > -1) {
                    int ybottom = Integer.parseInt(String.valueOf(e.getValue()));
                    double bottomvalue = (double) ybottom / 1000;
                    erodibilityybottomvalueLabel.set2decimalText(bottomvalue);
                    SharedParameters.YRANDBOTTOM = bottomvalue;
                    SharedParameters.EROSIONNEEDED = true;
                }
            }
            //CLIMATE
            else if ((e.getSource() == climateSlider) && climatedefault1) {
                try {
                    int defaultrain = Integer.parseInt(String.valueOf(e.getValue()));
                    double defaultrainfall = (double) defaultrain * 0.001;
                    climatedefaultvalueLabel.set2decimalText(defaultrainfall);
                    SharedParameters.RAINFALLRATEDEFAULT = getDouble(climatedefaultvalueLabel.getText());
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == climateSlider) && climateincrease && climateincreaselow) {
                try {
                    int increaselow = Integer.parseInt(String.valueOf(e.getValue()));
                    double inclow = (double) increaselow * 0.001;
                    double inclowcheck = (double) increaselow * 0.001;
                    SharedParameters.RAININCREASELOW = inclowcheck;
                    SharedParameters.RAINFALLRATEDEFAULT = 0;
                    if (inclow > 0.14) {
                        inclowcheck = 0.14;
                    }
                    climateincreasingvaluelowLabel.set2decimalText(inclowcheck);
                    if (SharedParameters.RAININCREASEHIGH < SharedParameters.RAININCREASELOW) {
                        climateincreasingvaluehighLabel.set2decimalText(inclowcheck + 0.01);
                        SharedParameters.RAININCREASEHIGH = inclowcheck + 0.01;
                    }
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == climateSlider) && climateincrease && climateincreasehigh) {
                try {
                    int increasehigh = Integer.parseInt(String.valueOf(e.getValue()));
                    double inchigh = (double) increasehigh * 0.001;
                    double inchighcheck = (double) increasehigh * 0.001;
                    SharedParameters.RAININCREASEHIGH = inchighcheck;
                    SharedParameters.RAINFALLRATEDEFAULT = 0;
                    if (inchigh < 0.02) {
                        inchighcheck = 0.02;
                    }
                    climateincreasingvaluehighLabel.set2decimalText(inchighcheck);
                    if (SharedParameters.RAININCREASEHIGH < SharedParameters.RAININCREASELOW) {
                        climateincreasingvaluelowLabel.set2decimalText(inchighcheck - 0.01);
                        SharedParameters.RAININCREASELOW = inchighcheck - 0.01;
                    }
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == climateSlider) && climatedecrease && climatedecreaselow) {
                try {
                    int decreaselow = Integer.parseInt(String.valueOf(e.getValue()));
                    double declow = (double) decreaselow * 0.001;
                    double declowcheck = (double) decreaselow * 0.001;
                    if (declow > 0.14) {
                        declowcheck = 0.14;
                    }
                    SharedParameters.RAINDECREASELOW = declowcheck;
                    SharedParameters.RAINFALLRATEDEFAULT = 0;
                    climatedecreasingvaluelowLabel.set2decimalText(declowcheck);
                    if (SharedParameters.RAINDECREASEHIGH < SharedParameters.RAINDECREASELOW) {
                        climatedecreasingvaluehighLabel.set2decimalText(declowcheck + 0.01);
                        SharedParameters.RAINDECREASEHIGH = declowcheck + 0.01;
                    }
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == climateSlider) && climatedecrease && climatedecreasehigh) {
                try {
                    int decreasehigh = Integer.parseInt(String.valueOf(e.getValue()));
                    double dechigh = (double) decreasehigh * 0.001;
                    double dechighcheck = (double) decreasehigh * 0.001;
                    SharedParameters.RAINDECREASEHIGH = dechighcheck;
                    SharedParameters.RAINFALLRATEDEFAULT = 0;
                    if (dechigh < 0.02) {
                        dechighcheck = 0.02;
                    }
                    climatedecreasingvaluehighLabel.set2decimalText(dechighcheck);
                    if (SharedParameters.RAINDECREASEHIGH < SharedParameters.RAINDECREASELOW) {
                        climatedecreasingvaluelowLabel.set2decimalText(dechighcheck - 0.01);
                        SharedParameters.RAINDECREASELOW = dechighcheck - 0.01;
                    }
                } catch (NumberFormatException nfe) {
                }
            }
            //TECTONICS
            else if ((e.getSource() == tectonicsSlider) && tectonicsx) {
                try {
                    int tectonicsxpoint = Integer.parseInt(String.valueOf(e.getValue()));
                    SharedParameters.TECTONICSXPOINT = tectonicsxpoint;
                    tectonicsbpointyLabel.setText("" + tectonicsminimumypoint + " ");
                    tectonicsbpointxLabel.setText("" + tectonicsxpoint + " ");
                    SharedParameters.TECTONICSYTOP = 0.0;
                    SharedParameters.TECTONICSYBOTTOM = 0.0;
                    SharedParameters.APPLYTECTONICS = true;
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == tectonicsSlider) && tectonicsy) {
                try {
                    int tectonicsypoint = Integer.parseInt(String.valueOf(e.getValue()));
                    SharedParameters.TECTONICSYPOINT = tectonicsypoint;
                    tectonicsbpointxLabel.setText("" + tectonicsminimumxpoint);
                    tectonicsbpointyLabel.setText("" + tectonicsypoint + " ");
                    SharedParameters.TECTONICSXLEFT = 0.0;
                    SharedParameters.TECTONICSXRIGHT = 0.0;
                    SharedParameters.APPLYTECTONICS = true;
                } catch (NumberFormatException nfe) {
                }
            } else if ((e.getSource() == tectonicsadvancedSlider) && tectonicsx && tectonicsxleft) {
                if (SharedParameters.TECTONICSXPOINT > -1) {
                    double tectonicsleftvalue = 0;
                    int tectonicsxleft = Integer.parseInt(String.valueOf(e.getValue()));
                    tectonicsleftvalue = tectonicsxleft * 0.00001;
                    tectonicsxleftvalueLabel.set4decimalText(tectonicsleftvalue);
                    SharedParameters.TECTONICSPERCENTAGE = SharedParameters.TECTONICSXLEFT = tectonicsleftvalue;
                    SharedParameters.TECTONICSXRIGHT = 0.0;
                    SharedParameters.TECTONICSYTOP = 0.0;
                    SharedParameters.TECTONICSYBOTTOM = 0.0;
                    SharedParameters.RESETINTERVALSLEGEND = true;
                }
            } else if ((e.getSource() == tectonicsadvancedSlider) && tectonicsx && tectonicsxright) {
                if (SharedParameters.TECTONICSXPOINT > -1) {
                    double tectonicsrightvalue = 0;
                    int tectonicsxright = Integer.parseInt(String.valueOf(e.getValue()));
                    tectonicsrightvalue = tectonicsxright * 0.00001;
                    tectonicsxrightvalueLabel.set4decimalText(tectonicsrightvalue);
                    SharedParameters.TECTONICSPERCENTAGE = SharedParameters.TECTONICSXRIGHT = tectonicsrightvalue;
                    SharedParameters.TECTONICSXLEFT = 0.0;
                    SharedParameters.TECTONICSYTOP = 0.0;
                    SharedParameters.TECTONICSYBOTTOM = 0.0;
                    SharedParameters.RESETINTERVALSLEGEND = true;
                }
            } else if ((e.getSource() == tectonicsadvancedSlider) && tectonicsy && tectonicsytop) {
                if (SharedParameters.TECTONICSYPOINT > -1) {
                    double tectonicstopvalue = 0;
                    int tectonicsytop = Integer.parseInt(String.valueOf(e.getValue()));
                    tectonicstopvalue = tectonicsytop * 0.00001;
                    tectonicsytopvalueLabel.set4decimalText(tectonicstopvalue);
                    SharedParameters.TECTONICSPERCENTAGE = SharedParameters.TECTONICSYTOP = tectonicstopvalue;
                    SharedParameters.TECTONICSXLEFT = 0.0;
                    SharedParameters.TECTONICSXRIGHT = 0.0;
                    SharedParameters.TECTONICSYBOTTOM = 0.0;
                    SharedParameters.RESETINTERVALSLEGEND = true;
                }
            } else if ((e.getSource() == tectonicsadvancedSlider) && tectonicsy && tectonicsybottom) {
                if (SharedParameters.TECTONICSYPOINT > -1) {
                    double tectonicsbottomvalue = 0;
                    int tectonicsybottom = Integer.parseInt(String.valueOf(e.getValue()));
                    tectonicsbottomvalue = tectonicsybottom * 0.00001;
                    tectonicsybottomvalueLabel.set4decimalText(tectonicsbottomvalue);
                    SharedParameters.TECTONICSPERCENTAGE = SharedParameters.TECTONICSYBOTTOM = tectonicsbottomvalue;
                    SharedParameters.TECTONICSXLEFT = 0.0;
                    SharedParameters.TECTONICSXRIGHT = 0.0;
                    SharedParameters.TECTONICSYTOP = 0.0;
                    SharedParameters.RESETINTERVALSLEGEND = true;
                }
            } else if (e.getSource() == columnintervalSlider) {
                columnintervalvalueLabel.setText(String.valueOf(e.getValue()));
                SharedParameters.COLINTERVALS = Integer.parseInt(String.valueOf(e.getValue()));

            } else if (e.getSource() == rowintervalSlider) {
                rowintervalvalueLabel.setText(String.valueOf(e.getValue()));
                SharedParameters.ROWINTERVALS = Integer.parseInt(String.valueOf(e.getValue()));
            }
        }//end of routine started check
    }//end of adjustmentValueChanged

    /**
     * ********************************************************************************************
     * to set arrows according to option selected
     * *********************************************************************************************
     */
    void setArrow(int arrowOption) {
        switch (arrowOption) {
            case 1: {
                gridxarrow.setText("                           ");
                gridyarrow.setText("                           ");
                endtimearrow.setText("                           ");
                topographyarrow.setText("                           ");
                erodibilityuniformarrow.setText("                              ");
                erodibilitybreakxarrow.setText("           ");
                erodibilitybreakyarrow.setText("           ");
                erodibilitybreakxleftarrow.setText("                ");
                erodibilitybreakxrightarrow.setText("                ");
                lowclimatearrow.setText("                           ");
                highclimatearrow.setText("                           ");
                climatedefaultarrow.setText("                           ");
                increasingclimatearrow.setText("        ");
                decreasingclimatearrow.setText("        ");
                climateseparatorLabel.setForeground(Color.black);
                tectonicsbreakxarrow.setText("      ");
                tectonicsbreakyarrow.setText("      ");
                tectonicsbreaktoparrow.setText("      ");
                tectonicsbreakbottomarrow.setText("      ");
                break;
            }
            case 2: {
                gridxarrow.setForeground(highlights);
                gridxarrow.setText("___________________________");
                break;
            }
            case 3: {
                gridyarrow.setForeground(highlights);
                gridyarrow.setText("___________________________");
                break;
            }
            case 4: {
                endtimearrow.setForeground(highlights);
                endtimearrow.setText("___________________________");
                break;
            }
            case 5: {
                topographyarrow.setForeground(highlights);
                topographyarrow.setText("___________________________");
                break;
            }
            case 6: {
                erodibilitybreakxarrow.setForeground(highlights);
                erodibilitybreakxarrow.setText("___________");
                erodibilitybreakyarrow.setText("           ");
                erodibilitybreakxleftarrow.setText("                ");
                erodibilitybreakxrightarrow.setText("                ");
                break;
            }
            case 7: {
                erodibilitybreakyarrow.setForeground(highlights);
                erodibilitybreakxarrow.setText("           ");
                erodibilitybreakyarrow.setText("___________");
                erodibilitybreakxleftarrow.setText("                ");
                erodibilitybreakxrightarrow.setText("                ");
                break;
            }
            case 8: {

                erodibilitybreakxleftarrow.setForeground(highlights);
                erodibilitybreakxleftarrow.setText("________________");
                erodibilitybreakxrightarrow.setText("                ");
                break;
            }
            case 9: {
                erodibilitybreakxrightarrow.setForeground(highlights);
                erodibilitybreakxleftarrow.setText("                ");
                erodibilitybreakxrightarrow.setText("________________");
                break;
            }
            case 10: {
                climatedefaultarrow.setForeground(highlights);
                climatedefaultarrow.setText("___________________________");
                climateseparatorLabel.setForeground(Color.black);
                break;
            }
            case 11: {
                climateseparatorLabel.setForeground(highlights);
                climateseparatorLabel.setVisible(true);
                increasingclimatearrow.setForeground(highlights);
                increasingclimatearrow.setText("________");
                break;
            }
            case 12: {
                climateseparatorLabel.setForeground(highlights);
                climateseparatorLabel.setVisible(true);
                decreasingclimatearrow.setForeground(highlights);
                decreasingclimatearrow.setText("________");
                break;
            }
            case 13: {
                lowclimatearrow.setForeground(highlights);
                lowclimatearrow.setText("___________________________");
                highclimatearrow.setText("                           ");
                break;
            }
            case 14: {
                highclimatearrow.setForeground(highlights);
                highclimatearrow.setText("___________________________");
                lowclimatearrow.setText("                           ");
                break;
            }
            case 15: {
                tectonicsbreakxarrow.setForeground(highlights);
                tectonicsbreakxarrow.setText("______");
                break;
            }
            case 16: {
                tectonicsbreakyarrow.setForeground(highlights);
                tectonicsbreakyarrow.setText("______");
                break;
            }
            case 17: {
                tectonicsbreaktoparrow.setForeground(highlights);
                tectonicsbreaktoparrow.setText("______");
                tectonicsbreakbottomarrow.setText("      ");
                break;
            }
            case 18: {
                tectonicsbreakbottomarrow.setForeground(highlights);
                tectonicsbreakbottomarrow.setText("______");
                tectonicsbreaktoparrow.setText("      ");
                break;
            }
            case 19: {
                erodibilityuniformarrow.setText("______________________________");
                erodibilityuniformarrow.setForeground(highlights);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * ********************************************************************************************
     * to set values in labels according to selection
     * *********************************************************************************************
     */
    void setLabelValue(int valueOption) {
        switch (valueOption) {
            case 14: {
                erodibilityxleftvalueLabel.setText("    ");
                erodibilityxrightvalueLabel.setText("    ");
                erodibilityytopvalueLabel.setText("    ");
                erodibilityybottomvalueLabel.setText("    ");
                erodibilityytopvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityybottomvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityxleftvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityxrightvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                break;
            }
            case 2: {
                erodibilityleft.setLabel("           ");
                erodibilitybottom.setLabel("           ");
                erodibilityxleftvalueLabel.setText("    ");
                erodibilityxrightvalueLabel.setText("    ");
                erodibilityytopvalueLabel.setText("    ");
                erodibilityybottomvalueLabel.setText("    ");
                erodibilitybpointxLabel.setText("  " + minimumxpoint);
                erodibilitybpointyLabel.setText("  " + minimumypoint);
                break;
            }
            case 3: {
                erodibilitybpointyLabel.setText("  " + minimumypoint);
                erodibilityxleftvalueLabel.setVisible(true);
                erodibilityxrightvalueLabel.setVisible(true);
                erodibilityytopvalueLabel.setVisible(false);
                erodibilityybottomvalueLabel.setVisible(false);
                erodibilityxleftvalueLabel.setForeground(highlights);
                erodibilityxleftvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityxleftvalueLabel.set2decimalText(0.01);
                erodibilityxrightvalueLabel.setForeground(highlights);
                erodibilityxrightvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityxrightvalueLabel.set2decimalText(0.01);
                break;
            }
            case 4: {
                erodibilitybpointxLabel.setText("  " + minimumxpoint);
                erodibilityxleftvalueLabel.setVisible(false);
                erodibilityxrightvalueLabel.setVisible(false);
                erodibilityytopvalueLabel.setVisible(true);
                erodibilityybottomvalueLabel.setVisible(true);
                erodibilityytopvalueLabel.setForeground(highlights);
                erodibilityytopvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityytopvalueLabel.set2decimalText(0.01);
                erodibilityybottomvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityybottomvalueLabel.setForeground(highlights);
                erodibilityybottomvalueLabel.set2decimalText(0.01);
                break;
            }
            case 5: {
                climateincreasingvaluelowLabel.setForeground(highlights);
                climateincreasingvaluehighLabel.setForeground(highlights);
                climateincreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasingvaluelowLabel.setVisible(true);
                climateincreasingvaluehighLabel.setVisible(true);
                climatedefaultvalueLabel.set1decimalText();
                climateincreasingvaluelowLabel.set2decimalText(0.05);
                climateincreasingvaluehighLabel.set2decimalText(0.06);
                climatedecreasingvaluelowLabel.setVisible(false);
                climatedecreasingvaluehighLabel.setVisible(false);
                break;
            }
            case 6: {
                climatedecreasingvaluelowLabel.setForeground(highlights);
                climatedecreasingvaluehighLabel.setForeground(highlights);
                climatedecreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingvaluelowLabel.setVisible(true);
                climatedecreasingvaluehighLabel.setVisible(true);
                climatedefaultvalueLabel.set1decimalText();
                climatedecreasingvaluelowLabel.set2decimalText(0.05);
                climatedecreasingvaluehighLabel.set2decimalText(0.06);
                climateincreasingvaluelowLabel.setVisible(false);
                climateincreasingvaluehighLabel.setVisible(false);
                break;
            }
            case 7: {
                tectonicsxleftvalueLabel.setVisible(true);
                tectonicsytopvalueLabel.setVisible(false);
                tectonicsxleftvalueLabel.setForeground(highlights);
                tectonicsxrightvalueLabel.setForeground(highlights);
                tectonicsxleftvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsxrightvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsxrightvalueLabel.setVisible(true);
                tectonicsybottomvalueLabel.setVisible(false);
                tectonicsxleftvalueLabel.set4decimalText(0);
                tectonicsxrightvalueLabel.set4decimalText(0);
                break;
            }
            case 8: {
                tectonicsxleftvalueLabel.setVisible(false);
                tectonicsytopvalueLabel.setVisible(true);
                tectonicsytopvalueLabel.setForeground(highlights);
                tectonicsybottomvalueLabel.setForeground(highlights);
                tectonicsytopvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsybottomvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsxrightvalueLabel.setVisible(false);
                tectonicsybottomvalueLabel.setVisible(true);
                tectonicsytopvalueLabel.set4decimalText(0);
                tectonicsybottomvalueLabel.set4decimalText(0);
                break;
            }
            case 9: {
                tectonicsxleftvalueLabel.setForeground(Color.black);
                tectonicsxrightvalueLabel.setForeground(Color.black);
                tectonicsytopvalueLabel.setForeground(Color.black);
                tectonicsybottomvalueLabel.setForeground(Color.black);
                break;
            }
            case 10: {
                tectonicsleft.setLabel("        ");
                tectonicsbottom.setLabel("        ");
                break;
            }
            case 11: {
                erodibilityxleftvalueLabel.setForeground(Color.black);
                erodibilityxrightvalueLabel.setForeground(Color.black);
                erodibilityytopvalueLabel.setForeground(Color.black);
                erodibilityybottomvalueLabel.setForeground(Color.black);
                break;
            }
            case 12: {
                tectonicsxleftvalueLabel.setText("          ");
                tectonicsxrightvalueLabel.setText("          ");
                tectonicsytopvalueLabel.setText("          ");
                tectonicsybottomvalueLabel.setText("          ");
                tectonicsxleftvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsxrightvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsytopvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsybottomvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
            }
            case 13: {
                climateincreasingvaluelowLabel.setText("     ");
                climateincreasingvaluehighLabel.setText("     ");
                climatedecreasingvaluelowLabel.setText("     ");
                climatedecreasingvaluehighLabel.setText("     ");
                climateincreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
            }
            default: {
                break;
            }
        }
    }

    /**
     * ********************************************************************************************
     * to highlight components and arrows when selected
     * *********************************************************************************************
     */
    void setComponentColor(int colorOption) {
        switch (colorOption) {
            case 1: {
                xmaxLabel.setForeground(Color.black);
                xmaxsliderLabel.setForeground(Color.black);
                xmaxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                xmaxsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                ymaxLabel.setForeground(Color.black);
                ymaxsliderLabel.setForeground(Color.black);
                ymaxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                ymaxsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                endtimeyearLabel.setForeground(Color.black);
                endtimesliderLabel.setForeground(Color.black);
                endtimeyearLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                endtimesliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                topographydefaultLabel.setForeground(Color.black);
                topographyslopeLabel.setForeground(Color.black);
                topographydefaultLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                topographyslopeLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityuniformCheck.setForeground(Color.black);
                erodibilityuniformvalueLabel.setForeground(Color.black);
                erodibilityuniformCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityuniformvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityrandomCheck.setForeground(Color.black);
                randomsliderLabel.setForeground(Color.black);
                randomsliderLabel1.setForeground(Color.black);
                erodibilityrandomCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                randomsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                randomsliderLabel1.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilitybpointxCheck.setForeground(Color.black);
                erodibilitybpointxLabel.setForeground(Color.black);
                erodibilitybpointxCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilitybpointxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilitybpointyCheck.setForeground(Color.black);
                erodibilitybpointyLabel.setForeground(Color.black);
                erodibilitybpointyCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilitybpointyLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilityleft.setForeground(Color.black);
                erodibilitybottom.setForeground(Color.black);
                erodibilityleft.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilitybottom.setFont(new Font("Times Roman", Font.BOLD, 10));
                erodibilitylabelnorth.setForeground(Color.black);
                erodibilitylabelsouth.setForeground(Color.black);
                climatedefaultCheck.setForeground(Color.black);
                climatedefaultCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedefaultvalueLabel.setForeground(Color.black);
                climatedefaultvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasingCheck.setForeground(Color.black);
                climateincreasingCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingCheck.setForeground(Color.black);
                climatedecreasingCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasinglow.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasinglow.setForeground(Color.black);
                climateincreasinghigh.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasinghigh.setForeground(Color.black);
                climateincreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingvaluelowLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climatedecreasingvaluehighLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                climateincreasingvaluelowLabel.setForeground(Color.black);
                climateincreasingvaluehighLabel.setForeground(Color.black);
                climatedecreasingvaluelowLabel.setForeground(Color.black);
                climatedecreasingvaluehighLabel.setForeground(Color.black);
                tectonicsdefaultCheck.setForeground(Color.black);
                tectonicsdefaultCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicslabelCheck1.setForeground(Color.black);
                tectonicslabelCheck1.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsbpointxCheck.setForeground(Color.black);
                tectonicsbpointxCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsbpointyCheck.setForeground(Color.black);
                tectonicsbpointyCheck.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsbpointxLabel.setForeground(Color.black);
                tectonicsbpointxLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsbpointyLabel.setForeground(Color.black);
                tectonicsbpointyLabel.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsleft.setForeground(Color.black);
                tectonicsbottom.setForeground(Color.black);
                tectonicsleft.setFont(new Font("Times Roman", Font.BOLD, 10));
                tectonicsbottom.setFont(new Font("Times Roman", Font.BOLD, 10));
                break;
            }
            case 2: {
                xmaxLabel.setForeground(highlights);
                xmaxsliderLabel.setForeground(highlights);
                xmaxLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                xmaxsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 3: {
                ymaxLabel.setForeground(highlights);
                ymaxsliderLabel.setForeground(highlights);
                ymaxLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                ymaxsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 4: {
                endtimeyearLabel.setForeground(highlights);
                endtimesliderLabel.setForeground(highlights);
                endtimeyearLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                endtimesliderLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 5: {
                topographydefaultLabel.setForeground(highlights);
                topographyslopeLabel.setForeground(highlights);
                topographydefaultLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                topographyslopeLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 6: {
                erodibilityuniformCheck.setForeground(highlights);
                erodibilityuniformvalueLabel.setForeground(highlights);
                erodibilityuniformCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                erodibilityuniformvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 7: {
                erodibilityrandomCheck.setForeground(highlights);
                randomsliderLabel.setForeground(highlights);
                randomsliderLabel1.setForeground(highlights);
                erodibilityrandomCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                randomsliderLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                randomsliderLabel1.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 8: {
                erodibilitybpointxCheck.setForeground(highlights);
                erodibilitybpointxLabel.setForeground(highlights);
                erodibilitybpointxCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                erodibilitybpointxLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                erodibilityleft.setForeground(highlights);
                erodibilityleft.setLabel("Left =     ");
                erodibilitybottom.setForeground(highlights);
                erodibilitybottom.setLabel("Right =    ");
                break;
            }
            case 9: {
                erodibilitybpointyCheck.setForeground(highlights);
                erodibilitybpointyLabel.setForeground(highlights);
                erodibilitybpointyCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                erodibilitybpointyLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                erodibilityleft.setForeground(highlights);
                erodibilityleft.setLabel("Top =      ");
                erodibilitybottom.setForeground(highlights);
                erodibilitybottom.setLabel("Bottom =   ");
                break;
            }
            case 10: {
                climatedefaultCheck.setForeground(highlights);
                climatedefaultCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                climatedefaultvalueLabel.setForeground(highlights);
                climatedefaultvalueLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 11: {
                climateincreasingCheck.setForeground(highlights);
                climateincreasingCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                climateincreasinglow.setForeground(highlights);
                climateincreasinghigh.setForeground(highlights);
                break;
            }
            case 12: {
                climatedecreasingCheck.setForeground(highlights);
                climatedecreasingCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                climateincreasinglow.setForeground(highlights);
                climateincreasinghigh.setForeground(highlights);
                break;
            }
            case 13: {
                erodibilityxleftvalueLabel.setForeground(Color.black);
                erodibilityxrightvalueLabel.setForeground(Color.black);
                erodibilityytopvalueLabel.setForeground(Color.black);
                erodibilityybottomvalueLabel.setForeground(Color.black);
                break;
            }
            case 14: {
                tectonicsdefaultCheck.setForeground(highlights);
                tectonicsdefaultCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                tectonicslabelCheck1.setForeground(highlights);
                tectonicslabelCheck1.setFont(new Font("Times Roman", Font.BOLD, 11));
                break;
            }
            case 15: {
                tectonicsbpointxCheck.setForeground(highlights);
                tectonicsbpointxCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                tectonicsbpointxLabel.setForeground(highlights);
                tectonicsbpointxLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                tectonicsleft.setForeground(highlights);
                tectonicsleft.setLabel("Left =  ");
                tectonicsbottom.setForeground(highlights);
                tectonicsbottom.setLabel("Right =  ");
                break;
            }
            case 16: {
                tectonicsbpointyCheck.setForeground(highlights);
                tectonicsbpointyCheck.setFont(new Font("Times Roman", Font.BOLD, 11));
                tectonicsbpointyLabel.setForeground(highlights);
                tectonicsbpointyLabel.setFont(new Font("Times Roman", Font.BOLD, 11));
                tectonicsleft.setForeground(highlights);
                tectonicsleft.setLabel("Top =   ");
                tectonicsbottom.setForeground(highlights);
                tectonicsbottom.setLabel("Bottom = ");
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * ********************************************************************************************
     * This function checks whether the input is a valid number and converts it to a double
     * *********************************************************************************************
     */
    private static double getDouble(String numStr) {
        double numDouble;

        numDouble = Double.parseDouble(numStr);

        return numDouble;
    }// end of getDouble


    /**
     * ********************************************************************************************
     * This function allows for a more controlled location of components on the applet.
     * *********************************************************************************************
     */
    void constraint(Container cont, Component comp,
                    int gridx, int gridy,
                    int gridwidth, int gridheight,
                    int fill, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.fill = fill;
        c.anchor = anchor;
        c.weightx = 1.0;
        c.weighty = 1.0;

        if (1 + 1 + 1 + 1 > 0) {
            c.insets = new Insets(1, 1, 1, 1);
        }

        ((GridBagLayout) cont.getLayout()).setConstraints(comp, c);
        cont.add(comp);
    }//end of constraint

}// end class ErosionUI

/**
 * ********************************************************************************************
 * CLASS:	      CustomPanel
 * <p/>
 * FUNCTION:     It allows the creation of a customized panel with or without a title.
 * The class contains:
 * -Constructors:
 * public CustomPanel()
 * public CustomPanel(int sentWidth, int sentHeight)
 * public CustomPanel(int sentWidth, int sentHeight, int top, int left, int bottom,
 * int right, boolean drawInsets, int insetColor1, int insetColor2, int insetColor3)
 * public CustomPanel(int sentWidth, int sentHeight, int top, int left, int bottom,
 * int right, String s, Color c, boolean drawInsets, int insetColor1,
 * int insetColor2, int insetColor3)
 * -Helping functions:
 * public void paint(Graphics g)
 * public Insets getInsets()
 * public Dimension getPreferredSize()
 * <p/>
 * INPUT:        Nothing.
 * <p/>
 * OUTPUT:       A customized panel.
 * <p/>
 * NOTES:        This class is derived from the class Panel.
 * *********************************************************************************************
 */
class CustomPanel extends Panel {
    //values for the default insets
    private int top = 5;
    private int left = 5;
    private int bottom = 5;
    private int right = 5;
    private int newWidth;
    private int newHeight;
    private int insetColor1;
    private int insetColor2;
    private int insetColor3;
    private boolean drawInsets = false;

    //variables for the string
    private String s = "";
    //orientation
    private String o = "";
    private String arrow = "";
    String chartoString = "";
    private final Font f = new Font("Times Roman", Font.BOLD, 14);
    Font f1 = new Font("Times Roman", Font.PLAIN, 12);
    private Color c;
    Color insetColor;
    private int ycoord;

    /**
     * ********************************************************************************************
     * constructor with no arguments
     * *********************************************************************************************
     */
    public CustomPanel() {
        super();
    }//end CustomPanel constructor

    /**
     * ********************************************************************************************
     * constructor with arguments
     * *********************************************************************************************
     */
    public CustomPanel(int sentWidth, int sentHeight) {
        super();
        newWidth = sentWidth;
        newHeight = sentHeight;
        drawInsets = false;
    }

    /**
     * ********************************************************************************************
     * constructor with arguments
     * *********************************************************************************************
     */
    public CustomPanel(int sentWidth, int sentHeight, String arrow, int ycoord) {
        super();
        newWidth = sentWidth;
        newHeight = sentHeight;
        this.arrow = arrow;
        this.ycoord = ycoord;
    }

    /**
     * ********************************************************************************************
     * constructor with arguments
     * *********************************************************************************************
     */
    public CustomPanel(int sentWidth, int sentHeight, int top, int left, int bottom, int right) {
        super();
        newWidth = sentWidth;
        newHeight = sentHeight;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    /**
     * ********************************************************************************************
     * constructor with arguments
     * *********************************************************************************************
     */
    public CustomPanel(int sentWidth, int sentHeight, int top, int left, int bottom, int right, int insetColor1, int insetColor2, int insetColor3) {
        super();
        newWidth = sentWidth;
        newHeight = sentHeight;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.drawInsets = true;
        this.insetColor1 = insetColor1;
        this.insetColor2 = insetColor2;
        this.insetColor3 = insetColor3;
    }

    /**
     * ********************************************************************************************
     * constructor with arguments
     * *********************************************************************************************
     */
    public CustomPanel(int sentWidth, int sentHeight, int top, int left, int bottom, int right, String s, String o, Color c, boolean drawInsets, int insetColor1, int insetColor2, int insetColor3) {
        super();
        newWidth = sentWidth;
        newHeight = sentHeight;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.s = s;
        this.o = o;
        this.c = c;
        this.drawInsets = drawInsets;
        this.insetColor1 = insetColor1;
        this.insetColor2 = insetColor2;
        this.insetColor3 = insetColor3;
    }//end CustomPanel constructor

    /**
     * ********************************************************************************************
     * paint method
     * *********************************************************************************************
     */
    public void paint(Graphics g) {
        Dimension d = getSize();
        //if the panel has a string, draw it
        if (!s.equals("")) {
            int x = 0;
            int y = 0;
            g.setColor(c);
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            if (o.equals("h")) {
                x = (d.width - fm.stringWidth(s)) / 2;
                y = d.height / 2;
                g.drawString(s, x, y);
            }
        }//end if

        if (!arrow.equals("")) {
            g.setColor(Color.black);
            g.drawLine(0, ycoord, d.width, ycoord);
        }

        if (drawInsets) {
            g.setColor(new Color(insetColor1 - 20, insetColor2 - 20, insetColor3 - 20));
            //draw the left inset
            for (int i = 0; i < left; ++i) {
                g.drawLine(i, i, i, d.height - i);
            }

            //draw the top inset
            for (int i = 0; i < top; ++i) {
                g.drawLine(i, i, d.width - i, i);
            }

            g.setColor(new Color(insetColor1 + 20, insetColor2 + 20, insetColor3 + 20));
            //draw the bottom inset
            for (int i = 0; i < bottom; ++i) {
                g.drawLine(i, d.height - i, d.width - i, d.height - i);
            }

            //draw the right inset
            for (int i = 0; i < right; ++i) {
                g.drawLine(d.width - i, i, d.width - i, d.height - i);
            }
        }//end drawInsets
    }//end paint

    /**
     * ********************************************************************************************
     * get the insets for border
     * *********************************************************************************************
     */
    public Insets getInsets() {
        return new Insets(top, left, bottom, right);

    }//end Insets

    //send the dimension of the panel
    public Dimension getPreferredSize() {
        return new Dimension(newWidth, newHeight);
    }

}// end of CustomPanel

/**
 * ********************************************************************************************
 * CLASS:	      SurfacePanel
 * <p/>
 * FUNCTION:     This class is a subclass of the class Panel.  It contains a Canvas and buttons to
 * to start the erosion process and to show or hide process information.
 * The class contains:
 * -Constructor:
 * SurfacePanel(SharedParameters globalvariables, ErosionCanvas e1,
 * ErosionSim e2)= constructor
 * -Helping functions:
 * public void actionPerformed(ActionEvent e) = to listen to buttons
 * public void adjustmentValueChanged(AdjustmentEvent e) = to listen to canvas
 * sliders
 * public Insets getInsets() = to set the insets
 * <p/>
 * INPUT:        Nothing.
 * <p/>
 * OUTPUT:       Nothing.
 * <p/>
 * NOTES:        This class is derived from the class Panel.
 * *********************************************************************************************
 */
class SurfacePanel extends Panel implements ActionListener, AdjustmentListener {
    private final static int EDGE = 5;
    private final ErosionSim esim;
    private final ErosionCanvas ecanv;

    private static Scrollbar viewHeadingSlider;
    private static Scrollbar viewAltitudeSlider;
    private final Button showsurfaceButton;
    private final Button resetButton;

    /**
     * ********************************************************************************************
     * constructor
     * *********************************************************************************************
     */
    SurfacePanel(SharedParameters globalvariables, ErosionCanvas e1, ErosionSim e2) {
        super();
        ecanv = e1;
        esim = e2;

        // Place the canvas on the panel
        Panel viewPanel = new Panel();
        viewPanel.setLayout(new BorderLayout());
        viewHeadingSlider = new Scrollbar(Scrollbar.HORIZONTAL,
                -30, 10, -180, 180 + 10);
        viewPanel.add(viewHeadingSlider, BorderLayout.SOUTH);
        viewHeadingSlider.addAdjustmentListener(this);

        viewAltitudeSlider = new Scrollbar(Scrollbar.VERTICAL,
                -30, 10, -90, 10);
        viewPanel.add(viewAltitudeSlider, BorderLayout.EAST);
        viewAltitudeSlider.addAdjustmentListener(this);
        viewPanel.add(ecanv, BorderLayout.CENTER);
        showsurfaceButton = new Button("Run");
        SharedParameters.SHOWSURFACEBUTTON = showsurfaceButton;
        new ToolTip("Click to start/continue the simulation", showsurfaceButton);
        showsurfaceButton.addActionListener(this);
        resetButton = new Button("Reset");
        resetButton.addActionListener(this);
        new ToolTip("Click to restart the simulation all over", resetButton);
        CustomPanel topLegend = new CustomPanel(400, 28, 1, 1, 1, 1);
        CustomPanel button1 = new CustomPanel(50, 28, 1, 1, 1, 1);
        button1.setLayout(new GridLayout(1, 1));
        CustomPanel button2 = new CustomPanel(50, 28, 1, 1, 1, 1);
        button2.setLayout(new GridLayout(1, 1));
        Label label0 = new Label("");
        Label label1 = new Label("");
        Label label2 = new Label("");
        Label label3 = new Label("");
        CustomPanel buttonPanel = new CustomPanel(350, 50, 1, 1, 1, 1, 169, 187, 211);
        CustomPanel buttoncanvasPanel = new CustomPanel(350, 450, 2, 2, 2, 2, 169, 187, 211);
        setLayout(new BorderLayout());
        ecanv.setBackgroundColor(Color.white);
        buttonPanel.setLayout(new GridLayout(2, 4));
        button1.add(showsurfaceButton);
        button2.add(resetButton);
        CustomPanel legends = new CustomPanel(30, 320, 1, 1, 1, 1, 169, 187, 211);
        legends.setLayout(new BorderLayout());
        Label ef = new Label("E");
        Label l = new Label("l");
        Label es = new Label("e");
        Label v = new Label("v");
        Label a = new Label("a");
        Label t = new Label("t");
        Label i = new Label("i");
        Label o = new Label("o");
        Label n = new Label("n");
        Label blank1 = new Label("");
        Label blank2 = new Label("");
        Label blank3 = new Label("");
        Label blank4 = new Label("");
        Label blank5 = new Label("");
        CustomPanel elevationPanel = new CustomPanel(10, 300, 1, 1, 1, 1);
        elevationPanel.setLayout(new GridLayout(14, 1));
        elevationPanel.add(blank1);
        elevationPanel.add(blank2);
        elevationPanel.add(ef);
        elevationPanel.add(l);
        elevationPanel.add(es);
        elevationPanel.add(v);
        elevationPanel.add(a);
        elevationPanel.add(t);
        elevationPanel.add(i);
        elevationPanel.add(o);
        elevationPanel.add(n);
        elevationPanel.add(blank3);
        elevationPanel.add(blank4);
        elevationPanel.add(blank5);
        CustomPanel colorRamp = new CustomPanel(10, 300, 4, 4, 4, 4);
        colorRamp.setLayout(new BorderLayout());
        legendPanel lPanel = new legendPanel();
        colorRamp.add(lPanel, BorderLayout.EAST);
        Label topheight = new Label(" high  ");
        Label bottomheight = new Label("    low ");
        legends.add(topheight, BorderLayout.NORTH);
        legends.add(colorRamp, BorderLayout.CENTER);
        legends.add(elevationPanel, BorderLayout.WEST);
        CustomPanel legendbottomPanel = new CustomPanel(10, 80, 4, 4, 44, 4);
        legendbottomPanel.add(bottomheight);
        legends.add(legendbottomPanel, BorderLayout.SOUTH);
        CustomPanel subPanel = new CustomPanel(320, 440, 0, 0, 0, 0);
        subPanel.setLayout(new BorderLayout());
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(label2);
        buttonPanel.add(label3);
        subPanel.add(viewPanel, BorderLayout.CENTER);
        subPanel.add(buttonPanel, BorderLayout.SOUTH);
        buttoncanvasPanel.setLayout(new BorderLayout());
        topLegend.add(SharedParameters.values1);
        topLegend.add(SharedParameters.values2);
        topLegend.add(SharedParameters.values3);
        topLegend.add(SharedParameters.values4);
        buttoncanvasPanel.add(topLegend, BorderLayout.NORTH);
        buttoncanvasPanel.add(subPanel, BorderLayout.CENTER);
        buttoncanvasPanel.add(legends, BorderLayout.WEST);
        add(buttoncanvasPanel, BorderLayout.CENTER);
    }//end SurfacePanel constructor

    /**
     * ********************************************************************************************
     * implementation of ActionListener
     * *********************************************************************************************
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showsurfaceButton) {
            if (showsurfaceButton.getLabel().equals("Run")) {
//       			gv.SEDIMENT = 0.0;
                SharedParameters.ROUTINESTARTED = true;
                esim.resume();
                showsurfaceButton.setLabel("Suspend");
                new ToolTip("Click to temporarily suspend the simulation", showsurfaceButton);
            } else {
                esim.suspend();
                SharedParameters.ROUTINESTARTED = false;
                showsurfaceButton.setLabel("Run");
                new ToolTip("Click to start/continue the simulation", showsurfaceButton);
            }
        }
        if (e.getSource() == resetButton) {
            esim.suspend();
            SharedParameters.STARTALLOVER = true;
            showsurfaceButton.setLabel("Suspend");
            new ToolTip("Click to temporarily suspend the simulation", showsurfaceButton);
            esim.resume();
        }
    }// end ActionPerformed

    public void adjustmentValueChanged(AdjustmentEvent e) {
        // These can be changed while simulation is running
        // So no check is needed
        if (e.getSource() == viewHeadingSlider) {
            // Horizontal scrollbar
            ecanv.setHeading(e.getValue());
        } else if (e.getSource() == viewAltitudeSlider) {
            // Vertical scrollbar
            ecanv.setAltitude(-e.getValue());
        }
    }

    public Insets getInsets() {
        return new Insets(EDGE, EDGE, EDGE, EDGE);
    }//end get Insets
}//end class SurfacePanel


/**
 * ********************************************************************************************
 * CLASS:	      decimalLabel
 * <p/>
 * FUNCTION:     This class helps in creating labels with the right number of the decimal places
 * for the user interface.
 * -Constructor:
 * decimalLabel()
 * -Helping functions:
 * protected void set1decimalText(double newnumber)
 * protected void set2decimalText(double newnumber)
 * protected void set3decimalText(double newnumber)
 * protected void set4decimalText(double newnumber)
 * protected void set5decimalText(double newnumber)
 * <p/>
 * NOTES:        This class is derived from the class Label
 * *********************************************************************************************
 */

class decimalLabel extends Label {

    decimalLabel() {
        super("");
    }

    void set1decimalText() {
        DecimalFormat df1 = new DecimalFormat("#0.0");
        this.setText(df1.format(0.1));
    }

    void set2decimalText(double newnumber) {
        DecimalFormat df1 = new DecimalFormat("#0.00");
        this.setText(df1.format(newnumber));
    }

    void set4decimalText(double newnumber) {
        DecimalFormat df1 = new DecimalFormat("#0.0000");
        this.setText(df1.format(newnumber));
    }

}

/**
 * ********************************************************************************************
 * CLASS:	      legendPanel
 * <p/>
 * FUNCTION:     This class is used for the bar with colors giving the reference to the image colors.
 * -Constructor:
 * legendPanel(int newWidth, int newHeight, int color1, int color2, int color3)
 * -Helping functions:
 * public void paint(Graphics g)
 * public Dimension getPreferredSize()
 * <p/>
 * NOTES:        This class is derived from the class Panel
 * *********************************************************************************************
 */

class legendPanel extends Panel {
    private final ErosionColors legendColor = new ErosionColors();
    private final int newHeight;
    private final int newWidth;

    legendPanel() {
        super();
        this.newHeight = 320;
        this.newWidth = 10;
        setBackground(new Color(169, 187, 211));
    }

    public void paint(Graphics g) {
        int y = 0;
        Dimension d = getSize();
        int index;

        for (y = 0; y < d.height; y++) {
            index = (d.height - y) * (legendColor.getSize() - 1) / d.height;
            g.setColor(new Color(legendColor.getColor1(index),
                    legendColor.getColor2(index),
                    legendColor.getColor3(index)));
            g.drawLine(0, y, newWidth, y);
        }
    }

    //send the dimension of the panel
    public Dimension getPreferredSize() {
        return new Dimension(newWidth, newHeight);
    }
}

/**
 * ********************************************************************************************
 * CLASS:	      ToolTip
 * <p/>
 * FUNCTION:     This class is used to create the tooltips next to the buttons and radio buttons.
 * -Constructor:
 * public ToolTip()
 * public ToolTip(String tip, Component owner)
 * -Helping functions:
 * public void paint(Graphics g)
 * public void addToolTip()
 * public void removeToolTip()
 * public void findMainContainer()
 * <p/>
 * NOTES:        This class is derived from the class Canvas
 * *********************************************************************************************
 */

class ToolTip extends Canvas {
    private final String tip;
    private Component owner;

    private Container mainContainer;
    private LayoutManager mainLayout;

    private boolean shown;

    public ToolTip() {
        tip = "";
        setBackground(new Color(255, 255, 220));
    }

    public ToolTip(String tip, Component owner) {
        this.tip = tip;
        this.owner = owner;
        owner.addMouseListener(new MAdapter(this));
        setBackground(new Color(255, 255, 220));
    }

    public void paint(Graphics g) {
        g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        g.drawString(tip, 3, getSize().height - 3);
    }

    public void addToolTip() {
        mainContainer.setLayout(null);

        FontMetrics fm = getFontMetrics(owner.getFont());
        int HORIZONTAL_ENLARGE = 10;
        setSize(fm.stringWidth(tip) + HORIZONTAL_ENLARGE, fm.getHeight());

        int VERTICAL_OFFSET = 30;
        setLocation((owner.getLocationOnScreen().x - mainContainer.getLocationOnScreen().x),
                (owner.getLocationOnScreen().y - mainContainer.getLocationOnScreen().y + VERTICAL_OFFSET));

        // correction, whole tool tip must be visible 
        if (mainContainer.getSize().width < (getLocation().x + getSize().width)) {
            setLocation(mainContainer.getSize().width - getSize().width, getLocation().y);
        }

        mainContainer.add(this, 0);
        mainContainer.validate();
        repaint();
        shown = true;
    }


    public void removeToolTip() {
        if (shown) {
            mainContainer.remove(0);
            mainContainer.setLayout(mainLayout);
            mainContainer.validate();
        }
        shown = false;
    }

    public void findMainContainer() {
        Container parent = owner.getParent();
        while (true) {
            if ((parent instanceof Applet) || (parent instanceof Frame)) {
                mainContainer = parent;
                break;
            } else {
                parent = parent.getParent();
            }
        }
        mainLayout = mainContainer.getLayout();
    }
}

/**
 * ********************************************************************************************
 * CLASS:	      MAdapter
 * <p/>
 * FUNCTION:     This class is used to manipulate the tooltips
 * -Constructor:
 * public MAdapter(ToolTip t)
 * -Helping functions:
 * public void mouseEntered(MouseEvent e)
 * public void mouseExited(MouseEvent e)
 * public void mousePressed(MouseEvent e)
 * <p/>
 * NOTES:        This class is derived from the class Canvas
 * *********************************************************************************************
 */

class MAdapter extends MouseAdapter {

    private ToolTip t = new ToolTip();

    public MAdapter(ToolTip t) {
        this.t = t;
    }

    public void mouseEntered(MouseEvent me) {
        t.findMainContainer();
        t.addToolTip();
    }

    public void mouseExited(MouseEvent me) {
        t.removeToolTip();
    }

    public void mousePressed(MouseEvent me) {
        t.removeToolTip();
    }
}
