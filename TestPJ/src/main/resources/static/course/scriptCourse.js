// UI refsr
const grid = document.getElementById('grid');
const q = document.getElementById('q');
const category = document.getElementById('category');
const resetBtn = document.getElementById('reset');
const chips = document.getElementById('chips');

const videoModal = document.getElementById('videoModal');
const videoTitle = document.getElementById('videoTitle');
const closeVideo = document.getElementById('closeVideo');
const markCompleteBtn = document.getElementById('markComplete');

const quizModal = document.getElementById('quizModal');
const quizTitle = document.getElementById('quizTitle');
const closeQuiz = document.getElementById('closeQuiz');

let currentCourse = null;
let state = { q: '', category: '전체' };

function renderChips() {
    const items = [];
    if (state.q) items.push(`검색: "${state.q}"`);
    if (state.category !== '전체') items.push(`분야: ${state.category}`);
    chips.innerHTML = items.map(t => `<span class="chip">${t}</span>`).join('');
}

function render(data = []) {
    // 필터링/검색 로직
    const filteredData = data.filter(c => {
        const k = (c.courses_name).toLowerCase();
        const okQ = !state.q || k.includes(state.q.toLowerCase());
        const okC = state.category === '전체' || c.courses_category === state.category;
        return okQ && okC;
    });

    grid.innerHTML = filteredData.map(course=> `
        <article class="card">
            <div class="thumb">
                <img src="${course.thumbnail_url}" alt="${course.courses_name} 썸네일">
            </div>
            <div class="card-body">
                <div class="title">${course.courses_name}</div>
                <div class="meta">
                    <span class="badge">${course.courses_category}</span>
                    <span class="badge gray">진도: ${course.duration_sec  || 0}%</span>
                </div>
                <div class="actions">
                    <button class="btn primary" data-play="${course.id}">강의 재생</button>
                    <button class="btn" data-quiz="${course.id}">퀴즈 풀기</button>
                    <span class="like-wrapper">
                    <span class="badge gray">${course.like_count}</span>
                    <button class="like-btn" data-like="${course.id}">♡</button>
                    </span>
                </div>
            </div>
        </article>
    `).join('');

    renderChips();
}

// 초기 로딩 시 백엔드 API 호출
fetch('/api/courses')
    .then(response => response.json())
    .then(coursesFromApi => {
        // 백엔드에서 가져온 데이터로 화면을 렌더링
        render(coursesFromApi);

        // 검색/필터링 이벤트 리스너를 API 데이터에 연결
        q.addEventListener('input', e => { state.q = e.target.value.trim(); render(coursesFromApi); });
        category.addEventListener('change', e => { state.category = e.target.value; render(coursesFromApi); });
        resetBtn.addEventListener('click', () => {
            state = { q: '', category: '전체' };
            q.value = '';
            category.value = '전체';
            render(coursesFromApi);
        });

        // 나머지 이벤트 리스너들은 기존과 동일하게 유지
    })
    .catch(error => {
        console.error('Error fetching courses:', error);
        alert('강의 목록을 불러오는 데 실패했습니다.');
    });

// --- Video ---
function openVideo(course) {
    currentCourse = course;
    videoTitle.textContent = `${course.courses_name}`;
    videoModal.classList.add('open');
    videoModal.removeAttribute('aria-hidden'); // 모달이 열리면 aria-hidden 제거

    // 비디오 태그에 controlsList="nodownload nofullscreen" 추가
    const videoPlayer = `<video id="videoElement" width="100%" height="315" controls controlsList="nodownload nofullscreen"><source src="${course.video_url}" type="video/mp4"></video>`;
    document.getElementById('videoBox').innerHTML = videoPlayer;

    const videoElement = document.getElementById('videoElement');
    let lastTime = 0; // 이전에 재생된 시간을 저장할 변수

    // timeupdate 이벤트 리스너 정의 (메모리 누수 방지를 위해 함수를 별도로 정의)
    const timeUpdateHandler = () => {
        // 사용자가 타임라인을 앞으로 건너뛰려고 할 때 (0.5초 이상 점프 시)
        if (videoElement.currentTime > lastTime + 0.5) {
            videoElement.currentTime = lastTime; // 이전 시간으로 되돌림
        } else {
            lastTime = videoElement.currentTime; // 정상적인 재생은 lastTime 업데이트
        }
    };

    // 이벤트 리스너 추가
    videoElement.addEventListener('timeupdate', timeUpdateHandler);

    // 모달이 닫힐 때 이벤트 리스너를 제거하여 메모리 누수 방지
    closeVideo.addEventListener('click', () => {
        videoElement.removeEventListener('timeupdate', timeUpdateHandler);
        closeVideoModal();
    });
}
function closeVideoModal(){
    videoModal.classList.remove('open');
    videoModal.setAttribute('aria-hidden', 'true'); // 모달이 닫히면 aria-hidden 추가
}


// --- data ---
grid.addEventListener('click', (e) => {

    const playId = e.target.getAttribute('data-play');
    const coursesId = e.target.getAttribute('data-quiz');

    if (playId) {
        // 백엔드 API 호출
        fetch(`/api/courses/${playId}`)
            .then(response => response.json())
            .then(course => {
                // 받은 데이터로 동영상 모달 열기
                openVideo(course);
            })
            .catch(error => console.error('Error fetching course:', error));
    }

    // 퀴즈 버튼 클릭 시 페이지 연결
    if (coursesId) {
        // 기존 fetch API 호출 대신 페이지를 직접 이동
        window.location.href = `/quiz/${coursesId}`;
    }

});

// --- Quiz ---
function openQuiz(course) {
    if (course.progress < 100) {
        alert("⚠️ 수강 완료 후 퀴즈를 풀 수 있습니다.");
        return;
    }
    currentCourse = course;
    quizTitle.textContent = `퀴즈 · ${course.title}`;
    quizModal.classList.add('open');
}
function closeQuizModal(){ quizModal.classList.remove('open'); }

// Event wiring
q.addEventListener('input', e => { state.q = e.target.value.trim(); render(); });
category.addEventListener('change', e => { state.category = e.target.value; render(); });
resetBtn.addEventListener('click', () => {
    state = { q: '', category: '전체' };
    q.value = '';
    category.value = '전체';
    render();
});

closeVideo.addEventListener('click', closeVideoModal);
videoModal.addEventListener('click', (e)=>{ if(e.target===videoModal) closeVideoModal(); });

closeQuiz.addEventListener('click', closeQuizModal);
quizModal.addEventListener('click', (e)=>{ if(e.target===quizModal) closeQuizModal(); });

document.addEventListener('keydown', (e)=>{
    if(e.key==='Escape'){ closeVideoModal(); closeQuizModal(); }
});

markCompleteBtn.addEventListener('click', () => {
    if (!currentCourse) return;
    currentCourse.progress = Math.min(100, currentCourse.progress + 100);
    render();
    alert('강의 진도가 100% 증가했습니다.');
});

// 하트 클릭 + 좋아요 수 변동
grid.addEventListener('click', (e) => {
    const likeBtn = e.target;
    const likeId = likeBtn.getAttribute('data-like');
    if (likeId) {
        const isLiked = likeBtn.classList.contains('liked');
        const method = isLiked ? 'DELETE' : 'POST';
        const url = `/api/courses/${likeId}/like`;

        fetch(url, { method: method })
            .then(response => {
                if (!response.ok) {
                    throw new Error('좋아요 업데이트 실패.');
                }
                return response.json(); // 응답을 JSON으로 파싱
            })
            .then(data => {
                // 서버에서 받은 최신 좋아요 수로 화면 업데이트
                const likeCountSpan = likeBtn.previousElementSibling;
                if (likeCountSpan && data.like_count !== undefined) {
                    likeCountSpan.textContent = data.like_count;
                }

                // 하트 아이콘 토글
                if (isLiked) {
                    likeBtn.classList.remove('liked');
                    likeBtn.textContent = '♡';
                } else {
                    likeBtn.classList.add('liked');
                    likeBtn.textContent = '♥';
                }
            })
            .catch(error => {
                console.error('좋아요 업데이트 실패:', error);
                alert('좋아요 상태를 변경하는 데 실패했습니다. 다시 시도해 주세요.');
            });
    }
});

// initial paint
render();