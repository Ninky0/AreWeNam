
$(document).ready(() => {
    checkSession();
    getBoards();
});

// let checkSession = () => {
//     let hUserId = $('#hiddenUserId').val();
//
//     if (hUserId == null || hUserId === '')
//         window.location.href = "/user/login";
// }

let getBoards = () => {
    let currentPage = 1;
    const pageSize = 10; // 한 페이지에 보여줄 게시글 수

    // 초기 게시글 로드
    loadBoard(currentPage, pageSize);

    // 다음 페이지 버튼 클릭 이벤트
    $('#nextPage').on('click', () => {
        currentPage++;
        loadBoard(currentPage, pageSize);
    });

    // 이전 페이지 버튼 클릭 이벤트
    $('#prevPage').on('click', function() {
        if (currentPage > 1) {
            currentPage--;
            loadBoard(currentPage, pageSize);
        }
    });
}

// 게시글 데이터를 로드하는 함수
let loadBoard = (page, size) => {
    $.ajax({
        type: 'GET',
        url: '/admin/product/list',
        data: {
            page: page,
            size: size
        },
        success: (response) => {
            $('#boardContent').empty(); // 기존 게시글 내용 비우기
            if (response.boards.length <= 0) {
                // 게시글이 없는 경우 메시지 출력
                $('#boardContent').append(
                    `<tr>
                        <td colspan="4" style="text-align: center;">글이 존재하지 않습니다.</td>
                    </tr>`
                );
            } else {
                response.boards.forEach((item) => {
                    $('#boardContent').append(
                        `
                    <tr>
                                <td>${product.id}</td>
                                <td><img src="${product.picture}" alt="${product.name}"></td>
                                <td>${product.name}</td>
                                <td>${product.price.toLocaleString()} 원</td>
                                <td>${product.season === 1 ? 'Winter' : 'Summer'}</td>
                                <td>${product.temperature}°C</td>
                            </tr>

                    `
                    );
                });

            }
            // 페이지 정보 업데이트
            $('#pageInfo').text(page);

            // 이전/다음 버튼 상태 설정
            $('#prevPage').prop('disabled', page === 1);
            $('#nextPage').prop('disabled', response.last);
        },
        error: function (error) {
            console.error('오류 발생:', error);
            alert('게시판 데이터를 불러오는데 오류가 발생했습니다.');
        }
    });
}