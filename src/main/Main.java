package main;

import javax.swing.*;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/piece/black_knight.png")));
        frame.setIconImage(icon.getImage());

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.launch();
    }
}