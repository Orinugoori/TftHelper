# 🤝 기여 가이드

TFT Helper 프로젝트에 기여해주셔서 감사합니다! 이 문서는 프로젝트에 기여하는 방법을 안내합니다.

## 📋 목차
- [기여 방법](#기여-방법)
- [개발 환경 설정](#개발-환경-설정)
- [코딩 스타일](#코딩-스타일)
- [커밋 컨벤션](#커밋-컨벤션)
- [PR 가이드라인](#pr-가이드라인)
- [이슈 리포팅](#이슈-리포팅)

## 🛠️ 기여 방법

### 1. 버그 리포트
- [Bug Report 템플릿](.github/ISSUE_TEMPLATE/bug_report.md) 사용
- 재현 가능한 단계 포함
- 스크린샷 또는 로그 첨부

### 2. 기능 제안
- [Feature Request 템플릿](.github/ISSUE_TEMPLATE/feature_request.md) 사용
- 구체적인 사용 사례 설명
- UI/UX 모킹업 첨부 (선택사항)

### 3. 코드 기여
1. 이슈 확인 및 할당 요청
2. Fork & Clone
3. Feature Branch 생성
4. 변경사항 구현
5. 테스트 추가/수정
6. 문서 업데이트
7. Pull Request 제출

## 🔧 개발 환경 설정

### 필수 요구사항
- Android Studio Arctic Fox 이상
- JDK 11 이상
- Android SDK API 24-34
- Git

### 설정 단계
```bash
# 1. Repository Fork 후 Clone
git clone https://github.com/YOUR_USERNAME/TftHelper.git
cd TftHelper

# 2. 원본 Repository 추가
git remote add upstream https://github.com/orinugoori/TftHelper.git

# 3. 개발 브랜치 생성
git checkout -b feature/your-feature-name

# 4. Android Studio에서 프로젝트 열기
# File > Open > TftHelper 폴더 선택
```

### 브랜치 전략
- `main`: 안정적인 릴리즈 브랜치
- `dev`: 개발 통합 브랜치
- `feature/*`: 새로운 기능 개발
- `bugfix/*`: 버그 수정
- `hotfix/*`: 긴급 수정

## 📝 코딩 스타일

### Kotlin 스타일 가이드
우리는 [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)를 따릅니다.

#### 명명 규칙
```kotlin
// 클래스: PascalCase
class AugmentCalculator

// 함수 및 변수: camelCase
fun calculateProbability()
val selectedAugment = "골드"

// 상수: UPPER_SNAKE_CASE
const val MAX_AUGMENT_COUNT = 3

// 패키지: lowercase
package com.orinugoori.tfthelper.domain.usecase
```

#### 코드 구조
```kotlin
// 1. 패키지 선언
package com.orinugoori.tfthelper.presentation.screens

// 2. 임포트 (그룹별 정렬)
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.domain.model.Augment

// 3. 클래스/함수 구현
@Composable
fun MyScreen() {
    // 구현
}
```

### Compose 스타일 가이드
```kotlin
// ✅ 좋은 예
@Composable
fun AugmentCard(
    augment: Augment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        // 내용
    }
}

// ❌ 나쁜 예
@Composable
fun augmentCard(augment: Augment, onClick: () -> Unit) {
    // modifier 매개변수 없음
    // 명명 규칙 위배
}
```

## 🎯 커밋 컨벤션

### 커밋 메시지 형식
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 스타일 변경 (포맷팅, 세미콜론 누락 등)
- `refactor`: 코드 리팩토링
- `test`: 테스트 추가/수정
- `chore`: 빌드 프로세스 또는 보조 도구 변경

### 예시
```bash
# 기능 추가
git commit -m "feat(calculator): add third augment probability calculation"

# 버그 수정
git commit -m "fix(search): resolve crash when searching with empty query"

# 리팩토링
git commit -m "refactor(ui): extract common components to separate files"
```

## 📥 Pull Request 가이드라인

### PR 템플릿
```markdown
## 📋 변경사항 요약
간단하게 변경사항을 설명해주세요.

## 🎯 변경 이유
왜 이 변경이 필요한지 설명해주세요.

## 🧪 테스트
- [ ] 기존 테스트 통과
- [ ] 새로운 테스트 추가
- [ ] 수동 테스트 완료

## 📱 스크린샷 (UI 변경 시)
변경된 UI가 있다면 Before/After 스크린샷을 첨부해주세요.

## ✅ 체크리스트
- [ ] 코딩 스타일 가이드 준수
- [ ] 문서 업데이트 (필요시)
- [ ] 커밋 메시지 컨벤션 준수
- [ ] 충돌 해결 완료
```

### PR 프로세스
1. **Draft PR 생성**: 작업 중인 내용 공유
2. **코드 리뷰 요청**: 최소 1명의 리뷰어 지정
3. **피드백 반영**: 리뷰어 의견 수렴 및 수정
4. **최종 승인**: 모든 체크 통과 후 머지

### 리뷰 기준
- **기능성**: 의도한 대로 작동하는가?
- **코드 품질**: 가독성, 유지보수성
- **성능**: 불필요한 성능 저하 없는가?
- **테스트**: 적절한 테스트 커버리지
- **문서화**: 필요한 문서 업데이트

## 🐛 이슈 리포팅

### 버그 리포트 시 포함할 정보
1. **재현 단계**: 단계별 상세 설명
2. **예상 결과**: 정상 동작 설명
3. **실제 결과**: 발생한 문제 설명
4. **환경 정보**: OS, 기기, 앱 버전
5. **로그/스크린샷**: 오류 증거

### 기능 제안 시 포함할 정보
1. **문제 정의**: 현재 불편한 점
2. **해결 방안**: 제안하는 기능
3. **사용 사례**: 구체적인 활용 예시
4. **우선순위**: 중요도 평가
5. **대안**: 다른 해결 방법

## 🏷️ 라벨 시스템

### 이슈 라벨
- `bug`: 버그 리포트
- `enhancement`: 기능 개선
- `feature`: 새로운 기능
- `documentation`: 문서 관련
- `good first issue`: 초보자 친화적
- `help wanted`: 도움 필요
- `priority:high`: 높은 우선순위
- `priority:low`: 낮은 우선순위

### 컴포넌트 라벨
- `ui`: UI/UX 관련
- `api`: API 관련
- `performance`: 성능 관련
- `accessibility`: 접근성 관련

## 🧪 테스트 가이드라인

### 테스트 작성 규칙
1. **단위 테스트**: 각 함수/클래스별 테스트
2. **통합 테스트**: 컴포넌트 간 상호작용 테스트
3. **UI 테스트**: 사용자 시나리오 테스트

### 테스트 네이밍
```kotlin
// 형식: [테스트 대상]_[조건]_[예상 결과]
fun calculateProbability_withValidInput_returnsCorrectValue()
fun searchAugments_withEmptyQuery_returnsAllAugments()
```

## 📚 문서화 가이드라인

### 코드 문서화
```kotlin
/**
 * 증강체 확률을 계산하는 함수
 * 
 * @param firstAugment 첫 번째 선택된 증강체
 * @param secondAugment 두 번째 선택된 증강체 (선택사항)
 * @return 다음 증강체별 확률 리스트
 */
fun calculateAugmentProbability(
    firstAugment: String,
    secondAugment: String? = null
): List<Pair<String, Int>>
```

### README 업데이트
새로운 기능 추가 시 다음 섹션들을 업데이트해주세요:
- 주요 기능
- 사용법
- 스크린샷
- API 변경사항

## 🎖️ 기여자 인정

### 기여자 목록
모든 기여자는 README.md의 Contributors 섹션에 추가됩니다.

### 기여 유형
- 💻 코드 기여
- 📖 문서 작성
- 🐛 버그 리포트
- 💡 아이디어 제안
- 🎨 디자인
- 🧪 테스트

## 📞 소통 채널

### GitHub
- **Issues**: 버그 리포트, 기능 제안
- **Discussions**: 일반적인 질문, 아이디어 토론
- **Pull Requests**: 코드 리뷰, 개발 논의

### 응답 시간
- **이슈 응답**: 2-3일 내
- **PR 리뷰**: 5-7일 내
- **긴급 버그**: 24시간 내

## ⚡ 빠른 시작 체크리스트

- [ ] Repository Fork
- [ ] 로컬 환경 설정
- [ ] 이슈 선택 또는 생성
- [ ] Feature Branch 생성
- [ ] 변경사항 구현
- [ ] 테스트 작성/실행
- [ ] 커밋 (컨벤션 준수)
- [ ] Pull Request 생성
- [ ] 코드 리뷰 대응
- [ ] 머지 승인 대기

---

## 🙏 감사의 말

TFT Helper 프로젝트에 기여해주시는 모든 분들께 진심으로 감사드립니다. 여러분의 기여로 더 나은 앱을 만들 수 있습니다!

질문이 있으시면 언제든지 이슈를 생성하거나 Discussions에서 문의해주세요. 🚀