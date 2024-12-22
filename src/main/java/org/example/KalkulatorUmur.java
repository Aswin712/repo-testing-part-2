package org.example;

import javax.swing.*; // Import untuk komponen GUI dari Swing
import java.awt.*; // Import untuk layout manager dan komponen lainnya
import java.io.*; // Import untuk operasi file (pembacaan dan penulisan)
import java.time.LocalDate; // Import untuk manipulasi tanggal
import java.time.Period; // Import untuk menghitung selisih waktu
import java.time.format.DateTimeFormatter; // Import untuk memformat tanggal
import java.util.ArrayList; // Import untuk menggunakan ArrayList
import java.util.List; // Import untuk menggunakan List
import java.util.regex.Pattern; // Import untuk menggunakan pola regex

public class KalkulatorUmur {

    // Nama file untuk menyimpan data
    private static final String FILE_NAME = "data.txt";
    // Pola regex untuk validasi nama hanya berupa huruf dan spasi
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    public static void main(String[] args) {
        // Menjalankan aplikasi di thread Event Dispatch Thread untuk keamanan UI
        SwingUtilities.invokeLater(() -> {
            // Membuat jendela aplikasi dengan judul "Aplikasi Penghitung Umur"
            JFrame frame = new JFrame("Aplikasi Penghitung Umur");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Menutup aplikasi ketika jendela ditutup
            frame.setSize(500, 400); // Ukuran jendela aplikasi

            // Membuat panel dengan GridLayout (6 baris, 2 kolom) untuk menampung elemen-elemen GUI
            JPanel panel = new JPanel(new GridLayout(6, 2));

            // Elemen input nama dan tanggal lahir
            JLabel nameLabel = new JLabel("Nama:"); // Label untuk nama
            JTextField nameField = new JTextField(); // Field input untuk nama

            JLabel dobLabel = new JLabel("Tanggal Lahir (yyyy-MM-dd):"); // Label untuk tanggal lahir
            JTextField dobField = new JTextField(); // Field input untuk tanggal lahir

            // Tombol untuk menghitung umur
            JButton calculateButton = new JButton("Hitung Umur");
            JLabel resultLabel = new JLabel("Umur:"); // Label untuk menampilkan hasil umur

            // Tombol untuk operasi CRUD (Create, Read, Update, Delete)
            JButton saveButton = new JButton("Simpan Data");
            JButton viewButton = new JButton("Lihat Data");
            JButton updateButton = new JButton("Update Data");
            JButton deleteButton = new JButton("Hapus Data");

            // Area untuk menampilkan data dari file
            JTextArea displayArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(displayArea); // Membuat scroll pane untuk display area
            displayArea.setEditable(false); // Menonaktifkan edit langsung pada display area

            // Event listener untuk tombol hitung umur
            calculateButton.addActionListener(e -> {
                try {
                    // Mengambil input tanggal lahir
                    String dobInput = dobField.getText();
                    // Mengubah input string menjadi objek LocalDate
                    LocalDate dob = LocalDate.parse(dobInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalDate now = LocalDate.now(); // Mendapatkan tanggal saat ini
                    // Menghitung selisih umur dalam tahun, bulan, dan hari
                    Period age = Period.between(dob, now);
                    // Menampilkan hasil umur
                    resultLabel.setText("Age: " + age.getYears() + " Tahun");
                } catch (Exception ex) {
                    // Menampilkan pesan kesalahan jika format tanggal salah
                    JOptionPane.showMessageDialog(frame, "Invalid Date Format! Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Event listener untuk menyimpan data
            saveButton.addActionListener(e -> {
                try {
                    String name = nameField.getText(); // Mengambil input nama
                    String dob = dobField.getText(); // Mengambil input tanggal lahir
                    // Validasi input nama dan tanggal lahir
                    if (name.isEmpty() || dob.isEmpty() || !NAME_PATTERN.matcher(name).matches()) {
                        throw new IllegalArgumentException("Name must be alphabetic and Date of Birth cannot be empty.");
                    }
                    saveRecord(name, dob); // Memanggil fungsi untuk menyimpan data
                    // Menampilkan pesan jika data berhasil disimpan
                    JOptionPane.showMessageDialog(frame, "Record Saved Successfully!");
                } catch (Exception ex) {
                    // Menampilkan pesan kesalahan jika ada kesalahan pada input
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Event listener untuk membaca data dari file dan menampilkan ke area tampilan
            viewButton.addActionListener(e -> {
                displayArea.setText(""); // Membersihkan area tampilan
                List<String> records = readRecords(); // Membaca data dari file
                records.forEach(record -> displayArea.append(record + "\n")); // Menampilkan setiap record
            });

            // Event listener untuk memperbarui data
            updateButton.addActionListener(e -> {
                try {
                    String name = nameField.getText(); // Mengambil input nama
                    String newDob = dobField.getText(); // Mengambil input tanggal lahir yang baru
                    // Validasi input
                    if (name.isEmpty() || newDob.isEmpty() || !NAME_PATTERN.matcher(name).matches()) {
                        throw new IllegalArgumentException("Name must be alphabetic and new Date of Birth cannot be empty.");
                    }
                    updateRecord(name, newDob); // Memanggil fungsi untuk memperbarui data
                    JOptionPane.showMessageDialog(frame, "Record Updated Successfully!"); // Pesan sukses
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Pesan kesalahan
                }
            });

            // Event listener untuk menghapus data
            deleteButton.addActionListener(e -> {
                try {
                    String name = nameField.getText(); // Mengambil input nama
                    // Validasi input
                    if (name.isEmpty() || !NAME_PATTERN.matcher(name).matches()) {
                        throw new IllegalArgumentException("Name must be alphabetic and cannot be empty.");
                    }
                    deleteRecord(name); // Memanggil fungsi untuk menghapus data
                    JOptionPane.showMessageDialog(frame, "Record Deleted Successfully!"); // Pesan sukses
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Pesan kesalahan
                }
            });

            // Menambahkan elemen-elemen ke dalam panel
            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(dobLabel);
            panel.add(dobField);
            panel.add(calculateButton);
            panel.add(resultLabel);
            panel.add(saveButton);
            panel.add(viewButton);
            panel.add(updateButton);
            panel.add(deleteButton);

            // Menambahkan panel dan scroll pane ke frame
            frame.add(panel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null); // Menempatkan frame di tengah layar
            frame.setVisible(true); // Menampilkan frame
        });
    }

    // Metode untuk menyimpan data ke file
    public static void saveRecord(String name, String dob) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(name + "," + dob); // Menulis data ke file dalam format nama,tanggal lahir
            writer.newLine(); // Menambahkan baris baru setelah menulis data
        }
    }

    // Metode untuk membaca semua data dari file
    public static List<String> readRecords() {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            // Membaca setiap baris dari file
            while ((line = reader.readLine()) != null) {
                records.add(line); // Menambahkan baris ke dalam list
            }
        } catch (IOException ex) {
            records.add("Error reading records: " + ex.getMessage()); // Menangani kesalahan pembacaan file
        }
        return records; // Mengembalikan semua data dalam bentuk List
    }

    // Metode untuk memperbarui data di file
    public static void updateRecord(String name, String newDob) throws IOException {
        List<String> records = readRecords(); // Membaca semua data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            boolean found = false;
            // Menulis ulang data ke file
            for (String record : records) {
                String[] parts = record.split(","); // Memisahkan nama dan tanggal lahir
                if (parts[0].equalsIgnoreCase(name)) { // Memeriksa jika nama cocok
                    writer.write(name + "," + newDob); // Menulis data baru
                    found = true; // Menandai bahwa data ditemukan
                } else {
                    writer.write(record); // Menulis data lama jika tidak cocok
                }
                writer.newLine(); // Menambahkan baris baru
            }
            if (!found) {
                throw new IllegalArgumentException("Record not found for update."); // Jika nama tidak ditemukan
            }
        }
    }

    // Metode untuk menghapus data dari file
    public static void deleteRecord(String name) throws IOException {
        List<String> records = readRecords(); // Membaca semua data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            boolean found = false;
            // Menulis ulang data ke file tanpa data yang akan dihapus
            for (String record : records) {
                String[] parts = record.split(","); // Memisahkan nama dan tanggal lahir
                if (!parts[0].equalsIgnoreCase(name)) { // Jika nama tidak cocok, data ditulis kembali
                    writer.write(record);
                    writer.newLine(); // Menambahkan baris baru
                } else {
                    found = true; // Menandai bahwa data ditemukan dan dihapus
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Record not found for deletion."); // Jika nama tidak ditemukan
            }
        }
    }
}
