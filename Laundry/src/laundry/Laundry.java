/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laundry;

import java.sql.*;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angga
 */
public class Laundry {
    
    static Connection conn;
    static Statement stm;
    
    public static void main(String[] args) {
        
        // instansiasi kelas database dan method connect
        conn = new Database().connect();
        
        try{
           stm = conn.createStatement(); 
        }catch(SQLException e){
            System.err.println(e);
        }
        
        Scanner inputInt = new Scanner(System.in);
        
        int pilih;
        boolean ulang = true;
        
        while(ulang){
        System.out.println("WELCOME TO MY LAUNDRY");
        System.out.println("======Rp8000/Kg======");
        System.out.println("1. Cuci laundry");
        System.out.println("2. Tracking laundry");
        System.out.println("3. Update status");
        System.out.println("4. Lihat pesanan");
        System.out.println("5. Hapus customer");
        System.out.println("6. Exit");
        System.out.print("Pilih (1-6) : ");
        pilih = inputInt.nextInt();
        
        switch(pilih){
            case 1:
                laundry();
                ulang = ulangi("Ke menu utama? [y/t] : ");
                break;
            case 2:
                tracking();
                ulang = ulangi("Ke menu utama? [y/t] : ");
                break;
            case 3:
                updateStatus();
                ulang = ulangi("Ke menu utama? [y/t] : ");
                break;
            case 4:
                lihatPesanan();
                ulang = ulangi("Ke menu utama? [y/t] : ");
                break;
            case 5:
                hapusPesanan();
                ulang = ulangi("Ke menu utama? [y/t] : ");
                break;
            default:
                System.exit(0);
        }
        
        }
   
    }
    
    static void laundry(){
        
        Scanner inputInt = new Scanner(System.in);
        Scanner inputStr = new Scanner(System.in);
        
        String nama,alamat,tanggal,wa;
        int total_laundry,harga,bayar,kembalian = 0;
        
        System.out.println("=> CUCI LAUNDRY <=");
        
        System.out.print("Nama : ");
        nama = inputStr.nextLine();
        
        System.out.print("Alamat : ");
        alamat = inputStr.nextLine();
        
        System.out.print("No. WA : 0");
        wa = inputStr.next();
        
        System.out.print("Total Cucian (KG) : ");
        total_laundry = inputStr.nextInt();
        
        harga = total_laundry * 8000;
        System.err.println("Harga : "+harga);
        
        System.out.print("Bayar : ");
        bayar = inputInt.nextInt();
        
        if(bayar<harga){
            System.err.println("Uang Tidak Cukup!");
            return;
        }
        
        kembalian = bayar-harga;
        System.err.println("Kembalian : "+kembalian);
        
        tanggal = dateNow();
        String kode = kode_pesanan();
        
        String sql = "INSERT INTO customers VALUES('"+kode+"','"+nama+"','"+alamat+"','+62"+wa+"','"+total_laundry+"','"+tanggal+"','"+harga+"','"+bayar+"','pendaftaran')";
        
        try{
            // query insert ke database
            stm.executeUpdate(sql);
            // jika data berhasil input ke database
            System.out.println("Transaksi BERHASIL diproses!\nEstimasi selesai dalam waktu 2-5 Hari.");
            System.out.println("Kode Pesanan Anda "+kode);
        } catch (SQLException e){
            System.err.println("Data gagal diupdate!\n->"+e);
        }
    }
    
    static void tracking(){
        System.out.println("=> TRACKING PESANAN <=");
        cari();
    }
    
    static void updateStatus(){
        ResultSet hasil;
        Scanner inputStr = new Scanner(System.in);
        String kode = null;
        String status = null;
        String tanya;
        System.out.println("=> UPDATE STATUS PESANAN <=");
        cari();
        System.out.print("Kode Pesanan : R-");
        kode = inputStr.next();
        // cari pesanan dengan kode yang diinputkan
        String sql = "SELECT * FROM customers WHERE kode_pesanan='R-"+kode+"'";
        try{
            hasil = stm.executeQuery(sql);
            while(hasil.next()){
                System.out.println("Status sebelumnya "+(status=hasil.getString("status"))); // variabel status untuk mengecek status terakhir
            }
        }catch(SQLException e){
            System.out.println(e);
        }
        // jika variabel status kosong, berarti kode yang dimasukan salah
        if(status==null){
            System.err.println("Kode tidak ditemukan!");
            return;
        } else if(status.equalsIgnoreCase("pendaftaran")){
            System.out.print("Ubah status menjadi 'PROSES CUCI'? [y/t] : ");
            tanya = inputStr.next();
            if(tanya.equalsIgnoreCase("y")){
                status = "proses cuci";
            } else {
                return;
            }
        } else if(status.equalsIgnoreCase("proses cuci")){
            System.out.print("Ubah status menjadi 'BISA AMBIL'? [y/t] : ");
            tanya = inputStr.next();
            if(tanya.equalsIgnoreCase("y")){
                status = "bisa ambil";
            } else {
                return;
            }
        } else if(status.equalsIgnoreCase("selesai")){
            System.out.println("Pesanan tidak bisa diupdate");
            return;
        } else {
            System.out.print("Yakin ingin menyelesaikan pesanan ini? [y/t] ");
            tanya = inputStr.next();
            if(tanya.equalsIgnoreCase("y")){
                status = "selesai";
            } else {
                return;
            }
        }
        
        String update_pesanan = "UPDATE customers SET status='"+status+"' WHERE kode_pesanan='R-"+kode+"'";
        
        try{
            stm.executeUpdate(update_pesanan);
            System.out.println("Data BERHASIL diupdate.");
        }catch(SQLException e){
            System.err.println("Ada kesalahan!\n->"+e);
        }
            
    }
    
    static void lihatPesanan(){
        try {
            ResultSet rs;
            System.out.println("=> DATA CUSTOMERS MY LAUNDRY <=");
            
            String sql = "SELECT * FROM customers ORDER BY kode_pesanan DESC";
            
            rs = stm.executeQuery(sql);
            // tampilkan hasil query
            String tabel_format = "| %-2d | %-15s | %-15s | %-14s | %-13s | %-15s | %-11s |%n";
            System.out.format("+----+-----------------+-----------------+----------------+---------------+-----------------+-------------+%n");
            System.out.format("| No |      Nama       |      Alamat     |    Whatsapp    | Total laundry | Tanggal laundry |   Status    |%n");
            System.out.format("+----+-----------------+-----------------+----------------+---------------+-----------------+-------------+%n");
            int no=1;
            while(rs.next()){
                System.out.format(tabel_format,no,rs.getString("name"),rs.getString("address"),rs.getString("whatsapp"),rs.getString("total_laundy")+" Kg",rs.getString("date_start"),rs.getString("status"));
                no++;
            }
            System.out.format("+----+-----------------+-----------------+----------------+---------------+-----------------+-------------+%n");
        } catch (SQLException ex) {
            Logger.getLogger(Laundry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void hapusPesanan(){
        System.out.println("=> HAPUS DATA CUSTOMER <=");
        cari();
        ResultSet rs;
        Scanner inputStr = new Scanner(System.in);
        String kode,nama=null,confirm;
        int hapus = 0;
        System.out.print("Kode Pesanan : R-");
        kode = inputStr.next();
        
        String sql = "DELETE FROM customers WHERE kode_pesanan='R-"+kode+"'";
        try{
            rs = stm.executeQuery("SELECT name FROM customers WHERE kode_pesanan='R-"+kode+"'");
            while(rs.next()){
                nama = rs.getString("name");
            }
            if(nama!=null){
                System.out.print("Data "+nama+" akan DIHAPUS? [y/t] :");
                confirm = inputStr.next();
                if(confirm.equalsIgnoreCase("y")){
                    hapus = stm.executeUpdate(sql);
                    if(hapus>0){
                        System.out.println("Data BERHASIL Dihapus!");
                    } else {
                        System.err.println("Data GAGAL Dihapus!");
                    }
                } else {
                    System.err.println("Data GAGAL Dihapus!");
                }
                
            } else {
                System.err.println("Kode tidak ditemukan!");
            }
        }catch(SQLException e){
            System.err.println(e);
        }
    }
    
    static String kode_pesanan(){
        String kode = null;
        Calendar tanggal = Calendar.getInstance();
        kode = "R-"+tanggal.get(Calendar.SECOND)+tanggal.get(Calendar.MILLISECOND);
        return kode;
    }
    
    static void cari(){
        
        ResultSet rs;
        
        Scanner input = new Scanner(System.in);
        
        String nama,cek_nama = null;
        System.out.print("Cari Nama : ");
        nama = input.nextLine();
        
        String sql = "SELECT * FROM customers WHERE name LIKE('%"+nama+"%')";
        
        try{
            rs = stm.executeQuery(sql);
            String tbl = "| %-12s | %-13s | %-13s | %-12s |%n";
            System.out.format("+--------------+---------------+---------------+--------------+%n");
            System.out.format("| Kode Pesanan |     Nama      |     Alamat    |    Status    |%n");
            System.out.format("+--------------+---------------+---------------+--------------+%n");
            
            while(rs.next()){
                System.out.format(tbl,rs.getString("kode_pesanan"),cek_nama = rs.getString("name"),rs.getString("address"),rs.getString("status"));
            }
            if(cek_nama==null){
            System.out.format("|                 %-43s |%n","Data "+nama+" Tidak Ditemukan!");
            }
            System.out.println("+--------------+---------------+---------------+--------------+"); 
                    
        } catch(SQLException e){
            System.out.println("Ada kesalahan!\n->"+e);
        }
    }
    
    static String dateNow(){
        // Tanggal saat ini
        Calendar tanggal = Calendar.getInstance();
        String date = tanggal.get(Calendar.DATE)+"-"+tanggal.get(Calendar.MONTH)+"-"+tanggal.get(Calendar.YEAR)+" "+tanggal.get(Calendar.HOUR)+":"+tanggal.get(Calendar.MINUTE);
        return date;
    }
    
    static boolean ulangi(String pesan){
        Scanner input = new Scanner(System.in);
        String lagi;
        System.out.print(pesan);
        lagi = input.next();
        while(!lagi.equalsIgnoreCase("y") && !lagi.equalsIgnoreCase("t")){
            System.err.println("Masukan huruf Y dan T saja!");
            System.out.print(pesan);
            lagi = input.next();
        }
        return lagi.equalsIgnoreCase("y");
    }
}