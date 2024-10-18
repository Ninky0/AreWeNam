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

    const handleImageUpload = (file) => {
        const reader = new FileReader();
        reader.onload = () => insertImage(reader.result);
        if (file) {
            reader.readAsDataURL(file);
            console.log('File selected:', file.name);
        }
    };

    quill.getModule('toolbar').addHandler('image', function() {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = () => handleImageUpload(input.files[0]);
    });

    const productForm = document.getElementById('productForm');
    if (productForm) {
        productForm.onsubmit = function(event) {
            event.preventDefault();
            const formData = new FormData(this);
            const description = document.querySelector('#editor .ql-editor').innerHTML;

            // Create a temporary div to modify the HTML
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = description;
            const images = tempDiv.querySelectorAll('img');
            images.forEach((img, index) => {
                const localImagePath = `/uploads/image_${index}.png`;
                img.src = localImagePath;
            });

            formData.append('description', tempDiv.innerHTML);
            console.log('Form data prepared:');
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }

            fetch('/admin/product/upload', { //여기가 문제로군
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Server response:', data);
                    console.log('Redirecting to:', data.url); // Log the redirect URL
                    // Show alert and redirect to product list
                    alert('상품이 성공적으로 등록되었습니다!');
                    window.location.href = data.url; // Use the URL from the response
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
