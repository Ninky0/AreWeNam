let quill;

document.addEventListener('DOMContentLoaded', function() {
    quill = new Quill('#editor', {
        theme: 'snow',
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'],
                ['image']
            ]
        }
    });

    const insertImage = (imageSrc) => {
        const range = quill.getSelection();
        if (range) {
            quill.insertEmbed(range.index, 'image', imageSrc);
            console.log('Image inserted:', imageSrc);
        }
    };

    // quill에 이미지 등록하는거
    const handleImageUpload = (file) => {
        let formData = new FormData();
        formData.append('file', file);

        $.ajax({
            type: 'POST',
            url: '/admin/product/uploadImage',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                console.log('Server response:', response);
                if (response.imageUrl) {
                    insertImage(response.imageUrl); // 이미지 URL을 에디터에 삽입
                    console.log('Image uploaded and inserted:', response.imageUrl);
                } else {
                    alert('이미지 URL을 받지 못했습니다.');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('상품 등록 중 오류가 발생했습니다.');
            }
        });
    };

    quill.getModule('toolbar').addHandler('image', function() {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();
        input.onchange = () => {
            const file = input.files[0];
            handleImageUpload(file);
        };
    });


    const productForm = document.getElementById('productForm');
    if (productForm) {
        productForm.onsubmit = function(event) {
            event.preventDefault();
            const formData = new FormData(this);
            const description = document.querySelector('#editor .ql-editor').innerHTML;
            formData.append('description', description); // 이미지 URL이 포함된 에디터 내용 추가

            console.log('Form data prepared:');
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }

            fetch('/admin/product', {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Server response:', data);
                    if (data.url) {
                        alert('상품이 성공적으로 등록되었습니다!');
                        window.location.href = data.url;
                    } else {
                        alert('응답 URL이 없습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('상품 등록 중 오류가 발생했습니다.');
                });
        };
    }


    const mainPictureInput = document.getElementById('mainPicture');
    if (mainPictureInput) {
        mainPictureInput.addEventListener('change', function(event) {
            const input = event.target;
            const imagePreview = document.getElementById('imagePreview');
            const placeholderText = document.getElementById('placeholderText');

            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    imagePreview.src = e.target.result;
                    imagePreview.style.display = 'block';
                    placeholderText.style.display = 'none';
                    console.log('Main image previewed:', e.target.result);
                };
                reader.readAsDataURL(input.files[0]);
            } else {
                imagePreview.src = '';
                imagePreview.style.display = 'none';
                placeholderText.style.display = 'block';
            }
        });
    }
});

function submitForm() {
    const productForm = document.getElementById('productForm');
    productForm.dispatchEvent(new Event('submit'));
}
