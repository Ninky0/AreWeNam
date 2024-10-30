// 상품 검색 모달 열기
function openProductSearchModal() {
    document.getElementById('productSearchInput').value = ''; // 검색어 초기화
    loadProducts(0); // 첫 페이지 로드 시 모든 상품 불러오기
    document.getElementById('productModal').style.display = 'block'; // 모달 열기
}

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

// 상품 목록 테이블 업데이트 함수 (기존 코드 그대로 사용)
function updateProductList(products) {
    const productList = document.getElementById('productList');
    productList.innerHTML = ''; // 기존 목록 비우기

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
        row.onclick = () => selectProduct(product.id); // 상품 선택 시 ID 반환
        productList.appendChild(row);
    });
}

// 검색 버튼 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('searchButton').addEventListener('click', searchProducts); // 검색 버튼 클릭 시 호출
});
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

// 상품을 선택하고 모달을 닫는 함수
function selectProduct(productId) {
    document.getElementById('product_id').value = productId;

    fetch(`/user/product/ootd_detail/${productId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답에 문제가 있습니다.');
            }
            return response.json();
        })
        .then(data => {
            // 선택한 상품 정보 표시 영역 업데이트
            document.getElementById('selectedProductName').textContent = data.name;
            document.getElementById('selectedProductPrice').textContent = data.price.toLocaleString() + ' 원';
            document.getElementById('selectedProductCategory').textContent = data.category;
            document.getElementById('selectedProductSeason').textContent = getSeasonLabel(data.season);
            document.getElementById('selectedProductTemperature').textContent = getTemperatureLabel(data.temperature);
            document.getElementById('selectedProductImage').src = data.mainPicturePath;

            // 상품 정보 표시 영역을 보이도록 설정
            document.getElementById('selectedProductDetails').style.display = 'flex';
        })
        .catch(error => {
            console.error('상품 정보를 불러오는 중 오류가 발생했습니다:', error);
            alert('상품 정보를 불러오는 중 오류가 발생했습니다.');
        });

    closeProductSearchModal();
}

// 페이지가 로드될 때 선택한 상품 정보가 보이지 않도록 설정
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('selectedProductDetails').style.display = 'none';
});

// 모달을 닫는 함수
function closeProductSearchModal() {
    document.getElementById('productModal').style.display = 'none';
}
function previewMainImage(event) {
    const file = event.target.files[0];
    const imagePreview = document.getElementById("imagePreview");
    const placeholderText = document.getElementById("placeholderText");

    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            imagePreview.src = e.target.result;
            imagePreview.style.display = "block"; // 이미지가 로드되면 표시
            placeholderText.style.display = "none"; // 플레이스홀더 텍스트 숨기기
        };
        reader.readAsDataURL(file);
    } else {
        imagePreview.style.display = "none";
        placeholderText.style.display = "block";
    }
}
function submitForm() {
    const formData = new FormData(document.getElementById("ootdForm"));

    fetch('/user/ootd_write', { // URL 경로 확인 후 수정
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("서버 응답이 문제입니다.");
            }
            return response.json();
        })
        .then(data => {
            if (data.url) {
                alert(data.message || "상품 등록이 완료되었습니다.");
                window.location.href = data.url;
            } else {
                alert("응답 데이터에 URL이 없습니다. 서버 응답을 확인하세요.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("게시글 등록 중 오류가 발생했습니다.");
        });
}
function loadProducts(page) {
    const searchTerm = document.getElementById('productSearchInput').value;
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
