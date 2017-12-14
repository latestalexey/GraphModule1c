/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 *   $Id: WallZGRViewer.java 5328 2015-02-06 11:54:34Z epietrig $
 */

package net.claribole.zgrviewer;

import javax.swing.JMenuBar;

import java.util.Vector;

import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.cluster.ClusterGeometry;
import fr.inria.zvtm.cluster.ClusteredView;

import fr.lri.smarties.libserver.Smarties;
import fr.lri.smarties.libserver.SmartiesColors;
import fr.lri.smarties.libserver.SmartiesEvent;
import fr.lri.smarties.libserver.SmartiesPuck;
import fr.lri.smarties.libserver.SmartiesDevice;
import fr.lri.smarties.libserver.SmartiesWidget;
import fr.lri.smarties.libserver.SmartiesWidgetHandler;
import java.util.Observer;
import java.util.Observable;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class WallZGRViewer extends ZGRViewer {

    ClusterGeometry cg;

    SmartiesManager stm;

    public WallZGRViewer(ZGROptions options){
        super(options);
    }

    @Override
    void initGUI(ZGROptions options, boolean viewOnJPanel){
        VirtualSpaceManager.INSTANCE.setMaster("zgrv");
        exitVMonClose = !viewOnJPanel;
        cfgMngr.notifyPlugins(Plugin.NOTIFY_PLUGIN_GUI_INITIALIZING);
        Utils.initLookAndFeel();
        JMenuBar jmb = initViewMenu();
        grMngr.createFrameView(grMngr.createZVTMelements(false), options.opengl ? View.OPENGL_VIEW : View.STD_VIEW, jmb);
        cfgMngr.notifyPlugins(Plugin.NOTIFY_PLUGIN_GUI_VIEW_CREATED);
        grMngr.parameterizeView(new ZgrvEvtHdlr(this, this.grMngr));
        cfgMngr.notifyPlugins(Plugin.NOTIFY_PLUGIN_GUI_INITIALIZED);
        // wall
        cg = new ClusterGeometry(options.blockWidth, options.blockHeight, options.numCols, options.numRows);
        Vector ccameras = new Vector(1);
        ccameras.add(grMngr.mainCamera);
        ClusteredView cv = new ClusteredView(cg, options.numRows-1, options.numCols, options.numRows, ccameras);
        VirtualSpaceManager.INSTANCE.addClusteredView(cv);
        cv.setBackgroundColor(cfgMngr.backgroundColor);
        stm = new SmartiesManager(this);
    }

    // for Smarties config
    int getDisplayWidth(){
        return cg.getWidth();
    }

    int getDisplayHeight(){
        return cg.getHeight();
    }

    int getColumnCount(){
        return cg.getColumns();
    }

    int getRowCount(){
        return cg.getRows();
    }

    public static void main(String[] args){
        ZGROptions options = new ZGROptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch(CmdLineException ex){
            System.err.println(ex.getMessage());
            parser.printUsage(System.err);
            return;
        }
        if (!options.fullscreen && Utils.osIsMacOS()){
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        System.out.println("--help for command line options");
        new WallZGRViewer(options);
    }

}

class SmartiesManager implements Observer {

    WallZGRViewer application;

    Smarties smarties;

    SmartiesPuck pinchPuck;
    SmartiesDevice pinchDevice;
    SmartiesPuck dragPuck;
    SmartiesDevice dragDevice;

    float prevMFPinchD = 0;
    float prevMFMoveX = 0;
    float prevMFMoveY = 0;

    SmartiesManager(WallZGRViewer app){
        this.application = app;

        smarties = new Smarties(application.getDisplayWidth(), application.getDisplayHeight(),
                                application.getColumnCount(), application.getRowCount());

        smarties.initWidgets(3,2);

        // SmartiesWidget sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON,
        //                                        "<", 1,1,1,1);
        // sw.handler = new PrevTimeStepEvent();

        // sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON,
        //                                        ">", 2,1,1,1);
        // sw.handler = new NextTimeStepEvent();

        // sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_BUTTON,
        //                                        "Home", 3,1,1,1);
        // sw.handler = new HomeEvent();

        // sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_TOGGLE_BUTTON,
        //                                        "SHP", 1,2,1,1);
        // sw.on = true;
        // sw.handler = new ToggleShpEvent();

        // sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_TOGGLE_BUTTON,
        //                                        "VEG", 2,2,1,1);
        // sw.on = true;
        // sw.handler = new ToggleVTEvent();

        // sw = smarties.addWidget(SmartiesWidget.SMARTIES_WIDGET_TYPE_TOGGLE_BUTTON,
        //                                        "SITES", 3,2,1,1);
        // sw.on = true;
        // sw.handler = new ToggleSitesEvent();

        smarties.addObserver(this);

        smarties.Run();
    }


    public void update(Observable obj, Object arg){
        // if (arg instanceof SmartiesEvent) {
        //     final SmartiesEvent se = (SmartiesEvent)arg;
        //     switch (se.type){
        //         case SmartiesEvent.SMARTIE_EVENTS_TYPE_WIDGET:{
        //             if (se.widget.handler != null) se.widget.handler.callback(se.widget, se, this);
        //             break;
        //         }
        //         default:{break;}
        //     }
        // } else {
        //     System.out.println("!(arg instanceof SmartiesEvent)");
        // }
    }

    // class HomeEvent implements SmartiesWidgetHandler {
    //     public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
    //         application.mCamera.setLocation(application.gm.getKlabinCenterCoords());
    //         return true;
    //     }
    // }

    // class ToggleShpEvent implements SmartiesWidgetHandler {
    //     public boolean callback(SmartiesWidget sw, SmartiesEvent se, Object user_data){
    //         application.gm.toggleContourShape();
    //         return true;
    //     }
    // }

}
