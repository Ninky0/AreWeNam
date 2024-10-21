document.addEventListener('DOMContentLoaded', function() {
    // Quill 에디터 설정
    let quill = new Quill('#editor', {
        theme: 'snow',
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'],
                ['image']
            ]
        }
    });

    // 에디터 초기화 시 기존 설명 로드
    quill.root.innerHTML = document.getElementById('description').textContent;

    // 폼 제출 시 Quill 내용 동기화 및 제출 처리
    function submitForm() {
        const description = quill.root.innerHTML;
        document.getElementById('description').value = description;

        const formData = new FormData(document.getElementById('productForm'));

        // FormData 출력하여 값 확인 (디버깅용)
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }

        fetch(document.getElementById('productForm').action, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('상품이 성공적으로 수정되었습니다!');
                    window.location.href = '/admin/product/list'; // 상품 리스트로 이동
                } else {
                    alert('상품 수정 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('상품 수정 중 오류가 발생했습니다.');
            });
    }

    // 이미지 미리보기 처리
    window.previewMainImage = function(event) {
        const input = event.target;
        const imagePreview = document.getElementById('imagePreview');
        const placeholderText = document.getElementById('placeholderText');

        // Clear the existing preview image
        imagePreview.src = ''; // Reset the src attribute to an empty string
        imagePreview.style.display = 'none'; // Hide the image element
        placeholderText.style.display = 'block'; // Show the placeholder text

        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result; // Set the new image src from FileReader
                imagePreview.style.display = 'block'; // Show the image element
                placeholderText.style.display = 'none'; // Hide the placeholder text
            };

            // Read the selected file and update the preview
            reader.readAsDataURL(input.files[0]);
        }
    }

    // 폼 제출 함수 연결
    window.submitForm = submitForm;
});