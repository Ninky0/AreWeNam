$(document).ready(function() {
    const customerId = $('#customerId').val(); // 실제 customerId 값을 가져옵니다.
    loadPosts(0); // 초기 페이지 로드

    function loadPosts(page) {
        $.ajax({
            url: `/user/api/posts?customerId=${customerId}&page=${page}&size=5`,
            type: 'GET',
            success: function(response) {
                $('#post_list').empty(); // 기존 게시글 초기화
                const posts = response.content; // API 응답에서 content를 가져옵니다.

                posts.forEach(function(post) {
                    const createdAt = new Date(post.createdAt).toLocaleDateString(); // 작성일 형식 변환
                    const imagePath = post.picturePath || '/images/default-placeholder.png'; // 이미지 경로 설정
                    const content = post.source === 'ootd' ? post.tag : post.content; // OOTD는 tag, Review는 content 사용

                    // 게시글 HTML 요소 생성
                    const postRow = `
                        <tr>
                            <td>${post.id}</td>
                            <td>${createdAt}</td>
                            <td>${content || '내용 없음'}</td>
                            <td><img class="post-image" src="${imagePath}" alt="게시물 이미지" style="width: 50px; height: auto; border-radius: 5px;"></td>
                        </tr>`;
                    $('#post_list').append(postRow); // 게시글을 테이블에 추가
                });

                updatePagination(response.startPage, response.endPage, response.totalPages, page); // 페이지네이션 업데이트
            },
            error: function(xhr, status, error) {
                console.error("게시물을 불러오는데 실패했습니다: " + error);
            }
        });
    }

    // 페이지네이션 링크 이벤트 핸들러 설정
    $('#pagination').on('click', 'a', function(event) {
        event.preventDefault();
        const page = $(this).data('page'); // data-page 속성에서 페이지 번호를 얻음
        loadPosts(page);
    });

    // 페이지네이션 UI 업데이트 함수
    function updatePagination(startPage, endPage, totalPages, currentPage) {
        const pagination = $('#pagination');
        pagination.empty();

        if (currentPage > 0) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="0">처음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}">이전</a></li>`);
        }

        for (let i = startPage; i <= endPage; i++) {
            const activeClass = currentPage === i ? 'active' : '';
            pagination.append(`<li class="${activeClass} page-item"><a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`);
        }

        if (currentPage < totalPages - 1) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}">다음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${totalPages - 1}">마지막</a></li>`);
        }
    }
});
