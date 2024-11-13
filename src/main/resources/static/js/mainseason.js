$(document).ready(function() {
    let selectedSeason = 1;

    $('.season-link').on('click', function(e) {
        e.preventDefault();  // 링크 클릭 기본 동작을 막음
        const season = $(this).attr('data-season');  // season 속성 값을 가져옴

        console.log('Season:', season);  // 값 확인용 로그

        selectedSeason = season;

        const url = `/home/seasonproduct_list?season=${season}`;
        window.location.href = url; // 페이지 이동
    });

});