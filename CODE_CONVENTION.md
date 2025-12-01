# HaruHana


## 네이밍 컨벤션

- 클래스명: `PascalCase` (ex. MemberService, TokenBlacklistService)
- 메서드명: `camelCase` (ex. findById, addToBlacklist)
- 변수명: `camelCase` (ex. memberId, accessToken)
- 상수: `UPPER_SNAKE_CASE` (ex. BLACKLIST_PREFIX)
- 패키지/폴더명: 소문자 (ex. service, domain)

## 코드 작성 규칙

- 클래스 선언부와 필드(멤버 변수) 사이는 한 줄 개행

    ```java
    public class AnyController {
    
            private final AnyService anyService;
    
    }
    ```

- 메서드 구현부 작성 시 , 메소드 첫줄 개행하지 않음

  ```java
    public void anyMethod(Long id) {
        Member member = memberFinder.findById(id).orElseThrow(); 
  }
  ```

- 필드 선언 순서는 `static final` → `final` → `instance 변수` 순
- 상수와 변수 사이에 한 줄 개행
- 메서드는 `public` > `protected` > `private` 순서로 배치
- Lombok 사용 시 `@Data`, `@Setter`는 지양. `@Getter`, `@Builder` 등 필요한 것만 명시적으로 사용
- **3항 연산자** (조건 ? A : B)는 너무 복잡한 조건에는 사용하지 않음 (간단할 때만 사용)
- `return 문` 전에는 한 줄 개행(단, 메서드 구현부가 return만 있을 땐 개행 생략)

    ```java
    public Member findMember(Long id) {
    
        // some code
    
        return member;
    }
    ```

## 5. 주석

- 주석은 코드 위에 작성
- 불필요하거나, 당연한 내용/코드와 스펙이 달라진 구버전 주석은 작성하지 않음
- API/비즈니스 로직에 필요한 경우 Javadoc 스타일 주석 권장

## 6. Layer
- **_Presentation Layer_**
  - 외부 변화에 민감한, 외부 의존성이 높은 영역입니다.
    외부 영역에 대한 처리를 담당하는 코드나 요청, 응답 클래스들도 이 레이어에 속합니다.
- **_Business Layer_**
  - 비즈니스 로직을 투영하는 레이어입니다.
- **_Implement Layer_**
  - 비즈니스 로직을 이루기 위해 도구로서 상세 구현 로직을 갖고 있는 클래스들이 있습니다.
    이곳은 가장 많은 클래스들이 존재하고 있으면서 구현 로직을 담당하기 때문에 재사용성도 높은 핵심 레이어입니다.
- **_Data Access Layer_**
  - 상세 구현 로직이 다양한 자원에 접근할 수 있는 기능을 제공하는 레이어입니다.
### Layer 규칙
- **1**. 레이어는 위에서 아래로만 의존할 수 있습니다.
- **2**. 레이어의 참조 방향이 역류 되지 않아야 한다.
- **3**. 레이어의 참조가 하위 레이어를 건너 뛰지 않아야 한다.
- **4**. 동일 레이어 간에는 서로 참조하지 않아야 한다.(다만 Implement Layer 내에서는 허용)
