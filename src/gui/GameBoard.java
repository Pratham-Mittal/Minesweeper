package gui;

import game.Field;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;



public class GameBoard {
    private Field f;
    private boolean Activegame = false;
    private boolean gamestart = false;
    private final boolean dev;
    private final int rows;
    private final int columns;
    private final int virus;
    private boolean speedSanitizeMode = false;
    private final Color pausedTile = new Color(113, 113, 114);
    private final Color SanitizedTile = Color.yellow;
    private final Color defaultButton = new Color(125, 125, 125);
    private final Color selectedButton = Color.yellow;
    private final Color digitColors[] = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.BLACK};
    private final Color uncoveredTile = new Color(20, 200, 255);
    private final Color alternateTile[] = {new Color(255, 255, 255), new Color(255, 255, 255)};
    private JButton virusfieldBut[][];
    private Timer timer;
    private JLabel sanitizer;
    private JLabel time;
    private final Image sanitizeImg;
    private final ImageIcon sanitizeicnImg;
    private final Image virusImg;
    private final ImageIcon virusicnImg;
    private JButton pauseBut;
    private JButton restartBut;


    GameBoard(int rows, int cols, int virus) {
        readConfFile();
        dev = false;
        
        this.rows = rows;
        this.columns = cols;
        this.virus = virus;
        f = new Field(this, rows, cols, virus);
        
        sanitizeImg = Toolkit.getDefaultToolkit().createImage("resources/images/sanitizer.png");
        sanitizeicnImg = new ImageIcon(sanitizeImg.getScaledInstance(22,22, Image.SCALE_SMOOTH));
        virusImg = Toolkit.getDefaultToolkit().createImage("resources/images/corona.png");
        virusicnImg = new ImageIcon(virusImg.getScaledInstance(22,22, Image.SCALE_SMOOTH));
        
        final JFrame frame = new JFrame("Covidsweeper - " + cols + "x" + rows + " : " + virus);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(cols * 47, rows * 47 + 100);
        frame.setResizable(false);
        frame.add(fieldPanel(), BorderLayout.CENTER);
        frame.add(systemPanel(frame), BorderLayout.SOUTH);
        frame.add(timePan(), BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void readConfFile() {
        File configFile = new File("config/config_file.txt");
        if (configFile.exists()) {
            try {
                Scanner scanner = new Scanner(configFile);
                String modeConfig = scanner.next();
                switch (modeConfig) {
                    case "Normal":
                        speedSanitizeMode = false;
                        break;
                    case "SpeedSanitize":
                        speedSanitizeMode = true;
                        break;
                    default:
                        writeFile("Normal");
                        break;
                }
                scanner.close();
            } catch (Exception ignored) {
                System.exit(1);
            }
        } else {
            writeFile("Normal");
        }
    }

    private void writeFile(String mode) {
        try {
            PrintWriter writer = new PrintWriter("config/config_file.txt");
            writer.println(mode);
            writer.close();
        } catch (Exception ignored) {
            System.exit(1);
        }
    }

    private JPanel fieldPanel() {
        JPanel virusfieldPanel = new JPanel();
        virusfieldPanel.setLayout(new GridLayout(rows, columns));
        virusfieldBut = new JButton[rows][columns];
        createfieldBut(virusfieldPanel);
        return virusfieldPanel;
    }

    private void createfieldBut(JPanel virusfieldPanel) {              //creates virus field
        int colorCount;
        for (int i = 0; i < rows; i++) {
            colorCount = i % 2;
            for (int j = 0; j < columns; j++) {
                virusfieldBut[i][j] = new JButton();
                virusfieldBut[i][j].setFocusable(false);
                virusfieldBut[i][j].setPreferredSize(new Dimension(35,35));
                virusfieldBut[i][j].setMargin(new Insets(0,0,0,0));
                virusfieldBut[i][j].setFont(new Font("Ariel", Font.BOLD, 10));
                virusfieldPanel.add(virusfieldBut[i][j]);
                virusfieldBut[i][j].setBackground(alternateTile[colorCount % 2]);
                addfieldButAL(i, j);
                colorCount++;
            }
        }
    }

    private void addfieldButAL(int row, int col) {                        // mouse listener for field
        virusfieldBut[row][col].addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (Activegame) {
                    if (speedSanitizeMode) {
                        speedSanitizeButAct(mouseEvent);
                    } else {
                        normalButAct(mouseEvent);
                    }
                } else if (!gamestart && mouseEvent.getButton() == 1) {
                    startGame();
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
            private void speedSanitizeButAct(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 1) {
                    speedMouseBut1Act();
                } else if (mouseEvent.getButton() == 3) {
                    altMouseBut1Act();
                }
                checkForWinningMove();
            }
            private void speedMouseBut1Act() {
                mouseBut3Act();
                constantMouseBut1Act();
            }

            private void normalButAct(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 1) {
                    normalMouseBut1Act();
                } else if (mouseEvent.getButton() == 3) {
                    mouseBut3Act();
                }
                checkForWinningMove();
            }

            private void normalMouseBut1Act() {
                altMouseBut1Act();
                constantMouseBut1Act();
            }

            private void altMouseBut1Act() {
                if (!f.isUncovered(row, col) && !f.isSanitized(row, col)) {
                    int adjacent = f.findVirus(row, col);
                    if (adjacent == -1) {

                        gameOver();
                    } else {
                         uncoverfieldBut(adjacent, row, col);
                    }
                }
            }
            private void constantMouseBut1Act() {
                if (f.isUncovered(row, col) && f.getAdjacentVirusCount(row, col) != 0 && f.countAdjacentFlags(row, col) == f.getAdjacentVirusCount(row, col)) {
                    boolean foundvirus = digAdj();
                    if (foundvirus) {
                        gameOver();
                    }
                }
            }

            private boolean digAdj() {
                boolean foundvirus = false;
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        if (f.isInsideBounds(row, col, k, l)) {
                            if (f.findVirus(row + k, col + l) == -1) {
                                foundvirus = true;
                            }
                            if (!f.isSanitized(row + k, col + l)) {
                                virusfieldBut[row + k][col + l].setBackground(uncoveredTile);
                            }
                        }
                    }
                }
                return foundvirus;
            }

            private void mouseBut3Act() {
                if (!f.isUncovered(row, col)) {
                    if (f.isSanitized(row, col)) {
                        f.unwash(row, col);
                        virusfieldBut[row][col].setBackground(alternateTile[(row%2+col)%2]);
                        virusfieldBut[row][col].setText("");
                        virusfieldBut[row][col].setIcon(null);
                    } else {
                        f.sanitizer(row, col);
                        virusfieldBut[row][col].setBackground(SanitizedTile);
                        virusfieldBut[row][col].setIcon(sanitizeicnImg);
                    }
                    sanitizer.setText(f.getFlagCounter() + "/" + virus);
                }
            }

            private void startGame() {
                inTimer();
                timer.start();
                gamestart = true;
                Activegame = true;
                pauseBut.setEnabled(true);
                restartBut.setEnabled(true);
                f.generatefield(row, col);
                altMouseBut1Act();
                constantMouseBut1Act();
            }

            private void inTimer() {                           //game timer
                timer = new Timer(1000, new ActionListener() {
                    int count = 1;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (Activegame) {
                            updateTime(time, count);
                            count++;
                        }
                    }
                });
            }

            private void updateTime(JLabel timeLabel, int count) {              // method to update time
                if (count % 60 < 10 && count / 60 < 10) {
                    timeLabel.setText("0" + count / 60 + ":0" + count % 60);
                } else if (count % 60 >= 10 && count / 60 < 10) {
                    timeLabel.setText("0" + count / 60 + ":" + count % 60);
                } else if (count % 60 < 10 && count / 60 >= 10) {
                    timeLabel.setText(count / 60 + ":0" + count % 60);
                } else if (count % 60 >= 10 && count / 60 >= 10) {
                    timeLabel.setText(count / 60 + ":" + count % 60);
                }
            }

            private void checkForWinningMove() {
                if (isWinningMove()) {
                    gameOver();
                }
            }

            private boolean isWinningMove() {
                return f.getCorrectFlagCounter() == virus && f.getFlagCounter() == virus && f.getNumUncovered() == rows * columns - virus;
            }

            private void gameOver() {
                repaint();
                timer.stop();
                Activegame = false;
                pauseBut.setEnabled(false);
                showvirus();
            }
        });
    }

    private JPanel systemPanel(JFrame frame) {              //creates the game frame
        JPanel systemPanel = new JPanel();

        sanitizer = new JLabel();
        sanitizer.setText(f.getFlagCounter() + "/" + virus);
        sanitizer.setPreferredSize(new Dimension(40,24));
        systemPanel.add(sanitizer);

        JButton changeBoardBut = new JButton();
        changeBoardBut.setText("Change Board");
        changeBoardBut.addActionListener(actionEvent -> {
            frame.dispose();
            new Menu();
        });
        changeBoardBut.setFocusable(false);
        systemPanel.add(changeBoardBut);

        pauseBut = new JButton();
        pauseBut.setText("Pause");
        pauseBut.setEnabled(false);
        pauseBut.addActionListener(actionEvent -> {
            if (gamestart) {
                if (Activegame) {
                    pauseBut.setText("Play");
                    Activegame = false;
                    setBlankBoard();
                } else {
                    pauseBut.setText("Pause");
                    repaint();
                    Activegame = true;
                }
            }
        });
        pauseBut.setFocusable(false);
        systemPanel.add(pauseBut);

        restartBut= new JButton();
        restartBut.setText("Restart");
        restartBut.setEnabled(false);
        restartBut.addActionListener(actionEvent -> {
            timer.stop();
            restartGame();
        });
        restartBut.setFocusable(false);
        systemPanel.add(restartBut);

        JButton speedSanitizeBut = new JButton();
        if (speedSanitizeMode) {
            speedSanitizeBut.setBackground(selectedButton);
        } else {
            speedSanitizeBut.setBackground(defaultButton);
        }
        speedSanitizeBut.setIcon(new ImageIcon(sanitizeImg.getScaledInstance(15,15, Image.SCALE_SMOOTH)));
        speedSanitizeBut.addActionListener(actionEvent -> {
            if (!speedSanitizeMode) {
                speedSanitizeBut.setBackground(selectedButton);
                writeFile("SpeedSanitize");
            } else {
                speedSanitizeBut.setBackground(defaultButton);
                writeFile("Normal");
            }
            speedSanitizeMode = !speedSanitizeMode;
        });
        speedSanitizeBut.setFocusable(false);
        systemPanel.add(speedSanitizeBut);

        return systemPanel;
    }

    private void repaint() {
        int color;
        for (int i = 0; i < rows; i++) {
            color = i % 2;
            for (int j = 0; j < columns; j++) {
                if (f.isUncovered(i, j)) {
                    virusfieldBut[i][j].setBackground(uncoveredTile);
                    if (f.getAdjacentVirusCount(i, j) > 0) {
                        virusfieldBut[i][j].setForeground(digitColors[f.getAdjacentVirusCount(i, j)]);
                        virusfieldBut[i][j].setText(f.getAdjacentVirusCount(i, j) + "");
                    }
                } else {
                    sanitizer.setText(f.getFlagCounter() + "/" + virus);
                    if (f.isSanitized(i, j)) {
                        virusfieldBut[i][j].setBackground(SanitizedTile);
                        virusfieldBut[i][j].setIcon(sanitizeicnImg);
                    } else {
                        virusfieldBut[i][j].setBackground(alternateTile[color % 2]);
                        virusfieldBut[i][j].setText("");
                        virusfieldBut[i][j].setIcon(null);
                    }
                }
                color++;
            }
        }
        if (dev) {
            showvirus();
        }
    }
    private void restartGame() {     //restart game method
        f = new Field(this, rows, columns, virus);
        pauseBut.setEnabled(false);
        time.setText("00:00");
        pauseBut.setText("Pause");
        gamestart = false;
        Activegame = false;
        restartBut.setEnabled(false);
        repaint();
    }
    public void uncoverfieldBut(int adjacent, int row, int col) {          //uncovers the field if lost
        virusfieldBut[row][col].setBackground(uncoveredTile);
        if (f.getAdjacentVirusCount(row, col) > 0) {
            virusfieldBut[row][col].setForeground(digitColors[adjacent]);
            virusfieldBut[row][col].setText(adjacent + "");
        }
    }

    private void showvirus() {            //shows the position of virus
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (f.getAdjacentVirusCount(i, j) == -1) {
                    if (!f.isSanitized(i, j)) {
                        virusfieldBut[i][j].setIcon(virusicnImg);
                    } else {
                        virusfieldBut[i][j].setBackground(Color.GREEN);
                    }
                } else if (f.isSanitized(i, j)) {
                    virusfieldBut[i][j].setIcon(null);
                    virusfieldBut[i][j].setForeground(digitColors[0]);
                    virusfieldBut[i][j].setBackground(Color.RED);
                    virusfieldBut[i][j].setText("x");
                }
            }
        }
    }
    private JPanel timePan() {
        JPanel timePan = new JPanel();
        time = new JLabel();
        time.setText("00:00");
        timePan.add(time);
        return timePan;
    }

    private void setBlankBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                virusfieldBut[i][j].setIcon(null);
                virusfieldBut[i][j].setBackground(pausedTile);
                virusfieldBut[i][j].setText("");
            }
        }
    }





}
