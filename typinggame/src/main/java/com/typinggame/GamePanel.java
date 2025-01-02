package com.typinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public final class GamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16;
    String rightChars = "";
    final int scale = 3;
    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 25;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    int speed = 2;
    int timeToNewWord = 100;
    int speedup = 0;
    int newWord = 0;
    String wordTyped = "";
    int fps = 60;
    boolean lost = true;
    String filePathWithFileName = "randoword.txt";
    KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    List<Word> words;
    Font font = new Font("Arial", Font.BOLD, 24);
    FontMetrics metrics = getFontMetrics(font);
    int charToTest = 0;
    int rightWords = 0; 
    boolean gameStarted = false;
    int topScore = 0;
    public GamePanel() throws FileNotFoundException, IOException {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        words = new ArrayList<>();
        
        getRandomWord();
        getTopScore();
        
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        
    }

    public void getRandomWord(){
        
        int y = getRandomNumber( 100,  500);
            try {
                String sanaString = getRandomLineFromTheFile(filePathWithFileName);
                Word sana = new Word(y, sanaString);
                sana.x -=  300;
                words.add(sana);
            } catch (Exception e) {
                
            } 
        
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @Override
    public void run() {
        double drawInterval = 1000000000 / fps;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            long currentTime = System.nanoTime();
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateWordPosition(){

    }

    

    public void update() {

        if (!lost) {
            
            
            if (!words.isEmpty()) {
                
                for(Word word : words){
                    word.x += speed;
                }
                
                if (words.get(0).x + metrics.stringWidth(words.get(0).wordString) >= 1200) {
                    lost = true;
                }
            
                if (words.get(0).wordString.equals(wordTyped)) {
                    words.remove(0);
                    wordTyped = "";
                    rightChars = "";
                    charToTest = 0;
                    rightWords += 1;
                    speedup += 1;
                }

                if (wordTyped.length() > 0) {
                    checkRightCharacters();
                }
            }
            if (speedup == 10) {
                speedup = 0;
                speed += 0.5;
            
            }
            if (newWord != timeToNewWord) {
                

                newWord += 1;
            }else{
                getRandomWord();
                newWord = 0;
            }
        }else{

        }
    }
    public String getRandomLineFromTheFile(String filePathWithFileName) throws Exception {
        File file = new File(filePathWithFileName); 
        String randomLine;
        try (RandomAccessFile f = new RandomAccessFile(file, "r")) {
            final long randomLocation = (long) (Math.random() * f.length());
            f.seek(randomLocation);
            f.readLine();
            randomLine = f.readLine();
        }
        return randomLine;
    }
    public void getTopScore() throws FileNotFoundException, IOException{
        File file = new File("topScore.txt");
        try(RandomAccessFile f = new RandomAccessFile(file,"r")){
            String line = f.readLine();
            topScore = Integer.parseInt(line);
        }

    }
    public void updateTopScore() throws FileNotFoundException, IOException{
        File file = new File("topScore.txt");
        try(RandomAccessFile f = new RandomAccessFile(file,"rw")){
            
            f.writeBytes(String.valueOf(rightWords));
        }
    }
    public void handleKeyPress(KeyEvent e) throws FileNotFoundException, IOException {
        if (lost) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                startNewGame();
            }
        }else{
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            
                if (wordTyped.length() > 0) {
                    wordTyped = wordTyped.substring(0, wordTyped.length() - 1);
                }
            }else if(e.getKeyChar() != ' '){
                wordTyped += e.getKeyChar();
            }
        }
       
    }
    public void startNewGame() throws FileNotFoundException, IOException{
        if (topScore < rightWords) {
            updateTopScore();
        }
        
        wordTyped = "";
        words.clear();
        rightWords = 0;
        lost = false;
        gameStarted = true;
    }
    public void handleKeyRelease(KeyEvent e) {
       
    }
    public void checkRightCharacters(){


        if (words.get(0).wordString.length() > charToTest && wordTyped.length() > charToTest) {
            if (words.get(0).wordString.charAt(charToTest) == wordTyped.charAt(charToTest)) {
                rightChars += words.get(0).wordString.charAt(charToTest);
                charToTest++;
            }
        }
        
        
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(font);
    

        for (int i = 0; i < words.size(); i++) {
            g2.setColor(Color.white);
            g2.drawString(words.get(i).wordString, words.get(i).x, words.get(i).y);
        }
    
        
        if (wordTyped.length() > 0) {
            String currentWord = words.get(0).wordString;
            int xPosition = 500;
            int yPosition = 500;
    
            for (int i = 0; i < wordTyped.length(); i++) {
                char typedChar = wordTyped.charAt(i);
                g2.setColor(Color.RED);
    
                if (i < currentWord.length() && typedChar == currentWord.charAt(i)) {
                    g2.setColor(Color.GREEN);
                }
    
                
                g2.drawString(String.valueOf(typedChar), xPosition, yPosition);
    
                
                xPosition += metrics.charWidth(typedChar);
            }
        }
    
        if (lost) {
            if (!gameStarted) {
                g2.setColor(Color.red);
                g2.drawString("Welcome your current top Score is "+ topScore, 350, 100);
                g2.drawString("Press space to start the game", 350, 150);
            }else{
                g2.setColor(Color.red);
                g2.drawString("You lost with " + rightWords + " points!", 350, 100);
            
                g2.drawString("Press space to play again!", 350, 150);
            }
        }else{
            g2.setColor(Color.RED);
            g2.drawString(rightWords + "", 20, 20);
        }
        
    }
    

    
}
