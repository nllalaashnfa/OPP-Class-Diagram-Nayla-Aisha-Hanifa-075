import java.util.*;


// ABSTRACT CLASS — Transaksi                    
abstract class Transaksi {
    protected int    id;
    protected String tanggal;
    protected double jumlah;
    protected String keterangan;

    private static int idCounter = 1;

    public Transaksi(String tanggal, double jumlah, String keterangan) {
        this.id         = idCounter++;
        this.tanggal    = tanggal;
        this.jumlah     = jumlah;
        this.keterangan = keterangan;
    }

    public abstract String getTipe();

    public void tampilkanInfo() {
        System.out.printf("    [#%02d | %-6s] %s | %-30s | Rp %,.0f%n",
                id, getTipe(), tanggal, keterangan, jumlah);
    }

    public double getJumlah()    { return jumlah; }
    public String getTanggal()   { return tanggal; }
    public String getKeterangan(){ return keterangan; }
    public int    getId()        { return id; }
}

//SUBCLASS — Pemasukan                          
class Pemasukan extends Transaksi {
    private String tipe;

    public Pemasukan(String tanggal, double jumlah, String keterangan, String tipe) {
        super(tanggal, jumlah, keterangan);
        this.tipe = tipe;
    }

    @Override
    public String getTipe() { return "MASUK"; }

    public String getTipePemasukan() { return tipe; }
}

//SUBCLASS — Pengeluaran                        
class Pengeluaran extends Transaksi {
    private String kategori;

    public Pengeluaran(String tanggal, double jumlah, String keterangan, String kategori) {
        super(tanggal, jumlah, keterangan);
        this.kategori = kategori;
    }

    @Override
    public String getTipe() { return "KELUAR"; }

    public String getKategori() { return kategori; }
}

//CLASS — TargetTabungan                        
class TargetTabungan {
    private String namaTarget;
    private double targetNominal;
    private double terkumpul;

    public TargetTabungan(String namaTarget, double targetNominal) {
        this.namaTarget    = namaTarget;
        this.targetNominal = targetNominal;
        this.terkumpul     = 0;
    }

    public void tambahTabungan(double jumlah) {
        this.terkumpul += jumlah;
    }

    public void tampilkanInfo() {
        double persen = (terkumpul / targetNominal) * 100;
        double sisa   = targetNominal - terkumpul;
        String bar    = buatProgressBar(persen);
        String status = terkumpul >= targetNominal
                ? "TERCAPAI!"
                : "Sisa: Rp " + String.format("%,.0f", sisa);
        System.out.printf("    %-22s | %s %.1f%% | %s%n",
                namaTarget, bar, persen, status);
    }

    private String buatProgressBar(double persen) {
        int total  = 20;
        int terisi = (int) Math.min(persen / 100 * total, total);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < total; i++) bar.append(i < terisi ? "=" : "-");
        bar.append("]");
        return bar.toString();
    }

    public String getNamaTarget()    { return namaTarget; }
    public double getTerkumpul()     { return terkumpul; }
    public double getTargetNominal() { return targetNominal; }
    public boolean sudahTercapai()   { return terkumpul >= targetNominal; }
}


//CLASS — AnggaranBulanan                       
class AnggaranBulanan {
    private Map<String, Double> batasAnggaran;
    private Map<String, Double> realisasi;

    public AnggaranBulanan() {
        this.batasAnggaran = new LinkedHashMap<>();
        this.realisasi     = new LinkedHashMap<>();
    }

    public void setBatas(String kat, double limit) {
        batasAnggaran.put(kat, limit);
        realisasi.put(kat, 0.0);
    }

    public void tambahRealisasi(String kat, double jml) {
        realisasi.put(kat, realisasi.getOrDefault(kat, 0.0) + jml);
    }

    public boolean isMelebihiBatas(String kat) {
        if (!batasAnggaran.containsKey(kat)) return false;
        return realisasi.getOrDefault(kat, 0.0) > batasAnggaran.get(kat);
    }

    public void tampilkanLaporan() {
        System.out.println("    " + "-".repeat(68));
        System.out.printf( "    %-18s | %-14s | %-14s | %s%n",
                "Kategori", "Anggaran", "Terpakai", "Status");
        System.out.println("    " + "-".repeat(68));
        for (String kat : batasAnggaran.keySet()) {
            double batas  = batasAnggaran.get(kat);
            double real   = realisasi.getOrDefault(kat, 0.0);
            String status = (batas > 0 && real > batas) ? "!! MELEBIHI" : "OK";
            System.out.printf("    %-18s | Rp %,10.0f | Rp %,10.0f | %s%n",
                    kat, batas, real, status);
        }
        System.out.println("    " + "-".repeat(68));
    }

    public Map<String, Double> getBatasAnggaran() { return batasAnggaran; }
    public Map<String, Double> getRealisasi()     { return realisasi; }
}

//CLASS — DompetDigital                         
class DompetDigital {
    private String namaDompet;
    private double saldo;

    public DompetDigital(String namaDompet, double saldoAwal) {
        this.namaDompet = namaDompet;
        this.saldo      = saldoAwal;
    }

    public void tambahSaldo(double jumlah)  { this.saldo += jumlah; }
    public void kurangiSaldo(double jumlah) { this.saldo -= jumlah; }

    public String getNamaDompet() { return namaDompet; }
    public double getSaldo()      { return saldo; }
}

//CLASS UTAMA — AnakKos                         
class AnakKos {
    private String nama;
    private double totalSaldo;

    private List<Pemasukan>      daftarPemasukan;
    private List<Pengeluaran>    daftarPengeluaran;
    private List<TargetTabungan> daftarTarget;
    private List<DompetDigital>  daftarDompet;
    private AnggaranBulanan      anggaranAktif;

    private Map<String, AnggaranBulanan> historiAnggaran;

    public AnakKos(String nama) {
        this.nama              = nama;
        this.totalSaldo        = 0;
        this.daftarPemasukan   = new ArrayList<>();
        this.daftarPengeluaran = new ArrayList<>();
        this.daftarTarget      = new ArrayList<>();
        this.daftarDompet      = new ArrayList<>();
        this.historiAnggaran   = new LinkedHashMap<>();
    }

    public void tambahDompet(DompetDigital dompet) {
        daftarDompet.add(dompet);
        totalSaldo += dompet.getSaldo();
        System.out.printf("  [OK] Dompet \"%s\" ditambahkan | Saldo: Rp %,.0f%n",
                dompet.getNamaDompet(), dompet.getSaldo());
    }

    public void mulaiAnggaranBulan(String bulan) {
        anggaranAktif = new AnggaranBulanan();
        historiAnggaran.put(bulan, anggaranAktif);
        System.out.println("  [OK] Anggaran bulan " + bulan + " berhasil dibuat.");
    }

    public void setAnggaranKategori(String kategori, double limit) {
        if (anggaranAktif != null) anggaranAktif.setBatas(kategori, limit);
    }

    public void tambahTarget(String namaTarget, double nominal) {
        daftarTarget.add(new TargetTabungan(namaTarget, nominal));
        System.out.printf("  [TARGET] \"%s\" ditambahkan | Nominal: Rp %,.0f%n",
                namaTarget, nominal);
    }

    public void tambahPemasukan(String tanggal, double jumlah, String keterangan, String tipe) {
        Pemasukan p = new Pemasukan(tanggal, jumlah, keterangan, tipe);
        daftarPemasukan.add(p);
        totalSaldo += jumlah;
        System.out.printf("  (+) %s | %-30s | +Rp %,.0f  =>  Saldo: Rp %,.0f%n",
                tanggal, keterangan, jumlah, totalSaldo);
    }

    public void tambahPengeluaran(String tanggal, double jumlah, String keterangan, String kategori) {
        if (jumlah > totalSaldo) {
            System.out.printf("  [DITOLAK] \"%s\" — butuh Rp %,.0f, saldo hanya Rp %,.0f%n",
                    keterangan, jumlah, totalSaldo);
            return;
        }
        Pengeluaran p = new Pengeluaran(tanggal, jumlah, keterangan, kategori);
        daftarPengeluaran.add(p);
        totalSaldo -= jumlah;

        if (anggaranAktif != null) {
            anggaranAktif.tambahRealisasi(kategori, jumlah);
            if (anggaranAktif.isMelebihiBatas(kategori)) {
                double real  = anggaranAktif.getRealisasi().get(kategori);
                double batas = anggaranAktif.getBatasAnggaran().get(kategori);
                System.out.printf("  [ALERT]  Anggaran \"%s\" melebihi batas! (Rp %,.0f / Rp %,.0f)%n",
                        kategori, real, batas);
            }
        }

        System.out.printf("  [-] %s | %-30s | -Rp %,.0f  =>  Saldo: Rp %,.0f%n",
                tanggal, keterangan, jumlah, totalSaldo);
    }

    public void simpanKeTarget(String namaTarget, double jumlah, String tanggal) {
        for (TargetTabungan t : daftarTarget) {
            if (t.getNamaTarget().equalsIgnoreCase(namaTarget)) {
                if (jumlah > totalSaldo) {
                    System.out.printf("  [DITOLAK] Nabung ke \"%s\" gagal — saldo tidak cukup%n", namaTarget);
                    return;
                }
                t.tambahTabungan(jumlah);
                totalSaldo -= jumlah;
                
                if (anggaranAktif != null) {
                    anggaranAktif.tambahRealisasi("TABUNGAN", jumlah);
                }

                System.out.printf("  [NABUNG] %s | -> %-22s | Rp %,.0f | Progres: %.1f%% | Saldo: Rp %,.0f%n",
                        tanggal, namaTarget, jumlah,
                        t.getTerkumpul() / t.getTargetNominal() * 100, totalSaldo);
                if (t.sudahTercapai()) {
                    System.out.println(" YEYYYY TARGET \"" + namaTarget.toUpperCase() + "\" TERCAPAI! ");
                }
                return;
            }
        }
        System.out.println("  [!] Target \"" + namaTarget + "\" tidak ditemukan.");
    }

    public void tampilkanBreakdownKategori(String bulan) {
        AnggaranBulanan ab = historiAnggaran.get(bulan);
        if (ab == null) { System.out.println("    (tidak ada data)"); return; }

        Map<String, Double> real = ab.getRealisasi();
        double grandTotal = real.values().stream().mapToDouble(Double::doubleValue).sum();

        System.out.println("    " + "-".repeat(52));
        System.out.printf( "    %-18s | %-14s | %s%n", "Kategori", "Total", "Porsi");
        System.out.println("    " + "-".repeat(52));
        for (Map.Entry<String, Double> e : real.entrySet()) {
            if (e.getValue() > 0) {
                double pct = grandTotal > 0 ? (e.getValue() / grandTotal) * 100 : 0;
                System.out.printf("    %-18s | Rp %,10.0f | %.1f%%%n",
                        e.getKey(), e.getValue(), pct);
            }
        }
        System.out.println("    " + "-".repeat(52));
        System.out.printf( "    %-18s | Rp %,10.0f |%n", "TOTAL PENGELUARAN", grandTotal);
        System.out.println("    " + "-".repeat(52));
    }

    public void tampilkanLaporanAnggaran(String bulan) {
        AnggaranBulanan ab = historiAnggaran.get(bulan);
        if (ab == null) { System.out.println("    (tidak ada data anggaran)"); return; }
        ab.tampilkanLaporan();
    }

    public void tampilkanRingkasan() {
        double totalMasuk  = daftarPemasukan.stream().mapToDouble(Transaksi::getJumlah).sum();
        double totalKeluar = daftarPengeluaran.stream().mapToDouble(Transaksi::getJumlah).sum();
        double totalNabung = daftarTarget.stream().mapToDouble(TargetTabungan::getTerkumpul).sum();

        double rasio  = totalMasuk > 0 ? totalSaldo / totalMasuk : 0;
        String status = (rasio >= 0.30) ? "AMAN" : (rasio >= 0.10) ? "PERHATIAN" : "KRITIS";

        System.out.println();
        System.out.println("  ══════════════════════════════════════════════════");
        System.out.printf( "        RINGKASAN KEUANGAN %-23s%n", nama.toUpperCase());
        System.out.println("  ══════════════════════════════════════════════════");
        System.out.printf( "     Total Pemasukan    :  Rp %,20.0f    %n", totalMasuk);
        System.out.printf( "     Total Pengeluaran  :  Rp %,20.0f    %n", totalKeluar);
        System.out.printf( "     Total Ditabung     :  Rp %,20.0f    %n", totalNabung);
        System.out.printf( "     Saldo Akhir        :  Rp %,20.0f    %n", totalSaldo);
        System.out.println("  ══════════════════════════════════════════════════");
        System.out.printf( "     Status Keuangan    :  %-26s  %n", status);
        
    }

    public void tampilkanRiwayatTransaksi() {
        List<Transaksi> semua = new ArrayList<>();
        semua.addAll(daftarPemasukan);
        semua.addAll(daftarPengeluaran);
        semua.sort(Comparator.comparingInt(Transaksi::getId));

        System.out.println("    " + "-".repeat(70));
        System.out.printf( "    %-8s | %-10s | %-30s | %s%n",
                "Tipe/ID", "Tanggal", "Keterangan", "Jumlah");
        System.out.println("    " + "-".repeat(70));
        for (Transaksi t : semua) t.tampilkanInfo();
        System.out.println("    " + "-".repeat(70));
    }

    public void tampilkanTargetTabungan() {
        if (daftarTarget.isEmpty()) {
            System.out.println("    (tidak ada target tabungan)");
            return;
        }
        for (TargetTabungan t : daftarTarget) t.tampilkanInfo();
    }

    public double getTotalSaldo() { return totalSaldo; }
}

//MAIN CLASS                            
public class App {
    static void garis(char c, int n) { System.out.println(String.valueOf(c).repeat(n)); }
    static void header(String judul) {
        System.out.println();
        System.out.println("  " + judul);
        garis('-', 62);
    }

    public static void main(String[] args) {
        garis('=', 62);
        System.out.println("     MoneyKos| Manajemen Keuangan Anak Kos");
        garis('=', 62);

        // [1] PROFIL NAYLA
        header("[1] PROFIL PENGGUNA");
        AnakKos user = new AnakKos("Nayla Aisha");
        System.out.println("  Nama    : Nayla Aisha");
        System.out.println("  Asal    : Surabaya");
        System.out.println("  Kampus  : Institut Teknologi Sepuluh Nopember (ITS)");
        System.out.println("  Jurusan : Teknologi Informasi");

        // [2] SETUP DOMPET
        header("[2] SETUP DOMPET");
        user.tambahDompet(new DompetDigital("Tunai", 50_000));
        user.tambahDompet(new DompetDigital("BCA Mobile", 2_000_000));
        user.tambahDompet(new DompetDigital("GoPay", 75_000));

        // [3] TARGET TABUNGAN
        header("[3] TARGET TABUNGAN");
        user.tambahTarget("iPad Pro M2", 12_000_000);
        user.tambahTarget("blindbox hirono", 300_000);

        // [4] ANGGARAN MARET
        header("[4] ANGGARAN BULAN MARET 2026");
        user.mulaiAnggaranBulan("Maret 2026");
        user.setAnggaranKategori("MAKANAN", 500_000);
        user.setAnggaranKategori("KEBUTUHAN_KOST", 350_000);
        user.setAnggaranKategori("HIBURAN", 100_000);
        user.setAnggaranKategori("PENDIDIKAN", 200_000);
        user.setAnggaranKategori("TABUNGAN", 0);

        // [5] PEMASUKAN MARET
        header("[5] PEMASUKAN MARET 2026");
        user.tambahPemasukan("01 Mar", 2_000_000, "Kiriman bulanan Mama", "Kiriman Ortu");

        // [6] PENGELUARAN MARET
        header("[6] PENGELUARAN MARET 2026");
        user.tambahPengeluaran("01 Mar", 500_000, "Bayar kos", "KEBUTUHAN_KOST");
        user.tambahPengeluaran("02 Mar", 20_000, "Sarapan nasi kuning", "MAKANAN");
        user.tambahPengeluaran("04 Mar", 45_000, "Print laporan tugas IT", "PENDIDIKAN");
        user.tambahPengeluaran("08 Mar", 120_000, "Nonton bioskop", "HIBURAN");
        user.tambahPengeluaran("12 Mar", 90_000, "Beli buku algoritma", "PENDIDIKAN");
        user.tambahPengeluaran("15 Mar", 150_000, "Makan seafood", "MAKANAN");
        user.tambahPengeluaran("18 Mar", 110_000, "Game online", "HIBURAN");
        user.tambahPengeluaran("20 Mar", 310_000, "Beli Sprei baru", "KEBUTUHAN_KOST");

        // [7] MENABUNG MARET
        header("[7] MENABUNG MARET 2026");
        user.simpanKeTarget("iPad Pro M2", 200_000, "25 Mar");
        user.simpanKeTarget("blindbox hirono", 300_000, "29 Mar");

        // [8] LAPORAN MARET
        header("[8] LAPORAN AKHIR MARET 2026");
        System.out.println("  > Breakdown Pengeluaran per Kategori:");
        user.tampilkanBreakdownKategori("Maret 2026");
        System.out.println("  > Evaluasi Anggaran:");
        user.tampilkanLaporanAnggaran("Maret 2026");
        System.out.println("  > Progress Target Tabungan:");
        user.tampilkanTargetTabungan();

        // [9] RINGKASAN AKHIR
        header("[9] RINGKASAN KEUANGAN AKHIR");
        user.tampilkanRingkasan();
        System.out.println();
        garis('=', 62);
    }
}