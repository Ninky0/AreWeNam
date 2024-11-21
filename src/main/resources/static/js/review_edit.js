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

function submitForm() {
    const form = document.getElementById("ootdForm");
    const formData = new FormData(form);
    const productId = document.getElementById('productId').value;
    const purchaseId = document.getElementById('purchaseId').value;

    fetch(`/review/${productId}/${purchaseId}`, {
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
