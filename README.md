# FoodKu (Kotlin + Jetpack Compose)

Aplikasi pesan-antar makanan bertema ShopeeFood dengan **3 peran pengguna**: Pembeli, Penjual, dan Administrator.

## Peran & Hak Akses

| Peran | Bisa melakukan |
|---|---|
| **Pembeli** | Daftar sendiri, cari restoran, pesan menu, lacak status pesanan, batalkan pesanan yang belum diproses |
| **Penjual** | Daftar (butuh persetujuan admin), kelola menu (tambah/ubah/hapus), buka/tutup toko, proses pesanan masuk |
| **Administrator** | Kelola semua akun (setujui penjual, blokir, hapus), lihat semua pesanan, lihat statistik & pendapatan |

## Akun Administrator Bawaan

```
username : admin
password : Admin123!
```

> Segera ganti password lewat menu **Toko / Akun → Ganti Password** setelah masuk pertama kali.

## Alur Pendaftaran

1. **Pembeli** — daftar di halaman login → langsung aktif → bisa masuk.
2. **Penjual** — daftar dengan nama toko → akun berstatus *menunggu persetujuan* → admin menyetujui di tab **Pengguna / Dasbor** → baru bisa masuk.
3. **Administrator** — tidak bisa didaftarkan sendiri; hanya dibuat dari akun bawaan di atas.

## Penyimpanan Data

Aplikasi ini belum memakai server. Semua akun, toko, dan pesanan disimpan lokal di perangkat
(`SharedPreferences`, JSON) dengan password di-hash **SHA-256 + salt** — password asli tidak pernah disimpan.

## Build APK Otomatis (GitHub Actions)

Workflow `.github/workflows/build-apk.yml` berjalan setiap kali ada push ke `main`:

- Build `assembleDebug` dengan JDK 17 + Gradle 8.9
- Hasil diunggah sebagai artifact (30 hari)
- APK juga diterbitkan ke release **`latest-apk`** → link download selalu versi terbaru

Bisa juga dijalankan manual: tab **Actions** → **Build APK** → **Run workflow**.

## Menjalankan di Android Studio

1. Buka Android Studio (Ladybug atau lebih baru) → **File > Open** → pilih folder proyek ini.
2. Tunggu Gradle Sync selesai.
3. Pilih emulator/HP, klik **Run**.
