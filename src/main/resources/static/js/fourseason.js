$(document).ready(function () {
    let currentPage = 0;
    let selectedSeason = parseInt(document.getElementById('main-season').value); // 숫자로 변환

    applySeasonEffect(selectedSeason);
    loadProducts(selectedSeason, currentPage);

    $('.season-tab').on('click', function () {
        selectedSeason = $(this).data('season');
        $('.season-tab').removeClass('active');
        $(this).addClass('active');

        // 계절에 맞는 배경 이미지 적용
        applySeasonEffect(selectedSeason);

        loadProducts(selectedSeason, 0);
        currentPage = 0;
    });

    $(document).on('mousemove', function (e) {
        createMouseTrail(e.pageX, e.pageY);
    });

    function applySeasonEffect(season) {
        const videoElement = $('#background-video');
        let videoSrc = '';

        switch (season) {
            case 1:
                videoSrc = '/uploads/spring_bg.mp4';
                break;
            case 2:
                videoSrc = '/uploads/summer_bg.mp4';
                break;
            case 3:
                videoSrc = '/uploads/autumn_bg.mp4';
                break;
            case 4:
                videoSrc = '/uploads/winter_bg.mp4';
                break;
        }

        // 비디오 소스 변경
        videoElement.find('source').attr('src', videoSrc);
        videoElement[0].load(); // 비디오 소스를 새로 로드하여 변경 적용

        // 비디오 자동 재생
        videoElement[0].play().catch(error => {
            console.error("Auto-play was prevented by the browser:", error);
        });
    }
    let currentEffect = null; // 현재 표시되는 효과를 저장하는 변수

    function createMouseTrail(x, y) {
        // 기존 효과가 있으면 제거하고 새로운 효과 추가
        if (currentEffect) {
            currentEffect.remove();
        }

        const effectImage = $('<div></div>').addClass(getTrailClass(selectedSeason));

        effectImage.css({
            top: y + 'px',
            left: x + 'px',
            position: 'absolute',
            pointerEvents: 'none',
            zIndex: 9999,
            animation: 'bounceAnimation 1s ease infinite' // 통통 튀는 느낌을 주는 애니메이션
        });

        $('body').append(effectImage);
        currentEffect = effectImage;

        // 지정된 시간 후 효과 제거
        setTimeout(() => {
            if (currentEffect === effectImage) {
                effectImage.remove();
                currentEffect = null;
            }
        }, 1000); // 효과 지속 시간 조정
    }

    function getTrailClass(season) {
        return season === 1 ? 'trail-petal' :
            season === 2 ? 'trail-raindrop' :
                season === 3 ? 'trail-leaf' :
                    'trail-snowflake';
    }

    function loadProducts(season, page) {
        $.ajax({
            url: '/home/seasonproduct_list',
            method: 'POST',
            data: {season: season, page: page},
            success: function (data) {
                updateProductGrid(data.content);
                updatePagination(data);
            },
            error: function () {
                alert('상품 목록을 가져오는 데 실패했습니다.');
            }
        });
    }

    function updateProductGrid(products) {
        const grid = $('#productGrid');
        grid.empty();

        products.forEach(product => {
            const productItem = `
                <div class="product-item ${getHoverEffectClass(selectedSeason)}">
                    <a href="/user/product/detail/${product.id}">
                        <img src="${product.mainPicturePath}" alt="${product.name}" class="product-image">
                        <p class="product-name">${product.name}</p>
                        <p class="product-price">${product.price} 원</p>
                        <div class="product-details">
                            <span>${product.category || '카테고리 없음'}</span> |
                            <span>${getSeasonName(product.season)}</span> |
                            <span>${getTemperatureRange(product.temperature)}</span>
                        </div>
                    </a>
                </div>`;
            grid.append(productItem);
        });
    }

    function updatePagination(pageable) {
        const pagination = $('#pagination');
        pagination.empty();

        if (pageable.pageNumber > 0) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="0">처음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${pageable.pageNumber - 1}">이전</a></li>`);
        }

        for (let i = 0; i < pageable.totalPages; i++) {
            pagination.append(`<li class="page-item ${pageable.pageNumber === i ? 'active' : ''}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`);
        }

        if (pageable.pageNumber < pageable.totalPages - 1) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${pageable.pageNumber + 1}">다음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${pageable.totalPages - 1}">마지막</a></li>`);
        }

        $('.page-link').on('click', function (e) {
            e.preventDefault();
            const page = $(this).data('page');
            if (page !== undefined) {
                loadProducts(selectedSeason, page);
                currentPage = page;
            }
        });
    }

    function getSeasonName(season) {
        return season === 1 ? '봄' : season === 2 ? '여름' : season === 3 ? '가을' : '겨울';
    }

    function getTemperatureRange(temperature) {
        const ranges = {
            1: '≥28°C', 2: '27~23°C', 3: '22~20°C', 4: '19~17°C',
            5: '16~12°C', 6: '11~9°C', 7: '8~5°C', 8: '≤4°C'
        };
        return ranges[temperature] || '온도 정보 없음';
    }

    function getHoverEffectClass(season) {
        return season === 1 ? 'spring-hover-effect' :
            season === 2 ? 'summer-hover-effect' :
                season === 3 ? 'autumn-hover-effect' :
                    'winter-hover-effect';
    }
});
