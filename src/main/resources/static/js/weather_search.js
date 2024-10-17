let regions = [];

// 페이지 로드 시 CSV 데이터를 서버에서 받아옴
window.onload = function () {
    fetch('/user/weather/regions')
        .then(response => response.json())
        .then(data => {
            regions = data;
            populateRegions();
        })
        .catch(error => console.error('Error fetching regions:', error));
};

// 대범위 지역을 <select> 태그에 추가하고 기본값 설정
function populateRegions() {
    const regionSelect = document.getElementById('region');
    const uniqueRegions = [...new Set(regions.map(item => item.regionParent))]; // 대범위 지역 중복 제거

    uniqueRegions.forEach(region => {
        const option = document.createElement('option');
        option.value = region;
        option.textContent = region;

        // "서울특별시"를 기본값으로 설정
        if (region === "서울특별시") {
            option.selected = true;
        }
        regionSelect.appendChild(option);
    });

    // "서울특별시" 선택 시 소범위 지역도 기본값으로 설정
    populateSubRegions();
}

// 대범위 지역 선택 시 소범위 지역을 <select> 태그에 추가하고 기본값 설정
function populateSubRegions() {
    const selectedRegion = document.getElementById('region').value;
    const region2Select = document.getElementById('region2');
    region2Select.innerHTML = ''; // 기존 옵션 제거

    const subRegions = regions.filter(item => item.regionParent === selectedRegion);
    subRegions.forEach(region => {
        const option = document.createElement('option');
        option.value = region.regionChild;
        option.textContent = region.regionChild;

        // "종로구"를 기본값으로 설정
        if (region.regionChild === "종로구") {
            option.selected = true;
        }
        region2Select.appendChild(option);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('searchButton').addEventListener('click', submitSearchForm);
});

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
        T1H: { name: '1시간 기온', unit: '°C', bit: 10 }, // 1시간 기온
        TMP: { name: '1시간 기온', unit: '°C', bit: 10 }, // 1시간 기온
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
        return `${categoryInfo.name}: ${value}${categoryInfo.unit || ''}`;
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

function submitSearchForm() {
    const selectedRegion = document.getElementById('region').value;
    const selectedSubRegion = document.getElementById('region2').value;

    const selectedRegionData = regions.find(item => item.regionParent === selectedRegion && item.regionChild === selectedSubRegion);

    if (selectedRegionData) {
        const nx = selectedRegionData.nx;
        const ny = selectedRegionData.ny;

        // 날씨 API 요청
        fetch(`/user/weather/search?nx=${nx}&ny=${ny}`)
            .then(response => response.json())
            .then(data => {
                // weather-text 요소를 비움
                const weatherTextDiv = document.getElementById('weather-text');
                weatherTextDiv.innerHTML = ''; // 기존 내용 제거

                // 날씨 데이터가 존재하는지 확인
                if (data && data.response && data.response.body && data.response.body.items && data.response.body.items.item) {
                    const weatherItems = data.response.body.items.item;

                    // 각 날씨 항목을 사용자 친화적인 텍스트로 변환하여 출력
                    weatherItems.forEach(item => {
                        const friendlyText = convertCategoryName(item.category, item.obsrValue);
                        const p = document.createElement('p');
                        p.textContent = friendlyText;
                        weatherTextDiv.appendChild(p);
                    });
                } else {
                    weatherTextDiv.textContent = '날씨 정보를 불러오지 못했습니다.';
                }
            })
            .catch(error => {
                console.error('Error fetching weather:', error);
                document.getElementById('weather-text').textContent = '날씨 정보를 불러오는 중 오류가 발생했습니다.';
            });
    }
}