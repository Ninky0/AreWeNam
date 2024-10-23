$(document).ready(function() {
    // "전체 선택" 체크박스 클릭 시
    document.getElementById('selectAll').addEventListener('change', function() {
        const checkboxes = document.querySelectorAll('.checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        });
    });

    // 각 체크박스 클릭 시 전체 선택 체크박스 상태 업데이트
    const checkboxes = document.querySelectorAll('.checkbox:not(#selectAll)');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const allChecked = Array.from(checkboxes).every(cb => cb.checked);
            document.getElementById('selectAll').checked = allChecked;
        });
    });

    // 모달 열기 및 닫기 기능
    const optionChangeBtns = document.querySelectorAll('.option-change-btn');
    const modal = document.getElementById('optionModal');
    const closeModal = document.querySelector('.close');
    const saveChangesBtn = document.getElementById('saveChanges');

    optionChangeBtns.forEach((btn) => {
        btn.addEventListener('click', () => {
            const selectedRows = Array.from(document.querySelectorAll('tbody tr'))
                .filter(row => row.querySelector('.checkbox').checked);

            if (selectedRows.length === 0) {
                alert('옵션을 변경할 상품을 선택해 주세요.');
                return;
            }

            // 선택된 행의 첫 번째 행에서 현재 옵션 정보를 불러오기
            const firstRow = selectedRows[0];
            const seasonDisplay = firstRow.querySelector('.seasonDisplay').textContent;

            // 모달에 현재 옵션 설정
            document.getElementById('season').value = seasonDisplay;

            // 모달 표시
            modal.style.display = 'block';
        });
    });

    closeModal.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }

    saveChangesBtn.addEventListener('click', () => {
        const season = document.getElementById('season').value;

        // 모든 선택된 행의 상품 정보를 업데이트
        const selectedRows = Array.from(document.querySelectorAll('tbody tr'))
            .filter(row => row.querySelector('.checkbox').checked);

        selectedRows.forEach(row => {
            const seasonDisplay = row.querySelector('.seasonDisplay');
            seasonDisplay.textContent = season;
        });

        // 모달 닫기
        modal.style.display = 'none';
    });

    // 페이지 로드 시 총합 및 초기 가격 업데이트
    document.querySelectorAll('tbody tr').forEach(row => {
        const priceElement = row.querySelector('.th_2');
        const quantityElement = row.querySelector('.quantity');
        const price = parseInt(priceElement.getAttribute('data-price'));
        const quantity = parseInt(quantityElement.textContent);

        // 주문 금액 업데이트
        const orderAmount = price * quantity;
        priceElement.textContent = orderAmount.toLocaleString() + ' 원';
    });

    // 수량 변경 버튼 클릭 시 총합 및 주문금액 업데이트
    document.querySelectorAll('.increaseBtn, .decreaseBtn').forEach(button => {
        button.addEventListener('click', (event) => {
            const row = event.target.closest('tr'); // 클릭한 버튼의 부모 행을 찾음
            const quantityElement = row.querySelector('.quantity'); // 주문 수량 요소
            const priceElement = row.querySelector('.th_2'); // 가격 요소
            const price = parseInt(priceElement.getAttribute('data-price')); // 가격 가져오기
            let quantity = parseInt(quantityElement.textContent); // 현재 수량 가져오기

            // 수량 증가 또는 감소
            if (event.target.classList.contains('increaseBtn')) {
                quantity += 1; // 수량 증가
            } else {
                quantity = Math.max(1, quantity - 1); // 수량 감소 (1 미만으로는 안됨)
            }

            // 수량 업데이트
            quantityElement.textContent = quantity; // 수량 업데이트

            // 주문 금액 업데이트
            const orderAmount = price * quantity;
            priceElement.textContent = orderAmount.toLocaleString() + ' 원'; // 총 주문 금액 업데이트

            // 총합 업데이트
            updateTotals();
        });
    });

    // 총합 업데이트 함수
    function updateTotals() {
        let totalAmount = 0;
        const totalShipping = 100000; // 각 상품에 대한 고정 배송비 설정
        let shippingCost = 0;

        document.querySelectorAll('tbody tr').forEach(row => {
            const priceElement = row.querySelector('.th_2');
            const quantityElement = row.querySelector('.quantity');

            const priceText = priceElement.getAttribute('data-price');
            const price = parseInt(priceText);
            const quantity = parseInt(quantityElement.textContent);

            totalAmount += price * quantity;
        });

        const itemCount = document.querySelectorAll('tbody tr').length;
        shippingCost = itemCount * totalShipping;
        const grandTotal = totalAmount + shippingCost;

        document.getElementById('totalAmount').textContent = totalAmount.toLocaleString() + ' 원';
        document.getElementById('totalShipping').textContent = shippingCost.toLocaleString() + ' 원';
        document.getElementById('grandTotal').textContent = grandTotal.toLocaleString() + ' 원';
    }

// 페이지 로드 시 총합을 업데이트
    updateTotals();


    $('.delete-btn').click(function() {
        const customerId = $('#customerId').val(); // customerId 가져오기
        const selectedRows = $('.checkbox:checked').map(function() {
            return {
                productId: $(this).closest('tr').data('product-id') // data 속성에서 제품 ID 가져오기
            };
        }).get(); // jQuery 객체를 배열로 변환

        if (selectedRows.length === 0) {
            alert('삭제할 제품을 선택해주세요.');
            return;
        }

        if (confirm('선택한 제품을 삭제하시겠습니까?')) {
            deleteSelectedProducts(customerId, selectedRows);
        }
    });

    function deleteSelectedProducts(customerId, selectedRows) {
        $.ajax({
            type: 'DELETE',
            url: '/user/shoppingcart', // 템플릿 리터럴 사용
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(selectedRows), // 선택된 상품 ID JSON 형식으로 변환
            dataType: 'json',
            success: function(response) {
                alert('선택된 제품이 삭제되었습니다.');
                location.reload();
            },
            error: function(error) {
                console.error('오류 발생:', error);
                alert('제품 삭제 중 오류가 발생했습니다.');
            }
        });
    }

});
