$(document).ready(function() {
    $('#season').change(function() {
        var selectedSeason = $(this).val();



        // "선택할래" 옵션이 선택되면 특정 페이지로 이동
        if (selectedSeason === "") {
            location.reload(); // 현재 페이지 새로고침
        } else {
            loadProducts(selectedSeason, 0); // 기본적으로 첫 페이지 요청
        }
    });

    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();

        var selectedSeason = $('#season').val();
        var page = $(this).data('page'); // data-page 속성에서 페이지 번호 가져오기

        // 페이지 번호가 undefined일 경우 기본 페이지 번호(0)를 설정
        if (page === undefined) {
            page = 0; // 기본 페이지 번호
        }

        console.log('Selected Season:', selectedSeason);
        console.log('Page Number:', page); // page 값을 로그로 출력

        // 드롭다운에서 선택된 계절 값이 없을 때 처리
        if (!selectedSeason) {
            alert('계절을 선택해 주세요.');
            return; // 계절이 선택되지 않으면 함수 종료
        }

        // "선택할래" 옵션일 때 AJAX 요청 방지
        if (selectedSeason === "") {
            return; // 선택할래일 때 함수 종료
        }

        // page가 null인 경우 처리
        if (page === undefined || page === null) {
            console.error('Page number is undefined or null.');
            return; // 페이지가 없으면 함수 종료
        }

        loadProducts(selectedSeason, page);
    });

    function loadProducts(season, page) {
        $.ajax({
            url: '/user/seasonproduct_list',
            method: 'POST',
            data: { season: season, page: page }, // 선택한 계절과 페이지를 서버에 전달
            success: function(data) {
                console.log(data); // 데이터 구조 확인
                console.log("Products loaded: ", data);
                console.log("Received response: ", data); // 성공적으로 응답을 받았을 때 로그
                updateProductTable(data.content); // 제품 목록 업데이트
                updatePagination(data); // 페이지네이션 업데이트
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error:', textStatus, errorThrown);
                alert('상품 목록을 가져오는 데 실패했습니다.');
            }
        });
    }
});

function updateProductTable(products) {
    var tbody = $('tbody');
    tbody.empty(); // 기존 데이터를 지웁니다.

    // 새로운 데이터로 테이블을 채웁니다.
    $.each(products, function(index, product) {
        console.log(product);
        var row = '<tr>' +
            '<td>' + (index + 1) + '</td>' +
            '<td><a href="/user/seasonproduct_list/' + product.id + '"><img src="' + product.mainPicturePath + '" alt="' + product.name + '"/></a></td>' +
            '<td><a href="/user/seasonproduct_list/' + product.id + '">' + product.name + '</a></td>' +
            '<td>' + (product.price ? Math.floor(product.price) : 'N/A') + '</td>' +
            '<td>' + (product.season == 1 ? '봄' : (product.season == 2 ? '여름' : (product.season == 3 ? '가을' : '겨울'))) + '</td>' +
            '<td>' + (product.temperature == 1 ? '≥28°C' :
                (product.temperature == 2 ? '27~23°C' :
                    (product.temperature == 3 ? '22~20°C' :
                        (product.temperature == 4 ? '19~17°C' :
                            (product.temperature == 5 ? '16~12°C' :
                                (product.temperature == 6 ? '11~9°C' :
                                    (product.temperature == 7 ? '8~5°C' : '≤4°C'))))))) + '</td>' +
            '</tr>';
        tbody.append(row);
    });
}

function updatePagination(pageable) {
    var pagination = $('.pagination');
    pagination.empty(); // 기존 페이지네이션 지우기

    // 페이지네이션 생성
    if (pageable.pageNumber > 0) {
        pagination.append('<li class="page-item"><a class="page-link" href="#" data-page="0">처음</a></li>');
        pagination.append('<li class="page-item"><a class="page-link" href="#" data-page="' + (pageable.pageNumber - 1) + '">이전</a></li>');
    }

    for (var i = 0; i < pageable.totalPages; i++) {
        pagination.append('<li class="page-item ' + (pageable.pageNumber === i ? 'active' : '') + '"><a class="page-link" href="#" data-page="' + i + '">' + (i + 1) + '</a></li>');
    }

    if (pageable.pageNumber < pageable.totalPages - 1) {
        pagination.append('<li class="page-item"><a class="page-link" href="#" data-page="' + (pageable.pageNumber + 1) + '">다음</a></li>');
        pagination.append('<li class="page-item"><a class="page-link" href="#" data-page="' + (pageable.totalPages - 1) + '">마지막</a></li>');
    }
}