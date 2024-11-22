# Weather Wear, What to Wear ; WWWW

*날씨*에 따라 *옷을 추천*해주는 쇼핑몰, '**웨더웨어, 왓투웨어**' 입니다~

# trouble issue
- '기상청 api' 요청 횟수가 제한되어 날씨 정보를 매번 가져오는것에 한계가 있었습니다.
    -  -> 스케줄러를 이용하여 정해진 시간에 요청을 보내, db에 저장하여 사용하였습니다.

- 판매 상품, 리뷰, 주문 현황 등, 대량의 데이터를 불러오는 페이지에 페이징 처리를 하여, 로딩 지연시간에 대한 문제를 개선시켰습니다.

# ER - Diagram

<img width="547" alt="image" src="https://github.com/user-attachments/assets/86458470-0f62-4ac1-8bef-d4e5856a828b">


# 실행 화면
- 메인화면 1
      - 선택한 지역에 대한 기온을 가져와, 기온에 맞는 상품들을 추천해줍니다.
<img width="1377" alt="image" src="https://github.com/user-attachments/assets/76dde608-7523-4fb7-93da-0737d6b8549c">

- 메인화면 2
    - 선택한 계절에 맞는 상품들을 추천해줍니다.
<img width="1417" alt="image" src="https://github.com/user-attachments/assets/797d52f0-75c5-4a21-a624-634e23f7b872">
<img width="578" alt="image" src="https://github.com/user-attachments/assets/cbd5fb12-42ed-43f7-afed-a1d9fa88f02d">

- 커뮤니티 (OOTD)
      - 고객은 자신의 평소 코디 사진, 해시태그와 함께, 쇼핑몰 내의 연관 상품을 연결하여 게시물을 등록할 수 있습니다.
  
  <img width="290" alt="image" src="https://github.com/user-attachments/assets/b30a2a3c-86b3-491e-a5b8-f5b074e26e40">
  <img width="602" alt="image" src="https://github.com/user-attachments/assets/acf36438-5f90-4b1a-a375-e13a462447fb">

- 구매자 마이페이지
- 장바구니
- 구매목록 - 리뷰
- 작성한 글 -리뷰 & ootd

- 판매자 판매글 등록
      - 판매자는 날씨, 기온, 계절들을 선택하여 상품을 등록할 수 있습니다.
  
  <img width="302" alt="image" src="https://github.com/user-attachments/assets/e6ec5288-8f68-4054-a372-4db989fe596e">


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
