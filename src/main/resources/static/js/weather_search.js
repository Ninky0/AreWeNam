document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('searchButton').addEventListener('click', submitSearchForm);
    fetchRegionsAndWeather();
});

let regions = [];
let temperature = 0.0;

// 초기 데이터 로드
function fetchRegionsAndWeather() {
    fetch('/home/weather/regions')
        .then(response => response.json())
        .then(data => {
            regions = data;
            populateRegions();
            return submitSearchForm(); // 날씨 정보를 먼저 가져옴
        })
        .then(() => {
            fetchRecommendedProducts(); // 날씨 정보 설정 후 추천 상품 로드
        })
        .catch(error => console.error('Error:', error));
}

function fetchRecommendedProducts() {
    fetch('/home/recommend/temp?temperature=' + getCurrentTemperature())
        .then(response => response.json())
        .then(products => {
            const slider = document.getElementById('productSlider');
            slider.innerHTML = '';

            products.forEach(product => {
                const seasonName = getSeasonName(product.season);
                const item = `
                    <div class="bn2-product-item">
                        <a href="/user/product/detail/${product.id}">
                            <img src="${product.mainPicturePath}" alt="${product.name}">
                        </a>
                        <p class="bn2-title">${product.name}</p>
                        <p class="bn2-price">${product.price} 원</p>
                        <div class="bn2-product-info">
                            <span>${product.category || '카테고리 없음'}</span>ㆍ
                            <span>${seasonName}</span>ㆍ
                            <span>${product.temperature || '온도 정보 없음'}°C</span>
                        </div>
                    </div>
                `;
                slider.innerHTML += item;
            });

            // 슬라이더 끝에 모든 슬라이드 복제하여 무한 스크롤 구현
            const originalSlides = Array.from(slider.children);
            originalSlides.forEach(slide => {
                const clone = slide.cloneNode(true);
                slider.appendChild(clone);
            });

            startAutoSlide(originalSlides.length);
        })
        .catch(error => console.error('Error fetching recommended products:', error));
}

// 계절 숫자를 이름으로 변환하는 함수
function getSeasonName(season) {
    switch (season) {
        case 1: return '봄';
        case 2: return '여름';
        case 3: return '가을';
        case 4: return '겨울';
        default: return '계절 정보 없음';
    }
}
function startAutoSlide(totalItems) {
    const slider = document.getElementById('productSlider');
    const itemWidth = 250; // 아이템 너비
    const gap = 11; // 간격
    const slideDistance = itemWidth + gap; // 이동 거리
    let currentIndex = 0;
    let autoSlideInterval;

    // 슬라이더의 전체 너비 설정
    slider.style.width = `${(slider.children.length * slideDistance)}px`;

    autoSlideInterval = setInterval(() => {
        currentIndex++;
        slider.style.transition = 'transform 0.5s ease-in-out';
        slider.style.transform = `translateX(-${slideDistance * currentIndex}px)`;

        // 마지막 슬라이드가 보이는 시점에서 원래의 첫 번째 슬라이드로 이동
        slider.addEventListener('transitionend', () => {
            if (currentIndex >= totalItems) { // 원래 슬라이드 길이를 넘어가면
                currentIndex = 0;
                slider.style.transition = 'none';
                slider.style.transform = `translateX(0)`;
                setTimeout(() => {
                    slider.style.transition = 'transform 0.5s ease-in-out';
                }, 50);
            }
        });
    }, 3000); // 3초마다 슬라이드 이동
}

function getCurrentTemperature() {
    return temperature;
}

function setTemperature(temp) {
    temperature = temp;
}

function populateRegions() {
    const regionSelect = document.getElementById('region');
    const uniqueRegions = [...new Set(regions.map(item => item.regionParent))];

    uniqueRegions.forEach(region => {
        const option = document.createElement('option');
        option.value = region;
        option.textContent = region;

        if (region === "서울특별시") {
            option.selected = true;
        }
        regionSelect.appendChild(option);
    });

    populateSubRegions();
}

function populateSubRegions() {
    const selectedRegion = document.getElementById('region').value;
    const region2Select = document.getElementById('region2');
    region2Select.innerHTML = '';

    const subRegions = regions.filter(item => item.regionParent === selectedRegion);
    subRegions.forEach(region => {
        const option = document.createElement('option');
        option.value = region.regionChild;
        option.textContent = region.regionChild;

        if (region.regionChild === "종로구") {
            option.selected = true;
        }
        region2Select.appendChild(option);
    });
}

function submitSearchForm() {
    const selectedRegion = document.getElementById('region').value;
    const selectedSubRegion = document.getElementById('region2').value;

    return fetch(`/home/weather/search?parent=${selectedRegion}&child=${selectedSubRegion}`)
        .then(response => response.json())
        .then(data => {
            updateWeatherUI(data);
            setTemperature(data.temperature);
        })
        .catch(error => {
            console.error('Error fetching weather:', error);
            document.getElementById('weather-text').textContent = '날씨 정보를 불러오는 중 오류가 발생했습니다.';
        });
}

function updateWeatherUI(data) {
    const weatherTextDiv = document.getElementById('weather-text');
    const weatherIconImg = document.querySelector('.weather-icon-container img');
    weatherTextDiv.innerHTML = '';

    if (data && data.description && data.temperature) {
        const temperatureText = `${data.temperature}°C`;
        const weatherDescription = data.description;

        const temperatureP = document.createElement('p');
        temperatureP.textContent = temperatureText;
        weatherTextDiv.appendChild(temperatureP);

        switch (weatherDescription) {
            case '맑음':
                weatherIconImg.src = '/images/sunny.png';
                break;
            case '구름 많음':
                weatherIconImg.src = '/images/cloudy.png';
                break;
            case '비':
                weatherIconImg.src = '/images/rainy.png';
                break;
            case '눈':
                weatherIconImg.src = '/images/snowy.png';
                break;
            case '흐림':
                weatherIconImg.src = '/images/overcast.png';
                break;
            default:
                weatherIconImg.src = '/images/cloudy.png';
        }

        weatherIconImg.alt = "Weather Icon: " + weatherDescription;
    } else {
        weatherTextDiv.textContent = '날씨 정보를 불러오지 못했습니다.';
    }
}



// ~~~~~~~~~~~~~~~~~~~~~~~아래 코드는 안쓰는데 값 보려고 넣어둔거에요~~~~~~~~~~~~~~~~~~~~~~~

// 각 category 값을 사용자 친화적인 텍스트로 변환하는 함수
function convertCategoryName(category, value) {
    // Missing 값 처리
    if (value > 900 || value < -900) {
        return `${category}: 자료 없음 (Missing)`;
    }

    const categoryMap = {
        POP: { name: '강수확률', unit: '%', bit: 8 },    // 강수확률
        PTY: { name: '강수형태', values: { '0': '없음', '1': '비', '2': '비/눈', '3': '눈', '4': '소나기' }, bit: 4 },  // 강수형태
        PCP: { name: '1시간 강수량', customFormatter: formatPrecipitation, bit: 8 }, // 강수량
        RN1: { name: '1시간 강수량', customFormatter: formatPrecipitation, bit: 8 }, // 강수량
        REH: { name: '습도', unit: '%', bit: 8 },        // 습도
        SNO: { name: '1시간 신적설', customFormatter: formatSnow, bit: 8 }, // 신적설
        SKY: { name: '하늘상태', values: { '1': '맑음', '3': '구름많음', '4': '흐림' }, bit: 4 },  // 하늘상태
        T1H: { name: '', unit: '°C', bit: 10 }, // 1시간 기온
        TMP: { name: '', unit: '°C', bit: 10 }, // 1시간 기온
        TMN: { name: '일 최저기온', unit: '°C', bit: 10 }, // 일 최저기온
        TMX: { name: '일 최고기온', unit: '°C', bit: 10 }, // 일 최고기온
        UUU: { name: '풍속(동서성분)', customFormatter: formatWindComponent, unit: 'm/s', bit: 12 }, // 동서 바람 성분
        VVV: { name: '풍속(남북성분)', customFormatter: formatWindComponent, unit: 'm/s', bit: 12 }, // 남북 바람 성분
        WAV: { name: '파고', unit: 'M', bit: 8 },        // 파고
        VEC: { name: '풍향', unit: 'deg', bit: 10 },     // 풍향
        WSD: { name: '풍속', unit: 'm/s', bit: 10 },     // 풍속
    };
    const categoryInfo = categoryMap[category];

    if (categoryInfo) {
        // 코드 값 변환이 필요한 경우
        if (categoryInfo.values) {
            return `${categoryInfo.name}: ${categoryInfo.values[value] || '강수없음'}`;
        }

        // 특정 포맷팅이 필요한 경우
        if (categoryInfo.customFormatter) {
            return `${categoryInfo.name}: ${categoryInfo.customFormatter(value)}`;
        }

        // 단순 값 + 단위 표시
        return `${categoryInfo.name} ${value}${categoryInfo.unit || ''}`;
    }
    return `${category}: ${value}`;
}

// 강수량(RN1, PCP) 포맷팅 함수
function formatPrecipitation(value) {
    if (value < 1.0) {
        return '1.0mm 미만';
    } else if (value >= 1.0 && value < 30.0) {
        return `${value}mm`;
    } else if (value >= 30.0 && value < 50.0) {
        return '30.0~50.0mm';
    } else if (value >= 50.0) {
        return '50.0mm 이상';
    }
    return '강수없음';
}

// 신적설(SNO) 포맷팅 함수
function formatSnow(value) {
    if (value < 1.0) {
        return '1.0cm 미만';
    } else if (value >= 1.0 && value < 5.0) {
        return `${value}cm`;
    } else if (value >= 5.0) {
        return '5.0cm 이상';
    }
    return '적설없음';
}

// 동서성분(UUU) 및 남북성분(VVV) 포맷팅 함수
function formatWindComponent(value) {
    if (value > 0) {
        return `${value} (동/북쪽으로)`;
    } else if (value < 0) {
        return `${Math.abs(value)} (서/남쪽으로)`;
    }
    return '바람 없음';
}