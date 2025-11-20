import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class TileMapEditor extends JFrame {
    private static final int TILE_SIZE = 24;
    private static final int MAP_WIDTH = 16;
    private static final int MAP_HEIGHT = 9;
    
    private ArrayList<BufferedImage> tileImages;
    private int[][] tileMap;
    private int selectedTile;
    
    private JPanel tilesetPanel;
    private MapCanvas mapCanvas;
    private JLabel statusLabel;
    
    public TileMapEditor() {
        tileImages = new ArrayList<>();
        tileMap = new int[MAP_HEIGHT][MAP_WIDTH];
        selectedTile = 0;
        
        // Initialize map with -1 (empty)
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                tileMap[y][x] = -1;
            }
        }
        
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Tile Map Editor - 16x9");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(45, 45, 48));
        
        // Top toolbar with modern styling
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(new Color(37, 37, 38));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 60, 60)));
        
        JButton loadTilesBtn = createStyledButton("📁 Load Tiles", new Color(0, 120, 212));
        JButton exportBtn = createStyledButton("💾 Export Map", new Color(16, 124, 16));
        JButton clearBtn = createStyledButton("🗑️ Clear Map", new Color(232, 17, 35));
        
        loadTilesBtn.addActionListener(e -> loadTiles());
        exportBtn.addActionListener(e -> exportMap());
        clearBtn.addActionListener(e -> clearMap());
        
        toolbar.add(loadTilesBtn);
        toolbar.add(exportBtn);
        toolbar.add(clearBtn);
        add(toolbar, BorderLayout.NORTH);
        
        // Left panel - Tileset (bigger)
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(new Color(37, 37, 38));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel tilesetLabel = new JLabel("TILESET");
        tilesetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tilesetLabel.setForeground(new Color(204, 204, 204));
        tilesetLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        leftPanel.add(tilesetLabel, BorderLayout.NORTH);
        
        tilesetPanel = new JPanel();
        tilesetPanel.setLayout(new BoxLayout(tilesetPanel, BoxLayout.Y_AXIS));
        tilesetPanel.setBackground(new Color(37, 37, 38));
        
        JScrollPane tilesetScroll = new JScrollPane(tilesetPanel);
        tilesetScroll.setPreferredSize(new Dimension(280, 500));
        tilesetScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        tilesetScroll.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(tilesetScroll, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        
        // Center - Map Canvas
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(45, 45, 48));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel mapLabel = new JLabel("MAP EDITOR (16 × 9)");
        mapLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mapLabel.setForeground(new Color(204, 204, 204));
        mapLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        centerPanel.add(mapLabel, BorderLayout.NORTH);
        
        mapCanvas = new MapCanvas();
        JPanel canvasWrapper = new JPanel(new GridBagLayout());
        canvasWrapper.setBackground(new Color(30, 30, 30));
        canvasWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        canvasWrapper.add(mapCanvas);
        
        centerPanel.add(canvasWrapper, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(37, 37, 38));
        statusPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(60, 60, 60)));
        
        statusLabel = new JLabel("🎨 Load tiles to begin editing");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(204, 204, 204));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 10));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadTiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "gif", "bmp"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        // Scale to 24x24 if needed
                        BufferedImage scaled = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = scaled.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.drawImage(img, 0, 0, TILE_SIZE, TILE_SIZE, null);
                        g2d.dispose();
                        
                        tileImages.add(scaled);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updateTilesetPanel();
            statusLabel.setText("✅ Loaded " + tileImages.size() + " tiles successfully");
        }
    }
    
    private void updateTilesetPanel() {
        tilesetPanel.removeAll();
        tilesetPanel.setBackground(new Color(37, 37, 38));
        
        for (int i = 0; i < tileImages.size(); i++) {
            final int index = i;
            JPanel tilePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw tile preview (larger)
                    g2d.drawImage(tileImages.get(index), 15, 10, TILE_SIZE * 2, TILE_SIZE * 2, null);
                    
                    // Draw selection highlight
                    if (index == selectedTile) {
                        g2d.setColor(new Color(0, 120, 212));
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawRect(12, 7, TILE_SIZE * 2 + 6, TILE_SIZE * 2 + 6);
                        
                        // Selected indicator
                        g2d.fillRect(0, 0, 5, getHeight());
                    }
                }
            };
            
            tilePanel.setPreferredSize(new Dimension(250, 70));
            tilePanel.setMaximumSize(new Dimension(250, 70));
            tilePanel.setMinimumSize(new Dimension(250, 70));
            tilePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
            tilePanel.setBackground(index == selectedTile ? new Color(45, 45, 48) : new Color(37, 37, 38));
            tilePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tilePanel.setLayout(null);
            tilePanel.setOpaque(true);
            
            // Tile label
            JLabel label = new JLabel("Tile " + i);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(new Color(204, 204, 204));
            label.setBounds(TILE_SIZE * 2 + 30, 10, 100, 20);
            tilePanel.add(label);
            
            // Tile info
            JLabel infoLabel = new JLabel("24×24 px");
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            infoLabel.setForeground(new Color(150, 150, 150));
            infoLabel.setBounds(TILE_SIZE * 2 + 30, 30, 100, 20);
            tilePanel.add(infoLabel);
            
            tilePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedTile = index;
                    updateTilesetPanel();
                    statusLabel.setText("✏️ Selected: Tile " + index + " - Click on map to paint");
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (index != selectedTile) {
                        tilePanel.setBackground(new Color(50, 50, 52));
                        tilePanel.repaint();
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (index != selectedTile) {
                        tilePanel.setBackground(new Color(37, 37, 38));
                        tilePanel.repaint();
                    }
                }
            });
            
            tilesetPanel.add(tilePanel);
        }
        
        tilesetPanel.revalidate();
        tilesetPanel.repaint();
    }
    
    private void clearMap() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                tileMap[y][x] = -1;
            }
        }
        mapCanvas.repaint();
        statusLabel.setText("🗑️ Map cleared - Ready to create new map");
    }
    
    private void exportMap() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            try (PrintWriter writer = new PrintWriter(file)) {
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    for (int x = 0; x < MAP_WIDTH; x++) {
                        writer.print(tileMap[y][x]);
                        if (x < MAP_WIDTH - 1) {
                            writer.print(" ");
                        }
                    }
                    writer.println();
                }
                statusLabel.setText("💾 Map exported successfully to " + file.getName());
                JOptionPane.showMessageDialog(this, 
                    "Map exported successfully!\n\nFile: " + file.getName(), 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting map: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class MapCanvas extends JPanel {
        public MapCanvas() {
            setPreferredSize(new Dimension(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE));
            setBackground(new Color(28, 28, 30));
            setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMouseAction(e);
                }
            });
            
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    handleMouseAction(e);
                }
            });
        }
        
        private void handleMouseAction(MouseEvent e) {
            if (tileImages.isEmpty()) return;
            
            int x = e.getX() / TILE_SIZE;
            int y = e.getY() / TILE_SIZE;
            
            if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    tileMap[y][x] = selectedTile;
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    tileMap[y][x] = -1; // Erase
                }
                repaint();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw tiles
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    int tileIndex = tileMap[y][x];
                    if (tileIndex >= 0 && tileIndex < tileImages.size()) {
                        g2d.drawImage(tileImages.get(tileIndex), x * TILE_SIZE, y * TILE_SIZE, null);
                    }
                }
            }
            
            // Draw grid
            g2d.setColor(new Color(60, 60, 60, 100));
            for (int x = 0; x <= MAP_WIDTH; x++) {
                g2d.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
            }
            for (int y = 0; y <= MAP_HEIGHT; y++) {
                g2d.drawLine(0, y * TILE_SIZE, MAP_WIDTH * TILE_SIZE, y * TILE_SIZE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TileMapEditor());
    }
}
