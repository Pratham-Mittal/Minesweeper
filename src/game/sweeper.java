package game;

import javax.swing.*;
import gui.Menu;

public class sweeper {

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(0);
        }
        new Menu();
    }

}

