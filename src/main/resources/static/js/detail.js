$(document).ready(function () {
    // 페이지가 로드되었을 때, 가격에 쉼표를 추가
    const priceField = $('#price');
    let originalPrice = parseInt(priceField.val(), 10);

    // originalPrice가 숫자인지 확인 (NaN일 경우 0으로 대체)
    if (isNaN(originalPrice)) {
        originalPrice = 0; // 기본값 설정 (필요 시 수정 가능)
    }

    // 숫자에 쉼표를 추가하여 표시
    priceField.val(originalPrice.toLocaleString());

    // 동적으로 가격을 입력받아야 한다면, 아래처럼 동작하게 추가할 수 있습니다.
    priceField.on('input', function () {
        // 기존 숫자를 제거하고 숫자만 남긴 후 쉼표 추가
        let rawValue = priceField.val().replace(/,/g, '');
        if (rawValue === '' || isNaN(parseInt(rawValue, 10))) {
            priceField.val('');
            return;
        }

        let formattedValue = parseInt(rawValue, 10).toLocaleString();

        // 쉼표가 추가된 값으로 다시 설정
        priceField.val(formattedValue);
    });

    // "장바구니 담기" 버튼 클릭 시 이벤트 리스너 등록
    $('#addToCartBtn').click(function (e) {
        e.preventDefault(); // 기본 동작 방지

        // 제품 ID와 수량 가져오기
        const productId = $('#productId').val();
        const quantity = $('#quantityInput').val();

        // JSON 데이터 생성
        const jsonData = JSON.stringify({
            "productId": productId,
            "quantity": quantity
        });

        // AJAX 요청을 보내기 전에 콘솔에 데이터 출력
        console.log("보낼 제품 ID:", productId);
        console.log("보낼 수량:", quantity);
        console.log("보낼 JSON 데이터:", jsonData);

        // 실제로 서버로 전송 (이 부분은 데이터 확인 후 실행)
        $.ajax({
            type: "POST",
            url: "/cart/add",
            contentType: "application/json",
            data: jsonData,
            success: function (response) {
                console.log("장바구니 추가 성공:", response);
                alert("상품이 장바구니에 추가되었습니다!");
            },
            error: function (xhr, status, error) {
                console.error("장바구니 추가 실패:", error);
                alert("상품 추가에 실패했습니다. 다시 시도해 주세요.");
            }
        });
    });
});
