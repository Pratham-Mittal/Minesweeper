package gui;

import javax.swing.*;
import java.awt.*;

public class Menu {

    public Menu() {                              //menu
        JFrame gameframe = new JFrame("Menu");
        gameframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameframe.setLayout(new BorderLayout());
        gameframe.setSize(250, 400);
        gameframe.setResizable(false);
        gameframe.add(selectionPanel(gameframe), BorderLayout.CENTER);
        gameframe.pack();
        gameframe.setVisible(true);
    }

    private JPanel selectionPanel(JFrame frame) {             //difficulty  selection panel
        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new GridLayout(5, 1));

        createMenu(frame, selectPanel, "Easy", 10, 10, 10);
        createMenu(frame, selectPanel, "Medium", 20, 20, 40);
        createMenu(frame, selectPanel, "Hard", 20, 30, 99);

        JButton custom = new JButton();
        custom.setText("Custom Board");
        custom.addActionListener(actionEvent -> {
            frame.remove(selectPanel);
            frame.add(customPanel(frame));
            frame.setVisible(true);
        });
        custom.setFocusable(false);
        custom.setPreferredSize(new Dimension(250, 55));
        selectPanel.add(custom);

        JButton quit = new JButton();
        quit.setText("Quit");
        quit.addActionListener(actionEvent -> System.exit(0));
        quit.setFocusable(false);
        quit.setPreferredSize(new Dimension(250, 55));
        selectPanel.add(quit);

        return selectPanel;
    }

    private JPanel customPanel(JFrame frame) {                  //panel for creating custom panel
        JPanel customPane = new JPanel();
        customPane.setLayout(new GridLayout(5, 1));

        JSpinner rowS = new JSpinner();
        JSpinner colS = new JSpinner();
        JSpinner virusS = new JSpinner();
        SpinnerModel rowModel = new SpinnerNumberModel(10, 4, 16, 1);
        SpinnerModel colModel = new SpinnerNumberModel(10, 8, 36, 1);
        SpinnerModel virusModel = new SpinnerNumberModel(15, 1, 70, 1);
        rowS.setModel(rowModel);
        colS.setModel(colModel);
        virusS.setModel(virusModel);
        customPane.add(rowS);
        customPane.add(colS);
        customPane.add(virusS);

        JButton startButton = new JButton();
        startButton.setText("Start");
        startButton.addActionListener(actionEvent -> {
            frame.dispose();
            int rows = (int) rowS.getValue();
            int columns = (int) colS.getValue();
            int virus = (int) (((int) virusS.getValue()) / 100.0 * (rows * columns));
            new GameBoard(rows, columns, virus);
        });
        startButton.setFocusable(false);
        customPane.add(startButton);

        JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(actionEvent -> {
            frame.remove(customPane);
            frame.add(selectionPanel(frame));
            frame.setVisible(true);
        });
        back.setFocusable(false);
        customPane.add(back);

        return customPane;
    }

    private void createMenu(JFrame frame, JPanel selectionPanel, String difficulty, int rows, int cols, int virus) {    //menu format
        JButton menuButton = new JButton();
        menuButton.setText(difficulty + " - " + cols + " x " + rows + " - " + virus + " virus");
        menuButton.addActionListener(actionEvent -> {
            frame.dispose();
            new GameBoard(rows, cols, virus);
        });
        menuButton.setFocusable(false);
        menuButton.setPreferredSize(new Dimension(250, 55));
        selectionPanel.add(menuButton);
    }

}
