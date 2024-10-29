document.addEventListener('DOMContentLoaded', () => {
    const slider = document.getElementById('bn1Slider');
    const slides = Array.from(slider.children);
    const slideCount = slides.length;
    const nextButton = document.getElementById('bn1NextButton');
    const prevButton = document.getElementById('bn1PrevButton');
    let currentIndex = 0;
    let autoSlideInterval;

    // 첫 슬라이드와 마지막 슬라이드 복제하여 양 끝에 추가
    const firstSlideClone = slides[0].cloneNode(true);
    const lastSlideClone = slides[slides.length - 1].cloneNode(true);
    slider.appendChild(firstSlideClone);
    slider.insertBefore(lastSlideClone, slides[0]);

    // 슬라이더 초기 위치 설정
    slider.style.transform = `translateX(-100%)`;

    function updateSlidePosition() {
        slider.style.transition = 'transform 0.5s ease-in-out';
        slider.style.transform = `translateX(-${(currentIndex + 1) * 100}%)`;
    }

    function nextSlide() {
        currentIndex++;
        updateSlidePosition();

        // 마지막 슬라이드에서 첫 슬라이드로 이동하는 효과
        if (currentIndex >= slideCount) {
            setTimeout(() => {
                slider.style.transition = 'none';
                currentIndex = 0;
                slider.style.transform = `translateX(-100%)`;
            }, 500);
        }
    }

    function prevSlide() {
        currentIndex--;
        updateSlidePosition();

        // 첫 슬라이드에서 마지막 슬라이드로 이동하는 효과
        if (currentIndex < 0) {
            setTimeout(() => {
                slider.style.transition = 'none';
                currentIndex = slideCount - 1;
                slider.style.transform = `translateX(-${slideCount * 100}%)`;
            }, 500);
        }
    }

    // 화살표 버튼 클릭 이벤트
    nextButton.addEventListener('click', () => {
        clearInterval(autoSlideInterval);
        nextSlide();
        startAutoSlide();
    });

    prevButton.addEventListener('click', () => {
        clearInterval(autoSlideInterval);
        prevSlide();
        startAutoSlide();
    });

    // 자동 슬라이드 설정
    function startAutoSlide() {
        autoSlideInterval = setInterval(nextSlide, 3000);
    }

    // 자동 슬라이드 시작
    startAutoSlide();
});
