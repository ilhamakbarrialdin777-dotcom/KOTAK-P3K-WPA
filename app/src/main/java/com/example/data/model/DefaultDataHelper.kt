package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DefaultDataHelper {

    const val COMPANY_NAME = "PT. WATU PERKASA ABADI"
    const val DEPARTMENT_NAME = "Departemen HSE (Health, Safety & Environment)"
    const val FORM_TITLE = "FORM PEMERIKSAAN BERKALA KOTAK P3K"
    const val FORM_SUBTITLE = "(Pertolongan Pertama Pada Kecelakaan)"
    const val REGULATION_REF = "Standar Permenakertrans No. PER.15/MEN/VIII/2008 tentang P3K di Tempat Kerja"

    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentPeriodString(): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }

    fun getDefaultConditionChecks(): List<BoxConditionCheck> {
        return listOf(
            BoxConditionCheck(
                no = 1,
                itemText = "Kotak P3K mudah dilihat dan mudah dijangkau (tidak terhalang barang)",
                isYes = true,
                note = ""
            ),
            BoxConditionCheck(
                no = 2,
                itemText = "Ada tanda P3K (palang hijau) di kotak / dinding",
                isYes = true,
                note = ""
            ),
            BoxConditionCheck(
                no = 3,
                itemText = "Kotak bersih, kering, tidak berkarat, dan tidak rusak",
                isYes = true,
                note = ""
            ),
            BoxConditionCheck(
                no = 4,
                itemText = "Isi kotak tertata rapi (tidak berantakan / tercecer)",
                isYes = true,
                note = ""
            ),
            BoxConditionCheck(
                no = 5,
                itemText = "Ada daftar isi kotak P3K dan nomor telepon darurat",
                isYes = true,
                note = ""
            )
        )
    }

    fun getDefaultStandardItems(): List<InspectionItem> {
        return listOf(
            // 1. PERBAN DAN PEMBALUT
            InspectionItem(
                no = 1,
                name = "Plester gulung",
                category = "1. PERBAN DAN PEMBALUT",
                usage = "Menempelkan kasa / perban agar tidak lepas",
                standardQty = 2,
                unit = "roll",
                currentQty = 2,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 2,
                name = "Plester luka (Hansaplast)",
                category = "1. PERBAN DAN PEMBALUT",
                usage = "Menutup luka kecil agar tidak kotor",
                standardQty = 1,
                unit = "pak",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 3,
                name = "Kasa / perban gulung",
                category = "1. PERBAN DAN PEMBALUT",
                usage = "Membalut dan menekan luka agar darah berhenti",
                standardQty = 5,
                unit = "gulung",
                currentQty = 5,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 4,
                name = "Kasa steril (kotak)",
                category = "1. PERBAN DAN PEMBALUT",
                usage = "Menutup luka agar tetap bersih",
                standardQty = 2,
                unit = "kotak",
                currentQty = 2,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 5,
                name = "Mitela (kain segitiga)",
                category = "1. PERBAN DAN PEMBALUT",
                usage = "Menggendong lengan, membalut kepala / bahu",
                standardQty = 2,
                unit = "lembar",
                currentQty = 2,
                conditionIsGood = true
            ),

            // 2. CAIRAN DAN OBAT LUAR
            InspectionItem(
                no = 6,
                name = "Povidone iodine (botol kuning)",
                category = "2. CAIRAN DAN OBAT LUAR",
                usage = "Membersihkan luka (antiseptik)",
                standardQty = 1,
                unit = "botol",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 7,
                name = "Cairan antiseptik / alkohol (botol biru)",
                category = "2. CAIRAN DAN OBAT LUAR",
                usage = "Membersihkan tangan dan alat",
                standardQty = 1,
                unit = "botol",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 8,
                name = "Botol kecil cairan (betadine)",
                category = "2. CAIRAN DAN OBAT LUAR",
                usage = "Mencegah infeksi luar",
                standardQty = 1,
                unit = "botol",
                currentQty = 1,
                conditionIsGood = true
            ),

            // 3. ALAT BANTU
            InspectionItem(
                no = 9,
                name = "Pinset",
                category = "3. ALAT BANTU",
                usage = "Mengambil kotoran / serpihan kecil dari luka",
                standardQty = 1,
                unit = "buah",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 10,
                name = "Peniti",
                category = "3. ALAT BANTU",
                usage = "Mengunci mitela dan perban",
                standardQty = 1,
                unit = "kotak",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 11,
                name = "Gelas cuci mata",
                category = "3. ALAT BANTU",
                usage = "Membilas mata yang terkena debu / bahan kimia",
                standardQty = 1,
                unit = "buah",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 12,
                name = "Sungkup napas buatan (pocket mask)",
                category = "3. ALAT BANTU",
                usage = "Membantu napas korban saat napas berhenti",
                standardQty = 1,
                unit = "set",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 13,
                name = "Senter",
                category = "3. ALAT BANTU",
                usage = "Penerangan pemeriksaan luka saat malam",
                standardQty = 1,
                unit = "set",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 14,
                name = "Gunting",
                category = "3. ALAT BANTU",
                usage = "Membantu memotong perban",
                standardQty = 1,
                unit = "set",
                currentQty = 1,
                conditionIsGood = true
            ),

            // 4. ALAT PELINDUNG DIRI PENOLONG
            InspectionItem(
                no = 15,
                name = "Sarung tangan",
                category = "4. ALAT PELINDUNG DIRI PENOLONG",
                usage = "Melindungi penolong dari darah / kotoran",
                standardQty = 1,
                unit = "pasang",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 16,
                name = "Masker medis",
                category = "4. ALAT PELINDUNG DIRI PENOLONG",
                usage = "Melindungi penolong dan korban dari penularan",
                standardQty = 1,
                unit = "pak",
                currentQty = 1,
                conditionIsGood = true
            ),

            // 5. BUKU DAN CATATAN
            InspectionItem(
                no = 17,
                name = "Buku Panduan P3K di Tempat Kerja",
                category = "5. BUKU DAN CATATAN",
                usage = "Pedoman cara memberi pertolongan pertama",
                standardQty = 1,
                unit = "buku",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 18,
                name = "Buku saku P3K (sampul biru)",
                category = "5. BUKU DAN CATATAN",
                usage = "Panduan cepat pertolongan pertama",
                standardQty = 1,
                unit = "buku",
                currentQty = 1,
                conditionIsGood = true
            ),
            InspectionItem(
                no = 19,
                name = "Buku catatan pemakaian P3K",
                category = "5. BUKU DAN CATATAN",
                usage = "Mencatat siapa memakai apa, kapan, dan kenapa",
                standardQty = 1,
                unit = "buku",
                currentQty = 1,
                conditionIsGood = true
            )
        )
    }

    fun getSampleInspections(): List<InspectionEntity> {
        val stdItems = getDefaultStandardItems()

        // Sample 1: Workshop Utama (Perlu Isi Ulang)
        val itemsSample1 = stdItems.map { item ->
            when (item.no) {
                1 -> item.copy(currentQty = 1, note = "Tersisa 1 roll, terpakai 1")
                3 -> item.copy(currentQty = 3, note = "Kurang 2 gulung")
                15 -> item.copy(currentQty = 0, note = "Habis dipakai tanggap darurat")
                else -> item
            }
        }
        val entity1 = InspectionEntity(
            id = 1,
            siteLocation = "Workshop Mekanikal Utama",
            boxPosition = "Di Tempel di dinding dekat pintu darurat",
            inspectionDate = "05/10/2026",
            periodMonthYear = "Oktober 2026",
            inspectorName = "ILHAM AKBAR RIALDIN",
            inspectorRole = "HSE Officer",
            conditionChecksJson = InspectionEntity.encodeConditionChecks(getDefaultConditionChecks()),
            itemsJson = InspectionEntity.encodeItems(itemsSample1),
            conclusionStatus = "BELUM_LENGKAP",
            replacementNotes = "Perlu isi ulang: Plester gulung (1 roll), Kasa gulung (2 gulung), Sarung tangan (1 pasang).",
            signatureData = "M 10 50 Q 30 20 60 40 T 120 40",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )

        // Sample 2: Kantor Utama (Lengkap)
        val entity2 = InspectionEntity(
            id = 2,
            siteLocation = "Kantor HSE & Administrasi",
            boxPosition = "Di Tempel di dinding pantry",
            inspectionDate = "01/10/2026",
            periodMonthYear = "Oktober 2026",
            inspectorName = "ILHAM AKBAR RIALDIN",
            inspectorRole = "HSE Officer",
            conditionChecksJson = InspectionEntity.encodeConditionChecks(getDefaultConditionChecks()),
            itemsJson = InspectionEntity.encodeItems(stdItems),
            conclusionStatus = "LENGKAP",
            replacementNotes = "Semua isi kotak lengkap, higienis, dan tersegel baik.",
            signatureData = "M 10 30 Q 50 10 90 40",
            createdAt = System.currentTimeMillis() - 86400000L * 6
        )

        return listOf(entity1, entity2)
    }
}
