# Weather Wear, What to Wear ; WWWW

*날씨*에 따라 *옷을 추천*해주는 쇼핑몰, '**웨더웨어, 왓투웨어**' 입니다~

# trouble issue
- '기상청 api' 요청 횟수가 제한되어 날씨 정보를 매번 가져오는것에 한계가 있었습니다.
    -  -> 스케줄러를 이용하여 정해진 시간에 요청을 보내, db에 저장하여 사용하였습니다.

- 판매 상품, 리뷰, 주문 현황 등, 대량의 데이터를 불러오는 페이지에 페이징 처리를 하여, 로딩 지연시간에 대한 문제를 개선시켰습니다.

# 실행 화면
- 메인화면 1
      - 선택한 지역에 대한 기온을 가져와, 기온에 맞는 상품들을 추천해줍니다.
<img width="1377" alt="image" src="https://github.com/user-attachments/assets/76dde608-7523-4fb7-93da-0737d6b8549c">




# 기술 스택
-   **백엔드:**
    -   **언어:** Java
    -   **프레임워크:** Spring Boot, Spring Security, Spring Data JPA
    -   **라이브러리:** RestTemplate
-   **프론트엔드:**
    -   **언어:** JavaScript, HTML, CSS
   
-   **데이터베이스(DB):** MySQL


<br>

# 참여인원
| 남인경 | 윤미영 | 이호영 |
|---------------------------|----------------------|----------------------|
| BE, FE | FE | BE |

<br>
