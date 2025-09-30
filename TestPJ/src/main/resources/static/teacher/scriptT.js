// UI 요소 가져오기
const courseListContainer = document.getElementById('course-list-container');
const categorySelect = document.getElementById('myCourse-Category');
const searchInput = document.getElementById('myCourse-search');
const prevBtn = document.getElementById('prev-page-btn');
const nextBtn = document.getElementById('next-page-btn');
const pageInfoSpan = document.getElementById('page-info');

// 상태 관리 변수
let currentPage = 0;
const pageSize = 5; // 한 페이지에 표시할 강의 수
let totalPages = 0;

// API 호출 및 강의 목록 렌더링 함수
async function fetchAndRenderCourses(page = 0, search = '', category = '전체') {
    try {
        const url = `/api/teacher/courses?page=${page}&size=${pageSize}&search=${encodeURIComponent(search)}&category=${encodeURIComponent(category)}`;
        const response = await fetch(url);
        if (!response.ok) throw new Error('강의 목록을 불러오는 데 실패했습니다.');
        const data = await response.json();

        totalPages = data.totalPages;
        // 데이터가 없는 페이지는 1/1로 표시되도록 조정
        pageInfoSpan.textContent = `${data.currentPage + 1} / ${data.totalPages}`;

        renderCourses(data.content);
        updatePaginationButtons();

    } catch (error) {
        console.error('오류:', error);
        // 사용자에게 노출되는 alert은 사용하지 않는 것이 좋습니다.
        // alert('강의 목록을 불러오는 데 실패했습니다.');
    }
}

// 강의 목록을 HTML로 변환하여 렌더링
function renderCourses(courses) {
    // 1. 유효하지 않은 데이터(null, undefined 등)를 필터링하여 빈 카드가 생성되는 것을 방지합니다.
    const validCourses = courses.filter(course => course && course.id);

    if (validCourses.length === 0) {
        courseListContainer.innerHTML = '<p class="no-courses">강의가 없습니다.</p>';
        return;
    }

    // 2. 필터링된 유효한 데이터만 매핑하여 HTML 카드를 생성합니다.
    courseListContainer.innerHTML = validCourses.map(course => `
        <div class="myCourse-card">
            <div class="myCourse-thumb">
                <img src="${course.thumbnail_url}" alt="${course.courses_name} 썸네일">
            </div>
            <div class="myCourse-info">
                <div class="courseTitle">
                    <h3><span>${course.courses_name}</span></h3>
                    <div class="myCourse-badge"><span>${course.courses_category}</span></div>
                </div>
                <p class="courseExplain"><span>${course.description}</span></p>
            </div>
            <div class="myCourse-actions">
                <button class="courseEdit-btn" data-id="${course.id}">강의 수정</button>
                <button class="quizEdit-btn" data-id="${course.id}">퀴즈 수정</button>
                <button class="courseDelete-btn" data-id="${course.id}">강의 삭제</button>
            </div>
        </div>
    `).join('');
}

// 페이지네이션 버튼 상태 업데이트
function updatePaginationButtons() {
    prevBtn.disabled = currentPage === 0;
    nextBtn.disabled = currentPage >= totalPages - 1;
}

// 이벤트 리스너: 검색, 필터링, 페이지네이션
searchInput.addEventListener('input', () => {
    currentPage = 0;
    fetchAndRenderCourses(currentPage, searchInput.value, categorySelect.value);
});

categorySelect.addEventListener('change', () => {
    currentPage = 0;
    fetchAndRenderCourses(currentPage, searchInput.value, categorySelect.value);
});

prevBtn.addEventListener('click', () => {
    if (currentPage > 0) {
        currentPage--;
        fetchAndRenderCourses(currentPage, searchInput.value, categorySelect.value);
    }
});

nextBtn.addEventListener('click', () => {
    if (currentPage < totalPages - 1) {
        currentPage++;
        fetchAndRenderCourses(currentPage, searchInput.value, categorySelect.value);
    }
});

// 이벤트 리스너: 강의 수정/삭제 버튼
courseListContainer.addEventListener('click', async (e) => {
    const target = e.target;
    const courseId = target.getAttribute('data-id');

    if (!courseId) return;

    // 강의 수정 버튼 클릭 시
    if (target.classList.contains('courseEdit-btn')) {
        // CourseController의 @GetMapping("/edit/{id}")로 이동
        window.location.href = `/course/edit/${courseId}`;
    }

    //  퀴즈 수정 버튼 클릭 시
    if (target.classList.contains('quizEdit-btn')) {
        // QuizController의 @GetMapping("/edit/{coursesId}")로 이동
        window.location.href = `/quiz/edit/${courseId}`;
    }

    if (target.classList.contains('courseDelete-btn')) {
        // alert() 대신 커스텀 모달 UI를 사용해야 합니다.
        // 이 환경에서는 confirm()을 사용하지 않는 것이 좋습니다.

        // **경고: confirm() 대신 커스텀 모달 UI를 사용해야 합니다.**
        if (window.confirm('정말로 이 강의를 삭제하시겠습니까?')) {
            try {
                // **삭제 요청 URL 확인**
                const response = await fetch(`/api/teacher/course/delete/${courseId}`, {
                    method: 'DELETE',
                });

                // **응답 상태 코드 확인**
                if (!response.ok) {
                    throw new Error('강의 삭제 실패');
                }

                // alert() 대신 커스텀 메시지 표시
                // alert('강의가 성공적으로 삭제되었습니다.');
                console.log('강의가 성공적으로 삭제되었습니다.');
                fetchAndRenderCourses(currentPage, searchInput.value, categorySelect.value);
            } catch (error) {
                console.error('오류:', error);
                // alert('강의 삭제에 실패했습니다.');
                console.log('강의 삭제에 실패했습니다.');
            }
        }
    }
});

// 초기 로딩 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderCourses();
});
