# FocusTimer (빡집중)

집중력 향상을 위한 안드로이드 타이머 애플리케이션입니다.

## 📱 프로젝트 개요

FocusTimer는 작업 시간과 휴식 시간을 설정하여 효율적인 작업 사이클을 만들어주는 앱입니다. 시간이 끝나면 자동으로 다음 단계로 넘어가며, 알림으로 알려드립니다. 집중력을 높이고 번아웃을 방지하는 최적의 작업 패턴을 찾을 수 있습니다.

## ✨ 주요 기능

- ⏱️ **포모도로 타이머**: 작업 시간과 휴식 시간을 설정하여 집중력 향상
- 🔥 **시각적 피드백**: Lottie 애니메이션을 활용한 동적 UI
- 📊 **통계 및 히스토리**: 학습 시간 추적 및 분석
- 🎯 **할 일 관리**: Todo 리스트 기능
- ⏰ **크로노타임**: 시간대별 생산성 분석
- 🔒 **앱 차단**: 접근성 서비스를 통한 앱 차단 기능
- ⌚ **Wear OS 지원**: 스마트워치 연동

## 🛠️ 기술 스택

- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose
- **아키텍처**: MVVM 패턴
- **백엔드**: Firebase (Authentication, Realtime Database)
- **애니메이션**: Lottie
- **최소 SDK**: 28 (Android 9.0)
- **타겟 SDK**: 34
- **컴파일 SDK**: 35

## 📁 프로젝트 구조

```
FocusTimer/
├── app/                                    # 메인 애플리케이션 모듈
│   ├── src/main/java/com/example/focustimer/
│   │   ├── Activity/                      # 액티비티 클래스
│   │   │   ├── MainActivity.kt            # 메인 액티비티 (앱 진입점)
│   │   │   ├── TimerActivity.kt           # 타이머 전용 액티비티
│   │   │   └── BlockActivity.kt           # 앱 차단 액티비티
│   │   │
│   │   ├── Page/                          # Compose UI 화면
│   │   │   ├── main/                     # 메인 페이지 관련
│   │   │   │   ├── PageMain.kt           # 메인 홈 화면
│   │   │   │   ├── PageExpresstion.kt    # 앱 설명/온보딩 화면
│   │   │   │   └── EditBoxScreen.kt      # 편집 화면
│   │   │   ├── timer/                    # 타이머 관련 화면
│   │   │   │   ├── PageTimer.kt          # 타이머 실행 화면
│   │   │   │   └── ChangeTimerOption.kt  # 타이머 설정 변경 화면
│   │   │   ├── Date/                     # 날짜/스케줄 관련 화면
│   │   │   │   ├── PageDate.kt           # 날짜 선택 화면
│   │   │   │   ├── PageDaySchedule.kt    # 일일 스케줄 화면
│   │   │   │   ├── PageWeekSchedule.kt   # 주간 스케줄 화면
│   │   │   │   └── WeekPicker.kt         # 주 선택 컴포넌트
│   │   │   ├── PageLoading.kt            # 로딩 화면
│   │   │   ├── PageLogin.kt              # 로그인 화면
│   │   │   └── TodoPage.kt               # 할 일 관리 화면
│   │   │
│   │   ├── navigation/                   # 네비게이션 설정
│   │   │   └── Navigation.kt             # 네비게이션 그래프 및 라우팅
│   │   │
│   │   ├── service/                      # 백그라운드 서비스
│   │   │   ├── TimerService_foreground.kt    # 포그라운드 타이머 서비스
│   │   │   ├── AppBlockAccessibilityService.kt  # 앱 차단 접근성 서비스
│   │   │   ├── BlockOverlayService.kt    # 앱 차단 오버레이 서비스
│   │   │   └── ServiceWatchCommunication.kt     # 워치 통신 서비스
│   │   │
│   │   ├── survery/                      # 설문조사 관련
│   │   │   ├── SurveyScreen.kt           # 설문조사 메인 화면
│   │   │   ├── SurveyQuestion.kt         # 설문 질문 컴포넌트
│   │   │   ├── SurveyMsti.kt             # MSTI 설문 관련
│   │   │   ├── SurveyCronotypeData.kt    # 크로노타입 데이터
│   │   │   ├── SurveyCronoViewModel.kt   # 크로노타입 ViewModel
│   │   │   └── ScoreDesign.kt            # 점수 표시 디자인
│   │   │
│   │   ├── viewmodel/                    # 앱 전용 ViewModel
│   │   │   └── HistoryViewModel.kt       # 히스토리 관리 ViewModel
│   │   │
│   │   ├── utils/                        # 유틸리티 클래스
│   │   │   ├── AppRoute.kt               # 앱 라우팅 상수
│   │   │   ├── Dimens.kt                 # 디자인 치수 상수
│   │   │   └── MyIntents.kt              # Intent 관련 유틸리티
│   │   │
│   │   ├── ui/theme/                     # UI 테마 설정
│   │   │   ├── Color.kt                  # 색상 정의
│   │   │   ├── Theme.kt                  # Material 테마 설정
│   │   │   └── Type.kt                   # 타이포그래피 설정
│   │   │
│   │   ├── test/                         # 폴더 구분전전
│   │   │   ├── appFirebase.kt            # Firebase 
│   │   │   ├── todoList.kt               # Todo 리스트 
│   │   │   └── TodoViewmodel.kt          # Todo ViewModel 
│   │   │
│   │   └── MyApplication.kt              # Application 클래스
│   │
│   ├── src/main/res/                     # 리소스 파일
│   │   ├── drawable/                     # 드로어블 리소스
│   │   ├── raw/                          # Lottie 애니메이션 JSON
│   │   └── values/                       # 문자열, 색상 등
│   │
│   └── build.gradle.kts                  # 앱 모듈 빌드 설정
│
├── shared/                                # 공유 모듈 (app, watch에서 공유)
│   ├── src/main/java/com/example/shared/
│   │   ├── model/                        # 데이터 모델
│   │   │   ├── Timer.kt                  # 타이머 모델
│   │   │   ├── TimerOption.kt            # 타이머 옵션 모델
│   │   │   ├── TimerOptions.kt           # 타이머 옵션 컬렉션
│   │   │   ├── MySubject.kt              # 과목/주제 모델
│   │   │   ├── HistoryData.kt            # 히스토리 데이터 모델
│   │   │   ├── CronoTime.kt              # 크로노타임 모델
│   │   │   ├── CronoTimeSchedule.kt      # 크로노타임 스케줄 모델
│   │   │   └── DateTimeWrapper.kt        # 날짜/시간 래퍼
│   │   │
│   │   ├── viewmodel/                    # 공유 ViewModel
│   │   │   ├── TimerViewModel.kt         # 타이머 ViewModel (싱글톤)
│   │   │   └── CronoTypeViewmodel.kt     # 크로노타입 ViewModel
│   │   │
│   │   ├── Myfirebase/                   # Firebase 관련 로직
│   │   │   ├── MyFireBase.kt             # Firebase 초기화 및 유틸리티
│   │   │   ├── FireBaseAuth.kt           # 인증 관련
│   │   │   ├── FirebaseHistory.kt        # 히스토리 데이터 관리
│   │   │   └── FireBaseSetting.kt        # 설정 데이터 관리
│   │   │
│   │   └── FireAnimationPart.kt          # Lottie 애니메이션 컴포넌트
│   │
│   └── build.gradle.kts                  # 공유 모듈 빌드 설정
│
├── watch/                                 # Wear OS 모듈
│   ├── src/main/java/com/example/focustimer/
│   │   ├── WatchActivity.kt              # 워치 메인 액티비티
│   │   ├── WatchController.kt            # 워치 컨트롤러
│   │   ├── WatchDataLayerListenerService.kt  # 데이터 레이어 리스너
│   │   └── theme/                        # 워치 테마
│   │       └── Theme.kt                  # 워치 Material 테마
│   │
│   └── build.gradle.kts                  # 워치 모듈 빌드 설정
│
├── gradle/                                # Gradle 설정
│   ├── libs.versions.toml                # 버전 카탈로그
│   └── wrapper/                          # Gradle Wrapper
│
├── build.gradle.kts                       # 프로젝트 루트 빌드 설정
├── settings.gradle.kts                    # 프로젝트 설정
└── README.md                              # 프로젝트 문서
```

## 🚀 시작하기

### 사전 요구사항

- Android Studio Hedgehog | 2023.1.1 이상
- JDK 11 이상
- Android SDK 28 이상

### 설치 방법

1. 저장소 클론
```bash
git clone [repository-url]
cd FocusTimer
```

2. Firebase 설정
   - `google-services.json` 파일을 프로젝트에 추가
   - Firebase 프로젝트에서 Authentication과 Realtime Database 활성화

3. 프로젝트 빌드
```bash
./gradlew build
```

4. 앱 실행
   - Android Studio에서 프로젝트를 열고 실행
   - 또는 `./gradlew installDebug` 명령어 사용

## 📝 주요 권한

- **오버레이 권한**: 앱 차단 기능을 위한 오버레이 표시
- **접근성 서비스**: 앱 차단 기능 제공

## 🔧 빌드 설정

프로젝트는 Gradle Version Catalog를 사용하여 의존성을 관리합니다.

주요 의존성:
- Jetpack Compose
- Firebase (Auth, Database)
- Navigation Compose
- DataStore
- Lottie
- YCharts

## 📄 라이선스

이 프로젝트의 라이선스 정보는 별도로 명시되지 않았습니다.

## 👥 기여

이슈 및 풀 리퀘스트를 환영합니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해주세요.
