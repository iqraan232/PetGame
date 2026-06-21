package virtualpetsimulator;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
 
public class PetGame extends JFrame {
 
    private Pet pet;
    private Timer gameTimer;
 
    // ── Stats bars ────────────────────────────────────────
    private AnimatedBar hungerBar;
    private AnimatedBar happinessBar;
    private AnimatedBar energyBar;
    private JLabel ageLabel;
    private JLabel stageLabel;
    private JLabel moodLabel;
 
    // ── Scene ─────────────────────────────────────────────
    private GamePanel gamePanel;
    private String currentScene = "ROOM";
 
    // ── Design tokens ─────────────────────────────────────
    static final Color BG_DARK   = new Color(15, 12, 41);
    static final Color BG_MID    = new Color(28, 24, 65);
    static final Color ACCENT    = new Color(124, 77, 255);
    static final Color ACCENT2   = new Color(255, 174, 0);
    static final Color GREEN     = new Color(60, 200, 100);
    static final Color RED_WARM  = new Color(255, 90, 90);
    static final Color TEXT_HI   = Color.WHITE;
    static final Color TEXT_LO   = new Color(180, 175, 220);
    static final Font  UI_FONT   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font  BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    static final Font  TITLE_FNT = new Font("Segoe UI", Font.BOLD, 20);
 
    // ── Food items ────────────────────────────────────────
    private final List<FoodItem> foodItems = new ArrayList<>();
 
    public PetGame(Pet pet) {
        this.pet = pet;
 
        setTitle("Virtual Pet Simulator – " + pet.getName());
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BG_DARK);
 
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);
 
        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSideBar(), BorderLayout.WEST);
 
        gamePanel = new GamePanel();
        root.add(gamePanel, BorderLayout.CENTER);
 
        updateStats();
        startGameTimer();
 
        setVisible(true);
    }
 
    // ─────────────────────────────────────────────────────
    // Top bar
    // ─────────────────────────────────────────────────────
 
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(20, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_MID, getWidth(), 0, BG_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(124, 77, 255, 80));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 20, 10, 20));
        bar.setPreferredSize(new Dimension(0, 90));
 
        JPanel namePanel = new JPanel(new GridLayout(3, 1, 0, 1));
        namePanel.setOpaque(false);
 
        JLabel nameLabel = new JLabel(petEmoji() + "  " + pet.getName());
        nameLabel.setFont(TITLE_FNT);
        nameLabel.setForeground(TEXT_HI);
 
        ageLabel = new JLabel("Age: 0");
        ageLabel.setFont(UI_FONT);
        ageLabel.setForeground(TEXT_LO);
 
        stageLabel = new JLabel("Stage: Baby");
        stageLabel.setFont(UI_FONT);
        stageLabel.setForeground(ACCENT2);
 
        namePanel.add(nameLabel);
        namePanel.add(ageLabel);
        namePanel.add(stageLabel);
        bar.add(namePanel, BorderLayout.WEST);
 
        JPanel barsPanel = new JPanel(new GridLayout(3, 1, 0, 6));
        barsPanel.setOpaque(false);
        barsPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
 
        hungerBar    = new AnimatedBar("🍖 Hunger",    new Color(255, 140, 50));
        happinessBar = new AnimatedBar("😊 Happiness", new Color(90, 200, 120));
        energyBar    = new AnimatedBar("⚡ Energy",    new Color(80, 160, 255));
 
        barsPanel.add(hungerBar);
        barsPanel.add(happinessBar);
        barsPanel.add(energyBar);
        bar.add(barsPanel, BorderLayout.CENTER);
 
        // Right: mood icon + "Change Pet" button
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
 
        moodLabel = new JLabel("😊");
        moodLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        moodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JButton changeBtn = new JButton("Change Pet");
        changeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        changeBtn.setForeground(TEXT_HI);
        changeBtn.setBackground(new Color(80, 50, 160));
        changeBtn.setBorder(BorderFactory.createLineBorder(ACCENT, 1, true));
        changeBtn.setFocusPainted(false);
        changeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeBtn.addActionListener(e -> confirmChangePet());
 
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(moodLabel);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(changeBtn);
        rightPanel.add(Box.createVerticalGlue());
        bar.add(rightPanel, BorderLayout.EAST);
 
        return bar;
    }
 
    private String petEmoji() {
        return switch (pet.getPetType()) {
            case "dog"       -> "🐶";
            case "cat"       -> "🐱";
            case "cockroach" -> "🪳";
            default          -> "🐾";
        };
    }
 
    // ─────────────────────────────────────────────────────
    // Side bar
    // ─────────────────────────────────────────────────────
 
    private JPanel buildSideBar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_MID, 0, getHeight(), BG_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(124, 77, 255, 80));
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(145, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(20, 12, 20, 12));
 
        side.add(Box.createVerticalStrut(10));
 
        ActionButton feedBtn  = new ActionButton("🍖", "Feed",  new Color(255, 140, 50));
        ActionButton playBtn  = new ActionButton("🎾", "Play",  new Color(90, 200, 120));
        ActionButton walkBtn  = new ActionButton("🌳", "Walk",  new Color(80, 160, 255));
        ActionButton sleepBtn = new ActionButton("💤", "Sleep", new Color(180, 120, 255));
 
        side.add(feedBtn);
        side.add(Box.createVerticalStrut(12));
        side.add(playBtn);
        side.add(Box.createVerticalStrut(12));
        side.add(walkBtn);
        side.add(Box.createVerticalStrut(12));
        side.add(sleepBtn);
        side.add(Box.createVerticalGlue());
 
        feedBtn.addActionListener(e -> showFeedMenu());
        playBtn.addActionListener(e -> {
            pet.play();
            currentScene = "PLAY";
            gamePanel.resetBall();
            updateStats();
            gamePanel.repaint();
        });
        walkBtn.addActionListener(e -> {
            pet.walk();
            currentScene = "PARK";
            updateStats();
            gamePanel.repaint();
        });
        sleepBtn.addActionListener(e -> {
            pet.sleep();
            currentScene = "SLEEP";
            updateStats();
            gamePanel.repaint();
        });
 
        return side;
    }
 
    // ─────────────────────────────────────────────────────
    // Feed menu  –  only shows foods valid for THIS pet
    // ─────────────────────────────────────────────────────
 
    private void showFeedMenu() {
        currentScene = "ROOM";
 
        JDialog dlg = new JDialog(this, "Choose Food for " + pet.getName(), true);
        dlg.setSize(420, 320);
        dlg.setLocationRelativeTo(this);
 
        JPanel content = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_MID, 0, getHeight(), BG_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 20, 16, 20));
        dlg.setContentPane(content);
 
        JLabel title = new JLabel(petEmoji() + "  What would you like to feed " + pet.getName() + "?",
                SwingConstants.CENTER);
        title.setFont(BOLD_FONT);
        title.setForeground(TEXT_HI);
        content.add(title, BorderLayout.NORTH);
        
        String[] petFoods = pet.getFoods(); // Polymorphism
 
        // Map food name → {emoji, color}
        Object[][] foodMeta = {
            // Dog foods
            {"Bone",         "🦴", new Color(255, 220, 140)},
            {"Meat",         "🥩", new Color(200, 80,  80) },
            // Cat foods
            {"Fish",         "🐟", new Color(100, 200, 255)},
            {"Milk",         "🥛", new Color(230, 230, 255)},
            // Shared
            {"Chicken",      "🍗", new Color(255, 200, 80) },
            // Cockroach foods
            {"Crumbs",       "🫘", new Color(180, 160, 120)},
            {"Rotting Fruit","🍂", new Color(150, 100, 50) },
            {"Sugar",        "🍬", new Color(255, 180, 220)},
        };
 
        int cols = Math.min(3, petFoods.length);
        int rows = (int) Math.ceil(petFoods.length / (double) cols);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 10, 10));
        grid.setOpaque(false);
 
        for (String foodName : petFoods) {
            String emoji = "🍽";
            Color  col   = new Color(150, 150, 200);
            for (Object[] meta : foodMeta) {
                if (((String) meta[0]).equalsIgnoreCase(foodName)) {
                    emoji = (String) meta[1];
                    col   = (Color)  meta[2];
                    break;
                }
            }
 
            final String  fName  = foodName;
            final String  fEmoji = emoji;
            final Color   fCol   = col;
 
            JButton btn = new JButton("<html><center>" + fEmoji + "<br>" + fName + "</center></html>") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(fCol.getRed(), fCol.getGreen(), fCol.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(fCol.getRed(), fCol.getGreen(), fCol.getBlue(), 120));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            btn.setForeground(TEXT_HI);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
            btn.addActionListener(ev -> {
                dlg.dispose();
                try {
                    pet.feed(fName, 20);
                    spawnFoodItem(fName, fEmoji, fCol);
                    updateStats();
                } catch (IllegalArgumentException ex) {
                    // Should never happen since we only show valid foods, but guard anyway
                    JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Invalid Food", JOptionPane.WARNING_MESSAGE);
                }
            });
 
            grid.add(btn);
        }
 
        content.add(grid, BorderLayout.CENTER);
        dlg.setVisible(true);
    }
 
    private void spawnFoodItem(String name, String emoji, Color col) {
        int gw = gamePanel.getWidth();
        int gh = gamePanel.getHeight();
        int fx = 100 + (int)(Math.random() * Math.max(1, gw - 200));
        int fy = 100 + (int)(Math.random() * Math.max(1, gh - 200));
        synchronized (foodItems) {
            foodItems.add(new FoodItem(name, emoji, col, fx, fy));
        }
        gamePanel.repaint();
    }
 
    // ─────────────────────────────────────────────────────
    // Change pet mid-game
    // ─────────────────────────────────────────────────────
 
    private void confirmChangePet() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to switch to a new pet?\nYour current pet's progress will be lost.",
            "Change Pet", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (gameTimer != null) gameTimer.stop();
            dispose();
            SwingUtilities.invokeLater(() -> new StartScreen().setVisible(true));
        }
    }
 
    // ─────────────────────────────────────────────────────
    // Stats update
    // ─────────────────────────────────────────────────────
 
    private void updateStats() {
        hungerBar.setTarget(pet.getHunger());
        happinessBar.setTarget(pet.getHappiness());
        energyBar.setTarget(pet.getEnergy());
 
        ageLabel.setText("Age: " + pet.getAge());
        stageLabel.setText("Stage: " + pet.getStage());
 
        int h = pet.getHappiness();
        if      (h > 70) moodLabel.setText("😊");
        else if (h > 40) moodLabel.setText("😐");
        else             moodLabel.setText("😢");
    }
 
    // ─────────────────────────────────────────────────────
    // Game timer
    // ─────────────────────────────────────────────────────
 
    private void startGameTimer() {
        gameTimer = new Timer(1000, e -> {
            try {
                pet.updateStats();
                updateStats();
 
                if (!pet.isAlive()) {
                    gameTimer.stop();
                    showGameOver();
                }
                gamePanel.repaint();
            } catch (Exception ex) {
                gameTimer.stop();
                JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gameTimer.start();
    }
 
    // ─────────────────────────────────────────────────────
    // Game Over dialog – restart or quit
    // ─────────────────────────────────────────────────────
 
    private void showGameOver() {
        String[] options = {"🔄 Restart", "🚪 Quit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "💀 Oh no!  " + pet.getName() + " couldn't survive!\n\n"
                + "Final Age : " + pet.getAge() + "\n"
                + "Stage     : " + pet.getStage() + "\n\n"
                + "Would you like to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]);
 
        if (choice == 0) {
            // Restart → go back to StartScreen
            dispose();
            SwingUtilities.invokeLater(() -> new StartScreen().setVisible(true));
        } else {
            System.exit(0);
        }
    }
 
    // ═════════════════════════════════════════════════════
    //  DATA: FoodItem
    // ═════════════════════════════════════════════════════
 
    class FoodItem {
        String name, emoji;
        Color  color;
        float  x, y, vx, vy;
        float  alpha  = 1f;
        boolean eaten = false;
        int flashTick = 0;
 
        FoodItem(String name, String emoji, Color color, float x, float y) {
            this.name  = name; this.emoji = emoji; this.color = color;
            this.x = x; this.y = y;
            this.vx = (float)(Math.random() * 1.4 - 0.7);
            this.vy = (float)(Math.random() * 1.4 - 0.7);
        }
    }
 
    // ═════════════════════════════════════════════════════
    //  DATA: Ball  (used in PLAY scene)
    // ═════════════════════════════════════════════════════
 
    class Ball {
        float x, y;
        float vx, vy;
        final int radius = 22;
        float spinAngle  = 0f;
        float squashX = 1f, squashY = 1f;
        float squashTimer = 0f;
 
        static final int TRAIL_LEN = 8;
        float[] trailX = new float[TRAIL_LEN];
        float[] trailY = new float[TRAIL_LEN];
        int trailHead  = 0;
 
        Ball(int panelW, int panelH) { reset(panelW, panelH); }
 
        void reset(int panelW, int panelH) {
            x = panelW / 2f; y = panelH / 2f;
            double angle = Math.random() * Math.PI * 2;
            float  speed = 3.5f + (float)(Math.random() * 2.0);
            vx = (float) Math.cos(angle) * speed;
            vy = (float) Math.sin(angle) * speed;
            squashX = squashY = 1f;
            squashTimer = 0f;
            for (int i = 0; i < TRAIL_LEN; i++) { trailX[i] = x; trailY[i] = y; }
        }
 
        boolean tick(int W, int H) {
            boolean bounced = false;
            trailX[trailHead] = x; trailY[trailHead] = y;
            trailHead = (trailHead + 1) % TRAIL_LEN;
            x += vx; y += vy;
            vy += 0.06f;
 
            if (x - radius < 0)    { x = radius;    vx =  Math.abs(vx); triggerSquash(true);  bounced = true; }
            else if (x + radius > W){ x = W - radius; vx = -Math.abs(vx); triggerSquash(true);  bounced = true; }
            if (y - radius < 0)    { y = radius;    vy =  Math.abs(vy); triggerSquash(false); bounced = true; }
            else if (y + radius > H - 20) {
                y = H - 20 - radius;
                vy = -Math.abs(vy) * 0.92f;
                triggerSquash(false); bounced = true;
            }
 
            float spd = (float) Math.sqrt(vx * vx + vy * vy);
            if (spd > 12f) { vx = vx / spd * 12f; vy = vy / spd * 12f; }
            if (spd < 2.0f && spd > 0) { vx = vx / spd * 2.0f; vy = vy / spd * 2.0f; }
            spinAngle += vx * 0.05f;
 
            if (squashTimer > 0) {
                squashTimer -= 0.15f;
                float t = Math.max(0f, squashTimer);
                squashX = 1f + 0.3f * t * (squashX > 1 ? -1 : 1);
                squashY = 1f + 0.3f * t * (squashY > 1 ? -1 : 1);
            } else { squashX = 1f; squashY = 1f; }
 
            return bounced;
        }
 
        void triggerSquash(boolean horizontal) {
            squashTimer = 1f;
            if (horizontal) { squashX = 0.7f; squashY = 1.3f; }
            else            { squashX = 1.3f; squashY = 0.7f; }
        }
 
        void bounceAwayFrom(float petCX, float petCY) {
            float dx = x - petCX, dy = y - petCY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 1) dist = 1;
            float nx = dx / dist, ny = dy / dist;
            float kick = 7f + (float)(Math.random() * 4.0);
            vx = nx * kick; vy = ny * kick - 2f;
            triggerSquash(Math.abs(nx) > Math.abs(ny));
        }
    }
 
    // ═════════════════════════════════════════════════════
    //  GAME PANEL
    // ═════════════════════════════════════════════════════
 
    class GamePanel extends JPanel {
 
        private float petX = 320, petY = 280;
        private float targetX = 320, targetY = 280;
        private float petVX = 0, petVY = 0;
        private int   direction = 1;
        private float walkX    = 320;
        private int   walkDir  = 4;
        private boolean mouseControlled = false;
        private float bounceOffset = 0f;
        private float bounceDir    = 0.3f;
 
        private Ball    ball;
        private boolean ballActive = false;
        private static final float PLAY_INTERACT_DIST = 90f;
 
        // Images
        private Image roomBg, playBg, parkBg, sleepBg;
        private Image dogImg, catImg, cockroachImg;
 
        private static final Font EMOJI_SM = new Font("Segoe UI Emoji", Font.PLAIN, 28);
        private static final Font EMOJI_XL = new Font("Segoe UI Emoji", Font.PLAIN, 48);
 
        public GamePanel() {
            setBackground(BG_DARK);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
 
            roomBg      = loadImage("/virtualpetsimulator/images/room.png");
            playBg      = loadImage("/virtualpetsimulator/images/play.png");
            parkBg      = loadImage("/virtualpetsimulator/images/park.png");
            sleepBg     = loadImage("/virtualpetsimulator/images/sleep.png");
            dogImg      = loadImage("/virtualpetsimulator/images/dog.png");
            catImg      = loadImage("/virtualpetsimulator/images/cat.png");
            cockroachImg= loadImage("/virtualpetsimulator/images/cockroach.png");
 
            MouseAdapter ma = new MouseAdapter() {
                @Override public void mouseMoved  (MouseEvent e) { setTarget(e); }
                @Override public void mouseDragged(MouseEvent e) { setTarget(e); }
                private void setTarget(MouseEvent e) {
                    mouseControlled = true;
                    targetX = e.getX() - 60;
                    targetY = e.getY() - 60;
                }
            };
            addMouseMotionListener(ma);
 
            Timer animTimer = new Timer(30, e -> { tick(); repaint(); });
            animTimer.start();
        }
 
        void resetBall() {
            int W = getWidth()  > 0 ? getWidth()  : 600;
            int H = getHeight() > 0 ? getHeight() : 400;
            ball = new Ball(W, H);
            ballActive = true;
        }
 
        private void tick() {
            int W = getWidth(), H = getHeight();
            if (W == 0 || H == 0) return;
 
            bounceOffset += bounceDir;
            if (Math.abs(bounceOffset) > 5) bounceDir = -bounceDir;
 
            if (currentScene.equals("PARK") && !mouseControlled) {
                walkX += walkDir;
                if (walkX > W - 180 || walkX < 50) walkDir *= -1;
                petX = walkX;
                petY = H * 0.48f;
                direction = walkDir > 0 ? 1 : -1;
            } else if (currentScene.equals("PLAY") && ballActive && !mouseControlled) {
                float dx = ball.x - (petX + 60);
                float dy = ball.y - (petY + 60);
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > PLAY_INTERACT_DIST) {
                    float spd = Math.min(dist * 0.04f, 4f);
                    petX += dx / dist * spd;
                    petY += dy / dist * spd;
                    direction = dx >= 0 ? 1 : -1;
                }
                petX = Math.max(10, Math.min(W - 150, petX));
                petY = Math.max(10, Math.min(H - 160, petY));
            } else if (mouseControlled) {
                float dx = targetX - petX;
                float dy = targetY - petY;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > 4) {
                    float spd = Math.min(dist * 0.12f, 14f);
                    petVX = dx / dist * spd; petVY = dy / dist * spd;
                    petX += petVX;           petY  += petVY;
                    direction = petVX >= 0 ? 1 : -1;
                }
                petX = Math.max(20, Math.min(W - 160, petX));
                petY = Math.max(20, Math.min(H - 160, petY));
            }
 
            if (currentScene.equals("PLAY") && ballActive && ball != null) {
                ball.tick(W, H);
                float petCX = petX + 70, petCY = petY + 70;
                float dx = ball.x - petCX, dy = ball.y - petCY;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < PLAY_INTERACT_DIST) ball.bounceAwayFrom(petCX, petCY);
            }
 
            synchronized (foodItems) {
                List<FoodItem> toRemove = new ArrayList<>();
                for (FoodItem f : foodItems) {
                    if (f.eaten) {
                        f.alpha -= 0.06f;
                        f.flashTick++;
                        if (f.alpha <= 0) toRemove.add(f);
                        continue;
                    }
                    f.x += f.vx; f.y += f.vy;
                    if (f.x < 20 || f.x > W - 60) f.vx *= -1;
                    if (f.y < 20 || f.y > H - 60) f.vy *= -1;
                    float fdx = petX + 60 - f.x, fdy = petY + 60 - f.y;
                    float fdist = (float) Math.sqrt(fdx * fdx + fdy * fdy);
                    if (fdist < 200) { f.vx += fdx / fdist * 0.4f; f.vy += fdy / fdist * 0.4f; }
                    float spd = (float) Math.sqrt(f.vx * f.vx + f.vy * f.vy);
                    if (spd > 5) { f.vx = f.vx / spd * 5; f.vy = f.vy / spd * 5; }
                    if (fdist < 50) f.eaten = true;
                }
                foodItems.removeAll(toRemove);
            }
        }
 
        private Image loadImage(String path) {
            try {
                java.net.URL url = getClass().getResource(path);
                if (url == null) return null;
                return new ImageIcon(url).getImage();
            } catch (Exception e) {
                return null;
            }
        }
 
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);
 
            drawBackground(g2);
            drawFoodItems(g2);
 
            if (currentScene.equals("PLAY") && ballActive && ball != null)
                drawBall(g2);
 
            drawPet(g2);
            drawHUD(g2);
            g2.dispose();
        }
 
        // ── Ball rendering ────────────────────────────────
 
        private void drawBall(Graphics2D g2) {
            int r = ball.radius;
            for (int i = 0; i < Ball.TRAIL_LEN; i++) {
                int idx = (ball.trailHead + i) % Ball.TRAIL_LEN;
                float age = (float) i / Ball.TRAIL_LEN;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, age * 0.35f));
                int tr = (int)(r * 0.7f * age);
                g2.setColor(new Color(255, 90, 30));
                g2.fillOval((int)ball.trailX[idx] - tr, (int)ball.trailY[idx] - tr, tr * 2, tr * 2);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
 
            int bx = (int) ball.x, by = (int) ball.y;
            int drawW = (int)(r * 2 * ball.squashX);
            int drawH = (int)(r * 2 * ball.squashY);
            int drawX = bx - drawW / 2, drawY = by - drawH / 2;
 
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(bx - drawW / 2, getHeight() - 28, (int)(drawW * 1.1f), 12);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
 
            RadialGradientPaint ballGrad = new RadialGradientPaint(
                    new Point2D.Float(drawX + drawW * 0.35f, drawY + drawH * 0.30f),
                    Math.max(drawW, drawH) * 0.65f,
                    new float[]{ 0f, 0.55f, 1f },
                    new Color[]{ new Color(255, 100, 80), new Color(220, 40, 30), new Color(140, 10, 10) });
            g2.setPaint(ballGrad);
            g2.fillOval(drawX, drawY, drawW, drawH);
 
            Graphics2D sg = (Graphics2D) g2.create();
            sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sg.setClip(new Ellipse2D.Float(drawX, drawY, drawW, drawH));
            sg.setColor(new Color(255, 255, 255, 90));
            sg.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            sg.translate(bx, by);
            sg.rotate(ball.spinAngle);
            int sr = (int)(r * ball.squashX), sv = (int)(r * ball.squashY);
            sg.drawArc(-sr, -sv, sr * 2, sv * 2,  20, 140);
            sg.drawArc(-sr, -sv, sr * 2, sv * 2, 200, 140);
            sg.dispose();
 
            RadialGradientPaint spec = new RadialGradientPaint(
                    new Point2D.Float(drawX + drawW * 0.30f, drawY + drawH * 0.22f),
                    drawW * 0.28f,
                    new float[]{ 0f, 1f },
                    new Color[]{ new Color(255, 255, 255, 160), new Color(255, 255, 255, 0) });
            g2.setPaint(spec);
            g2.fillOval(drawX, drawY, drawW, drawH);
 
            if (ball.squashTimer > 0.6f) {
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                g2.setColor(new Color(255, 220, 50, 200));
                g2.drawString("✨", bx + r - 4, by - r + 4);
            }
        }
 
        // ── Background ────────────────────────────────────
 
        private void drawBackground(Graphics2D g2) {
            Image bg = switch (currentScene) {
                case "PLAY"  -> playBg;
                case "PARK"  -> parkBg;
                case "SLEEP" -> sleepBg;
                default      -> roomBg;
            };
            if (bg != null) {
                g2.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            } else {
                paintFallbackBg(g2);
            }
        }
 
        private void paintFallbackBg(Graphics2D g2) {
            switch (currentScene) {
                case "PARK" -> {
                    g2.setPaint(new GradientPaint(0, 0, new Color(100, 180, 255),
                            0, getHeight(), new Color(60, 140, 80)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(40, 160, 60));
                    g2.fillRect(0, getHeight() - 100, getWidth(), 100);
                }
                case "SLEEP" -> {
                    g2.setColor(new Color(10, 10, 40));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                case "PLAY" -> {
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 220, 100),
                            0, getHeight(), new Color(200, 140, 60)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                default -> {
                    g2.setPaint(new GradientPaint(0, 0, new Color(80, 60, 140),
                            0, getHeight(), new Color(40, 30, 80)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        }
 
        // ── Food items ────────────────────────────────────
 
        private void drawFoodItems(Graphics2D g2) {
            synchronized (foodItems) {
                for (FoodItem f : foodItems) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            Math.max(0f, f.alpha)));
                    int haloR = 28;
                    RadialGradientPaint halo = new RadialGradientPaint(
                            f.x + 20, f.y + 20, haloR,
                            new float[]{0f, 1f},
                            new Color[]{ new Color(f.color.getRed(), f.color.getGreen(),
                                                   f.color.getBlue(), 100),
                                         new Color(0, 0, 0, 0)});
                    g2.setPaint(halo);
                    g2.fillOval((int)f.x - haloR + 20, (int)f.y - haloR + 20, haloR * 2, haloR * 2);
                    g2.setFont(EMOJI_SM);
                    g2.setColor(Color.WHITE);
                    g2.drawString(f.emoji, (int)f.x, (int)f.y + 20);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.drawString(f.name, (int)f.x - 4, (int)f.y + 38);
                    if (f.eaten) { g2.setFont(EMOJI_SM); g2.drawString("✨", (int)f.x - 10, (int)f.y - 10); }
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
        }
 
        // ── Pet drawing ───────────────────────────────────
 
        private void drawPet(Graphics2D g2) {
            int px = (int) petX;
            int py = (int)(petY + bounceOffset);
            int pw = 140, ph = 140;
 
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(px + 10, py + ph + 4, pw - 20, 16);
 
            // ── Select correct image per pet type ──────────
            Image img = switch (pet.getPetType()) {
                case "dog"       -> dogImg;
                case "cat"       -> catImg;
                case "cockroach" -> cockroachImg;
                default          -> null;
            };
 
            // ── Emoji fallback per pet type ─────────────────
            String fallbackEmoji = switch (pet.getPetType()) {
                case "dog"       -> "🐶";
                case "cat"       -> "🐱";
                case "cockroach" -> "🪳";
                default          -> "🐾";
            };
 
            if (img != null) {
                if (direction < 0) {
                    g2.scale(-1, 1);
                    g2.drawImage(img, -(px + pw), py, pw, ph, this);
                    g2.scale(-1, 1);
                } else {
                    g2.drawImage(img, px, py, pw, ph, this);
                }
            } else {
                g2.setFont(EMOJI_XL);
                g2.setColor(Color.WHITE);
                if (direction < 0) {
                    g2.scale(-1, 1);
                    g2.drawString(fallbackEmoji, -(px + pw / 2 + 24), py + ph / 2 + 16);
                    g2.scale(-1, 1);
                } else {
                    g2.drawString(fallbackEmoji, px + pw / 2 - 24, py + ph / 2 + 16);
                }
            }
 
            drawNameTag(g2, px, py, pw);
 
            if (currentScene.equals("SLEEP")) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new Color(180, 220, 255));
                g2.drawString("Z",  px + pw,      py - 10);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("z",  px + pw + 14, py - 22);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("z",  px + pw + 24, py - 30);
            }
        }
 
        private void drawNameTag(Graphics2D g2, int px, int py, int pw) {
            String tag = pet.getName() + "  •  " + pet.getStage();
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(tag);
            int tx = px + pw / 2 - tw / 2, ty = py - 14;
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(tx - 6, ty - fm.getAscent(), tw + 12, fm.getHeight() + 2, 10, 10);
            g2.setColor(new Color(124, 77, 255, 160));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(tx - 6, ty - fm.getAscent(), tw + 12, fm.getHeight() + 2, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString(tag, tx, ty);
        }
 
        // ── HUD ───────────────────────────────────────────
 
        private void drawHUD(Graphics2D g2) {
            String sceneName = switch (currentScene) {
                case "PARK"  -> "🌳 Park";
                case "PLAY"  -> "🎾 Playtime";
                case "SLEEP" -> "💤 Sleeping";
                default      -> "🏠 Room";
            };
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(sceneName) + 20;
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(getWidth() - sw - 14, 10, sw + 4, 28, 12, 12);
            g2.setColor(new Color(180, 160, 255));
            g2.drawString(sceneName, getWidth() - sw - 6, 29);
 
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(255, 255, 255, 80));
            String hint = currentScene.equals("PLAY")
                    ? "Move mouse to guide " + pet.getName() + " toward the ball!"
                    : "Move mouse to guide " + pet.getName();
            g2.drawString(hint, 10, getHeight() - 8);
        }
    }
 
    // ═════════════════════════════════════════════════════
    //  UI COMPONENTS
    // ═════════════════════════════════════════════════════
 
    class AnimatedBar extends JPanel {
        private final String label;
        private final Color  fillColor;
        private int current = 100, target = 100;
 
        AnimatedBar(String label, Color fillColor) {
            this.label = label; this.fillColor = fillColor;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 22));
            Timer anim = new Timer(20, e -> {
                if (current != target) { current += (target > current) ? 1 : -1; repaint(); }
            });
            anim.start();
        }
 
        void setTarget(int t) { this.target = Math.max(0, Math.min(100, t)); }
 
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
            int W = getWidth(), H = getHeight();
            int labelW = 100, barX = labelW, barW = W - labelW - 6, barH = H - 6, barY = 3;
 
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(TEXT_LO);
            g2.drawString(label, 2, H / 2 + 4);
 
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillRoundRect(barX, barY, barW, barH, barH, barH);
 
            int fillW = (int)(barW * current / 100.0);
            Color barColor = current > 60 ? fillColor : current > 30 ? new Color(255, 200, 50) : RED_WARM;
            g2.setPaint(new GradientPaint(barX, 0, barColor.brighter(), barX + fillW, 0, barColor.darker()));
            g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);
 
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(Color.WHITE);
            String pct = current + "%";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(pct, barX + barW - fm.stringWidth(pct) - 4, barY + barH - 3);
 
            g2.dispose();
        }
    }
 
    class ActionButton extends JPanel {
        private final String emoji, label;
        private final Color  accentCol;
        private boolean hovered = false;
        private float   glow    = 0f;
        private final List<ActionListener> listeners = new ArrayList<>();
 
        ActionButton(String emoji, String label, Color accentCol) {
            this.emoji = emoji; this.label = label; this.accentCol = accentCol;
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
            setPreferredSize(new Dimension(120, 62));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
            Timer glowTimer = new Timer(16, e -> {
                glow = hovered ? Math.min(1f, glow + 0.1f) : Math.max(0f, glow - 0.1f);
                repaint();
            });
            glowTimer.start();
 
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; }
                @Override public void mouseExited (MouseEvent e) { hovered = false; }
                @Override public void mouseClicked(MouseEvent e) {
                    ActionEvent ae = new ActionEvent(ActionButton.this,
                            ActionEvent.ACTION_PERFORMED, label);
                    for (ActionListener l : listeners) l.actionPerformed(ae);
                }
            });
        }
 
        void addActionListener(ActionListener l) { listeners.add(l); }
 
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            g2.setColor(new Color(accentCol.getRed(), accentCol.getGreen(), accentCol.getBlue(),
                    (int)(20 + glow * 50)));
            g2.fillRoundRect(0, 0, W, H, 14, 14);
            g2.setColor(new Color(accentCol.getRed(), accentCol.getGreen(), accentCol.getBlue(),
                    (int)(80 + glow * 120)));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, W - 1, H - 1, 14, 14);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            g2.setColor(Color.WHITE);
            g2.drawString(emoji, 14, H / 2 + 4);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(hovered ? Color.WHITE : TEXT_LO);
            g2.drawString(label, 44, H / 2 + 5);
            g2.dispose();
        }
    }
}