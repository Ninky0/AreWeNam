$(document).ready(() => {

    $('#quit').click(() => {

        let loginId = $('#loginId').val();
        let password = $('#password').val();

        var customerId = $('#customerId').val();

        let formData = {
            customerId: customerId,
            loginId: loginId,
            password: password
        }

        console.log(formData);

        if (confirm('정말 탈퇴하시겠습니까?')) {
            quitCustomer(formData);
        }else {
            alert('탈퇴를 취소해주셔서 감사합니다.\n홈 화면으로 이동합니다.')
            window.location.href = '/home';
        }

    });

});

function quitCustomer(formData){
    $.ajax({
        type: 'DELETE',
        url: '/mypage/quitout/' + formData.customerId, // 서버의 엔드포인트 URL
        data: JSON.stringify(formData), // 데이터를 JSON 형식으로 변환
        contentType: 'application/json; charset=utf-8', // 전송 데이터의 타입
        dataType: 'json', // 서버에서 받을 데이터의 타입
        success: function(response) {
            // 성공 시 실행될 콜백 함수
            alert('성공적으로 삭제하였습니다.');
            window.location.href = response.url;
        },
        error: function(error) {
            // 실패 시 실행될 콜백 함수
            console.error('오류 발생:', error);
            // 수정 필요
            alert('비밀번호가 일치하지 않거나, 오류가 발생하였습니다.');
            window.location.href = '/mypage';
            // alert('삭제 중 오리가 나타났습니다.');
            // alert('앗, 오리가 당신의 시야를 가려 실패했습니다.');
        }
    });
}