# 🎯 TFT Helper - 증강체 확률 계산기

> **롤토체스(TFT) 플레이어를 위한 증강체 확률 계산 및 리스트 확인 앱**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📱 주요 기능

- **🎯 증강체 확률 계산**: 선택한 증강체에 따른 다음 증강체 확률 계산
- **🔍 증강체 리스트 확인**: 티어별 증강체 목록 및 상세 정보
- **🔤 한글 초성 검색**: 빠른 증강체 검색 (ㅂㅎ → 백혈병 등)
- **📱 티어별 필터링**: 실버/골드/프리즘 티어별 분류
- **💾 오프라인 캐시**: 인터넷 연결 없이도 기본 기능 사용 가능

## 🏗️ 프로젝트 구조

이 프로젝트는 **Clean Architecture** 패턴을 기반으로 구성되어 있습니다.

```
app/src/main/java/com/orinugoori/tfthelper/
├── 📁 core/                           # 공통 모듈
│   ├── 📁 constants/                  # 앱 전체 상수
│   ├── 📁 extensions/                 # 확장 함수
│   └── 📁 utils/                      # 유틸리티 함수
├── 📁 domain/                         # 비즈니스 로직 계층
│   ├── 📁 model/                      # 도메인 모델
│   ├── 📁 repository/                 # Repository 인터페이스
│   └── 📁 usecase/                    # Use Case (비즈니스 로직)
├── 📁 data/                          # 데이터 계층
│   ├── 📁 local/                      # 로컬 데이터 소스
│   ├── 📁 remote/                     # 원격 데이터 소스
│   │   └── 📁 api/                    # API 서비스
│   ├── 📁 model/                      # 데이터 모델 (DTO)
│   └── 📁 repository/                 # Repository 구현체
└── 📁 presentation/                   # UI 계층
    ├── 📁 screens/                    # 화면별 UI
    │   ├── 📁 calculator/             # 확률 계산 화면들
    │   ├── 📁 augments/               # 증강체 리스트 화면
    │   └── 📁 splash/                 # 스플래시 화면
    ├── 📁 components/                 # 재사용 가능한 UI 컴포넌트
    ├── 📁 viewmodel/                  # ViewModel들
    ├── 📁 navigation/                 # 네비게이션 설정
    └── 📁 theme/                      # 디자인 시스템
```

## 🛠️ 기술 스택

### **언어 & 플랫폼**
- **Kotlin** - 메인 개발 언어
- **Android** - 타겟 플랫폼 (API 24+)

### **아키텍처 & 패턴**
- **Clean Architecture** - 계층 분리 및 관심사 분리
- **MVVM** - Model-View-ViewModel 패턴
- **Repository Pattern** - 데이터 소스 추상화

### **UI 프레임워크**
- **Jetpack Compose** - 선언형 UI 프레임워크
- **Material Design 3** - 디자인 시스템
- **Navigation Compose** - 화면 간 내비게이션

### **데이터 & 네트워킹**
- **Retrofit** - REST API 클라이언트
- **OkHttp** - HTTP 클라이언트
- **Gson** - JSON 직렬화/역직렬화
- **SharedPreferences** - 로컬 캐시

### **비동기 처리**
- **Kotlin Coroutines** - 비동기 프로그래밍
- **Flow** - 반응형 데이터 스트림

### **이미지 로딩**
- **Coil** - 이미지 로딩 및 캐싱

### **데이터 소스**
- **Riot Games Data Dragon API** - 공식 게임 데이터
- **Community Dragon** - 커뮤니티 데이터 (상세 설명)

## 🚀 시작하기

### **필수 요구사항**
- Android Studio Arctic Fox 이상
- JDK 11 이상
- Android SDK API 24-34
- Git

### **설치 및 실행**

1. **저장소 클론**
   ```bash
   git clone https://github.com/Orinugoori/TftHelper.git
   cd TftHelper
   ```

2. **Android Studio에서 열기**
   - File > Open > TftHelper 폴더 선택

3. **의존성 동기화**
   - Gradle Sync 실행

4. **앱 실행**
   - Run 'app' 또는 Shift + F10

## 📊 Clean Architecture 계층별 설명

### **🎯 Domain Layer (비즈니스 로직)**
- **Models**: 핵심 비즈니스 엔티티
- **Use Cases**: 비즈니스 규칙 및 로직
- **Repository Interfaces**: 데이터 접근 계약

```kotlin
// 예시: 증강체 검색 Use Case
class AugmentSearchUseCase(
    private val repository: AugmentRepository
) {
    operator fun invoke(query: String, tierFilter: String? = null): Flow<List<Augment>>
}
```

### **💾 Data Layer (데이터 관리)**
- **Repository Implementations**: 도메인 인터페이스 구현
- **Data Sources**: 로컬/원격 데이터 소스
- **DTOs**: API 응답 모델

```kotlin
// 예시: Repository 구현체
class AugmentRepositoryImpl(
    private val localDataSource: AugmentLocalDataSource,
    private val remoteDataSource: TFTApiService
) : AugmentRepository
```

### **🎨 Presentation Layer (UI)**
- **ViewModels**: UI 상태 관리
- **Composables**: UI 컴포넌트
- **Navigation**: 화면 간 이동

```kotlin
// 예시: ViewModel
class AugmentViewModel(
    private val augmentSearchUseCase: AugmentSearchUseCase
) : ViewModel()
```

## 🔧 개발 가이드라인

### **코딩 스타일**
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) 준수
- 명확한 네이밍 컨벤션 사용
- 함수는 단일 책임 원칙 준수

### **커밋 컨벤션**
```
<type>(<scope>): <subject>

# 예시
feat(domain): add augment search use case
fix(ui): resolve crash on empty search
refactor(data): extract common API logic
```

### **브랜치 전략**
- `main`: 안정적인 릴리즈 브랜치
- `develop`: 개발 통합 브랜치  
- `feature/*`: 새로운 기능 개발
- `refactoring-code-structure`: 현재 리팩토링 브랜치

## 📈 향후 계획

### **1단계: 구조 완성** ✅
- Clean Architecture 폴더 구조 완성
- Domain, Data, Presentation 계층 분리
- 기본 컴포넌트 및 UseCase 구현

### **2단계: 기능 구현** 🚧
- 기존 화면들을 새 구조로 마이그레이션
- ViewModel과 UseCase 연결
- Navigation 시스템 통합

### **3단계: 최적화** 📋
- Dependency Injection (Hilt) 적용
- 테스트 코드 작성
- 성능 최적화

### **4단계: 고도화** 💡
- 다크 테마 지원
- 다국어 지원
- 애니메이션 개선

## 🤝 기여하기

프로젝트에 기여하고 싶으시다면 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고해주세요.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참고하세요.

## 📞 연락처

- **개발자**: Orinugoori
- **GitHub**: [@Orinugoori](https://github.com/Orinugoori)
- **프로젝트 링크**: [https://github.com/Orinugoori/TftHelper](https://github.com/Orinugoori/TftHelper)

---

⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요!