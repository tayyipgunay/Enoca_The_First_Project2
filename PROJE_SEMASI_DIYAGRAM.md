# Proje Yapısı Görsel Diyagramı

## 📐 Paket Yapısı Ağaç Diyagramı

```mermaid
graph TD
    A[com.tayyipgunay.firststajproject] --> B[App.kt]
    A --> C[MainActivity.kt]
    A --> D[domain]
    A --> E[data]
    A --> F[presentation]
    A --> G[core]
    A --> H[di]
    
    D --> D1[model]
    D --> D2[repository]
    D --> D3[usecase]
    
    D1 --> D1A[Product.kt]
    D1 --> D1B[ProductSummary.kt]
    D1 --> D1C[Category.kt]
    D1 --> D1D[ModelTypeUi.kt]
    
    D2 --> D2A[ProductRepository.kt]
    D2 --> D2B[ProductRepository2.kt]
    
    D3 --> D3A[GetProductUseCase2.kt]
    D3 --> D3B[GetCategoriesUseCase.kt]
    D3 --> D3C[AddProductUseCase.kt]
    
    E --> E1[auth]
    E --> E2[network]
    E --> E3[paging]
    E --> E4[remote]
    E --> E5[repository]
    
    E1 --> E1A[TokenStore.kt]
    E2 --> E2A[AuthInterceptor.kt]
    E2 --> E2B[HttpErrorMapper.kt]
    E3 --> E3A[ProductPagingSource.kt]
    E3 --> E3B[PagingConstants.kt]
    E4 --> E4A[ProductApi.kt]
    E4 --> E4B[dto]
    E4B --> E4B1[ProductDto.kt]
    E4B --> E4B2[CategoryDto.kt]
    E5 --> E5A[ProductRepositoryImpl.kt]
    E5 --> E5B[ProductRepositoryImpl2.kt]
    
    F --> F1[products]
    F --> F2[add]
    F --> F3[common]
    F --> F4[ui]
    
    F1 --> F1A[list]
    F1A --> F1A1[ProductListScreen.kt]
    F1A --> F1A2[ProductListViewModel2.kt]
    F1A --> F1A3[ProductListContract.kt]
    F1A --> F1A4[ProductListReducer.kt]
    F1A --> F1A5[ProductSort.kt]
    
    F2 --> F2A[AddProductScreen.kt]
    F2 --> F2B[AddProductViewModel.kt]
    F2 --> F2C[AddProductContract.kt]
    F2 --> F2D[AddProductReducer.kt]
    
    F3 --> F3A[ConfirmId.kt]
    F3 --> F3B[events]
    F3B --> F3B1[MessageType.kt]
    
    F4 --> F4A[components]
    F4 --> F4B[state]
    F4 --> F4C[theme]
    F4A --> F4A1[Badges.kt]
    F4A --> F4A2[Chips.kt]
    F4B --> F4B1[States.kt]
    F4C --> F4C1[Theme.kt]
    F4C --> F4C2[Color.kt]
    F4C --> F4C3[Type.kt]
    
    G --> G1[error]
    G --> G2[mvi]
    G --> G3[util]
    
    G1 --> G1A[AppError.kt]
    G1 --> G1B[AppException.kt]
    G1 --> G1C[ProblemJson.kt]
    G1 --> G1D[ThrowableMapping.kt]
    G1 --> G1E[UserFacingMessage.kt]
    
    G2 --> G2A[MVIComponent.kt]
    
    G3 --> G3A[Constants.kt]
    G3 --> G3B[Resource.kt]
    G3 --> G3C[MultipartUtils.kt]
    G3 --> G3D[RequestBodies.kt]
    
    H --> H1[AppModule.kt]
```

## 🏗️ Mimari Katman İlişkileri

```mermaid
graph TB
    subgraph Presentation["🎨 PRESENTATION LAYER"]
        PS[ProductListScreen]
        AS[AddProductScreen]
        PV[ProductListViewModel2]
        AV[AddProductViewModel]
        PR[ProductListReducer]
        AR[AddProductReducer]
    end
    
    subgraph Domain["🎯 DOMAIN LAYER"]
        GPU[GetProductUseCase2]
        APU[AddProductUseCase]
        GCU[GetCategoriesUseCase]
        PR2[ProductRepository2 Interface]
        PR1[ProductRepository Interface]
        PM[Product Model]
        CM[Category Model]
    end
    
    subgraph Data["💾 DATA LAYER"]
        PRI2[ProductRepositoryImpl2]
        PRI1[ProductRepositoryImpl2]
        PA[ProductApi]
        PS2[ProductPagingSource]
        DTO1[ProductDto]
        DTO2[CategoryDto]
    end
    
    subgraph Network["🌐 NETWORK"]
        AI[AuthInterceptor]
        TS[TokenStore]
        HM[HttpErrorMapper]
        RT[Retrofit]
    end
    
    subgraph DI["💉 DEPENDENCY INJECTION"]
        AM[AppModule]
    end
    
    PS --> PV
    AS --> AV
    PV --> PR
    AV --> AR
    PV --> PR2
    AV --> APU
    APU --> PR1
    PR2 --> PRI2
    PR1 --> PRI1
    PRI2 --> PA
    PRI2 --> PS2
    PRI1 --> PA
    PA --> RT
    RT --> AI
    AI --> TS
    PRI2 --> HM
    PRI1 --> HM
    
    AM --> TS
    AM --> AI
    AM --> RT
    AM --> PA
    AM --> HM
    AM --> PRI1
    AM --> PRI2
    AM --> PR
    AM --> AR
```

## 🔄 MVI Pattern Akış Diyagramı

```mermaid
sequenceDiagram
    participant User
    participant Screen
    participant ViewModel
    participant Reducer
    participant Repository
    participant API
    
    User->>Screen: User Action
    Screen->>ViewModel: onIntent(Intent)
    ViewModel->>Reducer: process(Intent, State)
    Reducer-->>ViewModel: New State
    ViewModel->>Repository: getData()
    Repository->>API: API Call
    API-->>Repository: Response
    Repository-->>ViewModel: Flow<Data>
    ViewModel-->>Screen: StateFlow<State>
    ViewModel-->>Screen: SharedFlow<Effect>
    Screen->>User: Update UI
```

## 📊 Product List Feature Detaylı Akış

```mermaid
graph LR
    A[ProductListScreen] -->|onIntent| B[ProductListViewModel2]
    B -->|State| C[ProductListContract.State]
    B -->|Intent| D[ProductListContract.Intent]
    B -->|Effect| E[ProductListContract.Effect]
    B -->|reduce| F[ProductListReducer]
    B -->|getProductsPaging| G[ProductRepository2]
    G -->|impl| H[ProductRepositoryImpl2]
    H -->|getProducts| I[ProductApi]
    H -->|paging| J[ProductPagingSource]
    J -->|Flow| B
    I -->|Response| K[ProductDto]
    K -->|map| L[ProductSummary]
    L -->|PagingData| B
```

## 🔐 Authentication Flow

```mermaid
sequenceDiagram
    participant App
    participant TokenStore
    participant AuthInterceptor
    participant OkHttpClient
    participant Retrofit
    participant API
    
    App->>TokenStore: saveAccessToken()
    TokenStore->>TokenStore: Store in SharedPreferences
    
    Note over API: API Request
    API->>OkHttpClient: Request
    OkHttpClient->>AuthInterceptor: intercept()
    AuthInterceptor->>TokenStore: getAccessToken()
    TokenStore-->>AuthInterceptor: Token
    AuthInterceptor->>AuthInterceptor: Add Authorization Header
    AuthInterceptor-->>OkHttpClient: Request with Token
    OkHttpClient-->>Retrofit: Request
    Retrofit-->>API: HTTP Request
```

## 🎯 Use Case Pattern

```mermaid
graph TD
    A[ViewModel] -->|call| B[UseCase]
    B -->|use| C[Repository Interface]
    C -->|implemented by| D[Repository Implementation]
    D -->|call| E[API]
    E -->|returns| F[DTO]
    F -->|map to| G[Domain Model]
    G -->|return| B
    B -->|return| A
```


