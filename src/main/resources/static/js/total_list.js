$(document).ready(function () {
    let currentPage = 0; // 현재 페이지 번호
    const pageSize = 15; // 페이지당 로드할 상품 수

    // 현재 페이지의 상품을 로드하는 함수
    function loadProducts() {
        $.ajax({
            type: 'GET',
            url: '/user/product/api/list',
            data: { page: currentPage, size: pageSize }, // 현재 페이지와 페이지 크기를 쿼리로 전송
            success: (response) => {
                console.log("API 응답:", response);

                // 응답에 'products' 배열이 있는지 확인
                if (response && response.products) {
                    // 상품 목록을 HTML에 추가
                    response.products.forEach(product => {
                        $('#productGrid').append(`
                            <div class="product-item">
                                <a href="/user/product/detail/${product.id}">
                                    <img class="product-image" src="${product.mainPicturePath}" alt="${product.name}">
                                </a>
                                <div class="product-info">
                                    <a href="/user/product/detail/${product.id}" class="product-name">${product.name}</a>
                                    <p class="product-price">${Number(product.price).toLocaleString()} 원</p>
                                    <p class="product-details">
                                        <span>${getSeasonText(product.season)}</span>ㆍ
                                        <span>${product.temperature}°C</span>ㆍ
                                        <span>${product.category || '카테고리 없음'}</span>
                                    </p>
                                </div>
                            </div>
                        `);
                    });

                    // 마지막 페이지일 경우 '더보기' 버튼 숨김
                    if (response.last) {
                        $('#loadMoreBtn').hide();
                    } else {
                        currentPage++; // 다음 페이지를 준비
                    }
                } else {
                    console.error("오류: 응답에서 'products' 배열을 찾을 수 없습니다.");
                }
            },
            error: (xhr, status, error) => {
                console.error('상품을 불러오는 중 오류 발생:', status, error);
                alert('상품을 불러오는 중 문제가 발생했습니다. 다시 시도해주세요.');
            }
        });
    }

    // 초기 로드 - 첫 페이지의 상품을 로드
    loadProducts();

    // '더보기' 버튼 클릭 시 다음 페이지의 상품을 로드
    $('#loadMoreBtn').click(function () {
        loadProducts(); // 더보기 버튼 클릭 시 loadProducts() 함수 호출
    });

    // 계절 이름을 반환하는 헬퍼 함수
    function getSeasonText(season) {
        switch (season) {
            case 1: return '봄';
            case 2: return '여름';
            case 3: return '가을';
            case 4: return '겨울';
            default: return '계절 정보 없음';
        }
    }
});
