package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.xml.*;

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
    private final JButton btnDeliveryRecords;
    private final JLabel lblStatus;
    
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
        
        // Bottom status bar
        JPanel panStatus = new JPanel();
        panStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panStatus.setLayout(new BoxLayout(panStatus, BoxLayout.X_AXIS));
        lblStatus = new JLabel("Barre d'état");
        
        // Top tool-bar
        JToolBar tlbTop = new JToolBar();
        tlbTop.setFloatable(false);
        // and its buttons
        btnOpenMap = new JButton(new ImageIcon(getClass().getResource("/icons/map.png")));
        btnOpenMap.setToolTipText("Ouvrir une carte");
        btnOpenLoc = new JButton(new ImageIcon(getClass().getResource("/icons/pin.png")));
        btnOpenLoc.setToolTipText("Ouvrir des points de livraison");
        btnDeliveryRecords = new JButton(new ImageIcon(getClass().getResource("/icons/list.png")));
        btnDeliveryRecords.setToolTipText("Générer la liste de livraisons");
        btnUndo = new JButton(new ImageIcon(getClass().getResource("/icons/undo.png")));
        btnUndo.setToolTipText("Annuler");
        btnRedo = new JButton(new ImageIcon(getClass().getResource("/icons/redo.png")));
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
        btnListAdd = new JButton(new ImageIcon(getClass().getResource("/icons/add.png")));
        btnListAdd.setToolTipText("Ajouter une livraison");
        btnListMove = new JButton(new ImageIcon(getClass().getResource("/icons/move.png")));
        btnListMove.setToolTipText("Déplacer une livraison");
        btnListRemove = new JButton(new ImageIcon(getClass().getResource("/icons/delete.png")));
        btnListRemove.setToolTipText("Supprimer une livraison");
        
        
        // CREATING DISPLAY
        
        EmptyBorder spacer = new EmptyBorder(4, 4, 4, 4);
        EmptyBorder semispacer = new EmptyBorder(2,2,2,2);
        
        // Bottom status bar
        lblStatus.setBorder(semispacer);
        panStatus.add(lblStatus);
        
        // Top tool-bar
        tlbTop.add(btnOpenMap);
        tlbTop.add(btnOpenLoc);
        tlbTop.add(btnDeliveryRecords);
        tlbTop.add(btnUndo);
        tlbTop.add(btnRedo);
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
        
        btnUndo.addActionListener(e -> {
            controller.undo();
        });
        
        btnRedo.addActionListener(e -> {
            controller.redo();
        });
        
        btnOpenMap.addActionListener(e -> {
            try {
                controller.loadMap();
            } catch (XMLAttributeFormatException ex) {
                popupError("Attribut " + ex.getAttributeName() + " de valeur non conforme (" + ex.getAttributeValue() + ")");
            } catch (XMLMissingAttributeException ex) {
                popupError("Attribut manquant : " + ex.getMissingAttributeName());
            } catch (XMLDuplicateNodeException ex) {
                popupError("Définitions multiples d'un même élément : " + ex.getNodeId());
            } catch (XMLUndefinedNodeReferenceException ex) {
                popupError("Référence à un élément non défini : " + ex.getNodeId());
            } catch (XMLUnexpectedElementException ex) {
                popupError("Elément inattendu trouvé : " + ex.getElementName());
            } catch (Exception ex) {
                popupError("Fichier non conforme.");
            }
        });
        
        btnOpenLoc.addActionListener(e -> {
            try {
                controller.loadDeliveriesFile();
            } catch (XMLAttributeFormatException ex) {
                popupError("Attribut " + ex.getAttributeName() + " de valeur non conforme (" + ex.getAttributeValue() + ")");
            } catch (XMLMissingAttributeException ex) {
                popupError("Attribut manquant : " + ex.getMissingAttributeName());
            } catch (XMLMultipleDefinitionOfWarehouseException ex) {
                popupError("Définitions multiples de l'entrepôt");
            } catch (XMLUndefinedNodeReferenceException ex) {
                popupError("Référence à un élément non défini : " + ex.getNodeId());
            } catch (XMLUnexpectedElementException ex) {
                popupError("Elément inattendu trouvé : " + ex.getElementName());
            } catch (Exception ex) {
                popupError("Fichier non conforme.");
            }
        });
        
        btnDeliveryRecords.addActionListener(e -> {
            try {
                JFrame listDeliveries = new JFrame("Plan de route");
                listDeliveries.setVisible(true);
                
                DefaultListModel<String> model2 = new DefaultListModel<>(); 
                JList<String> list2 = new JList<>(model2);
                
                Section lastsection = null;
                for (Route route : map.getDeliveryMen().get(0).getRound().getItinerary()) {
                    for (Passage location : route.getPassages()) {
                        Section currentsection = location.getSection();
                        if (lastsection!=null && !currentsection.getName().equals(lastsection.getName())) 
                            ((DefaultListModel<String>)list2.getModel()).addElement(currentsection.getName());
                        lastsection = currentsection;
                    }
                }
                
                listDeliveries.add(list2);
                listDeliveries.pack();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        btnGenerate.addActionListener(e -> {
            int nbDeliveryMen = (int) numDeliveries.getValue();
            if (!map.isShorteningDeliveries()) {
                controller.generateDeliveryMen(nbDeliveryMen);
            } else {
                controller.stopGeneration();
            }
        });
        
        cckDirection.addItemListener(e -> {
            boolean checked = cckDirection.getModel().isSelected();
            mapViewGraphical.showDirection(checked);
        });
        
        cckLegend.addItemListener(e -> {
            boolean checked = cckLegend.getModel().isSelected();
            mapViewGraphical.showLegend(checked);
        });
        
        
        // READY
        
        frame.pack();
        frame.setVisible(true);
    }
    
    public final void setButtonsState(
        boolean canOpenMap, boolean canOpenLoc,
        boolean canGenerateDeliveryMen,
        boolean canEditDeliveries,
        boolean canUndo, boolean canRedo) {
        
        btnOpenMap.setEnabled(canOpenMap);
        btnOpenLoc.setEnabled(canOpenLoc);
        
        btnUndo.setEnabled(canUndo);
        btnRedo.setEnabled(canRedo);
        
        numDeliveries.setEnabled(canGenerateDeliveryMen);
        btnGenerate.setEnabled(canGenerateDeliveryMen);
        btnDeliveryRecords.setEnabled(canGenerateDeliveryMen);
        
        btnListAdd.setEnabled(canEditDeliveries);
        btnListMove.setEnabled(canEditDeliveries);
        btnListRemove.setEnabled(canEditDeliveries);
    }
    
    public File promptFile(String title){
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
    
    public void popupError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Erreur", JOptionPane.ERROR_MESSAGE);
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
    
    public void setStatusMessage(String message) {
        lblStatus.setText(message);
    }
    
}
