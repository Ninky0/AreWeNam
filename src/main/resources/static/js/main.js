document.addEventListener('DOMContentLoaded', () => {
    const slider = document.getElementById('productSlider');
    const slides = Array.from(slider.children);
    const totalItems = slides.length;
    const itemWidth = 250; // 각 슬라이드의 너비
    const gap = 10; // 슬라이드 사이의 간격
    const slideDistance = itemWidth + gap; // 각 슬라이드가 이동해야 하는 거리
    let currentIndex = 0; // 첫 번째 슬라이드에서 시작
    let autoSlideInterval;

    // 화살표 버튼 요소 가져오기
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');

    // 무한 루프 효과를 위해 모든 슬라이드를 한 번만 복제하여 끝에 추가
    slides.forEach(slide => {
        const clone = slide.cloneNode(true);
        slider.appendChild(clone);
    });

    // 슬라이더의 총 슬라이드 수를 반영하여 너비 설정
    const totalSlides = slider.children.length;
    slider.style.width = `${totalSlides * slideDistance}px`;

    // 슬라이드 위치를 업데이트하는 함수
    function updateSlidePosition() {
        slider.style.transition = 'transform 0.5s ease-in-out';
        slider.style.transform = `translateX(${-slideDistance * currentIndex}px)`;
    }

    // 다음 슬라이드로 이동하는 함수
    function nextSlide() {
        currentIndex++;
        updateSlidePosition();

        // 모든 슬라이드를 보여준 후 처음으로 부드럽게 돌아가기
        if (currentIndex >= totalItems) {
            setTimeout(() => {
                slider.style.transition = 'none';
                currentIndex = 0;
                slider.style.transform = `translateX(0)`;
                setTimeout(() => {
                    slider.style.transition = 'transform 0.5s ease-in-out';
                }, 50);
            }, 500); // 트랜지션이 끝난 후 실행
        }
    }

    // 이전 슬라이드로 이동하는 함수
    function prevSlide() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = totalItems - 1;
            slider.style.transition = 'none';
            slider.style.transform = `translateX(${-slideDistance * (totalItems * 2 - 1)}px)`;
            setTimeout(() => {
                slider.style.transition = 'transform 0.5s ease-in-out';
                updateSlidePosition();
            }, 50);
        } else {
            updateSlidePosition();
        }
    }

    // 자동 슬라이드 시작
    function startAutoSlide() {
        autoSlideInterval = setInterval(nextSlide, 3000); // 3초마다 자동 슬라이드
    }

    // 자동 슬라이드 정지
    function stopAutoSlide() {
        clearInterval(autoSlideInterval);
    }

    // 화살표 버튼에 이벤트 리스너 추가
    nextButton.addEventListener('click', () => {
        stopAutoSlide();
        nextSlide();
        startAutoSlide();
    });

    prevButton.addEventListener('click', () => {
        stopAutoSlide();
        prevSlide();
        startAutoSlide();
    });

    // 슬라이더에 마우스 오버 시 자동 슬라이드 정지
    slider.addEventListener('mouseenter', stopAutoSlide);

    // 슬라이더에서 마우스 아웃 시 자동 슬라이드 재시작
    slider.addEventListener('mouseleave', startAutoSlide);

    // 페이지 로드 시 자동 슬라이드 시작
    startAutoSlide();
});

let slideIndex = 0;
showSlides();

function showSlides() {
    let slides = document.getElementsByClassName("banner-slide");
    for (let i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    slideIndex++;
    if (slideIndex > slides.length) {slideIndex = 1}
    slides[slideIndex - 1].style.display = "block";
    setTimeout(showSlides, 5000); // 5초마다 슬라이드 변경
}

function plusSlides(n) {
    slideIndex += n;
    let slides = document.getElementsByClassName("banner-slide");
    if (slideIndex > slides.length) {slideIndex = 1}
    if (slideIndex < 1) {slideIndex = slides.length}
    for (let i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    slides[slideIndex - 1].style.display = "block";
}