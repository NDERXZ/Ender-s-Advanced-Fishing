# Ender's Advanced Fishing

<div align="center">

![Java](https://img.shields.io/badge/Java-21%20%7C%2025%20(MR--JAR)-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Paper%20%2F%20Purpur%201.21.x+-1E88E5?style=for-the-badge&logo=minecraft&logoColor=white)
![Release](https://img.shields.io/badge/Release-v1.4-00C853?style=for-the-badge)
![License](https://img.shields.io/badge/License-GNU%20GPL%20v2-blue?style=for-the-badge)

**An enterprise-grade, RPG-styled fishing system engineered for modern Paper and Purpur Minecraft servers.**

[Fitur Utama](#fitur-utama) • [Sistem Gameplay](#sistem-gameplay) • [Daftar Fishing Rod](#daftar-fishing-rod-26-joran) • [Daftar Umpan](#daftar-umpan-khusus) • [Katalog Ikan](#katalog-ikan-211-spesies) • [Perintah & Izin](#perintah--izin) • [Panduan Instalasi](#panduan-instalasi)

</div>

---

## Ringkasan Proyek

**Ender's Advanced Fishing** merombak total mekanik memancing vanilla Minecraft menjadi pengalaman RPG yang kompetitif, imersif, dan seimbang. Menampilkan sistem perkembangan **7 Tingkat Kelangkaan (Tier)** dari `ᴄᴏᴍᴍᴏɴ` hingga `sᴇᴄʀᴇᴛ`, mekanisme **Tier Locking**, **Catch Event Visuals & SFX**, proteksi item anti-eksploitasi, jurnal tangkapan interaktif 54-slot, serta isolasi dimensi penuh.

---

## Fitur Utama

- **7 Tingkat Kelangkaan (Tiers)**: `ᴄᴏᴍᴍᴏɴ`, `ᴜɴᴄᴏᴍᴍᴏɴ`, `ʀᴀʀᴇ`, `ᴇᴘɪᴄ`, `ʟᴇɢᴇɴᴅᴀʀʏ`, `ᴍʏᴛʜɪᴄ`, dan `sᴇᴄʀᴇᴛ` dengan gradasi warna RGB khusus.
- **Tier Locking**: Joran pancing memiliki batasan tier tangkapan (`allowed_tiers`). Pemain tidak bisa mendapatkan ikan tier tinggi sebelum meningkatkan joran mereka. Joran vanilla dibatasi hanya untuk tier `ᴄᴏᴍᴍᴏɴ` dan `ᴜɴᴄᴏᴍᴍᴏɴ`.
- **Isolasi Dimensi Penuh**: Sistem memancing kustom hanya aktif di dimensi atau dunia yang diizinkan (default: Overworld / `NORMAL`). Di dimensi lain (Nether, The End), sistem otomatis kembali 100% ke mekanik dan loot vanilla tanpa gangguan.
- **Catch Event Visuals & SFX**: Efek dramatis saat menarik ikan langka (partikel `FLASH`, `FIREWORK`, `SONIC_BOOM`, `DRAGON_BREATH`, `TOTEM_OF_UNDYING`, efek petir kosmetik, suara `UI_TOAST_CHALLENGE_COMPLETE`, `WARDEN_ROAR`, dan broadcast server).
- **Proteksi Item Kustom (Item Protection)**: Ikan kustom terlindungi secara menyeluruh: tidak dapat dimakan, tidak dapat dibakar/dimasak di Furnace/Smoker/Campfire, tidak dapat digunakan sebagai bahan crafting resep vanilla, dan tidak dapat ditaruh sebagai blok.
- **Fish Journal & Codex (`/eaf journal` / `/eaf codex`)**: GUI 54-slot interaktif dengan 7 tab simetris untuk setiap tier. Mencatat tanggal pertama ditangkap, rekor bobot terberat, kualitas tertinggi, dan total tangkapan. Ikan yang belum pernah ditangkap disamarkan dengan nama `???`.
- **Tipografi Latin Small Caps & Oceanic Gradient**: Desain estetis bebas emoji dengan gradasi RGB laut (`#0052D4` -> `#4364F7` -> `#6FB1FC`) dan font Small Caps (`ᴀ ʙ ᴄ ᴅ ᴇ ꜰ...`) pada seluruh antarmuka GUI, actionbar, judul, dan lore.
- **Multi-Engine Custom Textures**: Kompatibilitas tanpa celah dengan engine tekstur kustom (**Nexo**, **Oraxen**, dan **ItemsAdder**) dengan sistem fallback otomatis ke CustomModelData vanilla jika engine tidak tersedia.
- **Strike Minigame**: Minigame interaktif klik-kiri saat ikan menyambar kail, lengkap dengan hitungan klik real-time (`[ 0 / X ] | ᴛᴇʀsɪsᴀ X ᴋʟɪᴋ`), progress bar, dan konsumsi stamina.
- **Ekonomi Dinamis & Saturasi Pasar**: Harga jual ikan dipengaruhi oleh suplai dan permintaan. Menjual ikan yang sama secara massal akan menurunkan harga jualnya, yang pulih secara bertahap setiap 15 menit.
- **Integrasi AuraSkills & Vault**: Hadiah EXP memancing terintegrasi langsung dengan AuraSkills dan ekonomi server berbasis Vault.
- **Arsitektur Multi-Release JAR (Java 21 & Java 25)**: Satu berkas JAR universal yang berjalan optimal pada baseline Java 21 hingga bytecode terbaru Java 25 (Paper 26.2+).

---

## Sistem Gameplay

### 1. Tingkat Kelangkaan (Tiers)

| Tier | Visual Small Caps | Pengali Harga | Deskripsi |
|---|---|---|---|
| **COMMON** | `ᴄᴏᴍᴍᴏɴ` | `1.0x` | Ikan standar yang melimpah di seluruh perairan. |
| **UNCOMMON** | `ᴜɴᴄᴏᴍᴍᴏɴ` | `1.5x` | Ikan air tawar dan pesisir dengan kualitas daging lebih baik. |
| **RARE** | `ʀᴀʀᴇ` | `2.0x` | Spesies langka yang memerlukan joran memadai. |
| **EPIC** | `ᴇᴘɪᴄ` | `3.0x` | Tangkapan berukuran besar dan bernilai pasar tinggi. |
| **LEGENDARY** | `ʟᴇɢᴇɴᴅᴀʀʏ` | `5.0x` | Spesies langka legendaris dengan sambaran kuat dan pengumuman server. |
| **MYTHIC** | `ᴍʏᴛʜɪᴄ` | `8.0x` | Entitas purba berkekuatan mistis dengan visual dan SFX khusus. |
| **SECRET** | `sᴇᴄʀᴇᴛ` | `15.0x` | Tangkapan rahasia dengan nilai luar biasa, partikel sonik, dan broadcast server. |

---

## Daftar Fishing Rod (26 Joran)

| ID | Nama Joran | Tier | Durabilitas | Lure | Tangkapan yang Diizinkan (Allowed Tiers) |
|---|---|---|---|---|---|
| `admin_dev_rod` | Admin Dev Rod | **SECRET** | 10000 | Lv.7 | `SECRET` |
| `celestal_angelic_rod` | Angelic Rod | **SECRET** | 10000 | Lv.7 | `ALL` |
| `celestal_gold_rod` | Gold Rod | **SECRET** | 10000 | Lv.7 | `ALL` |
| `celestal_hyper_rod` | Hyper Rod | **SECRET** | 10000 | Lv.7 | `ALL` |
| `common_carbon_rod` | Carbon Rod | **BASIC** | 250 | Lv.1 | `COMMON, UNCOMMON` |
| `common_luck_rod` | Luck Rod | **BASIC** | 250 | Lv.1 | `COMMON, UNCOMMON` |
| `common_starter_rod` | Starter Rod | **BASIC** | 250 | Lv.1 | `COMMON, UNCOMMON` |
| `common_toy_rod` | Toy Rod | **BASIC** | 250 | Lv.1 | `COMMON, UNCOMMON` |
| `epic_chrome_rod` | Chrome Rod | **EXPERT** | 2000 | Lv.4 | `COMMON, UNCOMMON, RARE, EPIC, LEGENDARY` |
| `epic_steampunk_rod` | Steampunk Rod | **EXPERT** | 2000 | Lv.4 | `COMMON, UNCOMMON, RARE, EPIC, LEGENDARY` |
| `forgotten_withering_rod` | Withering Rod | **SECRET** | 10000 | Lv.7 | `ALL` |
| `legendary_astral_rod` | Astral Rod | **MASTER** | 3500 | Lv.5 | `COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC` |
| `legendary_fluorescent_rod` | Fluorescent Rod | **MASTER** | 3500 | Lv.5 | `COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC` |
| `legendary_hazmat_rod` | Hazmat Rod | **MASTER** | 3500 | Lv.5 | `COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC` |
| `mythic_angler_rod` | Angler Rod | **MYTHIC** | 6000 | Lv.6 | `ALL` |
| `mythic_ares_rod` | Ares Rod | **MYTHIC** | 6000 | Lv.6 | `ALL` |
| `mythic_bamboo_rod` | Bamboo Rod | **MYTHIC** | 6000 | Lv.6 | `ALL` |
| `mythic_ghostfinn_rod` | Ghostfinn Rod | **MYTHIC** | 6000 | Lv.6 | `ALL` |
| `rare_lucky_rod` | Lucky Rod | **ADVANCED** | 1000 | Lv.3 | `COMMON, UNCOMMON, RARE, EPIC` |
| `rare_midnight_rod` | Midnight Rod | **ADVANCED** | 1000 | Lv.3 | `COMMON, UNCOMMON, RARE, EPIC` |
| `secret_diamond_rod` | Diamond Rod | **SECRET** | 10000 | Lv.7 | `ALL` |
| `secret_element_rod` | Element Rod | **SECRET** | 10000 | Lv.7 | `ALL` |
| `uncommon_damascus_rod` | Damascus Rod | **INTERMEDIATE** | 500 | Lv.2 | `COMMON, UNCOMMON, RARE` |
| `uncommon_grass_rod` | Grass Rod | **INTERMEDIATE** | 500 | Lv.2 | `COMMON, UNCOMMON, RARE` |
| `uncommon_ice_rod` | Ice Rod | **INTERMEDIATE** | 500 | Lv.2 | `COMMON, UNCOMMON, RARE` |
| `uncommon_lava_rod` | Lava Rod | **INTERMEDIATE** | 500 | Lv.2 | `COMMON, UNCOMMON, RARE` |

---

## Daftar Umpan Khusus

| ID | Nama Umpan | Material | Harga | Deskripsi |
|---|---|---|---|---|
| `worm` | Cacing Tanah | `WHEAT_SEEDS` | $15 | Umpan dasar untuk ikan kecil |
| `cricket` | Jangkrik | `BEETROOT_SEEDS` | $45 | Umpan serangga lincah |
| `squid_tentacle` | Tentakel Cumi | `INK_SAC` | $120 | Umpan laut berbau tajam untuk predator |
| `aether_bait` | Umpan Aether | `HEART_OF_THE_SEA` | $350 | Umpan mistis berkilau dari kedalaman primordial |

---

## Katalog Ikan (211 Spesies)

### Ikan Tier ᴄᴏᴍᴍᴏɴ (34 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `azure_damsel` | Azure Damsel | `TROPICAL_FISH` | 3001 | 0.2kg - 5.0kg | $15 |
| `clownfish` | Clownfish | `TROPICAL_FISH` | 3002 | 0.2kg - 5.0kg | $15 |
| `watanabei_angelfish` | Watanabei Angelfish | `TROPICAL_FISH` | 3003 | 0.2kg - 5.0kg | $15 |
| `white_tang` | White Tang | `TROPICAL_FISH` | 3004 | 0.2kg - 5.0kg | $15 |
| `pygmy_goby` | Pygmy Goby | `COD` | 3005 | 0.2kg - 5.0kg | $15 |
| `herring_fish` | Herring Fish | `COD` | 3006 | 0.2kg - 5.0kg | $15 |
| `strawberry_dotty` | Strawberry Dotty | `TROPICAL_FISH` | 3007 | 0.2kg - 5.0kg | $15 |
| `copperband_butterfly` | Copperband Butterfly | `TROPICAL_FISH` | 3008 | 0.2kg - 5.0kg | $15 |
| `old_boot` | Old Boot | `LEATHER_BOOTS` | 3009 | 0.2kg - 5.0kg | $15 |
| `yellowstate_angelfish` | Yellowstate Angelfish | `TROPICAL_FISH` | 3010 | 0.2kg - 5.0kg | $15 |
| `vintage_blue_tang` | Vintage Blue Tang | `TROPICAL_FISH` | 3011 | 0.2kg - 5.0kg | $15 |
| `masked_angelfish` | Masked Angelfish | `TROPICAL_FISH` | 3012 | 0.2kg - 5.0kg | $15 |
| `conspi_angelfish` | Conspi Angelfish | `TROPICAL_FISH` | 3013 | 0.2kg - 5.0kg | $15 |
| `skunk_tilefish` | Skunk Tilefish | `COD` | 3014 | 0.2kg - 5.0kg | $15 |
| `fade_tang` | Fade Tang | `TROPICAL_FISH` | 3015 | 0.2kg - 5.0kg | $15 |
| `reef_chromis` | Reef Chromis | `TROPICAL_FISH` | 3016 | 0.2kg - 5.0kg | $15 |
| `jennifer_dottyback` | Jennifer Dottyback | `TROPICAL_FISH` | 3017 | 0.2kg - 5.0kg | $15 |
| `zoster_butterfly` | Zoster Butterfly | `TROPICAL_FISH` | 3018 | 0.2kg - 5.0kg | $15 |
| `boa_angelfish` | Boa Angelfish | `TROPICAL_FISH` | 3019 | 0.2kg - 5.0kg | $15 |
| `volcanic_basslet` | Volcanic Basslet | `TROPICAL_FISH` | 3020 | 0.2kg - 5.0kg | $15 |
| `volcano_corazon_damsel` | Volcano Corazon Damsel | `TROPICAL_FISH` | 3021 | 0.2kg - 5.0kg | $15 |
| `specked_butterfly` | Specked Butterfly | `TROPICAL_FISH` | 3022 | 0.2kg - 5.0kg | $15 |
| `sail_tang` | Sail Tang | `TROPICAL_FISH` | 3023 | 0.2kg - 5.0kg | $15 |
| `orangy_goby` | Orangy Goby | `COD` | 3024 | 0.2kg - 5.0kg | $15 |
| `ash_basslet` | Ash Basslet | `COD` | 3025 | 0.2kg - 5.0kg | $15 |
| `electric_eel` | Electric Eel | `SALMON` | 3026 | 0.2kg - 5.0kg | $15 |
| `boar_fish` | Boar Fish | `COD` | 3027 | 0.2kg - 5.0kg | $15 |
| `dead_fish` | Dead Fish | `COD` | 3028 | 0.2kg - 5.0kg | $15 |
| `skeleton_fish` | Skeleton Fish | `BONE` | 3029 | 0.2kg - 5.0kg | $15 |
| `beanie_leedsicheye` | Beanie Leedsicheye | `COD` | 3030 | 0.2kg - 5.0kg | $15 |
| `runic_wispeye` | Runic Wispeye | `PRISMARINE_SHARD` | 3031 | 0.2kg - 5.0kg | $15 |
| `waveback_fish` | Waveback Fish | `SALMON` | 3032 | 0.2kg - 5.0kg | $15 |
| `night_glider_fish` | Night Glider Fish | `COD` | 3033 | 0.2kg - 5.0kg | $15 |
| `sona_fin_fish` | Sona Fin Fish | `TROPICAL_FISH` | 3034 | 0.2kg - 5.0kg | $15 |

### Ikan Tier ᴜɴᴄᴏᴍᴍᴏɴ (48 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `conch_shell` | Conch Shell | `NAUTILUS_SHELL` | 3035 | 1.5kg - 25.0kg | $45 |
| `sea_shell` | Sea Shell | `NAUTILUS_SHELL` | 3036 | 1.5kg - 25.0kg | $45 |
| `yellow_damselfish` | Yellow Damselfish | `TROPICAL_FISH` | 3037 | 1.5kg - 25.0kg | $45 |
| `gar_fish` | Gar Fish | `SALMON` | 3038 | 1.5kg - 25.0kg | $45 |
| `bandit_angelfish` | Bandit Angelfish | `TROPICAL_FISH` | 3039 | 1.5kg - 25.0kg | $45 |
| `flame_angelfish` | Flame Angelfish | `TROPICAL_FISH` | 3040 | 1.5kg - 25.0kg | $45 |
| `glacerfin_snapper` | Glacerfin Snapper | `COD` | 3041 | 1.5kg - 25.0kg | $45 |
| `blue_banded_goby` | Blue-Banded Goby | `TROPICAL_FISH` | 3042 | 1.5kg - 25.0kg | $45 |
| `cypress_rauta` | Cypress Rauta | `COD` | 3043 | 1.5kg - 25.0kg | $45 |
| `tricolore_butterfly` | Tricolore Butterfly | `TROPICAL_FISH` | 3044 | 1.5kg - 25.0kg | $45 |
| `vintage_damsel` | Vintage Damsel | `TROPICAL_FISH` | 3045 | 1.5kg - 25.0kg | $45 |
| `purp_blisfish` | Purp Blisfish | `TROPICAL_FISH` | 3046 | 1.5kg - 25.0kg | $45 |
| `shrimp_goby` | Shrimp Goby | `TROPICAL_FISH` | 3047 | 1.5kg - 25.0kg | $45 |
| `bluemato_clownfish` | Bluemato Clownfish | `TROPICAL_FISH` | 3048 | 1.5kg - 25.0kg | $45 |
| `banded_butterfly` | Banded Butterfly | `TROPICAL_FISH` | 3049 | 1.5kg - 25.0kg | $45 |
| `arowana` | Arowana | `SALMON` | 3050 | 1.5kg - 25.0kg | $45 |
| `magma_goby` | Magma Goby | `TROPICAL_FISH` | 3051 | 1.5kg - 25.0kg | $45 |
| `lava_butterfly` | Lava Butterfly | `TROPICAL_FISH` | 3052 | 1.5kg - 25.0kg | $45 |
| `salmon` | Salmon | `SALMON` | 3053 | 1.5kg - 25.0kg | $45 |
| `flat_fish` | Flat Fish | `COD` | 3054 | 1.5kg - 25.0kg | $45 |
| `flying_fish` | Flying Fish | `PUFFERFISH` | 3055 | 1.5kg - 25.0kg | $45 |
| `wahoo` | Wahoo | `SALMON` | 3056 | 1.5kg - 25.0kg | $45 |
| `maze_angelfish` | Maze Angelfish | `TROPICAL_FISH` | 3057 | 1.5kg - 25.0kg | $45 |
| `lion_fish` | Lion Fish | `PUFFERFISH` | 3058 | 1.5kg - 25.0kg | $45 |
| `rockfish` | Rockfish | `COD` | 3059 | 1.5kg - 25.0kg | $45 |
| `coal_tang` | Coal Tang | `TROPICAL_FISH` | 3060 | 1.5kg - 25.0kg | $45 |
| `orange_basslet` | Orange Basslet | `TROPICAL_FISH` | 3061 | 1.5kg - 25.0kg | $45 |
| `racoon_butterfly_fish` | Racoon Butterfly Fish | `TROPICAL_FISH` | 3062 | 1.5kg - 25.0kg | $45 |
| `pilot_fish` | Pilot Fish | `COD` | 3063 | 1.5kg - 25.0kg | $45 |
| `red_snapper` | Red Snapper | `SALMON` | 3064 | 1.5kg - 25.0kg | $45 |
| `blackcap_basslet` | Blackcap Basslet | `TROPICAL_FISH` | 3065 | 1.5kg - 25.0kg | $45 |
| `silver_tuna` | Silver Tuna | `COD` | 3066 | 1.5kg - 25.0kg | $45 |
| `dark_eel` | Dark Eel | `SALMON` | 3067 | 1.5kg - 25.0kg | $45 |
| `viperfish` | Viperfish | `COD` | 3068 | 1.5kg - 25.0kg | $45 |
| `spotted_lantern_fish` | Spotted Lantern Fish | `GLOW_INK_SAC` | 3069 | 1.5kg - 25.0kg | $45 |
| `swordfish` | Swordfish | `SALMON` | 3070 | 1.5kg - 25.0kg | $45 |
| `ancient_arapaima` | Ancient Arapaima | `SALMON` | 3071 | 1.5kg - 25.0kg | $45 |
| `apyshorn_fish` | Apyshorn Fish | `COD` | 3072 | 1.5kg - 25.0kg | $45 |
| `drippy_tucanare` | Drippy Tucanare | `TROPICAL_FISH` | 3073 | 1.5kg - 25.0kg | $45 |
| `sail_fish` | Sail Fish | `SALMON` | 3074 | 1.5kg - 25.0kg | $45 |
| `water_snake` | Water Snake | `KELP` | 3075 | 1.5kg - 25.0kg | $45 |
| `thinkler_fish` | Thinkler Fish | `COD` | 3076 | 1.5kg - 25.0kg | $45 |
| `red_goatfish` | Red Goatfish | `SALMON` | 3077 | 1.5kg - 25.0kg | $45 |
| `zebra_snakehead` | Zebra Snakehead | `SALMON` | 3078 | 1.5kg - 25.0kg | $45 |
| `wing_parrotfish` | Wing Parrotfish | `TROPICAL_FISH` | 3079 | 1.5kg - 25.0kg | $45 |
| `foxtrot_koanfish` | Foxtrot Koanfish | `TROPICAL_FISH` | 3080 | 1.5kg - 25.0kg | $45 |
| `doober_fish` | Doober Fish | `COD` | 3081 | 1.5kg - 25.0kg | $45 |
| `baby_shrimp` | Baby Shrimp | `COD` | 3082 | 1.5kg - 25.0kg | $45 |

### Ikan Tier ʀᴀʀᴇ (27 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `korean_angelfish` | Korean Angelfish | `TROPICAL_FISH` | 3083 | 10.0kg - 100.0kg | $120 |
| `ballina_angelfish` | Ballina Angelfish | `TROPICAL_FISH` | 3084 | 10.0kg - 100.0kg | $120 |
| `barracuda_fish` | Barracuda Fish | `SALMON` | 3085 | 10.0kg - 100.0kg | $120 |
| `frog` | Frog | `SLIME_BALL` | 3086 | 10.0kg - 100.0kg | $120 |
| `darwin_clownfish` | Darwin Clownfish | `TROPICAL_FISH` | 3087 | 10.0kg - 100.0kg | $120 |
| `jewel_tang` | Jewel Tang | `AMETHYST_SHARD` | 3088 | 10.0kg - 100.0kg | $120 |
| `candy_butterfly` | Candy Butterfly | `TROPICAL_FISH` | 3089 | 10.0kg - 100.0kg | $120 |
| `fire_goby` | Fire Goby | `BLAZE_POWDER` | 3090 | 10.0kg - 100.0kg | $120 |
| `kau_cardinal` | Kau Cardinal | `TROPICAL_FISH` | 3091 | 10.0kg - 100.0kg | $120 |
| `rockform_cardinal` | Rockform Cardinal | `TROPICAL_FISH` | 3092 | 10.0kg - 100.0kg | $120 |
| `volsail_tang` | Volsail Tang | `TROPICAL_FISH` | 3093 | 10.0kg - 100.0kg | $120 |
| `white_clownfish` | White Clownfish | `TROPICAL_FISH` | 3094 | 10.0kg - 100.0kg | $120 |
| `scissortail_dartfish` | Scissortail Dartfish | `TROPICAL_FISH` | 3095 | 10.0kg - 100.0kg | $120 |
| `starfish` | Starfish | `PRISMARINE_CRYSTALS` | 3096 | 10.0kg - 100.0kg | $120 |
| `dark_tentacle` | Dark Tentacle | `INK_SAC` | 3097 | 10.0kg - 100.0kg | $120 |
| `charmed_tang` | Charmed Tang | `TROPICAL_FISH` | 3098 | 10.0kg - 100.0kg | $120 |
| `parrot_fish` | Parrot Fish | `TROPICAL_FISH` | 3099 | 10.0kg - 100.0kg | $120 |
| `catfish` | Catfish | `COD` | 3100 | 10.0kg - 100.0kg | $120 |
| `sheepshead_fish` | Sheepshead Fish | `COD` | 3101 | 10.0kg - 100.0kg | $120 |
| `coney_fish` | Coney Fish | `TROPICAL_FISH` | 3102 | 10.0kg - 100.0kg | $120 |
| `jellyfish` | Jellyfish | `SLIME_BALL` | 3103 | 10.0kg - 100.0kg | $120 |
| `freshwater_piranha` | Freshwater Piranha | `SALMON` | 3104 | 10.0kg - 100.0kg | $120 |
| `mossy_fishlet` | Mossy Fishlet | `MOSS_BLOCK` | 3105 | 10.0kg - 100.0kg | $120 |
| `viperangler_fish` | Viperangler Fish | `PUFFERFISH` | 3106 | 10.0kg - 100.0kg | $120 |
| `boney_eel` | Boney Eel | `BONE` | 3107 | 10.0kg - 100.0kg | $120 |
| `pirate_blue_lobster` | Pirate Blue Lobster | `PRISMARINE_SHARD` | 3108 | 10.0kg - 100.0kg | $120 |
| `toothy_fish` | Toothy Fish | `COD` | 3109 | 10.0kg - 100.0kg | $120 |

### Ikan Tier ᴇᴘɪᴄ (19 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `dorhey_tang` | Dorhey Tang | `TROPICAL_FISH` | 3110 | 50.0kg - 500.0kg | $350 |
| `unicorn_tang` | Unicorn Tang | `TROPICAL_FISH` | 3111 | 50.0kg - 500.0kg | $350 |
| `cow_clownfish` | Cow Clownfish | `TROPICAL_FISH` | 3112 | 50.0kg - 500.0kg | $350 |
| `narwhal` | Narwhal | `PRISMARINE_SHARD` | 3113 | 50.0kg - 500.0kg | $350 |
| `moorish_idol` | Moorish Idol | `TROPICAL_FISH` | 3114 | 50.0kg - 500.0kg | $350 |
| `sushi_cardinal` | Sushi Cardinal | `DRIED_KELP` | 3115 | 50.0kg - 500.0kg | $350 |
| `longnose_butterfly` | Longnose Butterfly | `TROPICAL_FISH` | 3116 | 50.0kg - 500.0kg | $350 |
| `firecoal_damsel` | Firecoal Damsel | `MAGMA_CREAM` | 3117 | 50.0kg - 500.0kg | $350 |
| `panther_grouper` | Panther Grouper | `TROPICAL_FISH` | 3118 | 50.0kg - 500.0kg | $350 |
| `domino_damsel` | Domino Damsel | `TROPICAL_FISH` | 3119 | 50.0kg - 500.0kg | $350 |
| `astra_damsel` | Astra Damsel | `ECHO_SHARD` | 3120 | 50.0kg - 500.0kg | $350 |
| `putterfish` | Putterfish | `PUFFERFISH` | 3121 | 50.0kg - 500.0kg | $350 |
| `vampire_squid` | Vampire Squid | `GLOW_INK_SAC` | 3122 | 50.0kg - 500.0kg | $350 |
| `monk_fish` | Monk Fish | `PUFFERFISH` | 3123 | 50.0kg - 500.0kg | $350 |
| `spear_guardian` | Spear Guardian | `PRISMARINE_SHARD` | 3124 | 50.0kg - 500.0kg | $350 |
| `goliath_tiger` | Goliath Tiger | `SALMON` | 3125 | 50.0kg - 500.0kg | $350 |
| `skeleton_angler_fish` | Skeleton Angler Fish | `BONE` | 3126 | 50.0kg - 500.0kg | $350 |
| `blobby_shieldfish` | Blobby Shieldfish | `SHIELD` | 3127 | 50.0kg - 500.0kg | $350 |
| `angrylion_fish` | Angrylion Fish | `PUFFERFISH` | 3128 | 50.0kg - 500.0kg | $350 |

### Ikan Tier ʟᴇɢᴇɴᴅᴀʀʏ (25 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `lined_cardinal_fish` | Lined Cardinal Fish | `TROPICAL_FISH` | 3129 | 200.0kg - 2500.0kg | $1200 |
| `yellowfin_tuna` | Yellowfin Tuna | `SALMON` | 3130 | 200.0kg - 2500.0kg | $1200 |
| `lake_sturgeon` | Lake Sturgeon | `SALMON` | 3131 | 200.0kg - 2500.0kg | $1200 |
| `bumblebee_grouper` | Bumblebee Grouper | `TROPICAL_FISH` | 3132 | 200.0kg - 2500.0kg | $1200 |
| `lobster` | Lobster | `PRISMARINE_SHARD` | 3133 | 200.0kg - 2500.0kg | $1200 |
| `lavafin_tuna` | Lavafin Tuna | `FIRE_CHARGE` | 3134 | 200.0kg - 2500.0kg | $1200 |
| `chrome_tuna` | Chrome Tuna | `IRON_INGOT` | 3135 | 200.0kg - 2500.0kg | $1200 |
| `starjam_tang` | Starjam Tang | `NETHER_STAR` | 3136 | 200.0kg - 2500.0kg | $1200 |
| `greenbee_grouper` | Greenbee Grouper | `TROPICAL_FISH` | 3137 | 200.0kg - 2500.0kg | $1200 |
| `blue_lobster` | Blue Lobster | `LAPIS_LAZULI` | 3138 | 200.0kg - 2500.0kg | $1200 |
| `enchanted_angelfish` | Enchanted Angelfish | `EXPERIENCE_BOTTLE` | 3139 | 200.0kg - 2500.0kg | $1200 |
| `magic_tang` | Magic Tang | `AMETHYST_CLUSTER` | 3140 | 200.0kg - 2500.0kg | $1200 |
| `ruby` | Ruby | `REDSTONE` | 3141 | 200.0kg - 2500.0kg | $1200 |
| `axolotl` | Axolotl | `TROPICAL_FISH` | 3142 | 200.0kg - 2500.0kg | $1200 |
| `synodontis` | Synodontis | `COD` | 3143 | 200.0kg - 2500.0kg | $1200 |
| `deep_sea_crab` | Deep Sea Crab | `NAUTILUS_SHELL` | 3144 | 200.0kg - 2500.0kg | $1200 |
| `temple_spokes_tuna` | Temple Spokes Tuna | `PRISMARINE_CRYSTALS` | 3145 | 200.0kg - 2500.0kg | $1200 |
| `manoai_statue_fish` | Manoai Statue Fish | `STONE` | 3146 | 200.0kg - 2500.0kg | $1200 |
| `primal_lobster` | Primal Lobster | `PRISMARINE_SHARD` | 3147 | 200.0kg - 2500.0kg | $1200 |
| `runic_lobster` | Runic Lobster | `ECHO_SHARD` | 3148 | 200.0kg - 2500.0kg | $1200 |
| `ancient_pufferfish` | Ancient Pufferfish | `PUFFERFISH` | 3149 | 200.0kg - 2500.0kg | $1200 |
| `primal_axolotl` | Primal Axolotl | `HEART_OF_THE_SEA` | 3150 | 200.0kg - 2500.0kg | $1200 |
| `runic_axolotl` | Runic Axolotl | `ECHO_SHARD` | 3151 | 200.0kg - 2500.0kg | $1200 |
| `flying_manta` | Flying Manta | `PHANTOM_MEMBRANE` | 3152 | 200.0kg - 2500.0kg | $1200 |
| `evolved_enchant_stone` | Evolved Enchant Stone | `ENCHANTED_BOOK` | 3153 | 200.0kg - 2500.0kg | $1200 |

### Ikan Tier ᴍʏᴛʜɪᴄ (21 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `dotted_stingray` | Dotted Stingray | `HEART_OF_THE_SEA` | 3154 | 1000.0kg - 10000.0kg | $4000 |
| `manta_ray` | Manta Ray | `PHANTOM_MEMBRANE` | 3155 | 1000.0kg - 10000.0kg | $4000 |
| `hammerhead_shark` | Hammerhead Shark | `PRISMARINE_SHARD` | 3156 | 1000.0kg - 10000.0kg | $4000 |
| `loggerhead_turtle` | Loggerhead Turtle | `TURTLE_SCUTE` | 3157 | 1000.0kg - 10000.0kg | $4000 |
| `shark` | Shark | `PRISMARINE_SHARD` | 3158 | 1000.0kg - 10000.0kg | $4000 |
| `frostglow_globe_jelly` | Frostglow Globe Jelly | `SEA_LANTERN` | 3159 | 1000.0kg - 10000.0kg | $4000 |
| `prismy_seahorse` | Prismy Seahorse | `PRISMARINE_CRYSTALS` | 3160 | 1000.0kg - 10000.0kg | $4000 |
| `blueflame_ray` | Blueflame Ray | `SOUL_CAMPFIRE` | 3161 | 1000.0kg - 10000.0kg | $4000 |
| `magma_shark` | Magma Shark | `MAGMA_BLOCK` | 3162 | 1000.0kg - 10000.0kg | $4000 |
| `hawks_turtle` | Hawks Turtle | `TURTLE_SCUTE` | 3163 | 1000.0kg - 10000.0kg | $4000 |
| `thresher_shark` | Thresher Shark | `PRISMARINE_SHARD` | 3164 | 1000.0kg - 10000.0kg | $4000 |
| `abyss_seahorse` | Abyss Seahorse | `ECHO_SHARD` | 3165 | 1000.0kg - 10000.0kg | $4000 |
| `blob_fish` | Blob Fish | `SLIME_BALL` | 3166 | 1000.0kg - 10000.0kg | $4000 |
| `crocodile` | Crocodile | `LEATHER` | 3167 | 1000.0kg - 10000.0kg | $4000 |
| `sea_crustacean` | Sea Crustacean | `NAUTILUS_SHELL` | 3168 | 1000.0kg - 10000.0kg | $4000 |
| `runic_sea_crustacean` | Runic Sea Crustacean | `ECHO_SHARD` | 3169 | 1000.0kg - 10000.0kg | $4000 |
| `ancient_squid` | Ancient Squid | `GLOW_INK_SAC` | 3170 | 1000.0kg - 10000.0kg | $4000 |
| `runic_squid` | Runic Squid | `ECHO_SHARD` | 3171 | 1000.0kg - 10000.0kg | $4000 |
| `cavern_dweller` | Cavern Dweller | `DEEPSLATE` | 3172 | 1000.0kg - 10000.0kg | $4000 |
| `primordial_octopus` | Primordial Octopus | `HEART_OF_THE_SEA` | 3173 | 1000.0kg - 10000.0kg | $4000 |
| `flatheaded_whale_shark` | Flatheaded Whale Shark | `PRISMARINE_SHARD` | 3174 | 1000.0kg - 10000.0kg | $4000 |

### Ikan Tier sᴇᴄʀᴇᴛ (37 Spesies)

| ID | Nama Ikan | Material | CMD | Rentang Bobot | Harga Dasar |
|---|---|---|---|---|---|
| `crystal_crab` | Crystal Crab | `AMETHYST_CLUSTER` | 3175 | 2000.0kg - 25000.0kg | $12500 |
| `orca` | Orca | `HEART_OF_THE_SEA` | 3176 | 2000.0kg - 25000.0kg | $12500 |
| `blob_shark` | Blob Shark | `SLIME_BALL` | 3177 | 2000.0kg - 25000.0kg | $12500 |
| `ghost_shark` | Ghost Shark | `GHAST_TEAR` | 3178 | 2000.0kg - 25000.0kg | $12500 |
| `skeleton_narwhal` | Skeleton Narwhal | `BONE` | 3179 | 2000.0kg - 25000.0kg | $12500 |
| `ghost_worm_fish` | Ghost Worm Fish | `PHANTOM_MEMBRANE` | 3180 | 2000.0kg - 25000.0kg | $12500 |
| `worm_fish` | Worm Fish | `STRING` | 3181 | 2000.0kg - 25000.0kg | $12500 |
| `megalodon` | Megalodon | `NETHER_STAR` | 3182 | 2000.0kg - 25000.0kg | $12500 |
| `1x1x1x1_comet_shark` | 1x1x1x1 Comet Shark | `NETHER_STAR` | 3183 | 2000.0kg - 25000.0kg | $12500 |
| `bloodmoon_whale` | Bloodmoon Whale | `REDSTONE_BLOCK` | 3184 | 2000.0kg - 25000.0kg | $12500 |
| `lochness_monster` | Lochness Monster | `HEART_OF_THE_SEA` | 3185 | 2000.0kg - 25000.0kg | $12500 |
| `monster_shark` | Monster Shark | `NETHER_STAR` | 3186 | 2000.0kg - 25000.0kg | $12500 |
| `eerie_shark` | Eerie Shark | `ECHO_SHARD` | 3187 | 2000.0kg - 25000.0kg | $12500 |
| `great_whale` | Great Whale | `HEART_OF_THE_SEA` | 3188 | 2000.0kg - 25000.0kg | $12500 |
| `frostborn_shark` | Frostborn Shark | `BLUE_ICE` | 3189 | 2000.0kg - 25000.0kg | $12500 |
| `armored_shark` | Armored Shark | `NETHERITE_INGOT` | 3190 | 2000.0kg - 25000.0kg | $12500 |
| `scare` | Scare | `JACK_O_LANTERN` | 3191 | 2000.0kg - 25000.0kg | $12500 |
| `queen_crab` | Queen Crab | `NAUTILUS_SHELL` | 3192 | 2000.0kg - 25000.0kg | $12500 |
| `king_crab` | King Crab | `NAUTILUS_SHELL` | 3193 | 2000.0kg - 25000.0kg | $12500 |
| `cryoshade_glider` | Cryoshade Glider | `PACKED_ICE` | 3194 | 2000.0kg - 25000.0kg | $12500 |
| `panther_eel` | Panther Eel | `BLACK_DYE` | 3195 | 2000.0kg - 25000.0kg | $12500 |
| `giant_squid` | Giant Squid | `GLOW_INK_SAC` | 3196 | 2000.0kg - 25000.0kg | $12500 |
| `depthseeker_ray` | Depthseeker Ray | `ECHO_SHARD` | 3197 | 2000.0kg - 25000.0kg | $12500 |
| `robot_kraken` | Robot Kraken | `BEACON` | 3198 | 2000.0kg - 25000.0kg | $12500 |
| `mosasaur_shark` | Mosasaur Shark | `PRISMARINE_CRYSTALS` | 3199 | 2000.0kg - 25000.0kg | $12500 |
| `king_jelly` | King Jelly | `SLIME_BLOCK` | 3200 | 2000.0kg - 25000.0kg | $12500 |
| `bone_whale` | Bone Whale | `BONE_BLOCK` | 3201 | 2000.0kg - 25000.0kg | $12500 |
| `elshark_gran_maja` | Elshark Gran Maja | `NETHER_STAR` | 3202 | 2000.0kg - 25000.0kg | $12500 |
| `ancient_whale` | Ancient Whale | `HEART_OF_THE_SEA` | 3203 | 2000.0kg - 25000.0kg | $12500 |
| `gladiator_shark` | Gladiator Shark | `NETHERITE_SWORD` | 3204 | 2000.0kg - 25000.0kg | $12500 |
| `ancient_lochness_monster` | Ancient Lochness Monster | `HEART_OF_THE_SEA` | 3205 | 2000.0kg - 25000.0kg | $12500 |
| `talon_serpent` | Talon Serpent | `PRISMARINE_SHARD` | 3206 | 2000.0kg - 25000.0kg | $12500 |
| `strawberry_choc_megalodon` | Strawberry Choc Megalodon | `COCOA_BEANS` | 3207 | 2000.0kg - 25000.0kg | $12500 |
| `krampus_shark` | Krampus Shark | `COAL_BLOCK` | 3208 | 2000.0kg - 25000.0kg | $12500 |
| `emerald_winter_whale` | Emerald Winter Whale | `EMERALD_BLOCK` | 3209 | 2000.0kg - 25000.0kg | $12500 |
| `winter_frost_shark` | Winter Frost Shark | `ICE` | 3210 | 2000.0kg - 25000.0kg | $12500 |
| `icebreaker_whale` | Icebreaker Whale | `BLUE_ICE` | 3211 | 2000.0kg - 25000.0kg | $12500 |

---

## Perintah & Izin

**Alias Perintah Utama**: `/endersfishing`, `/eaf`, `/fishing`

| Perintah | Deskripsi | Izin (Permission) | Default |
|---|---|---|---|
| `/eaf help` | Menampilkan panduan bantuan perintah | None | Semua Pemain |
| `/eaf journal` | Membuka antarmuka Fish Journal & Codex | `eaf.journal` | `true` |
| `/eaf chum [player] [jumlah]` | Memberikan Chum Bucket untuk memicu bait vortex | `eaf.chum` | `op` |
| `/eaf afk` | Mengaktifkan/menonaktifkan mode auto-fishing AFK | `eaf.afk` | `true` |
| `/eaf shop` | Membuka antarmuka toko joran, umpan, dan ikan | `eaf.shop` | `true` |
| `/eaf sell` | Menjual ikan yang sedang dipegang | `eaf.sell` | `true` |
| `/eaf sell all` | Menjual seluruh ikan di inventaris | `eaf.sell` | `true` |
| `/eaf repair` | Memperbaiki joran kustom yang sedang dipegang | `eaf.repair` | `true` |
| `/eaf stats [player]` | Melihat statistik memancing pemain | None | Semua Pemain |
| `/eaf collector` | Membuka antarmuka Mystic Fish Collector | `eaf.collector` | `true` |
| `/eaf tournament join` | Bergabung ke turnamen memancing yang sedang aktif | None | Semua Pemain |
| `/eaf rod <id> [jumlah]` | Memberikan joran pancing kustom | `eaf.rod` | `op` |
| `/eaf bait <id> [jumlah]` | Memberikan umpan pancing kustom | `eaf.bait` | `op` |
| `/eaf reload` | Memuat ulang seluruh berkas konfigurasi | `eaf.admin.reload` | `op` |
| `/eaf event <start|stop|info>` | Mengelola event memancing global server | `eaf.admin` | `op` |
| `/eaf tournament <start|stop|info>` | Mengelola turnamen memancing server | `eaf.admin.tournament` | `op` |

---

## Panduan Instalasi

1. **Unduh**: Dapatkan berkas `EndersAdvancedFishing-1.4.jar` dari build rilis.
2. **Pasang**: Masukkan berkas JAR ke dalam folder `plugins/` server Paper atau Purpur Anda.
3. **Dependensi Opsional**: Pasang [Vault](https://www.spigotmc.org/resources/vault.34315/) untuk integrasi ekonomi, [AuraSkills](https://wiki.aurelium.dev/auraskills/api) untuk perolehan EXP keahlian, dan [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) untuk leaderboard.
4. **Nyalakan Server**: Jalankan server untuk menghasilkan berkas konfigurasi secara otomatis di folder `plugins/EndersAdvancedFishing/`.

---

## Lisensi

Proyek ini dilisensikan di bawah [GNU General Public License v2.0 (GPL-2.0)](LICENSE).
