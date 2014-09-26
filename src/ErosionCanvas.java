/***********************************************************************************************
 CLASS:	      EROSIONCANVAS

 FUNCTION:     This class creates and renders the image representing the simulation.
 The class contains:
 -Constructor:
 *public EROSIONCANVAS() = all variables are initialized to default values.
 -Helping functions:
 *public void start() = to get the thread started.
 *public void paint(Graphics g) = to draw the image.
 *public void update(Graphics g) = to repaint the image.
 *public synchronized void resizeCanvas(int xs, int ys) =
 *public void setBackgroundColor(Color c) = to se the canvas background color.
 *public run() = called by start.
 *private boolean upToDate() = to control the graph orientation.
 *private void render() = to render the image.
 *private void drawBorders() =
 *private int calculateShading(int surfaceColor) = for image shading.
 *public void redraw()
 *public void setWallColor(int r, int g, int b)
 *public void setDataColor(int row, int col, int r, int g, int b)
 *public void setDataHeight(int row, int col, double height)
 *public synchronized void setGridSize(int rows, int columns)
 *public synchronized void setViewHeight(double height)
 *public synchronized void setAltitude(int val)
 *public synchronized void setHeading(int val)
 *private void setRenderOrder()
 *private void quadClipDraw(double x_in, double y_in, boolean end_flag)
 *private void scanConvert()
 *private void insertEdgeList(int index)
 *private void makeEdgeRec(int lower, int upper, int index)
 *private boolean buildEdgeList()
 *private void insertActiveEdgeList(Edge e)
 *private void buildActiveList(int scanline)
 *private void fillScan(int scanline)
 *private void updateActiveList(int scanline)
 *private void resortActiveList()
 *private void drawSquare(int x1, int y1, int x2, int y2, int color)
 *private void calculateClipToScreen()
 *private void calculateGridToWorld()
 *private void calculateCameraToClip()
 *private void calculateWorldToCamera()

 DATE CREATED: August 2002
 ***********************************************************************************************/

import java.awt.*;
import java.awt.image.MemoryImageSource;

class EROSIONCANVAS extends Canvas implements Runnable {

    // Polygon clipping
    private final static boolean MOVE = false;
    private final static boolean DRAW = true;
    private final static int MAX_VERTEX_LIST_SIZE = 4;
    private final Thread thread;   // The rendering thread
    private final double[] screen1old;
    private final double[] screen2old;
    private final double[] screen1new;
    private final double[] screen2new;
    private final double[] world1old;
    private final double[] world2old;
    private final double[] world1new;
    private final double[] world2new;
    private final double[] v1;

    // Grid information
    private final double[] v2;
    private final double[] normal;
    private final double[] clippedVertexX;
    private final double[] clippedVertexY;

    // Grid rendering parameters
    // Polygon scan conversion
    private final Edge[] edges;
    private final Xform clipToScreen;
    private final Xform cameraToClip;
    private final Xform cameraToScreen;
    private final Xform worldToCamera;
    private final Xform worldToScreen;
    private final Xform gridToWorld;
    private final Xform gridToScreen;
    private Image backing;   // For backing store
    private Graphics bg;
    private boolean backingImageSetup;
    private Image render;    // For rendering
    private int pixels[];
    private MemoryImageSource source;
    private boolean renderImageSetup;
    private boolean newCanvasSize;  // Canvas has changed size
    private boolean newOrientation; // Projection direction has changed
    private boolean dataChange;
    private boolean setup;
    private int xSize, ySize;  // The size of the rendering canvas in pixels
    private int gridxSize, gridySize;
    private double gridZ[];   // Data stored in a 1D grid for speed
    private int gridColor[];

    // For debugging purposes
    private int borderColor;
    private int xRenderStart, xRenderEnd, xRenderInc;
    private int yRenderStart, yRenderEnd, yRenderInc;
    private double normalFlip;
    private int nVertex;
    // Holds the transformation from clipping coordinates
    // to device coordinates
    private Edge edgeList;
    // Holds the transformation from camera coordinates
    // to clipping coordinates
    private Edge activeEdgeList;
    // Holds the transformation from camera coordinates
    // to clipping coordinates
    private int backgroundColor;
    // Holds the transformations from world coordinates to 
    // camera coordinates
    private int drawingColor;
    // Holds the composite transformation from world coordinates to 
    // screen coordinates.  This may be removed later.
    private double aspect;     // The aspect ratio of the canvas
    // Holds the transformations from grid coordinates to 
    // world coordinates
    private double heading, altitude;  // Viewing angles in radians
    // Holds the composite transformation from grid coordinates to 
    // screen coordinates

    public EROSIONCANVAS() {
        // Some initial values
        xSize = 200;
        ySize = 200;  // Just a random initial value

        backgroundColor = 0; // black

        heading = -30.0f * Math.PI / 180.0f;
        altitude = 30.0f * Math.PI / 180.0f;

        // 3D transformation initialization
        clipToScreen = new Xform();
        cameraToClip = new Xform();
        cameraToScreen = new Xform();
        worldToCamera = new Xform();
        worldToScreen = new Xform();
        gridToWorld = new Xform();
        gridToScreen = new Xform();

        aspect = (double) xSize / ySize;

        calculateClipToScreen();
        calculateCameraToClip();
        Xform.mult(cameraToScreen, clipToScreen, cameraToClip);
        calculateWorldToCamera();
        Xform.mult(worldToScreen, cameraToScreen, worldToCamera);
        calculateGridToWorld();
        Xform.mult(gridToScreen, worldToScreen, gridToWorld);

        // Data grid initialization
        gridxSize = gridySize = 1;
        gridZ = new double[1];
        gridColor = new int[1];

        xRenderStart = 0;
        xRenderEnd = gridxSize;
        xRenderInc = 1;
        yRenderStart = gridySize - 1;
        yRenderEnd = -1;
        yRenderInc = -1;

        // Rendering parameter initialization
        screen1old = new double[3];
        screen2old = new double[3];
        screen1new = new double[3];
        screen2new = new double[3];

        world1old = new double[3];
        world2old = new double[3];
        world1new = new double[3];
        world2new = new double[3];

        v1 = new double[3];
        v2 = new double[3];
        normal = new double[3];

        // State flag initialization
        newCanvasSize = false;
        newOrientation = false;
        dataChange = false;

        backingImageSetup = false;
        renderImageSetup = false;
        setup = false;


        // Set up the clipping pipeline
//        double[] unclippedVertexX = new double[MAX_VERTEX_LIST_SIZE];
//        double[] unclippedVertexY = new double[MAX_VERTEX_LIST_SIZE];
        clippedVertexX = new double[MAX_VERTEX_LIST_SIZE * 2];
        clippedVertexY = new double[MAX_VERTEX_LIST_SIZE * 2];
        nVertex = 0;

        // Set up the scan conversion structures
        edges = new Edge[MAX_VERTEX_LIST_SIZE * 2];
        for (int i = 0; i < edges.length; i++)
            edges[i] = new Edge();

        // Set up the new thread
        thread = new Thread(this);
    /*
    // Set up the debugging frame
	errs = new Frame("Debugging info");
	errs.setSize(500, 500);
	msg = new TextArea("");
	errs.add(msg);
	errs.show();
	*/
    }

    public void start() {
        // Reduce priority of renderer so that UI takes precedence
        Thread current = Thread.currentThread();
        thread.setPriority(current.getPriority() - 1);

        thread.start();
    }

    public void update(Graphics g) {
        // Don't clear background.  paint() will redraw entire canvas
        paint(g);
    }

    public void paint(Graphics g) {

        //msg.append("paint(): width=" + getSize().width + " height=" +
        //	       getSize().height + "\n");

        if (setup)
            g.drawImage(backing, 0, 0, xSize, ySize, this);

        if (getSize().width != xSize || getSize().height != ySize) {
            // Size has changed
            resizeCanvas(getSize().width, getSize().height);
        }
    }

    synchronized void resizeCanvas(int xs, int ys) {
        xSize = xs;
        ySize = ys;


        backing = createImage(xSize, ySize);
        // Put something in new image

        bg = backing.getGraphics();

        //msg.append("new backing area: " + xSize + ", " + ySize + "\n");

        // Some initialization code
        backingImageSetup = true;
        setup = renderImageSetup;

        // Start up the renderer
        newCanvasSize = true;

        notify();
    }

    public void setBackgroundColor(Color c) {
        backgroundColor = c.getRGB();
    }

    public void run() {

        while (true) {
            if (upToDate()) {
                synchronized (this) {
                    try {
                        //msg.append("Gonna wait.\n");
                        //msg.append("Altitude: " + altitude + "\n");
                        wait();
                    } catch (InterruptedException ie) {
                    }
                }
                continue;  // Recheck conditions
            }

            // Render here

            if (newCanvasSize) {
                // New rendering space needs to be allocated
                synchronized (this) {
                    // New rendering area
                    newCanvasSize = false;
                    pixels = new int[xSize * ySize];
                    source = new MemoryImageSource(xSize, ySize,
                            pixels, 0, xSize);
                    source.setAnimated(true);
                    render = createImage(source);

                    // New rendering parameters
                    aspect = (double) xSize / ySize;
                    calculateClipToScreen();
                    calculateCameraToClip();
                    Xform.mult(cameraToScreen,
                            clipToScreen, cameraToClip);
                    Xform.mult(worldToScreen,
                            cameraToScreen, worldToCamera);
                    Xform.mult(gridToScreen,
                            worldToScreen, gridToWorld);
                    renderImageSetup = true;
                    setup = backingImageSetup;

                    //msg.append("new rendering area: " + xSize + ", " + ySize + "\n");
                }
            }

            if (newOrientation) {
                // Calculate new rendering parameters

                newOrientation = false;

                calculateWorldToCamera();
                Xform.mult(worldToScreen,
                        cameraToScreen, worldToCamera);
                Xform.mult(gridToScreen,
                        worldToScreen, gridToWorld);
                setRenderOrder();
            }

            dataChange = false;
            //  Assume all changes to this point are caught

            // This thread can get here before drawing regions are set up.
            // Drawing regions can't be set up in constructor.  Info
            // from UI not available yet.
            if (!setup)
                continue;

            // Here's the picture
            //msg.append("Drawing...\n");
            // Background
            for (int i = xSize * ySize - 1; i >= 0; i--)
                pixels[i] = backgroundColor;

            render();

            // Refresh backing store
            source.newPixels(0, 0, xSize, ySize);
            bg.drawImage(render, 0, 0, xSize, ySize, this);
            //msg.append("calling repaint()\n");
            repaint();
        }
    }

    private boolean upToDate() {
        //msg.append("upToDate(): " + count++ + "\n");
        return !newCanvasSize && !newOrientation && !dataChange;

    }

    private void render() {
        int x, y;

        double z;

        // There are some speedup measures that can be taken here by
        // reusing values from one grid element to another

        // Y direction is outer loop
        for (y = yRenderStart; y != yRenderEnd; y += yRenderInc) {
            // Initialize trailing edge points
            z = gridZ[y * gridxSize + xRenderStart];
            screen1old[0] = screen1new[0] =
                    Xform.multRow(0, gridToScreen, xRenderStart, y, z);

            screen1old[1] = screen1new[1] =
                    Xform.multRow(1, gridToScreen, xRenderStart, y, z);

            world1old[0] = world1new[0] =
                    Xform.multRow(0, gridToWorld, xRenderStart, y, z);
            world1old[1] = world1new[1] =
                    Xform.multRow(1, gridToWorld, xRenderStart, y, z);
            world1old[2] = world1new[2] =
                    Xform.multRow(2, gridToWorld, xRenderStart, y, z);

            // Move one row along
            y += yRenderInc; // A temporary adjustment

            z = gridZ[y * gridxSize + xRenderStart];
            screen2old[0] = screen2new[0] =
                    Xform.multRow(0, gridToScreen, xRenderStart, y, z);

            screen2old[1] = screen2new[1] =
                    Xform.multRow(1, gridToScreen, xRenderStart, y, z);

            world2old[0] = world2new[0] =
                    Xform.multRow(0, gridToWorld, xRenderStart, y, z);
            world2old[1] = world2new[1] =
                    Xform.multRow(1, gridToWorld, xRenderStart, y, z);
            world2old[2] = world2new[2] =
                    Xform.multRow(2, gridToWorld, xRenderStart, y, z);

            y -= yRenderInc; // Correct the adjustment

            for (x = xRenderStart; x != xRenderEnd; ) {
                //msg.append("(" + y + ", " + x + "):\n");

                //  Move along one column
                x += xRenderInc;

                z = gridZ[y * gridxSize + x];
                screen1new[0] =
                        Xform.multRow(0, gridToScreen, x, y, z);

                screen1new[1] =
                        Xform.multRow(1, gridToScreen, x, y, z);

                world1new[0] =
                        Xform.multRow(0, gridToWorld, x, y, z);
                world1new[1] =
                        Xform.multRow(1, gridToWorld, x, y, z);
                world1new[2] =
                        Xform.multRow(2, gridToWorld, x, y, z);

                // Move one row along
                y += yRenderInc;  // Temporary adjustment

                z = gridZ[y * gridxSize + x];
                screen2new[0] =
                        Xform.multRow(0, gridToScreen, x, y, z);

                screen2new[1] =
                        Xform.multRow(1, gridToScreen, x, y, z);

                world2new[0] =
                        Xform.multRow(0, gridToWorld, x, y, z);
                world2new[1] =
                        Xform.multRow(1, gridToWorld, x, y, z);
                world2new[2] =
                        Xform.multRow(2, gridToWorld, x, y, z);


                y -= yRenderInc;  // Correct adjustment

                drawingColor = calculateShading(gridColor[y * gridxSize + x]);

                // Now draw quad -- CCW order not guaranteed yet

                quadClipDraw(screen1old[0], screen1old[1], MOVE);
                quadClipDraw(screen2old[0], screen2old[1], MOVE);
                quadClipDraw(screen2new[0], screen2new[1], MOVE);
                quadClipDraw(screen1new[0], screen1new[1], DRAW);

                // Save useful conversion work
                screen1old[0] = screen1new[0];
                screen1old[1] = screen1new[1];
                screen2old[0] = screen2new[0];
                screen2old[1] = screen2new[1];
                world1old[0] = world1new[0];
                world1old[1] = world1new[1];
                world1old[2] = world1new[2];
                world2old[0] = world2new[0];
                world2old[1] = world2new[1];
                world2old[2] = world2new[2];
            }

            Thread.yield(); // Play nice, let someone else compute.
        }
        // Draw border here
        drawBorders();
    }

    private void drawBorders() {
        int x, y;

        double z;

        // Side
        x = xRenderEnd;
        y = yRenderStart;
        z = gridZ[y * gridxSize + x];

        // Initialize trailing edge points
        screen1old[0] = screen1new[0] =
                Xform.multRow(0, gridToScreen, x, y, 0.0f);
        screen1old[1] = screen1new[1] =
                Xform.multRow(1, gridToScreen, x, y, 0.0f);


        screen2old[0] = screen2new[0] =
                Xform.multRow(0, gridToScreen, x, y, z);
        screen2old[1] = screen2new[1] =
                Xform.multRow(1, gridToScreen, x, y, z);

        world1old[0] = world1new[0] =
                Xform.multRow(0, gridToWorld, x, y, 0.0f);
        world1old[1] = world1new[1] =
                Xform.multRow(1, gridToWorld, x, y, 0.0f);
        world1old[2] = world1new[2] =
                Xform.multRow(2, gridToWorld, x, y, 0.0f);

        for (y = yRenderStart; y != yRenderEnd; ) {

            y += yRenderInc;

            z = gridZ[y * gridxSize + x];

            screen1new[0] =
                    Xform.multRow(0, gridToScreen, x, y, 0.0f);
            screen1new[1] =
                    Xform.multRow(1, gridToScreen, x, y, 0.0f);
            world1new[0] =
                    Xform.multRow(0, gridToWorld, x, y, 0.0f);
            world1new[1] =
                    Xform.multRow(1, gridToWorld, x, y, 0.0f);
            world1new[2] =
                    Xform.multRow(2, gridToWorld, x, y, 0.0f);

            screen2new[0] =
                    Xform.multRow(0, gridToScreen, x, y, z);
            screen2new[1] =
                    Xform.multRow(1, gridToScreen, x, y, z);
            world2new[0] =
                    Xform.multRow(0, gridToWorld, x, y, z);
            world2new[1] =
                    Xform.multRow(1, gridToWorld, x, y, z);
            world2new[2] =
                    Xform.multRow(2, gridToWorld, x, y, z);

            //		drawingColor = calculateShading(gridColor[y * gridxSize + x]);
            drawingColor = calculateShading(borderColor);

            // Now draw quad -- CCW order not guaranteed yet

            quadClipDraw(screen1old[0], screen1old[1], MOVE);
            quadClipDraw(screen2old[0], screen2old[1], MOVE);
            quadClipDraw(screen2new[0], screen2new[1], MOVE);
            quadClipDraw(screen1new[0], screen1new[1], DRAW);

            // Save useful conversion work
            screen1old[0] = screen1new[0];
            screen1old[1] = screen1new[1];
            screen2old[0] = screen2new[0];
            screen2old[1] = screen2new[1];
            world1old[0] = world1new[0];
            world1old[1] = world1new[1];
            world1old[2] = world1new[2];
            world2old[0] = world2new[0];
            world2old[1] = world2new[1];
            world2old[2] = world2new[2];
        }
        Thread.yield();  // Play nice

        // Front
        x = xRenderStart;
        y = yRenderEnd;
        z = gridZ[y * gridxSize + x];

        // Initialize trailing edge points
        screen1old[0] = screen1new[0] =
                Xform.multRow(0, gridToScreen, x, y, 0.0f);
        screen1old[1] = screen1new[1] =
                Xform.multRow(1, gridToScreen, x, y, 0.0f);

        screen2old[0] = screen2new[0] =
                Xform.multRow(0, gridToScreen, x, y, z);
        screen2old[1] = screen2new[1] =
                Xform.multRow(1, gridToScreen, x, y, z);

        world1old[0] = world1new[0] =
                Xform.multRow(0, gridToWorld, x, y, 0.0f);
        world1old[1] = world1new[1] =
                Xform.multRow(1, gridToWorld, x, y, 0.0f);
        world1old[2] = world1new[2] =
                Xform.multRow(2, gridToWorld, x, y, 0.0f);

        for (x = xRenderStart; x != xRenderEnd; ) {
            drawingColor = gridColor[y * gridxSize + x];

            x += xRenderInc;

            z = gridZ[y * gridxSize + x];

            screen1new[0] =
                    Xform.multRow(0, gridToScreen, x, y, 0.0f);
            screen1new[1] =
                    Xform.multRow(1, gridToScreen, x, y, 0.0f);
            world1new[0] =
                    Xform.multRow(0, gridToWorld, x, y, 0.0f);
            world1new[1] =
                    Xform.multRow(1, gridToWorld, x, y, 0.0f);
            world1new[2] =
                    Xform.multRow(2, gridToWorld, x, y, 0.0f);

            screen2new[0] =
                    Xform.multRow(0, gridToScreen, x, y, z);
            screen2new[1] =
                    Xform.multRow(1, gridToScreen, x, y, z);
            world2new[0] =
                    Xform.multRow(0, gridToWorld, x, y, z);
            world2new[1] =
                    Xform.multRow(1, gridToWorld, x, y, z);
            world2new[2] =
                    Xform.multRow(2, gridToWorld, x, y, z);

            //		drawingColor = calculateShading(gridColor[y * gridxSize + x]);
            drawingColor = calculateShading(borderColor);

            // Now draw quad -- CCW order not guaranteed yet

            quadClipDraw(screen1old[0], screen1old[1], MOVE);
            quadClipDraw(screen2old[0], screen2old[1], MOVE);
            quadClipDraw(screen2new[0], screen2new[1], MOVE);
            quadClipDraw(screen1new[0], screen1new[1], DRAW);

            // Save useful conversion work
            screen1old[0] = screen1new[0];
            screen1old[1] = screen1new[1];
            screen2old[0] = screen2new[0];
            screen2old[1] = screen2new[1];
            world1old[0] = world1new[0];
            world1old[1] = world1new[1];
            world1old[2] = world1new[2];
            world2old[0] = world2new[0];
            world2old[1] = world2new[1];
            world2old[2] = world2new[2];
        }
        Thread.yield();  // Play nice
    }

    private int calculateShading(int surfaceColor) {
        // Ka = 0.3  Kd = 0.7
        // Light in (-1, 0, 1) direction


        // Form first vector
        v1[0] = world1new[0] - world1old[0];
        v1[1] = world1new[1] - world1old[1];
        v1[2] = world1new[2] - world1old[2];

        // Form second vector
        v2[0] = world2old[0] - world1old[0];
        v2[1] = world2old[1] - world1old[1];
        v2[2] = world2old[2] - world1old[2];

        // Form cross product
        normal[0] = v1[1] * v2[2] - v1[2] * v2[1];
        normal[1] = v2[0] * v1[2] - v1[0] * v2[2];
        normal[2] = v1[0] * v2[1] - v2[0] * v1[1];

        // Must normalize normal vector -- ouch!
        double factor = normal[0] * normal[0]
                + normal[1] * normal[1] + normal[2] * normal[2];

        factor = Math.sqrt(factor);

        normal[0] /= factor;
        //normal[1] /= factor;
        normal[2] /= factor;

        // Hard wire to white for now

        // Dot product with normalized (-1, 0, 1)
        double dot = normal[0] * -0.70710678f + normal[2] * 0.70710678f;

        dot *= normalFlip;

        int r, g, b;
        r = (surfaceColor & 0x00ff0000) >> 16;
        g = (surfaceColor & 0x0000ff00) >> 8;
        b = (surfaceColor & 0x000000ff);

        if (dot < 0.0f) // Surface turned away from light
        {
            // Ambient only
            r = (int) (0.3f * r);
            g = (int) (0.3f * g);
            b = (int) (0.3f * b);
        } else {
            // Ambient and diffuse
            r = (int) (0.3f * r + 0.7f * dot * r);
            g = (int) (0.3f * g + 0.7f * dot * g);
            b = (int) (0.3f * b + 0.7f * dot * b);
        }

        return (0xff << 24) | (r << 16) | (g << 8) | b;

    }

    public synchronized void setHeading(int val) {
        // In the range of -180 to 180 degrees
        heading = (double) (val) * Math.PI / 180.0f;
        //msg.append("Heading: " + val + "\n");
        newOrientation = true;
        notify();
    }

    public synchronized void setAltitude(int val) {
        // In the range of 0 to 90 degrees
        altitude = (double) (val) * Math.PI / 180.0f;
        //msg.append("Altitude: " + val + "\n");
        newOrientation = true;
        notify();
    }

    public synchronized void setGridSize(int rows, int columns) {
        // Sets up a new data grid
        gridySize = rows;
        gridxSize = columns;

        // Allocate a new data grid for rendering
        // Allocation must be done while lock is held on this object
        // otherwise calls to setData*() may occur before the allocation
        // is done

        //msg.append("setGridSize()\n");

        // Releasing arrays first will hopefully reduce memory fragmentation
        gridZ = null;
        gridColor = null;

        gridZ = new double[gridxSize * gridySize];
        gridColor = new int[gridxSize * gridySize];

        calculateGridToWorld();

        Xform.mult(gridToScreen, worldToScreen, gridToWorld);

        setRenderOrder();

        notify();
    }

    public void setDataHeight(int row, int col, double height) {
        // No error checking is done here --- inherently dangerous

        //msg.append("setDataHeight: " + row + ", " + col +  ": " + height + "\n");

        gridZ[row * gridxSize + col] = height;

	/*
    // Hack for testing
	double x, y;
	x = (col - 25) / 25.0f;
	y = (row - 25) / 25.0f;

	gridZ[row * gridxSize + col] = 35.0f - 20.0f * (x * x + y * y); 
	*/

        dataChange = true;
    }

    public void setDataColor(int row, int col, int r, int g, int b) {

        // No error checking is done here --- inherently dangerous

        gridColor[row * gridxSize + col] = (255 << 24) | (r << 16)
                | (g << 8) | b;
        dataChange = true;
    }

    public void setWallColor() {
        // No error checking is done here

        borderColor = (255 << 24) | (200 << 16) | (180 << 8) | 100;
    }

    public void redraw() {
        // Forces a rerendering

        synchronized (this) {
            try {
                notify();
            } catch (IllegalMonitorStateException imse) {
            }
        }
    }

    private void setRenderOrder() {
        normalFlip = 1.0f;

        if (worldToCamera.get(0) > 0.0f)  // cosine of heading
        {
            // Y should decrease over rows
            yRenderStart = gridySize - 1;
            yRenderEnd = 0;
            yRenderInc = -1;
            normalFlip *= -1.0f;
        } else {
            // Y should increase over rows
            yRenderStart = 0;
            yRenderEnd = gridySize - 1;
            yRenderInc = 1;
        }

        if (worldToCamera.get(1) < 0.0f)  // -sine of heading
        {
            // X should decrease over columns
            xRenderStart = gridxSize - 1;
            xRenderEnd = 0;
            xRenderInc = -1;
            normalFlip *= -1.0f;
        } else {
            // X should increase over columns
            xRenderStart = 0;
            xRenderEnd = gridxSize - 1;
            xRenderInc = 1;
        }
    }

    private void quadClipDraw(double x_in, double y_in, boolean end_flag) {
        // Full clipping not really needed for this application
        // Just move vertices to edge


        //msg.append(nVertex + ": " + x_in + "  " + y_in + "\n");

        if (x_in < 0.0f) x_in = 0.0f;
        if (x_in >= xSize) x_in = xSize;
        if (y_in < 0.0f) y_in = 0.0f;
        if (y_in >= ySize) y_in = ySize;

        clippedVertexX[nVertex] = x_in;
        clippedVertexY[nVertex] = y_in;
        nVertex++;

        if (end_flag) {
            // Draw it!
            scanConvert();

            // Reset vertex count for next quad
            nVertex = 0;
        }
    }

    private void scanConvert() {
        //msg.append("scanConvert()\n");

        int scan;

        // Find screen extent in y

        double ymax;
        ymax = clippedVertexY[0];
        for (int i = 1; i < nVertex; i++) {
            if (clippedVertexY[i] > ymax)
                ymax = clippedVertexY[i];
        }

        activeEdgeList = null;

        if (!buildEdgeList())
            return;  // No edges cross a scanline

        for (scan = edgeList.y; scan <= ymax; scan++) {
            buildActiveList(scan);

            if (activeEdgeList != null) {
                fillScan(scan);
                updateActiveList(scan);
                resortActiveList();
            }
        }

    }

    private void insertEdgeList(int index) {
        Edge p = edgeList, pred = null;
        Edge q = edges[index];

        // Step through list
        while (true) {
            if (p == null)
                break;  // End of list

            if (q.y < p.y)
                break;  // We're here

            if (q.y == p.y && q.x < p.x)
                break;   // Also the proper position

            // Keep moving
            pred = p;
            p = p.next;

        }

        if (pred == null)  // Beginning of list
        {
            q.next = edgeList;
            edgeList = q;
        } else {
            q.next = p;
            pred.next = q;
        }
    }


    private void makeEdgeRec(int lower, int upper, int index) {
        double divisor, factor;

        divisor = clippedVertexY[upper] - clippedVertexY[lower];

        edges[index].dx = (clippedVertexX[upper] - clippedVertexX[lower])
                / divisor;

        // Not doing color interpolation now

        // Find initial sample position value where edge intersects scanline
        factor = Math.ceil(clippedVertexY[lower]) - clippedVertexY[lower];

        edges[index].x = clippedVertexX[lower] + factor * edges[index].dx;
        edges[index].y = (int) Math.ceil(clippedVertexY[lower]);

        // Determine last scanline for edge
        edges[index].yUpper = (int) Math.ceil(clippedVertexY[upper]) - 1;

        edges[index].next = null;

        insertEdgeList(index);

    }

    private boolean buildEdgeList() {
        int v1, v2;
        boolean crossScanline = false;

        int nEdges = 0;
        edgeList = null;

        v1 = nVertex - 1;
        for (v2 = 0; v2 < nVertex; v2++) {
            if (Math.ceil(clippedVertexY[v1]) !=
                    Math.ceil(clippedVertexY[v2])) {
                crossScanline = true;
                if (clippedVertexY[v1] < clippedVertexY[v2])
                    // increasing edge
                    makeEdgeRec(v1, v2, nEdges);
                else
                    // decreasing edge
                    makeEdgeRec(v2, v1, nEdges);
                nEdges++;
            }
            v1 = v2;
        }
        return crossScanline;
    }

    private void insertActiveEdgeList(Edge e) {
        Edge p = activeEdgeList, pred = null;

        // Step through list
        while (p != null && p.x < e.x) {
            pred = p;
            p = p.next;
        }

        if (pred == null)  // Beginning of list
        {
            e.next = activeEdgeList;
            activeEdgeList = e;
        } else {
            e.next = p;
            pred.next = e;
        }
    }

    private void buildActiveList(int scanline) {
        Edge temp;

        while (edgeList != null && edgeList.y == scanline) {
            // Transfer the edges to the active list

            temp = edgeList.next;
            insertActiveEdgeList(edgeList);
            edgeList = temp;
        }
    }

    private void fillScan(int scanline) {
        Edge p1, p2;
        int index, end, column;

        p1 = activeEdgeList;


        while (p1 != null) {
            // Edges should occur in pairs

            p2 = p1.next;

            column = (int) Math.ceil(p1.x);
            end = (int) Math.ceil(p2.x);

            if (column != end) {
                // Span crosses a sample point

                // No attribute interpolation for now

                index = scanline * xSize + column;
                for (; column < end; column++) {
                    pixels[index++] = drawingColor;
                }
            }
            p1 = p2.next;
        }
    }

    private void updateActiveList(int scanline) {
        // Remove finished edges from active edge list and
        // update the values along all other edges

        Edge p = activeEdgeList, pred = null;

        while (p != null) {
            if (scanline >= p.yUpper) {
                // Remove this edge from active consideration
                p = p.next;
                if (pred == null)
                    activeEdgeList = p;  // remove head of list
                else
                    pred.next = p;

            } else {
                // Update the attribute values
                p.x += p.dx;

                pred = p;
                p = p.next;
            }
        }
    }

    private void resortActiveList() {
        // Rebuild list completely

        Edge p = activeEdgeList, q;

        activeEdgeList = null;

        while (p != null) {
            q = p;
            p = p.next;
            insertActiveEdgeList(q);
        }
    }

    private void calculateClipToScreen() {
        // Calculate the transformation from clipping coordinates to
        // device coordinates

        clipToScreen.set(0, 0, xSize);
        clipToScreen.set(0, 3, -0.5f);
        clipToScreen.set(1, 1, -ySize);
        clipToScreen.set(1, 3, ySize - 0.5f);
    }

    private void calculateGridToWorld() {
        // Calculate the transformation from grid coordinates to
        // world coordinates

        double scale = Math.max(gridxSize, gridySize);
        scale = 2.0f / scale;

        gridToWorld.set(0, 0, scale);
        gridToWorld.set(1, 1, scale);
        gridToWorld.set(2, 2, scale);
        gridToWorld.set(0, 3, -scale * gridxSize / 2);
        gridToWorld.set(1, 3, -scale * gridySize / 2);
        gridToWorld.set(2, 3, -scale * 25.0f);
        // This last should change when the ability to set viewpoint
        // height is added.
    }

    private void calculateCameraToClip() {
        // Calculate the transformation from camera coordinates to
        // clipping coordinates

        if (aspect > 1.0f) {
            cameraToClip.set(0, 0, 1.0f / (2.0f * aspect));
            cameraToClip.set(1, 1, 0.5f);
        } else {
            cameraToClip.set(0, 0, 0.5f);
            cameraToClip.set(1, 1, aspect / 2.0f);
        }
        cameraToClip.set(0, 3, 0.5f);
        cameraToClip.set(1, 3, 0.5f);
    }

    private void calculateWorldToCamera() {
        double cosHeading, sinHeading, cosAltitude, sinAltitude;

        // This is a composite transformation consisting of
        // 1) a rotation in the xy plane (heading  0-2PI radians)
        // 2) a rotation in the yz plane (altitude 0-PI/2 radians)
        // 3) a rotation in yz by -90 degrees to put into camera coordinates

        cosHeading = Math.cos(heading);
        sinHeading = Math.sin(heading);
        cosAltitude = Math.cos(altitude);
        sinAltitude = Math.sin(altitude);

        worldToCamera.set(0, 0, cosHeading);
        worldToCamera.set(0, 1, -sinHeading);
        worldToCamera.set(1, 0, sinAltitude * sinHeading);
        worldToCamera.set(1, 1, sinAltitude * cosHeading);
        worldToCamera.set(1, 2, cosAltitude);
        worldToCamera.set(2, 0, -cosAltitude * sinHeading);
        worldToCamera.set(2, 1, -cosAltitude * cosHeading);
        worldToCamera.set(2, 2, sinAltitude);
    }

}

/**
 * *****************************************************************************************
 * CLASS:	      Xform
 * <p/>
 * FUNCTION:     Xform takes care of the transformations of the image.
 * The class contains:
 * -Utility functions:
 * public static void mult(Xform result, Xform op1, Xform op2)
 * public static double multRow(int row, Xform xform, double x, double y, double z)
 * public static void rotateXY(Xform xform, double angle)
 * public static void rotateYZ(Xform xform, double angle)
 * public static void rotateZX(Xform xform, double angle)
 * public double get(int row, int col)
 * public void set(int row, int col, double value)
 * <p/>
 * DATE CREATED: August 2002
 * *********************************************************************************************
 */
class Xform {
    // Matrix is assumed to be stored in row major order
    // 1-D array used for minor speedups

    private final double[] matrix = {1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f};


    public static void mult(Xform result, Xform op1, Xform op2) {
        // 4 by 4 matrix multiplication  - op1 * op2 goes into result

        // First row in result

        result.matrix[0] =
                op1.matrix[0] * op2.matrix[0] +
                        op1.matrix[1] * op2.matrix[4] +
                        op1.matrix[2] * op2.matrix[(2 * 4)] +
                        op1.matrix[3] * op2.matrix[(3 * 4)];

        result.matrix[1] =
                op1.matrix[0] * op2.matrix[1] +
                        op1.matrix[1] * op2.matrix[4 + 1] +
                        op1.matrix[2] * op2.matrix[2 * 4 + 1] +
                        op1.matrix[3] * op2.matrix[3 * 4 + 1];

        result.matrix[2] =
                op1.matrix[0] * op2.matrix[2] +
                        op1.matrix[1] * op2.matrix[4 + 2] +
                        op1.matrix[2] * op2.matrix[2 * 4 + 2] +
                        op1.matrix[3] * op2.matrix[3 * 4 + 2];

        result.matrix[3] =
                op1.matrix[0] * op2.matrix[3] +
                        op1.matrix[1] * op2.matrix[4 + 3] +
                        op1.matrix[2] * op2.matrix[2 * 4 + 3] +
                        op1.matrix[3] * op2.matrix[3 * 4 + 3];


        result.matrix[4] =
                op1.matrix[4] * op2.matrix[0] +
                        op1.matrix[4 + 1] * op2.matrix[4] +
                        op1.matrix[4 + 2] * op2.matrix[(2 * 4)] +
                        op1.matrix[4 + 3] * op2.matrix[(3 * 4)];

        result.matrix[4 + 1] =
                op1.matrix[4] * op2.matrix[1] +
                        op1.matrix[4 + 1] * op2.matrix[4 + 1] +
                        op1.matrix[4 + 2] * op2.matrix[2 * 4 + 1] +
                        op1.matrix[4 + 3] * op2.matrix[3 * 4 + 1];

        result.matrix[4 + 2] =
                op1.matrix[4] * op2.matrix[2] +
                        op1.matrix[4 + 1] * op2.matrix[4 + 2] +
                        op1.matrix[4 + 2] * op2.matrix[2 * 4 + 2] +
                        op1.matrix[4 + 3] * op2.matrix[3 * 4 + 2];

        result.matrix[4 + 3] =
                op1.matrix[4] * op2.matrix[3] +
                        op1.matrix[4 + 1] * op2.matrix[4 + 3] +
                        op1.matrix[4 + 2] * op2.matrix[2 * 4 + 3] +
                        op1.matrix[4 + 3] * op2.matrix[3 * 4 + 3];


        result.matrix[(2 * 4)] =
                op1.matrix[(2 * 4)] * op2.matrix[0] +
                        op1.matrix[2 * 4 + 1] * op2.matrix[4] +
                        op1.matrix[2 * 4 + 2] * op2.matrix[(2 * 4)] +
                        op1.matrix[2 * 4 + 3] * op2.matrix[(3 * 4)];

        result.matrix[2 * 4 + 1] =
                op1.matrix[(2 * 4)] * op2.matrix[1] +
                        op1.matrix[2 * 4 + 1] * op2.matrix[4 + 1] +
                        op1.matrix[2 * 4 + 2] * op2.matrix[2 * 4 + 1] +
                        op1.matrix[2 * 4 + 3] * op2.matrix[3 * 4 + 1];

        result.matrix[2 * 4 + 2] =
                op1.matrix[(2 * 4)] * op2.matrix[2] +
                        op1.matrix[2 * 4 + 1] * op2.matrix[4 + 2] +
                        op1.matrix[2 * 4 + 2] * op2.matrix[2 * 4 + 2] +
                        op1.matrix[2 * 4 + 3] * op2.matrix[3 * 4 + 2];

        result.matrix[2 * 4 + 3] =
                op1.matrix[(2 * 4)] * op2.matrix[3] +
                        op1.matrix[2 * 4 + 1] * op2.matrix[4 + 3] +
                        op1.matrix[2 * 4 + 2] * op2.matrix[2 * 4 + 3] +
                        op1.matrix[2 * 4 + 3] * op2.matrix[3 * 4 + 3];


        result.matrix[(3 * 4)] =
                op1.matrix[(3 * 4)] * op2.matrix[0] +
                        op1.matrix[3 * 4 + 1] * op2.matrix[4] +
                        op1.matrix[3 * 4 + 2] * op2.matrix[(2 * 4)] +
                        op1.matrix[3 * 4 + 3] * op2.matrix[(3 * 4)];

        result.matrix[3 * 4 + 1] =
                op1.matrix[(3 * 4)] * op2.matrix[1] +
                        op1.matrix[3 * 4 + 1] * op2.matrix[4 + 1] +
                        op1.matrix[3 * 4 + 2] * op2.matrix[2 * 4 + 1] +
                        op1.matrix[3 * 4 + 3] * op2.matrix[3 * 4 + 1];

        result.matrix[3 * 4 + 2] =
                op1.matrix[(3 * 4)] * op2.matrix[2] +
                        op1.matrix[3 * 4 + 1] * op2.matrix[4 + 2] +
                        op1.matrix[3 * 4 + 2] * op2.matrix[2 * 4 + 2] +
                        op1.matrix[3 * 4 + 3] * op2.matrix[3 * 4 + 2];

        result.matrix[3 * 4 + 3] =
                op1.matrix[(3 * 4)] * op2.matrix[3] +
                        op1.matrix[3 * 4 + 1] * op2.matrix[4 + 3] +
                        op1.matrix[3 * 4 + 2] * op2.matrix[2 * 4 + 3] +
                        op1.matrix[3 * 4 + 3] * op2.matrix[3 * 4 + 3];

    }

    public static double multRow(int row, Xform xform, double x, double y, double z) {
        return
                xform.matrix[(row * 4)] * x +
                        xform.matrix[row * 4 + 1] * y +
                        xform.matrix[row * 4 + 2] * z +
                        xform.matrix[row * 4 + 3];
    }

    public double get(int col) {
        // Return the matrix entry at the row and column

        return matrix[col];
    }

    public void set(int row, int col, double value) {
        // Set the matrix entry at the row and column

        matrix[row * 4 + col] = value;
    }
}

/**
 * *****************************************************************************************
 * CLASS:	      Edge
 * <p/>
 * FUNCTION:     Edge takes care of the topography edges.
 * The class contains:
 * -Constructor:
 * Edge()
 * DATE CREATED: August 2002
 * *********************************************************************************************
 */
class Edge {
    int yUpper; // Final scan line of edge
    int y;

    double x;
    double dx;
    Edge next;


    Edge() {
        x = 0.0f;
        y = 0;
        double b;
        double g;
        double r = g = b = 255.0f;

        double db;
        double dg;
        double dr;
        dx = dr = dg = db = 0.0f;

        next = null;
    }

    /*  // For debugging
    void print(TextArea msg)
    {
	msg.append("x: " + x + " y: " + y + "\n");
	msg.append("dx: " + dx + "\n");
    }
    */
}
