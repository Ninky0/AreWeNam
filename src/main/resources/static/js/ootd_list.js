$(document).ready(function () {
    let currentOffset = 0; // 현재 로드된 이미지 수를 추적
    const limit = 9; // 한 번에 로드할 이미지 수
    let isLoading = false; // 이미지 로딩 중인지 여부 추적

    // 이미지 목록을 동적으로 로드하는 함수
    function loadImages() {
        if (isLoading) return; // 이미 로딩 중이면 함수를 종료
        isLoading = true;

        $.ajax({
            url: '/user/api/ootd-images', // API 엔드포인트
            method: 'GET',
            data: {
                offset: currentOffset,
                limit: limit
            },
            success: function (data) {
                const container = $('#ootd-item-container');
                console.log('API 호출 성공:', data); // API 응답을 로그로 확인

                if (data.images && Array.isArray(data.images) && data.images.length > 0) {
                    data.images.forEach(image => {
                        const imageElement = `
                            <div class="ootd-item">
                                <img src="${image.picture}" alt="OOTD Image" class="ootd-image" data-id="${image.id}">
                            </div>`;
                        container.append(imageElement);
                    });

                    currentOffset += data.images.length; // 다음 로드를 위해 오프셋 업데이트

                    if (data.images.length < limit) {
                        $('#load-more').hide();
                    }

                    container.find('.ootd-image').off('click').on('click', function () {
                        const ootdId = $(this).data('id');
                        console.log('클릭된 이미지의 ootdId:', ootdId);
                        if (!ootdId) {
                            console.error('ootdId가 정의되지 않았습니다.');
                            return;
                        }
                        openOotdDetailModal(ootdId);
                    });
                } else {
                    console.error("No images found in response or unexpected response structure:", data);
                }
            },
            error: function (xhr, status, error) {
                console.error('이미지를 로드할 수 없습니다:', status, error);
                alert('이미지를 로드할 수 없습니다. 나중에 다시 시도해주세요.');
            },
            complete: function () {
                isLoading = false;
            }
        });
    }

    loadImages();

    $('#load-more').click(function () {
        loadImages();
    });

    $('.prev-btn').on('click', function () {
        changeSlide(-1);
    });
    $('.next-btn').on('click', function () {
        changeSlide(1);
    });

    $('<span class="close-btn">&times;</span>').appendTo('#ootdModalContent').click(closeOotdModal);
});

function openOotdDetailModal(ootdId) {
    $.ajax({
        url: `/user/api/ootd/detail/${ootdId}`,
        method: 'GET',
        success: function (data) {
            if (data) {
                console.log('OOTD Detail Data:', data);

                const productDetailUrl = `/user/product/detail/${data.product.id}`;

                $('#productDetailsLink').on('click', function () {
                    window.location.href = productDetailUrl;
                });

                $('#ootdModalImage').attr('src', data.ootd.picture || '/images/default-placeholder.png').on('error', function () {
                    $(this).attr('src', '/images/default-placeholder.png');
                });

                $('#ootdProductMainImage').attr('src', data.product.mainPicturePath || '/images/default-placeholder.png').on('error', function () {
                    $(this).attr('src', '/images/default-placeholder.png');
                });

                $('#ootdProductTagName').text(data.ootd.tag || '정보 없음'); // tag 값 추가
                $('#ootdProductName').text(data.product.name || '정보 없음');
                $('#ootdProductPrice').text(data.product.price ? `${data.product.price} 원` : '정보 없음');
                $('#ootdProductCategory').text(data.product.category || '정보 없음');
                $('#ootdProductSeason').text(getSeasonLabel(data.product.season));
                $('#ootdProductTemperature').text(getTemperatureLabel(data.product.temperature));

                $('#ootdModal').show();
                currentSlideIndex = 0;
                showSlide(currentSlideIndex);
            } else {
                console.error("서버에서 반환된 데이터에 문제가 있습니다:", data);
            }
        },
        error: function () {
            alert('OOTD 정보를 불러오는 중 오류가 발생했습니다.');
        }
    });
}

function closeOotdModal(event) {
    if (event) {
        event.stopPropagation();
    }
    $('#ootdModal').hide();
}

function getSeasonLabel(season) {
    return season === 1 ? '봄' : season === 2 ? '여름' : season === 3 ? '가을' : '겨울';
}

function getTemperatureLabel(temp) {
    switch (temp) {
        case 1:
            return '≥28°C';
        case 2:
            return '27~23°C';
        case 3:
            return '22~20°C';
        case 4:
            return '19~17°C';
        case 5:
            return '16~12°C';
        case 6:
            return '11~9°C';
        case 7:
            return '8~5°C';
        default:
            return '≤4°C';
    }
}

let currentSlideIndex = 0;
function changeSlide(direction) {
    currentSlideIndex += direction;
    showSlide(currentSlideIndex);
}

function showSlide(index) {
    const slides = $('.modal-slider img');
    if (index >= slides.length) {
        currentSlideIndex = 0;
    } else if (index < 0) {
        currentSlideIndex = slides.length - 1;
    }
    slides.hide();
    slides.eq(currentSlideIndex).show();
}
