Implementasikan integrasi Computer Vision YOLOv5n ONNX langsung di backend SmartClass menggunakan Java 21 dan Spring Boot.

Lokasi model:
src/main/resources/models/yolov5n.onnx

Konteks:

- Model YOLOv5n sudah diekspor ke ONNX dan sudah berhasil diuji menggunakan Python.
- Backend existing menggunakan Gradle Kotlin DSL, yaitu build.gradle.kts.
- Target saat ini adalah inferensi gambar di backend Java, bukan integrasi hardware dahulu.

Tugas:

1. Periksa keberadaan model ONNX, struktur backend, build.gradle.kts, SecurityConfig, pola controller, DTO, dan format respons API yang sudah digunakan.
2. Tambahkan dependency CPU ONNX Runtime Java versi 1.31.1:
   implementation("com.microsoft.onnxruntime:onnxruntime:1.31.1")
   Jangan menghapus atau mengubah dependency existing yang tidak berkaitan.
3. Buat YoloDetectionService yang memuat model satu kali saat aplikasi dimulai dan menggunakan OrtEnvironment serta OrtSession dengan lifecycle yang benar. Muat model dari classpath resource agar tetap dapat digunakan ketika aplikasi dikemas sebagai JAR.
4. Implementasikan preprocessing gambar yang sesuai dengan ekspor YOLOv5: RGB, normalisasi piksel ke rentang 0–1, ukuran input 640x640, dan letterbox yang mempertahankan rasio gambar.
5. Periksa input dan output model ONNX yang sebenarnya sebelum mengasumsikan bentuk tensor. Implementasikan decoding output YOLOv5, confidence threshold, pemilihan kelas person (COCO class index 0), non-maximum suppression, dan pemetaan bounding box kembali ke ukuran gambar asli.
6. Buat DTO respons yang berisi detected, personCount, daftar deteksi person dengan confidence dan bounding box, serta inferenceTimeMs.
7. Buat POST /api/vision/detect yang menerima gambar melalui multipart/form-data dengan field image. Validasi tipe dan ukuran file, tangani gambar invalid dan kegagalan inferensi dengan respons error yang sesuai.
8. Ikuti format respons dan pola exception existing. Periksa SecurityConfig dan batasi endpoint sesuai pola autentikasi aplikasi; ADMIN dan PETUGAS yang sudah login dapat menggunakan endpoint ini. Jangan membuka endpoint tanpa autentikasi.
9. Gunakan resource dan session secara aman, hindari memuat ulang model pada setiap request, serta pastikan tensor dan hasil inferensi ditutup setelah digunakan.
10. Tambahkan test untuk validasi gambar, decoding hasil, dan logika deteksi yang bisa diuji tanpa memuat ulang model. Tambahkan integration test inferensi bila model tersedia di test runtime.
11. Jalankan Gradle compile dan test yang relevan. Laporkan hasil aktual, endpoint, file yang berubah, serta keterbatasan yang masih ada.

Batasan:

- Jangan membuat service Python untuk inferensi.
- Jangan mengubah database, fitur Schedule, atau kontrol perangkat.
- Jangan mengklaim deteksi berhasil di Java hanya karena sebelumnya berhasil di Python.
- Jangan membuat asumsi output model tanpa memeriksa metadata dan tensor output.
- Jika ada ketidakcocokan versi atau masalah teknis, jelaskan dan perbaiki sebelum mengklaim implementasi selesai.

Fokus hanya pada inferensi YOLO di backend dan API pengujiannya.
