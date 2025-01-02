package com.typinggame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class KeyHandler implements KeyListener {

    private final GamePanel gamePanel;

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            gamePanel.handleKeyPress(e);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gamePanel.handleKeyRelease(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

   
}
