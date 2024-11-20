$(document).ready(function() {
    // 체크박스 클릭 이벤트
    $('.checkbox').change(function() {
        updateTotals(); // 체크박스 상태 변경 시 총합 업데이트
    });

    // 전체 선택/해제 체크박스
    $('#selectAll').change(function() {
        $('tbody .checkbox').prop('checked', this.checked);
        updateTotals(); // 전체 선택/해제 시 총합 업데이트
    });

    // 수량 변경 로직
    $('.increaseBtn, .decreaseBtn').click(function(event) {
        var row = $(this).closest('tr');
        var quantityElement = row.find('.quantity');
        var priceElement = row.find('.th_2');
        var price = parseInt(priceElement.data('price'));
        var quantity = parseInt(quantityElement.text());

        if ($(this).hasClass('increaseBtn')) {
            quantity += 1;
        } else {
            quantity = Math.max(1, quantity - 1);
        }

        quantityElement.text(quantity);
        var orderAmount = price * quantity;
        priceElement.text(orderAmount.toLocaleString() + ' 원');

        updateTotals();
    });

    // 총합 업데이트 함수
    function updateTotals() {
        var totalAmount = 0;
        var totalShipping = 0; // 배송비를 초기에 0으로 설정

        $('tbody tr').each(function() {
            var checkbox = $(this).find('.checkbox');
            if (checkbox.is(':checked')) {
                var priceElement = $(this).find('.th_2');
                var quantityElement = $(this).find('.quantity');
                var price = parseInt(priceElement.data('price'));
                var quantity = parseInt(quantityElement.text());

                totalAmount += price * quantity; // 체크된 상품의 가격과 수량을 곱하여 totalAmount에 추가
            }
        });

        if (totalAmount > 0) {
            totalShipping = 4000; // totalAmount가 0이 아니면 배송비를 4000원으로 설정
        }

        var grandTotal = totalAmount + totalShipping; // 총합 계산
        $('#totalAmount').text(totalAmount.toLocaleString() + ' 원');
        $('#totalShipping').text(totalShipping.toLocaleString() + ' 원');
        $('#grandTotal').text(grandTotal.toLocaleString() + ' 원');
    }

    updateTotals();

    $('.delete-btn').click(function() {
        const selectedRows = $('.checkbox:checked').map(function() {
            const productId = $(this).val(); // value 속성에서 제품 ID 가져오기
            console.log("productId 값:", productId); // 제품 ID 출력
            return {
                productId: productId // 가져온 productId를 객체에 저장
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
        // 전송할 데이터 확인
        console.log("전송되는 데이터:", JSON.stringify(selectedRows));
        console.log("선택된 행:", selectedRows);

        // 각 행의 productId 로그 출력
        selectedRows.forEach(row => {
            console.log("productId 값:", row.productId);
        });

        $.ajax({
            type: 'DELETE',
            url: '/mypage/shoppingcart',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(selectedRows), // 선택된 상품 ID JSON 형식으로 변환
            dataType: 'json',
            success: function(response) {
                console.log("서버 응답:", response); // 서버 응답 로그 출력
                alert(response.message); // JSON 객체에서 메시지 추출
                location.reload(); // 페이지 리로드
            },
            error: function(xhr, status, error) {
                console.error('오류 발생:', xhr.status, xhr.responseText);
                alert('제품 삭제 중 오류가 발생했습니다.');
            }
        });
    }

    $('#orderButton').click(function() {
        var customerId = $('#customerId').val(); // 고객 ID 가져오기
        console.log("customerId: " + customerId);

        var grandTotalText = $('#grandTotal').text().replace(/원/g, '').replace(/,/g, '');
        var grandTotal = parseInt(grandTotalText);

        const selectedProducts = $('.checkbox:checked').map(function() {
            const row = $(this).closest('tr');
            const productId = $(this).val(); // value 속성에서 제품 ID 가져오기
            const quantity = row.find('.quantity').text(); // 수량 정보도 함께 가져오기
            console.log("productId 값:", productId, "수량:", quantity, "총액:", grandTotal);
            return {
                productId: productId,
                quantity: parseInt(quantity) // 문자열을 숫자로 변환
            };
        }).get(); // jQuery 객체를 배열로 변환

        if (selectedProducts.length === 0) {
            alert('상품을 선택하세요.');
            return;
        }

        if (confirm('선택한 제품을 구매하시겠습니까?')) {
            purchaseSelectedProducts(customerId, selectedProducts, grandTotal);
        }
    });

    function purchaseSelectedProducts(customerId, selectedProductIds, grandTotal) {
        console.log("구매 요청 데이터:", JSON.stringify(selectedProductIds));

        $.ajax({
            type: 'POST',
            url: '/user/purchase',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({
                customerId: customerId,
                products: selectedProductIds,
                grandTotal: grandTotal
            }),
            dataType: 'json',
            success: function(response) {
                console.log("구매 처리 응답:", response);
                alert(response.message);
                location.reload(); // 페이지를 새로 고침하여 구매 후 상태를 반영
            },
            error: function(xhr, status, error) {
                console.error('구매 처리 중 오류 발생:', xhr.status, xhr.responseText);
                alert('상품 구매 중 오류가 발생했습니다.');
            }
        });
    }

});