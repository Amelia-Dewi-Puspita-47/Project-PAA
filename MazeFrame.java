package mazeframe;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Random;
//import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JSlider;

public class MazeFrame extends JFrame {
    private MazePanel mazePanel;
    private final JButton tombol1;
    private final JButton tombol2;
    private final JButton tombol3;
    private final JButton tombol4;
    private final JButton tombol5;
    private final JButton tombol6;
    private final JButton tombol7;
    private final JButton tombol8;
    private final JSlider slider;
   // private final JLabel label;
    private double distanceThreshold = 0; //nilai awal slider, saat JSlider digeser, nilai 'distanceThreshold' akan diperbarui

    public MazeFrame() {
        setTitle("Seek and Hide Droid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(null); // Menggunakan layout null

        mazePanel = new MazePanel();
        mazePanel.setBounds(0, 0, 800, 500); // Atur posisi dan ukuran panel
        add(mazePanel);

        tombol1 = new JButton("Acak Peta");
        tombol1.setBounds(500, 40, 200, 30);
        tombol1.addActionListener((ActionEvent e) -> {
            mazePanel.generateMaze(); //agar peta teracak saat tombol ditekan
        }); add(tombol1);

        tombol2 = new JButton("Acak Droid");
        tombol2.setBounds(500, 80, 200, 30);
        tombol2.addActionListener((ActionEvent e) -> {
            mazePanel.generateDroids(); // untuk mengacak posisi droid saat tombol ditekan
        }); add(tombol2);
        
        tombol3 = new JButton("Tambah Droid Merah");
        tombol3.setBounds(500, 120, 200, 30);
        tombol3.addActionListener((ActionEvent e) -> {
            if (mazePanel.getRedDroidsCount() < 3) { // Batas maksimum droid merah adalah 3
                mazePanel.addRedDroid(); // Memanggil metode addRedDroid() pada objek mazePanel
            }
        }); add(tombol3);
        
        tombol4 = new JButton("Sudut Pandang Droid Merah");
        tombol4.setBounds(500,160, 200, 30);
        tombol4.addActionListener((ActionEvent e) -> {
            mazePanel.povSeeker();
        }); add(tombol4);
        
        tombol5 = new JButton("Sudut Pandang Droid Hijau");
        tombol5.setBounds(500,200, 200, 30);
        tombol5.addActionListener((ActionEvent e) -> {
            mazePanel.povHider();
        }); add(tombol5);
        
        tombol6 = new JButton("Start");
        tombol6.setBounds(500,240, 200, 30);
        tombol6.addActionListener((ActionEvent e) -> {
            mazePanel.startMovingDroids();
        }); add(tombol6);
        
        tombol7 = new JButton("Pause");
        tombol7.setBounds(500,280, 200, 30);
        tombol7.addActionListener((ActionEvent e) -> {
            // Tambahkan logika di sini untuk pause
        }); add(tombol7);
        
        tombol8 = new JButton("Resume");
        tombol8.setBounds(500,320, 200, 30);
        tombol8.addActionListener((ActionEvent e) -> {
            // Tambahkan logika di sini untuk resume
        }); add(tombol8);
        
        /*label = new JLabel("Atur Jarak Pandang Droid Hijau");
        label.setBounds(500, 360, 200, 30);  // Atur posisi dan ukuran JLabel
        add(label);*/
        
        slider = new JSlider(0, 5, 0);
        slider.setBackground(Color.WHITE);
        JPanel sliderPanel = new JPanel();
        sliderPanel.setBounds(500, 360, 200, 50);
        sliderPanel.add(slider);
        add(sliderPanel);
        // Atur properti lainnya
        slider.setMajorTickSpacing(1);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            double newValue = source.getValue();
            distanceThreshold = newValue;
        });

        setVisible(true);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeFrame frame = new MazeFrame();
            frame.setVisible(true);
        });
    }
}

final class MazePanel extends JPanel {
    private static final int CELL_SIZE = 30;
    private static final int MAZE_SIZE = 13;

    private final boolean[][] cells;
    private final int[][] droids;
    private int redDroidsCount; // Jumlah droid merah

    public MazePanel() {
        setPreferredSize(new Dimension(CELL_SIZE * MAZE_SIZE, CELL_SIZE * MAZE_SIZE));
        setBackground(Color.WHITE);
        setLayout(null); // Menggunakan layout null

        cells = new boolean[MAZE_SIZE][MAZE_SIZE];
        droids = new int[5][2]; // array untuk menyimpan posisi droids (2 droid awal + maksimum 3 droid merah)
        redDroidsCount = 0;

        generateMaze();
        generateDroids();
        
    }
    
    public void generateMaze() {
        // Setel ulang semua sel menjadi dinding
        for (int x = 0; x < MAZE_SIZE; x++) {
            for (int y = 0; y < MAZE_SIZE; y++) {
                cells[x][y] = true;
            }
        }

        Random random = new Random();
        generateMaze(random.nextInt(MAZE_SIZE / 2) * 2 + 1, random.nextInt(MAZE_SIZE / 2) * 2 + 1);
        repaint();
    }
    
    private void generateMaze(int x, int y) {
        cells[x][y] = false; // Tandai sel sebagai jalan

        int[] directions = {0, 1, 2, 3}; //mewakili arah, 1 keatas, 2 kekanan, 3 kebawah, 4 kekiri
        shuffleArray(directions); //Acak urutan arah

        for (int direction : directions) {
            int nx = x; // Variabel nx dan ny digunakan untuk menyimpan koordinat x dan y dari sel yang akan diproses selanjutnya.
            int ny = y;
               
            //Setiap if statement memeriksa apakah memungkinkan untuk membuat jalan ke arah yang ditentukan dengan memeriksa kondisi seperti apakah sel tetangga sudah dijelajahi (cells[nx][ny]) dan memeriksa batasan labirin agar tidak melewati batas (x > 1, y > 1, x < MAZE_SIZE - 2, y < MAZE_SIZE - 2).
            if (direction == 0 && y > 1 && cells[x][y - 2]) {
                ny -= 2;
                cells[x][y - 1] = false;
            } else if (direction == 1 && y < MAZE_SIZE - 2 && cells[x][y + 2]) {
                ny += 2;
                cells[x][y + 1] = false;
            } else if (direction == 2 && x > 1 && cells[x - 2][y]) {
                nx -= 2;
                cells[x - 1][y] = false;
            } else if (direction == 3 && x < MAZE_SIZE - 2 && cells[x + 2][y]) {
                nx += 2;
                cells[x + 1][y] = false;
            }

            if (cells[nx][ny]) {
                generateMaze(nx, ny);
            }
        }
    }
    
    private void shuffleArray(int[] array) { //mengiterasi setiap arah dalam array directions:
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public void generateDroids() {
        Random random = new Random();
        // Setel ulang posisi droid agar berada di luar labirin
        droids[0][0] = -1;
        droids[0][1] = -1;
        droids[1][0] = -1;
        droids[1][1] = -1;

        // Hasilkan posisi acak untuk dua droid
        int x1, y1, x2, y2;
        do {
            x1 = random.nextInt(MAZE_SIZE);
            y1 = random.nextInt(MAZE_SIZE);
        } while (cells[x1][y1]); // Terus menghasilkan hingga posisinya bukan tembok
        do {
            x2 = random.nextInt(MAZE_SIZE);
            y2 = random.nextInt(MAZE_SIZE);
        } while (cells[x2][y2] || (x2 == x1 && y2 == y1)); // Terus generate sampai posisinya bukan tembok dan tidak sama dengan droid pertama

        // Atur posisi droid
        droids[0][0] = x1;
        droids[0][1] = y1;
        droids[1][0] = x2;
        droids[1][1] = y2;

        repaint();
    }
    
    public int getRedDroidsCount() { //untuk mengembalikan jumlah droid merah yang ada dalam labirin
        return redDroidsCount;
    }
    
    public void addRedDroid() {
        Random random = new Random();

        int x, y;
        do {
            x = random.nextInt(MAZE_SIZE);
            y = random.nextInt(MAZE_SIZE);
        } 
        while (cells[x][y] || isOccupied(x, y)); //terus generate sampai posisinya bukan tembok dan tidak ditempati droid lain

        //Setelah posisi yang valid untuk droid merah ditemukan, atur posisi droid merah
        //droid pertama dan kedua diindeks oleh droids[0] dan droids[1], sehingga droid merah baru dimulai dari indeks 2 dan seterusnya
        droids[redDroidsCount + 2][0] = x;
        droids[redDroidsCount + 2][1] = y;
        redDroidsCount++; //untuk mencerminkan penambahan droid merah baru

        repaint();
    }
    
    public void povSeeker() { //sudut pandang droid merah
        if (droids[0][0] >= 0 && droids[0][1] >= 0) {
            droids[0][0] = -MAZE_SIZE;
            droids[0][1] = -MAZE_SIZE;
            repaint();
        }
    }
    
    public void povHider() {
        double distanceThreshold = 3.0; //menetapkan ambang batas jarak untuk bidang tampilan

        //hapus semua warna sebelumnya
        for (Component component : getComponents()) {
            JPanel cell = (JPanel) component;
            cell.setBackground(Color.white); //tetapkan warna latar belakang ke warna jalur
            cell.setOpaque(true); //setel opaqueness ke true untuk memulihkan opaqueness yang diubah sebelumnya
        }

        //membuat lingkaran sudut pandang droid hijau yang baru
        double radius = distanceThreshold; //jarak sudut pandang droid hijau
        int centerX = droids[0][0];
        int centerY = droids[0][1];

        //mengubah warna sel-sel yang berada di luar sudut pandang droid hijau menjadi hitam
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                JPanel cell = (JPanel) getComponent(i * MAZE_SIZE + j);
                double distance = Math.sqrt(Math.pow(i - centerX, 2) + Math.pow(j - centerY, 2));
                if (distance > radius) {
                    cell.setBackground(Color.blue);
                }
                if (distance <= radius) {
                    if (cells[i][j]) {
                        cell.setBackground(Color.black);
                    }
                }
            }
        }

        //mengubah warna sel-sel yang telah dikunjungi oleh droid merah menjadi transparan
        for (int i = 2; i < redDroidsCount + 2; i++) {
            int x = droids[i][0];
            int y = droids[i][1];
            double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
            if (distance <= radius) {
                JPanel cell = (JPanel) getComponent(x * MAZE_SIZE + y);
                cell.setOpaque(false);
            }
        }
        repaint();
    }
    
    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < redDroidsCount + 2; i++) {
            if (droids[i][0] == x && droids[i][1] == y) {
                return true;
            }
        }
        return false;
    }
    
    public void startMovingDroids() {
        Timer timer = new Timer(100, (ActionEvent e) -> {
            Random random = new Random();
            
            for (int i = 0; i < 2; i++) {
                int currentX = droids[i][0];
                int currentY = droids[i][1];
        
            //Variabel currentX dan currentY menyimpan posisi saat ini dari droid.                
                int newX = currentX;
                int newY = currentY;
                
                if (i == 0) {
                    // Algoritma pergerakan droid hijau
                    int direction = random.nextInt(4);
                    if (direction == 0 && currentX > 0) {
                        newX--;
                    } else if (direction == 1 && currentX < MAZE_SIZE - 1) {
                        newX++;
                    } else if (direction == 2 && currentY > 0) {
                        newY--;
                    } else if (direction == 3 && currentY < MAZE_SIZE - 1) {
                        newY++;
                    }
                } else {
                    searchGreenDroidDFS():
                }
                
                if (isValidPosition(newX, newY)) {
                    droids[i][0] = newX;
                    droids[i][1] = newY;
                }
            }
            
            repaint();
        });

        timer.start();
    }

    private boolean isValidPosition(int x, int y) {
        if (x < 0 || y < 0 || x >= MAZE_SIZE || y >= MAZE_SIZE) {
            return false;
        }

        return !cells[x][y];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.translate(30, 30);
        
        g.setColor(Color.BLACK);
        for (int x = 0; x < MAZE_SIZE; x++) {
            for (int y = 0; y < MAZE_SIZE; y++) {
                if (cells[x][y]) {
                    int posX = x * CELL_SIZE;
                    int posY = y * CELL_SIZE;
                    g.fillRect(posX, posY, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        for (int i = 0; i < redDroidsCount + 2; i++) {
            if (i == 0) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }
            g.fillOval(droids[i][0] * CELL_SIZE, droids[i][1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
    }

    private void searchGreenDroidDFS() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}