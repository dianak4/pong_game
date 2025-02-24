package com.shpp.p2p.cs.dkostiuchenko.assignment4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class BreakoutGame extends JPanel implements MouseMotionListener, ActionListener {
    private static final int APPLICATION_WIDTH = 600;
    private static final int APPLICATION_HEIGHT = 600;
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    private static final int PADDLE_Y_OFFSET = 30;
    private static final int NBRICKS_PER_ROW = 10;
    private static final int NBRICK_ROWS = 10;
    private static final int BRICK_SEP = 4;
    private static final int BRICK_HEIGHT = 8;
    private static final int BALL_RADIUS = 10;
    private static final int BRICK_Y_OFFSET = 70;
    private static int NTURNS = 3;
    private int BRICK_NUM = NBRICK_ROWS * NBRICKS_PER_ROW;

    private Rectangle paddle;
    private Ellipse2D.Double ball;
    private Rectangle[][] bricks;
    private double vx, vy;
    private Timer timer;
    private JFrame frame;

    public BreakoutGame(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(APPLICATION_WIDTH, APPLICATION_HEIGHT));
        setBackground(Color.WHITE);
        addMouseMotionListener(this);
        bricks = new Rectangle[NBRICK_ROWS][NBRICKS_PER_ROW];
        paddle = new Rectangle((APPLICATION_WIDTH - PADDLE_WIDTH) / 2, APPLICATION_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Ellipse2D.Double((APPLICATION_WIDTH - BALL_RADIUS) / 2.0, (APPLICATION_HEIGHT - BALL_RADIUS) / 2.0, BALL_RADIUS, BALL_RADIUS);
        initializeBricks();
        initializeSpeed();
        timer = new Timer(13, this);
        timer.start();
    }

    private void initializeBricks() {
        int totalSep = (NBRICKS_PER_ROW - 1) * BRICK_SEP;
        double brickWidth = (APPLICATION_WIDTH - totalSep) / (double) NBRICKS_PER_ROW;
        for (int row = 0; row < NBRICK_ROWS; row++) {
            for (int col = 0; col < NBRICKS_PER_ROW; col++) {
                int x = (int) (col * (brickWidth + BRICK_SEP));
                int y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);
                bricks[row][col] = new Rectangle(x, y, (int) brickWidth, BRICK_HEIGHT);
            }
        }
    }

    private void initializeSpeed() {
        Random random = new Random();
        vx = 1.0 + random.nextDouble() * 2.0;
        if (random.nextBoolean()) vx = -vx;
        vy = 6.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fill(paddle);
        g2d.fill(ball);

        for (int row = 0; row < NBRICK_ROWS; row++) {
            g2d.setColor(getRowColor(row));
            for (Rectangle brick : bricks[row]) {
                if (brick != null) g2d.fill(brick);
            }
        }
    }

    private Color getRowColor(int row) {
        switch (row / 2) {
            case 0: return Color.RED;
            case 1: return Color.ORANGE;
            case 2: return Color.YELLOW;
            case 3: return Color.GREEN;
            default: return Color.CYAN;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveBall();
        checkForCollisions();
        repaint();
        if (BRICK_NUM == 0) {
            showMessage("You Win!");
        }
    }

    private void moveBall() {
        ball.x += vx;
        ball.y += vy;

        if (ball.x <= 0 || ball.x >= APPLICATION_WIDTH - BALL_RADIUS) {
            vx = -vx;
        }
        if (ball.y <= 0) {
            vy = -vy;
        }
        if (ball.y + BALL_RADIUS >= APPLICATION_HEIGHT) {
            NTURNS--;
            if (NTURNS == 0) {
                showMessage("Game Over!");
                return;
            }
            resetBall();
        }
    }

    private void resetBall() {
        ball.x = (APPLICATION_WIDTH - BALL_RADIUS) / 2.0;
        ball.y = (APPLICATION_HEIGHT - BALL_RADIUS) / 2.0;
        initializeSpeed();
    }

    private void checkForCollisions() {
        for (int row = 0; row < NBRICK_ROWS; row++) {
            for (int col = 0; col < NBRICKS_PER_ROW; col++) {
                if (bricks[row][col] != null && ball.intersects(bricks[row][col])) {
                    bricks[row][col] = null;
                    BRICK_NUM--;
                    vy = -vy;
                    return;
                }
            }
        }
        if (ball.intersects(paddle)) {
            vy = -vy;
            ball.y = paddle.y - BALL_RADIUS;
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
        System.exit(0);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        paddle.x = Math.max(0, Math.min(e.getX() - PADDLE_WIDTH / 2, APPLICATION_WIDTH - PADDLE_WIDTH));
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout");
        BreakoutGame game = new BreakoutGame(frame);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
