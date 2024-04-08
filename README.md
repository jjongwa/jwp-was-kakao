# 웹 애플리케이션 서버

## 진행 방법

* 웹 애플리케이션 서버 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정

* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 기능 요구사항

### 1. GET /index.html 응답하기

* HTTP Request Header 예

```
  GET /index.html HTTP/1.1
  Host: localhost:8080
  Connection: keep-alive
  Accept: */*
```

* 모든 Request Header 출력하기 힌트
  InputStream => InputStreamReader => BufferedReader
  구글에서 “java inputstream bufferedreader “로 검색 후 문제 해결
  BufferedReader.readLine() 메소드 활용해 라인별로 http header 읽는다.
  http header 전체를 출력한다.
  header 마지막은 while (!"".equals(line)) {} 로 확인 가능하다.
  line이 null 값인 경우에 대한 예외 처리도 해야 한다. 그렇지 않을 경우 무한 루프에 빠진다.(if (line == null) { return;})


* Request Line에서 path 분리하기 힌트
  Header의 첫 번째 라인에서 요청 URL(위 예의 경우 /index.html 이다.)을 추출한다.
  String[] tokens = line.split(" "); 활용해 문자열을 분리할 수 있다.
  구현은 별도의 유틸 클래스를 만들고 단위 테스트를 만들어 진행할 수 있다.


* path에 해당하는 파일 읽어 응답하기 힌트
  요청 URL에 해당하는 파일을 src/main/resources 디렉토리에서 읽어 전달하면 된다.
  utils.FileIoUtils의 loadFileFromClasspath() 메소드를 이용해 classpath에 있는 파일을 읽는다.


## 구현할 기능 목록

![img.png](img.png)

* [ ] 모든 Request Header를 출력할 수 있다.
  * [x] 메서드(`CustomMethod`)를 추출할 수 있다.
    * 메서드는 GET과 POST가 존재한다.
  * [x] path(`CustomPath`)를 추출할 수 있다.
  * [x] headers(`CustomHeaders`)를 추출할 수 있다.

* [ ] Request Line에서 path 분리할 수 있다.

* [ ] path에 해당하는 파일 읽어 응답할 수 있다.
