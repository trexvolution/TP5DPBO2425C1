import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductMenu extends JFrame {
    public static void main(String[] args) {
        // buat object window
        ProductMenu menu = new ProductMenu();

        // atur ukuran window
        menu.setSize(700, 600);

        // letakkan window di tengah layar
        menu.setLocationRelativeTo(null);

        // isi window
        menu.setContentPane(menu.mainPanel);

        // ubah warna background
        menu.getContentPane().setBackground(Color.WHITE);

        // tampilkan window
        menu.setVisible(true);

        // agar program ikut berhenti saat window diclose
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua produk
    private ArrayList<Product> listProduct;
    private Database database;

    private JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField hargaField;
    private JTable productTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox<String> kategoriComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel namaLabel;
    private JLabel hargaLabel;
    private JLabel kategoriLabel;
    private JComboBox SeriComboBox;
    private JLabel SeriLabel;

    // constructor
    public ProductMenu() {
        // inisialisasi listProduct
        listProduct = new ArrayList<>();

        //Buat objek database
        database = new Database();

        // isi listProduct
        populateList();

        // isi tabel produk
        productTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] kategoriData = {"???", "Pokemon", "Trainer", "Item", "Tool", "Stadium", "Energy" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // Isi Serial Combo
        String[] SerialSet = {"???", "XYS", "AS", "GS", "SVS"};
        SeriComboBox.setModel(new DefaultComboBoxModel<>(SerialSet));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1){
                    insertData();
                } else {
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pastikan ada data yang dipilih
                if (selectedIndex != -1) {
                    // tampilkan konfirmasi
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Apakah Anda yakin ingin menghapus data ini?",
                            "Konfirmasi Hapus",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    // jika user memilih YES, hapus data
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus terlebih dahulu!");
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedIndex = productTable.getSelectedRow();

                if (selectedIndex != -1) {
                    // Ambil nilai dari tabel
                    String curId = productTable.getModel().getValueAt(selectedIndex, 1).toString();
                    String curNama = productTable.getModel().getValueAt(selectedIndex, 2).toString();
                    String curHarga = productTable.getModel().getValueAt(selectedIndex, 3).toString();
                    String curKategori = productTable.getModel().getValueAt(selectedIndex, 4).toString();
                    String curSerial = productTable.getModel().getValueAt(selectedIndex, 5).toString();

                    // Isi field input
                    idField.setText(curId);
                    namaField.setText(curNama);
                    hargaField.setText(curHarga);
                    kategoriComboBox.setSelectedItem(curKategori);
                    SeriComboBox.setSelectedItem(curSerial);

                    // Ganti tombol jadi Update
                    addUpdateButton.setText("Update");
                    deleteButton.setVisible(true);
                }
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] cols = {"No", "ID Produk", "Nama", "Harga", "Kategori", "Seri"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        // isi tabel dengan listProduct
        try{
            ResultSet resultSet = database.selectQuery("SELECT * FROM product");

            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = i + 1;
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("harga");
                row[4] = resultSet.getString("kategori");
                row[5] = resultSet.getString("seri");
                tmp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tmp; // return juga harus diganti
    }

    public void insertData() {
        try {
            // ambil value dari textfield dan combobox
            String id = idField.getText();
            String nama = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            String serial = SeriComboBox.getSelectedItem().toString();


            // cek apakah ID sudah ada di database
            String checkQuery = "SELECT * FROM product WHERE id = '" + id + "'";
            ResultSet rs = database.selectQuery(checkQuery);

            if (rs.next()) {
                // jika ID sudah ditemukan
                JOptionPane.showMessageDialog(null,
                        "ID '" + id + "' sudah ada di database!",
                        "Error: Duplikat ID",
                        JOptionPane.ERROR_MESSAGE);
                return; // hentikan proses insert
            }

            // tambahkan data ke dalam list
            String sqlQuery = "INSERT INTO product VALUES ('" + id + "', '" + nama + "', " + harga + ", '" +kategori+"', '" + serial + "')";

            database.InsertUpdateDeleteQuery(sqlQuery);

            // update tabel
            productTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert Berhasil");
            JOptionPane.showMessageDialog(null, "Data Berhasil Ditambahkan");
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Harga Berupa Angka", "Error",
            JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan saat memeriksa ID " + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateData() {
        try {
            // ambil data dari form
            String id = idField.getText();
            String nama = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            String serial = SeriComboBox.getSelectedItem().toString();

            // ubah data produk di list
            String sqlQuery = "UPDATE product SET " + "nama = '" + nama + "', " + "harga = " + harga + ", "
                    + "kategori = '" + kategori + "', " + "seri = '" + serial + "' " + "WHERE id = '" + id + "'";

            database.InsertUpdateDeleteQuery(sqlQuery);

            // update tabel
            productTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update Berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Harga harus berupa angka", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteData() {

        try {
            // hapus data dari list
            String id = idField.getText();

            String sqlQuery = "DELETE FROM product WHERE id = '" + id + "'";

            database.InsertUpdateDeleteQuery(sqlQuery);

            // update tabel
            productTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Delete Berhasil");
            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menghapus data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        idField.setText("");
        namaField.setText("");
        hargaField.setText("");
        kategoriComboBox.setSelectedItem(0);
        SeriComboBox.setSelectedItem(0);

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }

    // panggil prosedur ini untuk mengisi list produk
    private void populateList() {
        listProduct.add(new Product("P001", "Budew", 45000.0, "Pokemon", "SVS"));
        listProduct.add(new Product("T001", "Arvin", 40000, "Trainer", "SVS"));
        listProduct.add(new Product("I001", "Nest Ball", 25000, "Item", "SVS"));
        listProduct.add(new Product("TO001", "Jimat Keberanian", 15000, "Tool", "SVS"));
        listProduct.add(new Product("S001", "Kota Mukun", 15000, "Stadium", "SVS"));
        listProduct.add(new Product("E001", "Energi Lumian", 25000, "Energi", "SVS"));
    }
}