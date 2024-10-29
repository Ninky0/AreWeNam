document.addEventListener('DOMContentLoaded', () => {
    const slider = document.getElementById('productSlider');
    const slides = Array.from(slider.children);
    const totalItems = slides.length;
    const itemWidth = 250;
    const gap = 10;
    const slideDistance = itemWidth + gap;
    let currentIndex = 0;
    let autoSlideInterval;

    const prevButton = document.getElementById('bn2PrevButton');
    const nextButton = document.getElementById('bn2NextButton');

    slides.forEach(slide => {
        const clone = slide.cloneNode(true);
        slider.appendChild(clone);
    });

    const totalSlides = slider.children.length;
    slider.style.width = `${totalSlides * slideDistance}px`;

    function updateSlidePosition() {
        slider.style.transition = 'transform 0.5s ease-in-out';
        slider.style.transform = `translateX(${-slideDistance * currentIndex}px)`;
    }

    function nextSlide() {
        currentIndex++;
        updateSlidePosition();
        if (currentIndex >= totalItems) {
            setTimeout(() => {
                slider.style.transition = 'none';
                currentIndex = 0;
                slider.style.transform = `translateX(0)`;
                setTimeout(() => {
                    slider.style.transition = 'transform 0.5s ease-in-out';
                }, 50);
            }, 500);
        }
    }

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

    function startAutoSlide() {
        autoSlideInterval = setInterval(nextSlide, 3000);
    }

    function stopAutoSlide() {
        clearInterval(autoSlideInterval);
    }

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

    slider.addEventListener('mouseenter', stopAutoSlide);
    slider.addEventListener('mouseleave', startAutoSlide);

    startAutoSlide();
});
