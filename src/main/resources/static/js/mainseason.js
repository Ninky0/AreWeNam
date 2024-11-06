$(document).ready(function() {
    $('.season-link').on('click', function(e) {
        e.preventDefault();  // 링크 클릭 기본 동작을 막음
        const season = $(this).attr('season');  // season 속성 값을 가져옴

        console.log('Season:', season);  // 값 확인용 로그

        const url = `/user/seasonproduct_list?season=${season}`;
        window.location.href = url; // 페이지 이동
    });
});