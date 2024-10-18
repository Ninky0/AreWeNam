// 사이드바 버튼 클릭 시 글자 색상 변경
document.querySelectorAll('.sidebar-button').forEach(button => {
    button.addEventListener('click', () => {
        button.style.color = '#FF0000'; // 클릭 시 색상 변경
    });
});

$(document).ready(() => {
    $('#logout').click((event) => {
        // 기본 링크 동작 방지
        event.preventDefault();

        $.ajax({
            type: 'POST',
            url: '/mypage/logout', // 로그아웃 처리 URL
            success: function(response) {
                // 성공 시 실행될 콜백 함수
                alert('로그아웃되었습니다.'); // 메시지 수정
                window.location.href = response.url; // 리다이렉트할 URL 설정
            },
            error: function(error) {
                // 실패 시 실행될 콜백 함수
                console.error('오류 발생:', error);
                alert('로그아웃 처리 중 오리가 나타났습니다.');
                alert('앗, 오리가 로그아웃을 물고 도망갔습니다.');
            }
        });
    });
});

