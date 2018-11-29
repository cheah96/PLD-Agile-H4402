package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;

import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author nmesnard, tzhang
 */
public class Window {
    private JFrame frame;
    
    private JButton btnOpenMap;
    private JButton btnOpenLoc;
    
    private JSpinner numDeliveries;
    private JButton btnGenerate;
    private JButton btnOptimize;
    
    private JButton btnListAdd;
    private JButton btnListMove;
    private JButton btnListRemove;
    
    private Map map = null;
    private final MainController controller;
    
    List<MapView> mapViews = new ArrayList<>();
    
    private class ButtonListener implements ActionListener{
        private final MainController controller;
        public ButtonListener(MainController controller) {
            this.controller = controller;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnOpenMap)
            {
                try 
                {
                    controller.loadNodesFile();
                    stateRefresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if (e.getSource() == btnOpenLoc)
            {
                try 
                {
                    controller.loadDeliveriesFile();
                    stateRefresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public Window(Map map, MainController controller) {
        this.map = map;
        this.controller = controller;
        
        // CREATING COMPONENTS
        
        // Window
        frame = new JFrame();
        frame.setTitle("PLD Livraison à Domicile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Top tool-bar
        JToolBar tlbTop = new JToolBar();
        tlbTop.setFloatable(false);
        // and its buttons
        btnOpenMap = new JButton(new ImageIcon("res/icons/map.png"));
        btnOpenLoc = new JButton(new ImageIcon("res/icons/pin.png"));
        
        // Centered map
        MapViewGraphical mapViewGraphical = new MapViewGraphical(map, controller);
        mapViews.add(mapViewGraphical);
        map.addPropertyChangeListener(mapViewGraphical);
        
        // Left panel
        JPanel panTools = new JPanel();
        
        // > Top settings
        JPanel panDeliveries = new JPanel();
        SpinnerModel model = new SpinnerNumberModel(3, 1, 12, 1);
        numDeliveries = new JSpinner(model);
        ((DefaultEditor) numDeliveries.getEditor()).getTextField().setEditable(false);
        JLabel lblDeliveries = new JLabel("livreurs");
        btnGenerate = new JButton("Générer");
        btnOptimize = new JButton("Optimiser");
        
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
        
        // Top tool-bar
        tlbTop.setBorder(spacer);
        tlbTop.add(btnOpenMap);
        tlbTop.add(btnOpenLoc);
        
        // - Top settings
        panDeliveries.setBorder(spacer);
        panDeliveries.add(numDeliveries);
        panDeliveries.add(lblDeliveries);
        panDeliveries.add(btnGenerate);
        panDeliveries.add(btnOptimize);
        
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
        JSplitPane panSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panTools, mapViewGraphical);
        frame.add(tlbTop, BorderLayout.NORTH);
        frame.add(panSplit, BorderLayout.CENTER);
        
        
        // EVENTS HANDLING
        
        // File opening
        ButtonListener btnListener = new ButtonListener(controller);
        btnOpenMap.addActionListener(e -> {
            try {
                controller.loadNodesFile();
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
                
            System.out.println("Génération avec " + nbDeliveryMen + " livreurs.");
            map.setDeliveryManCount(nbDeliveryMen);
            System.out.println("Distribution des livraisons...");
            map.distributeDeliveries();

            stateRefresh();
        });
        
        btnOptimize.addActionListener(e -> {
            System.out.println("Raccourcissement des livraisons...");
            int index = mapViewTextual.getActiveDeliveryManIndex(); // TODO : Hack
            map.shortenDeliveries();
            controller.showDeliveryManRound(index);

            stateRefresh();
        });
        
        // INITIAL STATE
        
        stateRefresh();
        
        
        // READY
        
        frame.pack();
        frame.setVisible(true);
    }

    protected void stateRefresh()
    {
        Boolean hasMap = (map != null);
        Boolean hasLoc = (!map.getDeliveries().isEmpty());
        
        btnOpenMap.setEnabled(true);
        btnOpenLoc.setEnabled(hasMap);
        
        numDeliveries.setEnabled(true);
        btnGenerate.setEnabled(hasLoc);
        btnOptimize.setEnabled(!map.getDeliveryMen().isEmpty());
        
        btnListAdd.setEnabled(hasLoc);
        btnListMove.setEnabled(hasLoc);
        btnListRemove.setEnabled(hasLoc);
    }
    
    public File askFile(String title){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        // fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile;
        }
        return null;
    }
    
    public void showDeliveryManRound(int deliveryManIndex) {
        for (MapView view : mapViews)
            view.showDeliveryManRound(deliveryManIndex);
    }
}
