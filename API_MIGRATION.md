# TFT Helper - API Migration to Community Dragon

## 🔄 변경사항 요약

이 커밋 시리즈에서는 **Data Dragon API에서 Community Dragon API로 마이그레이션**하여 **더 풍부한 증강 데이터(설명 포함)**를 제공하도록 변경했습니다.

## 🎯 주요 개선사항

### 1. **API 소스 변경**
- **기존**: Data Dragon API (`ddragon.leagueoflegends.com`)
- **변경**: Community Dragon API (`raw.communitydragon.org`)
- **이유**: Community Dragon은 더 자세한 데이터(설명, 세부 정보 등)를 제공

### 2. **데이터 품질 향상**
- ✅ **증강 설명 제공**: 이제 각 증강의 상세한 설명을 표시
- ✅ **정확한 이미지**: Community Dragon의 최신 이미지 사용
- ✅ **실시간 업데이트**: Community Dragon은 항상 최신 데이터 제공

### 3. **API 구조 개선**
```kotlin
// 기존 (Data Dragon)
data class Augment(
    val id: String,
    val name: String,
    val image: ImageInfo,
    val description: String = "" // 보통 비어있음
)

// 변경 (Community Dragon)
data class CommunityDragonAugment(
    val apiName: String,
    val name: String,
    val desc: String,    // 실제 설명 제공!
    val icon: String
)
```

## 📁 변경된 파일들

### 1. **ApiInterface.kt**
- Community Dragon API 엔드포인트 추가
- 데이터 변환 함수 구현
- 이중 API 구조 (Community Dragon + Data Dragon)

### 2. **AugmentRepository.kt**
- Community Dragon에서 데이터 페치
- 향상된 오류 처리 및 로깅
- 캐시 시스템 유지

### 3. **AugmentViewModel.kt**
- Community Dragon 통합
- 업데이트 로직 간소화
- 향상된 상태 관리

### 4. **AugmentsScreen.kt**
- Community Dragon 이미지 URL 사용
- 설명 표시 로직 개선
- UI 표시 업데이트

## 🔗 새로운 이미지 URL 구조
```
기존: https://ddragon.leagueoflegends.com/cdn/{version}/img/tft-augment/{image.full}
변경: https://raw.communitydragon.org/latest/game/assets/ux/tft/augmenticons/{image.full}
```

## 🚀 사용자에게 보이는 개선사항

1. **더 풍부한 정보**: 각 증강의 상세한 설명 제공
2. **최신 데이터**: 항상 최신 증강 정보 표시
3. **더 나은 검색**: 설명 기반 검색 지원
4. **향상된 UX**: "Community Dragon" 라벨로 데이터 소스 명시

## ⚠️ 주의사항

- Community Dragon API는 커뮤니티 운영이므로 가용성이 공식 API와 다를 수 있음
- 네트워크 오류 처리를 강화하여 안정성 확보
- 기존 캐시 시스템은 그대로 유지하여 오프라인 사용 지원

## 🔮 향후 계획

- 필요시 Data Dragon으로 폴백 메커니즘 구현
- Community Dragon의 추가 데이터 활용 검토 (챔피언, 아이템 등)
- API 응답 최적화 및 성능 개선

이제 앱은 훨씬 더 상세하고 유용한 TFT 증강 정보를 제공합니다! 🎉
