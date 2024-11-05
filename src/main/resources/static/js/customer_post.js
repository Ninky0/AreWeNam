$(document).ready(function () {
    const customerId = $('#customerId').val();
    const pageSize = 5;

    // 초기 설정: OOTD 탭 활성화 및 데이터 로드
    loadOotdPosts(0);

    // 탭 클릭 이벤트
    $('#ootdTab').on('click', function () {
        $('#ootdContent').show();
        $('#reviewContent').hide();
        $(this).addClass('active');
        $('#reviewTab').removeClass('active');
        loadOotdPosts(0); // OOTD 데이터 로드
    });

    $('#reviewTab').on('click', function () {
        $('#reviewContent').show();
        $('#ootdContent').hide();
        $(this).addClass('active');
        $('#ootdTab').removeClass('active');
        loadReviewPosts(0); // 리뷰 데이터 로드
    });

    // OOTD 게시글 로드 함수
    function loadOotdPosts(page) {
        $.ajax({
            url: `/mypage/posts/${customerId}?page=${page}&size=${pageSize}`,
            type: 'GET',
            success: function (response) {
                console.log("OOTD response:", response);
                renderPosts(response.content, '#boardContent', page, 'OOTD');
                updatePagination(response.totalPages, page, loadOotdPosts);
            },
            error: function (xhr, status, error) {
                console.error("Failed to load OOTD posts:", error);
            }
        });
    }

    // 리뷰 게시글 로드 함수
    function loadReviewPosts(page) {
        $.ajax({
            url: `/review/posts?customerId=${customerId}&page=${page}&size=${pageSize}`,
            type: 'GET',
            success: function (response) {
                console.log("Review response:", response);
                const reviews = Array.isArray(response) ? response : response.content;
                if (reviews && reviews.length > 0) {
                    renderPosts(reviews, '#reviewBoardContent', page, 'Review');
                    updatePagination(response.totalPages || 1, page, loadReviewPosts);
                } else {
                    console.error("Review posts content is missing.");
                }
            },
            error: function (xhr, status, error) {
                console.error("Failed to load review posts:", error);
            }
        });
    }

    // 게시글을 테이블에 렌더링하는 함수
    function renderPosts(posts, targetTableId, page, type) {
        $(targetTableId).empty();
        const startNumber = page * pageSize + 1;

        posts.forEach(function (post, index) {
            const postNumber = startNumber + index;
            const formattedDate = new Date(post.date || post.createdAt).toLocaleDateString('ko-KR');
            const picturePath = type === 'OOTD' ? post.picture : post.picturePath; // OOTD와 리뷰의 이미지 필드 구분
            const content = type === 'OOTD' ? post.tag || '내용 없음' : post.content || '내용 없음'; // OOTD의 tag와 리뷰의 content 필드 구분
            const loginId = post.loginId || '익명';
            const productName = type === 'OOTD' ? post.productName || '상품명 없음' : ''; // OOTD의 productName 처리

            $(targetTableId).append(
                `<tr>
                    <td>${postNumber}</td>
                    <td>${loginId}</td>
                    ${type === 'OOTD' ? `<td>${productName}</td>` : ''} <!-- OOTD인 경우에만 상품명 표시 -->
                    <td>${content}</td>
                    <td><img class="${type.toLowerCase()}-image" src="${picturePath}" alt="${type} 이미지" style="width: 100px; height: auto;"/></td>
                    <td>${formattedDate}</td>
                </tr>`
            );
        });
    }

    // 페이지네이션 업데이트 함수
    function updatePagination(totalPages, currentPage, loadFunction) {
        const pagination = $('#pagination');
        pagination.empty();

        if (currentPage > 0) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="0">처음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}">이전</a></li>`);
        }

        for (let i = 0; i < totalPages; i++) {
            const activeClass = currentPage === i ? 'active' : '';
            pagination.append(`<li class="${activeClass} page-item"><a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`);
        }

        if (currentPage < totalPages - 1) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}">다음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${totalPages - 1}">마지막</a></li>`);
        }

        pagination.find('.page-link').on('click', function (event) {
            event.preventDefault();
            const page = $(this).data('page');
            loadFunction(page);
        });
    }
});
