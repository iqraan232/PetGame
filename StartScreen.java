package virtualpetsimulator;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
 
public class StartScreen extends JFrame {
 
    private JTextField    nameField;
    private JComboBox<String> petBox;
    private float         titleAlpha = 0f;
    private Timer         fadeTimer;
 
    // ── Design tokens ──────────────────────────────────────
    private static final Color BG_TOP    = new Color(15, 12, 41);
    private static final Color BG_BOT    = new Color(48, 43, 99);
    private static final Color ACCENT    = new Color(124, 77, 255);
    private static final Color ACCENT2   = new Color(255, 174, 0);
    private static final Color TEXT_HI   = Color.WHITE;
    private static final Color TEXT_LO   = new Color(200, 195, 235);
    private static final Font  TITLE_FNT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font  LABEL_FNT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font  BODY_FNT  = new Font("Segoe UI", Font.PLAIN, 14);
 
    // Star field
    private static final int STAR_COUNT = 120;
    private final int[] starX = new int[STAR_COUNT];
    private final int[] starY = new int[STAR_COUNT];
    private final int[] starR = new int[STAR_COUNT];
 
    public StartScreen() {
        setTitle("Virtual Pet Adventure");
        setSize(720, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
 
        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = (int)(Math.random() * 720);
            starY[i] = (int)(Math.random() * 540);
            starR[i] = (int)(Math.random() * 2) + 1;
        }
 
        BackgroundPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBorder(new EmptyBorder(30, 60, 30, 60));
        setContentPane(root);
 
        // ── Title ─────────────────────────────────────────
        JLabel title = new JLabel("🐾  VIRTUAL PET ADVENTURE", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
                super.paintComponent(g);
            }
        };
        title.setFont(TITLE_FNT);
        title.setForeground(TEXT_HI);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(title, BorderLayout.NORTH);
 
        // ── Glass card ────────────────────────────────────
        GlassCard card = new GlassCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(8, 12, 8, 12);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
 
        gc.gridx = 0; gc.gridy = 0;
        card.add(styledLabel("Pet Name"), gc);
 
        gc.gridy = 1;
        nameField = new StyledTextField("e.g. Buddy");
        card.add(nameField, gc);
 
        gc.gridy = 2;
        card.add(styledLabel("Choose Your Pet"), gc);
 
        gc.gridy = 3;
        // ── Parrot removed; Cockroach added ───────────────
        petBox = new JComboBox<>(new String[]{"🐶  Dog", "🐱  Cat", "🪳  Cockroach"});
        styleComboBox(petBox);
        card.add(petBox, gc);
 
        gc.gridy = 4;
        gc.insets = new Insets(20, 12, 8, 12);
        JButton startBtn = new PulseButton("Start Adventure  →");
        card.add(startBtn, gc);
 
        JPanel cardWrap = new JPanel(new GridBagLayout());
        cardWrap.setOpaque(false);
        cardWrap.add(card);
        root.add(cardWrap, BorderLayout.CENTER);
 
        JLabel footer = new JLabel("Feed · Play · Walk · Sleep · Grow", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(TEXT_LO);
        root.add(footer, BorderLayout.SOUTH);
 
        startBtn.addActionListener(e -> startGame());
        nameField.addActionListener(e -> startGame());
 
        fadeTimer = new Timer(30, null);
        fadeTimer.addActionListener(e -> {
            titleAlpha = Math.min(1f, titleAlpha + 0.04f);
            title.repaint();
            if (titleAlpha >= 1f) fadeTimer.stop();
        });
        fadeTimer.start();
 
        setVisible(true);
    }
 
    // ── Helpers ───────────────────────────────────────────
 
    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FNT);
        lbl.setForeground(TEXT_HI);
        return lbl;
    }
 
    private void styleComboBox(JComboBox<String> box) {
        box.setFont(BODY_FNT);
        box.setForeground(TEXT_HI);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT : new Color(30, 25, 65));
                setForeground(TEXT_HI);
                setFont(BODY_FNT);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        box.setPreferredSize(new Dimension(300, 40));
        box.setBorder(BorderFactory.createLineBorder(new Color(124, 77, 255, 120), 1, true));
    }
 
    // ── Start game ───────────────────────────────────────
 
    private void startGame() {
        String rawName = nameField.getText().trim();
        if (rawName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a name for your pet.", "Missing Name",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String sel = petBox.getSelectedItem().toString();
        Pet pet;
        try {
            if      (sel.contains("Dog"))       pet = new Dog(rawName);
            else if (sel.contains("Cat"))       pet = new Cat(rawName);
            else if (sel.contains("Cockroach")) pet = new Cockroach(rawName);
            else throw new IllegalArgumentException("Unknown pet type: " + sel);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                "Could not create pet: " + ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        dispose();
        new PetGame(pet).setVisible(true);
    }
 
    // ─────────────────────────────────────────────────────
    // Inner components
    // ─────────────────────────────────────────────────────
 
    class BackgroundPanel extends JPanel {
        private float cloudX = -200;
        private Timer cloud;
 
        BackgroundPanel() {
            cloud = new Timer(25, e -> {
                cloudX = (cloudX > getWidth() + 300) ? -300 : cloudX + 0.6f;
                repaint();
            });
            cloud.start();
        }
 
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
            GradientPaint sky = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOT);
            g2.setPaint(sky);
            g2.fillRect(0, 0, getWidth(), getHeight());
 
            g2.setColor(new Color(255, 255, 255, 200));
            for (int i = 0; i < STAR_COUNT; i++)
                g2.fillOval(starX[i], starY[i], starR[i], starR[i]);
 
            g2.setColor(new Color(255, 248, 200));
            g2.fillOval(getWidth() - 120, 30, 70, 70);
            g2.setColor(BG_TOP);
            g2.fillOval(getWidth() - 105, 25, 65, 65);
 
            g2.setColor(new Color(255, 255, 255, 12));
            drawCloud(g2, (int)cloudX,       getHeight() / 3, 200, 60);
            drawCloud(g2, (int)cloudX + 350, getHeight() / 4, 150, 45);
 
            GradientPaint ground = new GradientPaint(0, getHeight() - 90,
                    new Color(30, 80, 40), 0, getHeight(), new Color(15, 50, 20));
            g2.setPaint(ground);
            g2.fillRoundRect(0, getHeight() - 90, getWidth(), 90, 20, 20);
 
            drawHouse(g2, 60,               getHeight() - 90);
            drawHouse(g2, getWidth() - 180, getHeight() - 90);
 
            g2.dispose();
        }
 
        private void drawCloud(Graphics2D g2, int x, int y, int w, int h) {
            g2.fillRoundRect(x, y + h / 3, w, h * 2 / 3, 30, 30);
            g2.fillOval(x + w / 4, y, w / 2, h);
            g2.fillOval(x + w / 2, y + h / 5, w / 3, h * 3 / 4);
        }
 
        private void drawHouse(Graphics2D g2, int x, int baseY) {
            g2.setColor(new Color(180, 120, 80, 90));
            g2.fillRect(x, baseY - 60, 80, 60);
            g2.setColor(new Color(160, 50, 50, 90));
            int[] px = {x - 10, x + 40, x + 90};
            int[] py = {baseY - 60, baseY - 100, baseY - 60};
            g2.fillPolygon(px, py, 3);
        }
    }
 
    class GlassCard extends JPanel {
        GlassCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(24, 32, 24, 32));
            setPreferredSize(new Dimension(400, 300));
        }
 
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 28));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            g2.setColor(new Color(255, 255, 255, 55));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
            g2.dispose();
            super.paintComponent(g);
        }
    }
 
    class StyledTextField extends JTextField {
        private final String hint;
 
        StyledTextField(String hint) {
            this.hint = hint;
            setOpaque(false);
            setFont(BODY_FNT);
            setForeground(TEXT_HI);
            setCaretColor(ACCENT2);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(124, 77, 255, 150), 1, true),
                    new EmptyBorder(8, 12, 8, 12)));
            setPreferredSize(new Dimension(300, 40));
        }
 
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !hasFocus()) {
                g.setColor(new Color(200, 195, 235, 140));
                g.setFont(BODY_FNT);
                g.drawString(hint, 14, getHeight() / 2 + 5);
            }
        }
    }
 
    class PulseButton extends JButton {
        private float glow    = 0f;
        private Timer hoverTimer;
        private boolean hovered = false;
 
        PulseButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(300, 46));
 
            hoverTimer = new Timer(16, e -> {
                glow = hovered ? Math.min(1f, glow + 0.08f) : Math.max(0f, glow - 0.08f);
                repaint();
                if (!hovered && glow == 0f) hoverTimer.stop();
            });
 
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  hoverTimer.start(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; }
            });
        }
 
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (glow > 0) {
                g2.setColor(new Color(124, 77, 255, (int)(glow * 80)));
                g2.fillRoundRect(-4, -4, getWidth() + 8, getHeight() + 8, 18, 18);
            }
            Color c1 = blend(ACCENT, new Color(160, 100, 255), glow);
            Color c2 = blend(new Color(80, 40, 180), new Color(100, 60, 220), glow);
            g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(new Color(180, 140, 255, 180));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
 
        private Color blend(Color a, Color b, float t) {
            return new Color(
                (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
        }
    }
}