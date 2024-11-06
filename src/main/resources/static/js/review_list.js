$(document).ready(function () {
    var productId = $('#productId').val();
    $('#writeBtn').attr('href', '/mypage/history');
    // alert("review list : "+productId); // 잘 들어옴
    const start = performance.now();
    loadReviews(0); // 초기 페이지 로드

    function loadReviews(page) {
        $.ajax({
            url: `/review/list/${productId}?page=${page}&size=5`,
            type: 'GET',
            success: function (response) {
                $('#boardContent').empty();
                var reviews = response.reviews;
                reviews.forEach(function (review) {
                    var customerLoginId = review.customer ? review.customer.loginId : '익명';
                    var formattedDate = new Date(review.date).toLocaleDateString('ko-KR'); // 'ko-KR'은 한국 날짜 형식으로 변경
                    $('#boardContent').append(
                        `<tr>
                            <td>${review.id}</td>
                            <td>${customerLoginId}</td>
                            <td>${productId}</td>
                            <td>${review.content}</td>
                            <td><img class="review-image" src="${review.picturePath}" alt="리뷰 이미지"/></td>
                            <td>${formattedDate}</td>
                        </tr>`
                    );
                });
                updatePagination(response.startPage, response.endPage, response.totalPages, page);
            },
            error: function (xhr, status, error) {
                console.error("리뷰를 불러오는데 실패했습니다: " + error);
            },
            complete: () => {
                const end = performance.now();
                console.log(`Page loaded in ${end - start} milliseconds`);
            }
        });
    }

    // 페이징 링크에 대한 이벤트 핸들러를 동적으로 설정
    $('#pagination').on('click', 'a', function (event) {
        event.preventDefault();
        var page = $(this).data('page'); // data-page 속성을 통해 페이지 번호를 얻음
        loadReviews(page);
    });

    function updatePagination(startPage, endPage, totalPages, currentPage) {
        var pagination = $('#pagination');
        pagination.empty();

        if (currentPage > 0) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="0">처음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}">이전</a></li>`);
        }

        for (let i = startPage; i <= endPage; i++) {
            let activeClass = currentPage === i ? 'active' : '';
            pagination.append(`<li class="${activeClass} page-item"><a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`);
        }

        if (currentPage < totalPages - 1) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}">다음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${totalPages - 1}">마지막</a></li>`);
        }
    }
});
