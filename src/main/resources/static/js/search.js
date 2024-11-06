// 페이지 로드 시 자동으로 상품 목록을 표시
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('productSearchInput').value = ''; // 검색어 초기화
    document.getElementById('productListSection').style.display = 'block'; // 상품 리스트 영역 표시
});

// 검색 버튼 클릭 시 호출될 함수
function searchProducts() {
    loadProducts(0); // 첫 페이지에서 검색어로 필터링하여 로드
}

// 상품 목록을 가져와 표시하는 함수, 검색어 필터 추가
function loadProducts(page) {
    const searchTerm = document.getElementById('productSearchInput').value; // 검색어 가져오기
    const url = `/user/product/search?page=${page}&size=10` + (searchTerm ? `&name=${encodeURIComponent(searchTerm)}` : '');

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답에 문제가 있습니다.');
            }
            return response.json();
        })
        .then(data => {
            console.log('Fetched data:', data); // API 응답 데이터 구조 확인
            if (data.content) { // data.content가 존재하는 경우에만 처리
                updateProductList(data.content); // 상품 목록 업데이트
                updatePagination(data); // 페이지네이션 업데이트
            } else {
                console.error("데이터가 예상한 구조가 아닙니다:", data); // 데이터 구조 오류 출력
            }
        })
        .catch(error => console.error('상품 목록을 가져오는 중 오류 발생:', error));
}

// 상품 목록 테이블 업데이트 함수
function updateProductList(products) {
    const productList = document.getElementById('productList');
    const productTableHeader = document.getElementById('productTableHeader');
    productList.innerHTML = ''; // 기존 목록 비우기

    if (products.length > 0) {
        productTableHeader.style.display = 'table-header-group'; // 상품이 있을 때만 헤더 표시
        products.forEach((product, index) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${index + 1}</td>
                <td><img src="${product.mainPicturePath}" alt="${product.name}" style="width: 50px; border-radius: 10px;"></td>
                <td>${product.name}</td>
                <td>${product.price.toLocaleString()} 원</td>
                <td>${getSeasonLabel(product.season)}</td>
                <td>${getTemperatureLabel(product.temperature)}</td>
            `;
            row.onclick = () => goToProductDetail(product.id); // 상품 선택 시 상세 페이지로 이동
            productList.appendChild(row);
        });
    } else {
        productTableHeader.style.display = 'none'; // 상품이 없을 때 헤더 숨김
        displayNoResultsMessage(); // "조회한 상품이 없습니다" 메시지 표시
    }
}

// 상품이 없을 때 표시할 메시지 함수
function displayNoResultsMessage() {
    const productList = document.getElementById('productList');
    productList.innerHTML = ''; // 기존 목록 비우기
    const row = document.createElement('tr');
    row.innerHTML = `<td colspan="6" style="text-align: center;">조회한 상품이 없습니다</td>`;
    productList.appendChild(row);
}

// 상세 페이지로 이동하는 함수
function goToProductDetail(productId) {
    window.location.href = `http://localhost:8080/user/product/detail/${productId}`;
}

// 계절 표시 함수
function getSeasonLabel(season) {
    return season === 1 ? '봄' : season === 2 ? '여름' : season === 3 ? '가을' : '겨울';
}

// 온도 표시 함수
function getTemperatureLabel(temp) {
    switch (temp) {
        case 1: return '≥28°C';
        case 2: return '27~23°C';
        case 3: return '22~20°C';
        case 4: return '19~17°C';
        case 5: return '16~12°C';
        case 6: return '11~9°C';
        case 7: return '8~5°C';
        default: return '≤4°C';
    }
}

// 페이지네이션 업데이트
function updatePagination(data) {
    const pagination = document.querySelector('.pagination');
    pagination.innerHTML = '';

    if (data.number > 0) {
        pagination.innerHTML += `<li><a href="#" onclick="loadProducts(0)">◀◀</a></li>`;
    }
    if (data.number > 0) {
        pagination.innerHTML += `<li><a href="#" onclick="loadProducts(${data.number - 1})">이전</a></li>`;
    }
    for (let i = data.number - 2; i <= data.number + 2; i++) {
        if (i >= 0 && i < data.totalPages) {
            pagination.innerHTML += `<li><a href="#" onclick="loadProducts(${i})" class="${i === data.number ? 'active' : ''}">${i + 1}</a></li>`;
        }
    }
    if (data.number < data.totalPages - 1) {
        pagination.innerHTML += `<li><a href="#" onclick="loadProducts(${data.number + 1})">다음</a></li>`;
    }
    if (data.number < data.totalPages - 1) {
        pagination.innerHTML += `<li><a href="#" onclick="loadProducts(${data.totalPages - 1})">▶▶</a></li>`;
    }
}

// 페이지 로드 시 상품 리스트 영역을 기본으로 표시하도록 설정
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('productListSection').style.display = 'block';
});
