# Time Letter Backend API

Time letter(이하 TL)는 느린 우체통을 모티브로 한 서비스입니다.

### Pre Image

![image](https://user-images.githubusercontent.com/26649731/186671901-8cb50560-3555-4a83-9565-0f767c2105f9.png)
![image](https://user-images.githubusercontent.com/26649731/186672385-a40baa28-aeeb-4fea-8480-a2031f9b7170.png)
![image](https://user-images.githubusercontent.com/26649731/186672569-681c96c1-5467-4b49-a9db-97f9c21a703e.png)
### Requirements

for building and running the application you need:

* Gradle
* java11
* Spring Boot
* H2 Database

## config
### 📍Spring Boot
- Version : 2.7.1
### 📍Java
- Version : 11
### 📍Swagger
- version : 3.0.0
- URL: https://api.timeletter.net/swagger-ui/index.html

### 개발 전 목표

* TDD 를 이용해서 개발하자
* Restful 한 API 를 개발하자
* Swagger || Spring Rest Docs 를 이용해서 개발하자
* 문서화를 잘 하자
* Git 기능을 잘 이용하자 

### 목표 기능

* 편지 API CRUD (~7/21, 완료)
* 카카오 로그인 및 회원가입 API 학습 및 적용 (~8/4, 완료)
* Swagger 적용 (~8/4, 완료)
* Validation 적용 (~8/10, 진행)

### 참고해본 레퍼런스

* 패키지 구조를 어떻게 관리하는것이 좋을까?
  * [스프링 패키지 구조 가이드](https://cheese10yun.github.io/spring-guide-directory/)
* Restful 하다는 것은 뭘까??
  * [Restful 하다는 것은 뭘까?](https://dkyou.tistory.com/356)
* Spring Boot Controller 에서 매개변수 넘겨받는 방법
  * [Controller 단에서 매개변수 넘겨받기](https://dkyou.tistory.com/357)
* Enum 관리를 해보자
  * [ENUM 으로 컬럼 관리하기](https://gofnrk.tistory.com/102)