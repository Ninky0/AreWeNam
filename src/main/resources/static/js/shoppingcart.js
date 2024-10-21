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

    // 총합 업데이트 함수
    function updateTotals() {
        const rows = document.querySelectorAll('tbody tr');
        let totalAmount = 0;
        let totalShipping = 0;

        rows.forEach(row => {
            const amountCell = row.querySelector('td:nth-child(5)'); // 주문금액
            const shippingCell = row.querySelector('td:nth-child(6)'); // 배송비

            const amount = parseInt(amountCell.textContent.replace(/[^0-9]/g, ''));
            const shipping = parseInt(shippingCell.textContent.replace(/[^0-9]/g, ''));

            totalAmount += amount;
            // 배송비는 고정값인 100,000원으로 설정
            const fixedShipping = 100000;
            totalShipping += fixedShipping; // 추가: 고정 배송비를 더함
        });

        const grandTotal = totalAmount + totalShipping;

        document.getElementById('totalAmount').textContent = totalAmount.toLocaleString() + '원';
        document.getElementById('totalShipping').textContent = totalShipping.toLocaleString() + '원';
        document.getElementById('grandTotal').textContent = grandTotal.toLocaleString() + '원';
    }

    // 페이지 로드 시 총합을 업데이트
    updateTotals();

    // 수량 변경 버튼 클릭 시 총합 및 주문금액 업데이트
    document.querySelectorAll('.increaseBtn, .decreaseBtn').forEach(button => {
        button.addEventListener('click', (event) => {
            const row = event.target.closest('tr'); // 클릭한 버튼의 부모 행을 찾음
            const quantityElement = row.querySelector('#number');
            const priceElement = row.querySelector('td:nth-child(5)'); // 주문금액 열
            const shippingElement = row.querySelector('td:nth-child(6)'); // 배송비 열

            // 현재 수량 가져오기
            let quantity = parseInt(quantityElement.textContent);

            if (event.target.classList.contains('increaseBtn')) {
                quantity += 1; // 수량 증가
            } else {
                quantity = Math.max(0, quantity - 1); // 수량 감소 (0 미만으로는 안됨)
            }

            // 수량 업데이트
            quantityElement.textContent = quantity;

            // 주문금액 계산 (예시로 가격을 단순화하여 사용)
            const pricePerItem = 268920; // 예시 가격, 실제로는 상품 가격에 따라 다르게 설정
            const newAmount = pricePerItem * quantity;

            // 주문금액 업데이트
            priceElement.textContent = newAmount.toLocaleString() + '원';

            // 총합 업데이트
            updateTotals();
        });
    });

    // 선택상품삭제 버튼 클릭 시
    const deleteBtns = document.querySelectorAll('.delete-btn');

    deleteBtns.forEach((btn) => {
        btn.addEventListener('click', () => {
            const selectedRows = document.querySelectorAll('.checkbox:checked'); // 체크된 행 선택

            if (selectedRows.length === 0) {
                alert('삭제할 상품을 선택해 주세요.');
                return;
            }

            selectedRows.forEach((checkbox) => {
                const row = checkbox.closest('tr'); // 체크된 행 선택
                row.remove(); // 해당 행 삭제
            });

            updateTotals(); // 총액 업데이트 함수 호출
        });
    });

});
