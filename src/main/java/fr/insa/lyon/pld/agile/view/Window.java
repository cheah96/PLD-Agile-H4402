package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.XMLParser;

import java.awt.*;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author nmesnard, tzhang
 */
public class Window {
    JFrame frame;
    JButton btnOpenMap;
    JButton btnOpenLoc;
    Map map;

    public Window() {
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
        JCanvas panMap = new JCanvas();
        
        // Left panel
        JPanel panTools = new JPanel();

        // > Top settings
        JPanel panDeliveries = new JPanel();
        SpinnerModel model = new SpinnerNumberModel(3, 1, 6, 1);     
        JSpinner spinner = new JSpinner(model);
        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        JLabel lblDeliveries = new JLabel("livreurs");
        JButton btnGenerate = new JButton("Générer");

        // > Main lists
        JPanel panLists = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        for (int count=0; count<3; count++) {
            String livreurName = "Livreur " + (count+1);
            JPanel panLivreur = makeListPanel(livreurName);
            tabbedPane.addTab(livreurName, null, panLivreur, livreurName);
        }
        // > and their buttons
        JButton btnListAdd = new JButton(new ImageIcon("res/icons/add.png"));
        JButton btnListMove = new JButton(new ImageIcon("res/icons/move.png"));
        JButton btnListRemove = new JButton(new ImageIcon("res/icons/delete.png"));


        // CREATING DISPLAY

        EmptyBorder spacer = new EmptyBorder(4, 4, 4, 4);

        // Top tool-bar
        tlbTop.setBorder(spacer);
        tlbTop.add(btnOpenMap);
        tlbTop.add(btnOpenLoc);

        // - Top settings
        panDeliveries.setBorder(spacer);
        panDeliveries.add(spinner);
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
        panLists.add(tabbedPane, c);
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
        JSplitPane panSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panTools, panMap);
        frame.add(tlbTop, BorderLayout.NORTH);
        frame.add(panSplit, BorderLayout.CENTER);
        // frame.add(panTools, BorderLayout.WEST);
        // frame.add(panMap, BorderLayout.CENTER);

        
        // EVENTS HANDLING
                
        // File opening
                
        btnOpenMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    try {
                        map = XMLParser.loadMap(selectedFile.toPath());
                    } catch (Exception err) {
                        // TODO Auto-generated catch block
                        err.printStackTrace();
                    }
                    panMap.setmap(map);
                } 
            }
        });
                
        btnOpenLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                } 
            }
        });

        // READY

        frame.pack();
        frame.setVisible(true);
    }

    protected static JPanel makeListPanel(String text) {
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(1, 1));

        // JLabel filler = new JLabel(text);
        // filler.setHorizontalAlignment(JLabel.CENTER);
        // pan.add(filler);

        String list[] = {"Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"};
        pan.add(new JList(list));
        return pan;
    }
}
