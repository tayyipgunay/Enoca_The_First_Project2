

### 4️⃣ Error Handling Akışı

```
Exception/Throwable
    │
    ├──► toAppError() extension
    │       │
    │       └──► AppError (sealed class)
    │               ├── Network
    │               ├── Http(status, message)
    │               └── Unknown(throwable)
    │
    ├──► AppException(appError: AppError)
    │       │
    │       └──► appError.toUserMessage()
    │               │
    │               └──► String (kullanıcıya gösterilecek mesaj)
    │
    └──► HttpErrorMapper
            │
            ├──► map(response: Response): String
            │       │
            │       └──► Moshi → ProblemJson
            │               └──► ProblemJson.detail ?: ProblemJson.title
```

## 📊 Dependency Injection (Hilt) Grafiği

```
AppModule (@Module @InstallIn(SingletonComponent::class))
    │
    ├──► TokenStore
    │       └──► SharedPreferences (Context)
    │
    ├──► AuthInterceptor
    │       └──► TokenStore.getAccessToken()
    │
    ├──► OkHttpClient
    │       ├──► AuthInterceptor
    │       └──► HttpLoggingInterceptor
    │
    ├──► Moshi
    │       └──► KotlinJsonAdapterFactory
    │
    ├──► Retrofit
    │       ├──► OkHttpClient
    │       ├──► Moshi
    │       └──► BASE_URL
    │
    ├──► ProductApi
    │       └──► Retrofit.create(ProductApi::class.java)
    │
    ├──► HttpErrorMapper
    │       └──► Moshi
    │
    ├──► ProductRepository (interface)
    │       └──► ProductRepositoryImpl
    │               ├──► ProductApi
    │               ├──► Context
    │               └──► HttpErrorMapper
    │
    ├──► ProductRepository2 (interface)
    │       └──► ProductRepositoryImpl2
    │               ├──► ProductApi
    │               └──► HttpErrorMapper
    │
    ├──► ProductListReducer
    │       └──► (stateless, no dependencies)
    │
    └──► AddProductReducer
            └──► (stateless, no dependencies)
```

## 🎯 MVI Pattern Detayı

```
┌─────────────────────────────────────────────────────────────┐
│                    MVI Component                             │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              ViewModel (MVIComponent)                │  │
│  │                                                      │  │
│  │  ┌──────────────────────────────────────────────┐  │  │
│  │  │  StateFlow<State>                            │  │  │
│  │  │  - Kalıcı UI durumu                          │  │  │
│  │  │  - Observable (collectAsState)               │  │  │
│  │  └──────────────────────────────────────────────┘  │  │
│  │                                                      │  │
│  │  ┌──────────────────────────────────────────────┐  │  │
│  │  │  SharedFlow<Effect>                         │  │  │
│  │  │  - Tek seferlik olaylar                     │  │  │
│  │  │  - Navigation, Messages, Dialogs            │  │  │
│  │  └──────────────────────────────────────────────┘  │  │
│  │                                                      │  │
│  │  ┌──────────────────────────────────────────────┐  │  │
│  │  │  fun onIntent(intent: Intent)                │  │  │
│  │  │  - Kullanıcı aksiyonları                     │  │  │
│  │  │  - State güncelleme                          │  │  │
│  │  │  - Effect emit                               │  │  │
│  │  └──────────────────────────────────────────────┘  │  │
│  │                                                      │  │
│  │         │                                            │  │
│  │         ▼                                            │  │
│  │  ┌──────────────────────────────────────────────┐  │  │
│  │  │         Reducer                              │  │  │
│  │  │  fun reduce(state, result): State            │  │  │
│  │  │  - Pure function                             │  │  │
│  │  │  - Immutable state updates                   │  │  │
│  │  └──────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Contract (State, Intent, Effect)       │  │
│  │                                                      │  │
│  │  - State: data class (immutable)                   │  │
│  │  - Intent: sealed interface (user actions)         │  │
│  │  - Effect: sealed interface (one-shot events)      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 📈 Paging 3 Akışı

```
ProductListViewModel2
    │
    └──► GetProductUseCase2.execute(sort)
            │
            └──► ProductRepository2.getProductsPaging(sort)
                    │
                    └──► Pager(
                            config = PagingConfig(
                                pageSize = 20,
                                prefetchDistance = 5,
                                initialLoadSize = 20,
                                enablePlaceholders = false
                            ),
                            pagingSourceFactory = {
                                ProductPagingSource { page, size ->
                                    api.getProducts(page, size, sort)
                                        .body()
                                        .orEmpty()
                                        .map { it.toSummaryDomain() }
                                }
                            }
                        ).flow
                            │
                            └──► Flow<PagingData<ProductSummary>>
                                    │
                                    └──► Screen.collectAsLazyPagingItems()
                                            │
                                            └──► LazyColumn { items(pagingItems) { ... } }
```

## 🔐 Authentication Flow

```
Constants.TOKEN (hardcoded)
    │
    ▼
AppModule.provideTokenStore()
    │
    └──► TokenStore.saveAccessToken(Constants.TOKEN)
            │
            ▼
AuthInterceptor.intercept()
    │
    ├──► TokenStore.getAccessToken()
    │       │
    │       └──► request.addHeader("Authorization", "Bearer $token")
    │
    └──► chain.proceed(request)
            │
            └──► API calls with authentication
```

## 📝 Özet İstatistikler

- **Toplam Paket Sayısı**: 12 ana paket
- **Toplam Sınıf Sayısı**: ~49 Kotlin dosyası
- **Mimari Katmanlar**: 3 (Presentation, Domain, Data)
- **Design Pattern**: MVI (Model-View-Intent)
- **Dependency Injection**: Hilt
- **UI Framework**: Jetpack Compose
- **Network**: Retrofit + Moshi
- **Paging**: Paging 3 Library
- **Asenkron İşlemler**: Kotlin Coroutines + Flow

## 🎨 Mimari Prensipler

1. **Clean Architecture**: Katmanlar arası bağımlılık yönü: Presentation → Domain ← Data
2. **Dependency Inversion**: Repository interface'leri Domain'de, implementasyonlar Data'da
3. **Single Responsibility**: Her sınıf tek bir sorumluluğa sahip
4. **MVI Pattern**: State, Intent, Effect ayrımı
5. **Immutable State**: StateFlow ile immutable state yönetimi
6. **Error Handling**: Merkezi hata yönetimi (AppError, AppException)
7. **Type Safety**: Sealed classes ve interfaces ile type-safe kod

---

**Son Güncelleme**: Proje yapısına göre güncellenmiştir.