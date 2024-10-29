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

    // 최대 파일 크기 설정 (2MB)
    const MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    // 에디터 초기화 시 기존 설명 로드
    quill.root.innerHTML = document.getElementById('description').textContent;

    // 폼 제출 시 Quill 내용 동기화 및 제출 처리
    function submitForm() {
        const description = quill.root.innerHTML;
        document.getElementById('description').value = description;

        const formData = new FormData(document.getElementById('productForm'));

        fetch(document.getElementById('productForm').action, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('네트워크 응답에 문제가 있습니다.');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert('상품이 성공적으로 수정되었습니다!');
                    window.location.href = '/admin/product/list';
                } else {
                    alert('상품 수정 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('상품 수정 중 오류가 발생했습니다.');
            });
    }

    // 이미지 압축 및 미리보기 처리 함수
    window.previewMainImage = function(event) {
        const input = event.target;
        const imagePreview = document.getElementById('imagePreview');
        const placeholderText = document.getElementById('placeholderText');

        if (input.files && input.files[0]) {
            const file = input.files[0];

            // 파일 크기 확인
            if (file.size > MAX_FILE_SIZE) {
                alert('이미지 파일 크기가 너무 큽니다. 2MB 이하의 파일을 선택해 주세요.');
                return;
            }

            // FileReader로 이미지 로드
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = new Image();
                img.src = e.target.result;

                img.onload = function() {
                    const canvas = document.createElement('canvas');
                    const maxSize = 800; // 최대 가로/세로 크기 설정
                    let width = img.width;
                    let height = img.height;

                    // 이미지 크기 조정
                    if (width > height) {
                        if (width > maxSize) {
                            height *= maxSize / width;
                            width = maxSize;
                        }
                    } else {
                        if (height > maxSize) {
                            width *= maxSize / height;
                            height = maxSize;
                        }
                    }
                    canvas.width = width;
                    canvas.height = height;

                    // Canvas에 이미지 그리기
                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, width, height);

                    // 압축된 이미지 데이터 URL 생성 (JPEG 형식, 80% 품질)
                    canvas.toBlob((blob) => {
                        const compressedFile = new File([blob], file.name, { type: "image/jpeg", lastModified: Date.now() });

                        // 압축된 이미지 미리보기
                        imagePreview.src = URL.createObjectURL(blob);
                        imagePreview.style.display = 'block';
                        placeholderText.style.display = 'none';

                        // 압축된 파일을 formData에 설정
                        const formData = new FormData(document.getElementById('productForm'));
                        formData.set('mainPicture', compressedFile);
                    }, "image/jpeg", 0.8);
                };
            };
            reader.readAsDataURL(file);
        } else {
            // 파일이 선택되지 않은 경우 미리보기를 초기화
            imagePreview.src = '';
            imagePreview.style.display = 'none';
            placeholderText.style.display = 'block';
        }
    };

    // 전역에서 submitForm 함수 접근 가능하도록 설정
    window.submitForm = submitForm;
});
