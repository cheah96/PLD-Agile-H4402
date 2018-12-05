package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author nmesnard, tzhang
 */
public class Window
{
    private final MainController controller;
    private Map map = null;
    
    private final JFrame frame;
    
    private final JButton btnOpenMap;
    private final JButton btnOpenLoc;
    private final JButton btnUndo;
    private final JButton btnRedo;
    private final JCheckBox cckLegend;
    private final JCheckBox cckDirection;
    
    private final JSpinner numDeliveries;
    private final JButton btnGenerate;
    
    private final JButton btnListAdd;
    private final JButton btnListMove;
    private final JButton btnListRemove;
    
    List<MapView> mapViews = new ArrayList<>();
    
    public Window(Map map, final MainController controller) {
        this.map = map;
        this.controller = controller;
        
        // CREATING COMPONENTS
        
        // Window
        frame = new JFrame();
        frame.setTitle("PLD Livraison à Domicile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(200,200); //initialize status bar
        
        // Bottom status bar
        JPanel panStatus = new JPanel();
        panStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panStatus.setLayout(new BoxLayout(panStatus, BoxLayout.X_AXIS));
        JLabel lblStatus = new JLabel("Barre d'état");
        
        // Top tool-bar
        JToolBar tlbTop = new JToolBar();
        tlbTop.setFloatable(false);
        // and its buttons
        btnOpenMap = new JButton(new ImageIcon("res/icons/map.png"));
        btnOpenMap.setToolTipText("Ouvrir une carte");
        btnOpenLoc = new JButton(new ImageIcon("res/icons/pin.png"));
        btnOpenLoc.setToolTipText("Ouvrir des points de livraison");
        btnUndo = new JButton(new ImageIcon("res/icons/undo.png"));
        btnUndo.setToolTipText("Annuler");
        btnRedo = new JButton(new ImageIcon("res/icons/redo.png"));
        btnRedo.setToolTipText("Refaire");
        cckLegend = new JCheckBox("Légende");
        cckDirection = new JCheckBox("Sens de parcours");
        
        // Centered map
        MapViewGraphical mapViewGraphical = new MapViewGraphical(map, controller);
        mapViewGraphical.setPreferredSize(new Dimension(480, 480));
        mapViews.add(mapViewGraphical);
        map.addPropertyChangeListener(mapViewGraphical);
        
        // Left panel
        JPanel panTools = new JPanel();
        
        // > Top settings
        JPanel panDeliveries = new JPanel();
        SpinnerModel model = new SpinnerNumberModel(3, 1, 12, 1);
        numDeliveries = new JSpinner(model);
        // ((JSpinner.DefaultEditor) numDeliveries.getEditor()).getTextField().setEditable(false);
        JLabel lblDeliveries = new JLabel("livreurs");
        btnGenerate = new JButton("Générer");
        
        // > Main lists
        JPanel panLists = new JPanel();
        MapViewTextual mapViewTextual = new MapViewTextual(map, controller);
        mapViews.add(mapViewTextual);
        map.addPropertyChangeListener(mapViewTextual);
        // > and their buttons
        btnListAdd = new JButton(new ImageIcon("res/icons/add.png"));
        btnListMove = new JButton(new ImageIcon("res/icons/move.png"));
        btnListRemove = new JButton(new ImageIcon("res/icons/delete.png"));
        
        
        // CREATING DISPLAY
        
        EmptyBorder spacer = new EmptyBorder(4, 4, 4, 4);
        EmptyBorder semispacer = new EmptyBorder(2,2,2,2);
        
        // Bottom status bar
        lblStatus.setBorder(semispacer);
        panStatus.add(lblStatus);
        
        // Top tool-bar
        tlbTop.add(btnOpenMap);
        tlbTop.add(btnOpenLoc);
        // tlbTop.add(btnUndo);
        // tlbTop.add(btnRedo);
        JPanel panSeparator = new JPanel();
        panSeparator.setOpaque(false);
        tlbTop.add(panSeparator);
        JPanel panDisplay = new JPanel();
        panDisplay.setLayout(new BoxLayout(panDisplay, BoxLayout.Y_AXIS));
        panDisplay.setOpaque(false);
        cckLegend.setOpaque(false);
        cckDirection.setOpaque(false);
        panDisplay.add(cckLegend);
        panDisplay.add(cckDirection);
        tlbTop.add(panDisplay);
        
        // - Top settings
        panDeliveries.setBorder(spacer);
        panDeliveries.add(numDeliveries);
        panDeliveries.add(lblDeliveries);
        panDeliveries.add(btnGenerate);
        
        // - Main lists
        panLists.setBorder(spacer);
        panLists.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        panLists.add(mapViewTextual, c);
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        panLists.add(btnListAdd, c);
        c.gridx = 1;
        panLists.add(btnListMove, c);
        c.gridx = 2;
        panLists.add(btnListRemove, c);
        
        // Left panel
        panTools.setBorder(spacer);
        panTools.setLayout(new BorderLayout());
        panTools.add(panDeliveries, BorderLayout.NORTH);
        panTools.add(panLists, BorderLayout.CENTER);
        
        // Window
        JPanel panMain = new JPanel (new BorderLayout());
        panMain.add(panStatus, BorderLayout.SOUTH);
        panMain.add(mapViewGraphical,BorderLayout.CENTER);
        JSplitPane panSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panTools, panMain);
        frame.add(tlbTop, BorderLayout.NORTH);
        frame.add(panSplit, BorderLayout.CENTER);
        
        
        // EVENTS HANDLING
        
        // File opening
        
        btnOpenMap.addActionListener(e -> {
            try {
                controller.loadMapFile();
                stateRefresh();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        btnOpenLoc.addActionListener(e -> {
            try {
                controller.loadDeliveriesFile();
                stateRefresh();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        btnGenerate.addActionListener(e -> {
            int nbDeliveryMen = (int) numDeliveries.getValue();
            controller.generateDeliveryMen(nbDeliveryMen);
            stateRefresh();
        });
        
        cckDirection.addItemListener(e -> {
            boolean checked = cckDirection.getModel().isSelected();
            mapViewGraphical.showDirection(checked);
        });

        cckLegend.addItemListener(e -> {
            boolean checked = cckLegend.getModel().isSelected();
            mapViewGraphical.showLegend(checked);
        });
        
        
        // INITIAL STATE
        
        stateRefresh();
        
        
        // READY
        
        frame.pack();
        frame.setVisible(true);
    }
    
    protected final void stateRefresh()
    {
        Boolean hasMap = (map != null && !map.getNodes().isEmpty());
        Boolean hasLoc = (hasMap && !map.getDeliveries().isEmpty());
        
        btnOpenMap.setEnabled(true);
        btnOpenLoc.setEnabled(hasMap);
        
        btnUndo.setEnabled(false);
        btnRedo.setEnabled(false);
        
        numDeliveries.setEnabled(true);
        btnGenerate.setEnabled(hasLoc);
        
        btnListAdd.setEnabled(hasLoc);
        btnListMove.setEnabled(hasLoc);
        btnListRemove.setEnabled(hasLoc);
    }
    
    public File askFile(String title){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("res/xml"));
        fileChooser.setDialogTitle(title);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile;
        }
        return null;
    }
    
    public void selectNode(Node node) {
        for (MapView view : mapViews) {
            view.selectNode(node);
        }
    }
    
    public void selectDeliveryMan(int deliveryManIndex) {
        for (MapView view : mapViews) {
            view.selectDeliveryMan(deliveryManIndex);
        }
    }
   
    
}
