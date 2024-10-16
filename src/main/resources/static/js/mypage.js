// 사이드바 버튼 클릭 시 글자 색상 변경
document.querySelectorAll('.sidebar-button').forEach(button => {
    button.addEventListener('click', () => {
        button.style.color = '#FF0000'; // 클릭 시 색상 변경
    });
});
