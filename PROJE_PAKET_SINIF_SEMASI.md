# Proje Paket ve Sınıf Yapısı Şeması

## 📦 Paket Hiyerarşisi

```
com.tayyipgunay.firststajproject
│
├── 📱 App.kt (Application)
├── 📱 MainActivity.kt (Activity)
│
├── 🎯 domain/ (Domain Layer - İş Mantığı)
│   ├── model/
│   │   ├── Product.kt
│   │   ├── ProductSummary.kt
│   │   ├── Category.kt
│   │   └── ModelTypeUi.kt
│   ├── repository/
│   │   ├── ProductRepository.kt (interface)
│   │   └── ProductRepository2.kt (interface)
│   └── usecase/
│       ├── GetProductUseCase2.kt
│       ├── GetCategoriesUseCase.kt
│       └── AddProductUseCase.kt
│
├── 💾 data/ (Data Layer - Veri Katmanı)
│   ├── auth/
│   │   └── TokenStore.kt
│   ├── network/
│   │   ├── AuthInterceptor.kt
│   │   └── HttpErrorMapper.kt
│   ├── paging/
│   │   ├── ProductPagingSource.kt
│   │   └── PagingConstants.kt
│   ├── remote/
│   │   ├── ProductApi.kt (Retrofit Interface)
│   │   └── dto/
│   │       ├── ProductDto.kt
│   │       └── CategoryDto.kt
│   └── repository/
│       ├── ProductRepositoryImpl.kt
│       └── ProductRepositoryImpl2.kt
│
├── 🎨 presentation/ (Presentation Layer - UI Katmanı)
│   ├── products/
│   │   └── list/
│   │       ├── ProductListScreen.kt
│   │       ├── ProductListViewModel2.kt
│   │       ├── ProductListContract.kt (State, Intent, Effect)
│   │       ├── ProductListReducer.kt
│   │       └── ProductSort.kt
│   ├── add/
│   │   ├── AddProductScreen.kt
│   │   ├── AddProductViewModel.kt
│   │   ├── AddProductContract.kt (State, Intent, Effect)
│   │   └── AddProductReducer.kt
│   ├── common/
│   │   ├── ConfirmId.kt
│   │   └── events/
│   │       └── MessageType.kt
│   └── ui/
│       ├── components/
│       │   ├── Badges.kt
│       │   └── Chips.kt
│       ├── state/
│       │   └── States.kt
│       └── theme/
│           ├── Theme.kt
│           ├── Color.kt
│           └── Type.kt
│
├── 🔧 core/ (Core - Ortak Yardımcılar)
│   ├── error/
│   │   ├── AppError.kt
│   │   ├── AppException.kt
│   │   ├── ProblemJson.kt
│   │   ├── ThrowableMapping.kt
│   │   └── UserFacingMessage.kt
│   ├── mvi/
│   │   └── MVIComponent.kt (interface)
│   └── util/
│       ├── Constants.kt
│       ├── Resource.kt
│       ├── MultipartUtils.kt
│       └── RequestBodies.kt
│
└── 💉 di/ (Dependency Injection)
    └── AppModule.kt (Hilt Module)
```

## 🏗️ Mimari Katmanlar ve İlişkiler

### Clean Architecture Katmanları

```
┌─────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                         │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │   Screen     │  │  ViewModel   │                    │
│  │  (Compose)   │◄─┤  (MVI)       │                    │
│  └──────────────┘  └──────┬───────┘                    │
│                           │                             │
│                    ┌──────▼───────┐                    │
│                    │   Reducer    │                    │
│                    └──────────────┘                    │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                 DOMAIN LAYER                            │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │  UseCase    │  │  Repository  │                    │
│  │             │◄─┤  (Interface) │                    │
│  └──────────────┘  └──────┬───────┘                    │
│                           │                             │
│                    ┌──────▼───────┐                    │
│                    │    Model     │                    │
│                    └──────────────┘                    │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                  DATA LAYER                             │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │  Repository  │  │     API      │                    │
│  │  (Impl)      │◄─┤  (Retrofit)  │                    │
│  └──────────────┘  └──────┬───────┘                    │
│                           │                             │
│                    ┌──────▼───────┐                    │
│                    │     DTO      │                    │
│                    └──────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

## 🔄 MVI Pattern Yapısı

```
┌─────────────────────────────────────────────────────┐
│              ProductListViewModel                    │
│  ┌──────────────────────────────────────────────┐  │
│  │  StateFlow<State>                            │  │
│  │  SharedFlow<Effect>                          │  │
│  │  Flow<PagingData<ProductSummary>>            │  │
│  └──────────────────────────────────────────────┘  │
│                      │                              │
│         ┌────────────┼────────────┐                 │
│         │            │            │                 │
│         ▼            ▼            ▼                 │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │  State   │ │  Intent  │ │  Effect  │          │
│  │(Contract)│ │(Contract)│ │(Contract)│          │
│  └──────────┘ └──────────┘ └──────────┘          │
│         │            │            │                 │
│         └────────────┼────────────┘                 │
│                      ▼                              │
│              ┌──────────────┐                       │
│              │   Reducer    │                       │
│              └──────────────┘                       │
└─────────────────────────────────────────────────────┘
```

## 📊 Sınıf İlişkileri

### Product List Feature

```
ProductListScreen
    │
    ├──► ProductListViewModel2
    │       │
    │       ├──► ProductListContract.State
    │       ├──► ProductListContract.Intent
    │       ├──► ProductListContract.Effect
    │       │
    │       ├──► ProductListReducer
    │       │
    │       └──► ProductRepository2
    │               │
    │               └──► ProductRepositoryImpl2
    │                       │
    │                       ├──► ProductApi
    │                       └──► HttpErrorMapper
```

### Add Product Feature

```
AddProductScreen
    │
    ├──► AddProductViewModel
    │       │
    │       ├──► AddProductContract.State
    │       ├──► AddProductContract.Intent
    │       ├──► AddProductContract.Effect
    │       │
    │       ├──► AddProductReducer
    │       │
    │       └──► AddProductUseCase
    │               │
    │               └──► ProductRepository
    │                       │
    │                       └──► ProductRepositoryImpl
    │                               │
    │                               ├──► ProductApi
    │                               └──► HttpErrorMapper
```

## 🔌 Dependency Injection (Hilt)

```
AppModule
    │
    ├──► TokenStore
    ├──► AuthInterceptor
    ├──► OkHttpClient
    ├──► Moshi
    ├──► Retrofit
    ├──► ProductApi
    ├──► HttpErrorMapper
    ├──► ProductRepository (→ ProductRepositoryImpl)
    ├──► ProductRepository2 (→ ProductRepositoryImpl2)
    ├──► ProductListReducer
    └──► AddProductReducer
```

## 🎯 Ana Bileşenler

### Domain Models
- **Product**: Ürün domain modeli
- **ProductSummary**: Ürün özet modeli (Paging için)
- **Category**: Kategori modeli
- **ModelTypeUi**: Model tipi enum

### Data Transfer Objects (DTOs)
- **ProductDto**: API'den gelen ürün verisi
- **CategoryDto**: API'den gelen kategori verisi

### Repositories
- **ProductRepository**: Ürün ekleme için repository interface
- **ProductRepository2**: Ürün listeleme (Paging) için repository interface
- **ProductRepositoryImpl**: ProductRepository implementasyonu
- **ProductRepositoryImpl2**: ProductRepository2 implementasyonu

### Use Cases
- **GetProductUseCase2**: Ürün listesi getirme use case
- **GetCategoriesUseCase**: Kategori listesi getirme use case
- **AddProductUseCase**: Ürün ekleme use case

### ViewModels
- **ProductListViewModel2**: Ürün listesi ViewModel (MVI)
- **AddProductViewModel**: Ürün ekleme ViewModel (MVI)

### Reducers
- **ProductListReducer**: Ürün listesi state yönetimi
- **AddProductReducer**: Ürün ekleme state yönetimi

### Screens (Jetpack Compose)
- **ProductListScreen**: Ürün listesi ekranı
- **AddProductScreen**: Ürün ekleme ekranı

## 🔐 Authentication & Network

```
TokenStore
    │
    └──► AuthInterceptor
            │
            └──► OkHttpClient
                    │
                    └──► Retrofit
                            │
                            └──► ProductApi
```

## 📄 Özet

Bu proje **Clean Architecture** ve **MVI (Model-View-Intent)** pattern'ini kullanarak geliştirilmiş bir Android uygulamasıdır.

- **3 Katmanlı Mimari**: Presentation → Domain → Data
- **MVI Pattern**: State, Intent, Effect yapısı
- **Dependency Injection**: Hilt kullanılıyor
- **Jetpack Compose**: Modern UI framework
- **Paging 3**: Ürün listesi için sayfalama
- **Retrofit + Moshi**: Network ve JSON parsing
- **Coroutines + Flow**: Asenkron işlemler

