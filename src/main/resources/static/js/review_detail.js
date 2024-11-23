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

document.addEventListener('DOMContentLoaded', function() {
    const deleteButton = document.getElementById('deleteButton');
    deleteButton.addEventListener('click', function() {
        const confirmation = confirm('정말로 이 리뷰를 삭제하시겠습니까?');

        if (confirmation) {
            const form = document.getElementById('ootdForm');
            const productId = form.productId.value;
            const purchaseId = form.purchaseId.value;
            const url = `/review/${productId}/${purchaseId}`;

            fetch(url, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        alert('리뷰가 성공적으로 삭제되었습니다.');
                        window.location.href = response.url;
                    } else {
                        throw new Error('리뷰를 삭제하는 데 실패했습니다.');
                    }
                })
                .catch(error => {
                    alert(error.message);
                });
        }
    });
});