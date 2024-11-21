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


function updateReview() {
    const form = document.getElementById("ootdForm");
    const formData = new FormData(form);

    fetch('/review/edit/' + form.productId.value + '/' + form.purchaseId.value, {
        method: 'PUT',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("서버에서 문제가 발생했습니다: " + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (data.url) {
                alert(data.message || "리뷰가 성공적으로 수정되었습니다.");
                window.location.href = data.url; // 서버에서 전달된 URL로 페이지 이동
            } else {
                alert("서버 응답에 URL이 포함되어 있지 않습니다.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("리뷰 수정 과정에서 오류가 발생했습니다: " + error.message);
        });
}

document.querySelector(".update-button").addEventListener('click', updateReview);



function deleteReview() {
    const form = document.getElementById("ootdForm");

    if (!confirm("이 리뷰를 삭제하시겠습니까?")) return;

    fetch('/review/delete/' + form.productId.value + '/' + form.purchaseId.value, {
        method: 'DELETE'
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("리뷰가 성공적으로 삭제되었습니다.");
                window.location.href = data.url; // 삭제 후 리다이렉션할 URL
            } else {
                alert(data.message || "리뷰 삭제 실패");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("리뷰 삭제 과정에서 오류가 발생했습니다: " + error.message);
        });
}

document.querySelector(".delete-button").addEventListener('click', deleteReview);

