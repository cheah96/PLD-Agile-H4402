package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.xml.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;

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
    private final JButton btnStatus;
    
    List<MapView> mapViews = new ArrayList<>();
    
    private final JScrollPane scrollPanRoadmap;
    private final RoadmapPanel panRoadmap;
    
    
    
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
        panStatus.setPreferredSize(new Dimension(32,32));
        panStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panStatus.setLayout(new BoxLayout(panStatus, BoxLayout.X_AXIS));
        lblStatus = new JLabel("Barre d'état");
        btnStatus = new JButton("");
        
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
        SpinnerModel model = new SpinnerNumberModel(3, 1, 30, 1);
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
        panStatus.add(Box.createHorizontalGlue());
        panStatus.add(btnStatus);
        
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
        
        // Roadmap (right panel)
        panRoadmap = new RoadmapPanel(this.map, this.controller);
        mapViews.add(panRoadmap);
        scrollPanRoadmap = new JScrollPane(panRoadmap);
        
        // Window
        JPanel panMain = new JPanel(new BorderLayout());
        panMain.add(panStatus, BorderLayout.SOUTH);
        panMain.add(mapViewGraphical,BorderLayout.CENTER);

        JSplitPane rightPanSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panMain, scrollPanRoadmap);
        JSplitPane leftPanSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panTools, rightPanSplit);
        
        scrollPanRoadmap.setPreferredSize(panStatus.getPreferredSize());

        frame.add(tlbTop, BorderLayout.NORTH);
        frame.add(leftPanSplit, BorderLayout.CENTER);
        
        
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
                popupError("Élément inattendu trouvé : " + ex.getElementName());
            } catch (UnreachableDeliveryException ex) {
                popupError("Point de livraison " + ex.getDelivery().getNode().getId() + " inaccessible en aller-retour depuis l'entrepôt");
            } catch (Exception ex) {
                popupError("Fichier non conforme.");
            }
        });
        
        btnGenerate.addActionListener(e -> {
            int nbDeliveryMen = (int) numDeliveries.getValue();
            controller.generateDeliveryMen(nbDeliveryMen);
        });
        
        btnStatus.addActionListener(e -> {
            controller.btnStatusClick();
        });
        
        cckDirection.addItemListener(e -> {
            boolean checked = cckDirection.getModel().isSelected();
            mapViewGraphical.showDirection(checked);
        });
        
        cckLegend.addItemListener(e -> {
            boolean checked = cckLegend.getModel().isSelected();
            mapViewGraphical.showLegend(checked);
        });
        
        bindKeyAction(frame, "ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.keyEscape();
            }
        });
        
        
        // READY
        
        frame.pack();
        rightPanSplit.setResizeWeight(0.9);
        rightPanSplit.setDividerLocation(0.7);
        frame.setVisible(true);
    }
    
    private static void bindKeyAction(JFrame frm, String key, AbstractAction action) {
        for (Component cmp : frm.getComponents()) {
            if (cmp instanceof JComponent) {
                bindKeyAction((JComponent) cmp, key, action);
                break;
            }
        }
    }
    private static void bindKeyAction(JComponent cmp, String key, AbstractAction action) {
        cmp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), "stroke" + key);
        cmp.getActionMap().put("stroke" + key, action);
    }
    
    public void setButtonsState(
        boolean canOpenMap, boolean canOpenLoc,
        boolean canGenerateDeliveryMen,
        boolean canEditDeliveries) {
        
        btnOpenMap.setEnabled(canOpenMap);
        btnOpenLoc.setEnabled(canOpenLoc);
        
        numDeliveries.setEnabled(canGenerateDeliveryMen);
        btnGenerate.setEnabled(canGenerateDeliveryMen);
        btnDeliveryRecords.setEnabled(canGenerateDeliveryMen);
        
        btnListAdd.setEnabled(canEditDeliveries);
        btnListMove.setEnabled(canEditDeliveries);
        btnListRemove.setEnabled(canEditDeliveries);
    }
    
    public void setUndoEnabled(boolean enabled) {
        btnUndo.setEnabled(enabled);
    }
    
    public void setRedoEnabled(boolean enabled) {
        btnRedo.setEnabled(enabled);
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
    
    public void clearStatus() {
        lblStatus.setText("");
        btnStatus.setVisible(false);
    }
    
    public void setStatusMessage(String message) {
        lblStatus.setText(message);
    }
    
    public void setStatusButton(String caption) {
        btnStatus.setText(caption);
        btnStatus.setVisible(true);
    }
    
}
