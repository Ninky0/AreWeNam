document.addEventListener('DOMContentLoaded', () => {
    const slider = document.getElementById('productSlider');
    const slides = Array.from(slider.children);
    const totalItems = slides.length;
    const itemWidth = 250; // Width of each slide
    const gap = 10; // Gap between slides
    const slideDistance = itemWidth + gap; // Distance each slide should move
    let currentIndex = totalItems; // Start at the first set of clones
    let autoSlideInterval;

    // Clone all slides at the start and end for seamless looping
    slides.forEach(slide => {
        const cloneStart = slide.cloneNode(true);
        const cloneEnd = slide.cloneNode(true);
        slider.appendChild(cloneEnd);
        slider.insertBefore(cloneStart, slider.firstChild);
    });

    // Update total slides count to reflect the new set of slides
    const updatedTotalItems = slider.children.length;

    // Set slider width to accommodate all slides and avoid wrapping
    slider.style.width = `${updatedTotalItems * slideDistance}px`;

    // Set initial slide position to start at the first set of real slides
    slider.style.transform = `translateX(${-slideDistance * currentIndex}px)`;

    function updateSlidePosition() {
        slider.style.transition = 'transform 0.5s ease-in-out';
        slider.style.transform = `translateX(${-slideDistance * currentIndex}px)`;
    }

    function nextSlide() {
        currentIndex++;
        updateSlidePosition();

        // When reaching the end, reset to the first set of slides without visible jump
        slider.addEventListener('transitionend', handleLooping, { once: true });
    }

    function prevSlide() {
        currentIndex--;
        updateSlidePosition();

        // When reaching the beginning, reset to the last set of slides without visible jump
        slider.addEventListener('transitionend', handleLooping, { once: true });
    }

    function handleLooping() {
        if (currentIndex >= updatedTotalItems - totalItems) {
            // Reached the end, reset to the first real slide
            slider.style.transition = 'none';
            currentIndex = totalItems; // Jump to the start of the real slides
            slider.style.transform = `translateX(${-slideDistance * currentIndex}px)`;
            requestAnimationFrame(() => {
                // Re-enable transition after one frame
                slider.style.transition = 'transform 0.5s ease-in-out';
            });
        } else if (currentIndex < totalItems) {
            // Reached the beginning, reset to the last real slide
            slider.style.transition = 'none';
            currentIndex = updatedTotalItems - totalItems * 2; // Jump to the end of the real slides
            slider.style.transform = `translateX(${-slideDistance * currentIndex}px)`;
            requestAnimationFrame(() => {
                // Re-enable transition after one frame
                slider.style.transition = 'transform 0.5s ease-in-out';
            });
        }
    }

    // Start auto-slide
    function startAutoSlide() {
        autoSlideInterval = setInterval(nextSlide, 3000); // Auto-slide every 3 seconds
    }

    // Stop auto-slide
    function stopAutoSlide() {
        clearInterval(autoSlideInterval);
    }

    // Add arrow button event listeners
    document.querySelector('.left-arrow').addEventListener('click', () => {
        stopAutoSlide(); // Stop auto-slide while manually navigating
        prevSlide();
        startAutoSlide(); // Resume auto-slide after manual navigation
    });

    document.querySelector('.right-arrow').addEventListener('click', () => {
        stopAutoSlide(); // Stop auto-slide while manually navigating
        nextSlide();
        startAutoSlide(); // Resume auto-slide after manual navigation
    });

    // Start auto-slide on page load
    startAutoSlide();
});