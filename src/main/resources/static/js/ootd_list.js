$(document).ready(function() {
    let currentOffset = 0; // 현재 로드된 이미지 수를 추적
    const limit = 9; // 한 번에 로드할 이미지 수

    function loadImages() {
        $.ajax({
            url: '/user/api/ootd-images', // API 엔드포인트
            method: 'GET',
            data: {
                offset: currentOffset,
                limit: limit
            },
            success: function(data) {
                const container = $('#ootd-item-container');

                // data.images가 정의되어 있는지 확인
                if (data.images && Array.isArray(data.images) && data.images.length > 0) {
                    data.images.forEach(image => {
                        // 이미지 태그를 컨테이너에 추가
                        container.append(`<img src="${image.picture}" alt="OOTD Image">`);
                    });

                    currentOffset += data.images.length; // 다음 로드를 위한 오프셋 업데이트

                    // 더 이상 로드할 이미지가 없으면 버튼 숨김
                    if (data.images.length < limit) {
                        $('#load-more').hide();
                    }
                } else {
                    console.error("No images found in response."); // 에러 로그 추가
                }
            },
            error: function() {
                alert('이미지를 로드할 수 없습니다. 나중에 다시 시도해주세요.');
            }
        });
    }

    // 초기 이미지 로드
    loadImages();

    // 버튼 클릭 시 더 많은 이미지 로드
    $('#load-more').click(function() {
        loadImages();
    });
});
