
## 📌 Proje Hakkında

Enoca staj programı kapsamında geliştirilmiş modern bir Android uygulaması. E-ticaret platformları için ürün yönetimi, görsel/AR dosya yükleme ve listeleme özellikleri sunar.

---

## 🎯 Temel Özellikler

### Ürün Yönetimi
- ✅ Yeni ürün ekleme (ad, fiyat, kategori, görsel, AR dosyası)
- ✅ Ürün listeleme (sayfalama, sıralama, filtreleme)
- ✅ Pull-to-refresh ile güncelleme
- ✅ Kategori bazlı organizasyon

### Performans
- ✅ Otomatik görsel optimizasyonu (1280px, JPEG 85%)
- ✅ Bellek dostu dosya yükleme (streaming)
- ✅ Hızlı ve akıcı kullanıcı deneyimi

---

## 🏗️ Mimari ve Teknolojiler

### Mimari Yapı
```
Clean Architecture + MVI Pattern

Presentation (UI)     →  Jetpack Compose, ViewModel
    ↕️
Domain (Business)     →  UseCases, Models
    ↕️
Data (Network)        →  Retrofit, Repository
```

### Kullanılan Teknolojiler

**UI & Framework**
- Jetpack Compose (Modern declarative UI)
- Material Design 3
- Hilt (Dependency Injection)
- Navigation Compose

**Async & State**
- Kotlin Coroutines & Flow
- ViewModel & StateFlow
- MVI Pattern (Intent → State → Event)

**Network**
- Retrofit (REST API)
- Moshi (JSON parsing)
- OkHttp (Logging & Interceptors)
- Coil (Image loading)

**Diğer**
- Multipart upload (görsel/AR dosyaları)
- SwipeRefresh
- Custom validasyon

---

## 📂 Paket Yapısı

```
com.tayyipgunay.firststajproject/
│
├── 📁 core/
│   │
│   ├── 📁 mvi/
│   │   └── 📄 MVIComponent.kt                      # MVI base interface
│   │
│   └── 📁 util/
│       ├── 📄 Async.kt                             # Async helper'lar
│       ├── 📄 Constants.kt                         # BASE_URL, endpoint'ler
│       ├── 📄 MultipartUtils.kt                    # File upload, downscale, compress
│       ├── 📄 RequestBodies.kt                     # Extension (toPlainBody)
│       └── 📄 Resource.kt                          # Sealed class (Success/Error/Loading)
│
├── 📁 data/
│   │
│   ├── 📁 remote/
│   │   │
│   │   ├── 📁 dto/
│   │   │   ├── 📄 CategoryDto.kt                   # Category DTO + mapper
│   │   │   └── 📄 ProductDto.kt                    # Product DTO + mapper
│   │   │
│   │   └── 📄 ProductApi.kt                        # Retrofit API interface
│   │
│   └── 📁 repository/
│       └── 📄 ProductRepositoryImpl.kt             # Repository implementation
│
├── 📁 di/
│   └── 📄 AppModule.kt                             # Hilt module (Retrofit, Repository DI)
│
├── 📁 domain/
│   │
│   ├── 📁 model/
│   │   ├── 📄 Category.kt                          # Domain model
│   │   ├── 📄 ModelTypeUi.kt                       # UI model (Giyim, Elektronik...)
│   │   ├── 📄 Product.kt                           # Domain model
│   │   └── 📄 ProductSummary.kt                    # List item model
│   │
│   ├── 📁 repository/
│   │   └── 📄 ProductRepository.kt                 # Repository interface
│   │
│   └── 📁 usecase/
│       ├── 📄 AddProductUseCase.kt                 # Ürün ekleme logic
│       ├── 📄 GetCategoriesUseCase.kt              # Kategori getirme
│       └── 📄 GetProductUseCase.kt                 # Ürün listesi getirme
│
├── 📁 presentation/
│   │
│   ├── 📁 add/                                     # Ürün Ekleme Feature
│   │   ├── 📄 AddProductEvent.kt                   # Events (ShowMessage, NavigateBack...)
│   │   ├── 📄 AddProductIntent.kt                  # User actions (Save, Name, Price...)
│   │   ├── 📄 AddProductScreen.kt                  # Compose UI + Form + Dialogs
│   │   ├── 📄 AddProductState.kt                   # UI state (name, price, error...)
│   │   └── 📄 AddProductViewModel.kt               # ViewModel + MVI logic
│   │
│   ├── 📁 common/
│   │   │
│   │   ├── 📄 ConfirmId.kt                         # Enum (SaveProduct, DeleteProduct...)
│   │   │
│   │   └── 📁 events/
│   │       ├── 📄 MessageChannel.kt                # Enum (Toast, Snackbar, Dialog)
│   │       ├── 📄 MessageType.kt                   # Enum (Success, Error, Warning, Info)
│   │       └── 📄 UiEvent.kt                       # Shared events (ShowMessage, ShowConfirmDialog...)
│   │
│   ├── 📁 products/
│   │   │
│   │   └── 📁 list/                                # Ürün Listesi Feature
│   │       ├── 📄 ProductListEvent.kt              # Events (ShowMessage)
│   │       ├── 📄 ProductListIntent.kt             # User actions (Load, Refresh, ChangeSort...)
│   │       ├── 📄 ProductListScreen.kt             # Compose UI + List + TopBar + Pagination
│   │       ├── 📄 ProductListState.kt              # UI state (items, isLoading, page, size...)
│   │       ├── 📄 ProductListViewModel.kt          # ViewModel + MVI logic
│   │       └── 📄 ProductSort.kt                   # Enum (PRICE_ASC, ACTIVE_FIRST...)
│   │
│   └── 📁 ui/
│       │
│       ├── 📁 components/
│       │   ├── 📄 PillChip.kt                      # Custom chip component
│       │   └── 📄 StatusBadge.kt                   # Aktif/Pasif badge
│       │
│       └── 📁 state/
│           └── 📄 States.kt                        # EmptyStateCard, ErrorStateCard, LoadingStateCard
│
└── 📄 MainActivity.kt                              # NavHost + Route management
```

**İstatistikler:**
- 📦 Toplam Paket: 17
- 📄 Kotlin Dosyası: 52
- 🏗️ Katman: 3 (Presentation, Domain, Data)
- 🎯 Feature: 2 (Add Product, Product List)
- 📐 Pattern: MVI + Clean Architecture

---

## 🔄 MVI Veri Akışı

```
User Action (Intent)  →  ViewModel  →  UseCase  →  Repository  →  API
                                          ↓
                                    State Update
                                          ↓
                                      UI Recompose
                                          ↓
                                One-Time Event (Snackbar, Navigation)
```

**Örnek:**
```kotlin
// Intent
Intent.Save → ViewModel

// State
data class AddProductState(
    val name: String,
    val isSaving: Boolean,
    val error: String?
)

// Event
Event.ShowMessage("Başarıyla kaydedildi!")
Event.NavigateBack
```

---

## ✨ Teknik Detaylar

### 1. Görsel Optimizasyonu
```kotlin
• Max boyut: 1280px (uzun kenar)
• JPEG kalite: %85
• Streaming upload: Bellekte tüm dosya tutulmaz
• Sonuç: %70 bellek tasarrufu
```

### 2. Validasyon
- Zorunlu alan kontrolü (ad, fiyat, kategori, görsel, AR)
- Locale-safe fiyat parsing (virgül/nokta desteği)
- Kullanıcı dostu Türkçe hata mesajları

### 3. Mesajlaşma Sistemi
- **Snackbar**: Bildirimler (varsayılan)
- **Toast**: Kısa mesajlar
- **Dialog**: Onaylar ve önemli bilgiler
- Kanal bazlı yönetim (MessageChannel enum)

### 4. Hata Yönetimi
```kotlin
• "İnternet bağlantısı hatası"
• "Sunucu hatası (500)"
• "Lütfen bir görsel seçin"
• Retry mekanizması
```

---

## 📡 API Entegrasyonu

**Base URL:** `http://37.156.246.102:9082`

### Endpoint'ler
```http
GET  /api/products        # Ürün listesi (page, size, sort)
POST /api/products        # Yeni ürün (multipart/form-data)
GET  /api/categories      # Kategori listesi
```

---

## 🚀 Kurulum

### Gereksinimler
- Android Studio Hedgehog+
- Min SDK 24 (Android 7.0)
- Target SDK 34
- Kotlin 1.9+

### Adımlar
1. Projeyi klonlayın
2. Android Studio'da açın
3. Gradle sync yapın
4. Run → 'app'

---

## 🎨 UI/UX Özellikleri

- Material Design 3 renk şeması
- Loading/Empty/Error state'leri
- Responsive layout
- Skeleton screens
- Snackbar feedback
- Confirm dialogları

---

## 🐛 Çözülen Sorunlar

| Sorun | Çözüm |
|-------|-------|
| Bellek artışı (225MB→366MB) | Görsel downscale + streaming upload |
| Null upload → 500 hatası | Frontend validasyonu |
| Virgül/nokta fiyat girişi | Locale-aware parsing |

---

## 📚 Öğrendiklerim

**Teknik**
- Clean Architecture & MVI pattern
- Jetpack Compose ile modern UI
- Coroutines & Flow ile reactive programming
- Multipart file upload optimizasyonu
- Memory profiling

**Best Practices**
- Single source of truth
- Unidirectional data flow
- Separation of concerns
- User-centric error handling

---

## 🔮 Gelecek Planları

- [ ] Unit/UI testleri
- [ ] Offline support (Room)
- [ ] Ürün düzenleme/silme
- [ ] AR preview
- [ ] Çoklu dil (i18n)

---

## 👨‍💻 Geliştirici

**Tayyip Günay** - Enoca Staj Projesi 2025

---

**⚡ Not:** Bu proje modern Android development pratiklerini göstermek için geliştirilmiştir.

---

 
                                       EKRAN GÖRÜNTÜLERİ
           
![WhatsApp Görsel 2025-10-08 saat 12 43 04_b0c28f2c](https://github.com/user-attachments/assets/33a7c2b5-57fd-473d-a946-b0548360caa2)
![WhatsApp Görsel 2025-10-08 saat 12 43 04_28261d88](https://github.com/user-attachments/assets/707b2afb-1189-4fa4-839e-cc7feb6c8572)
![WhatsApp Görsel 2025-10-08 saat 12 43 05_a67c09a0](https://github.com/user-attachments/assets/41445e6b-d738-4110-9866-db32b9095fc0)
![WhatsApp Görsel 2025-10-08 saat 12 43 05_8bf6af82](https://github.com/user-attachments/assets/431805b6-37cd-4fb2-9bdb-5d8127b48de7)
![WhatsApp Görsel 2025-10-08 saat 12 43 05_a5064e73](https://github.com/user-attachments/assets/41516311-6952-4d08-b41b-7ab6bf5d610c)
![WhatsApp Görsel 2025-10-08 saat 12 43 05_91836f87](https://github.com/user-attachments/assets/e8067921-150d-44b1-9f01-f9b84e2a463c)
![WhatsApp Görsel 2025-10-08 saat 12 43 08_088040ca](https://github.com/user-attachments/assets/9b4dd060-4124-432d-b3fd-f19fb43c360d)
![WhatsApp Görsel 2025-10-08 saat 12 43 07_d3a3561b](https://github.com/user-attachments/assets/760c8017-e7a5-429a-9f68-9912135e79da)
![WhatsApp Görsel 2025-10-08 saat 12 43 07_37efde8a](https://github.com/user-attachments/assets/0ae294ad-65de-4376-8d00-5882aa8a38ce)
![WhatsApp Görsel 2025-10-08 saat 12 43 06_42570358](https://github.com/user-attachments/assets/859e58c2-be4c-47e8-b881-4d0ae74ddb64)













