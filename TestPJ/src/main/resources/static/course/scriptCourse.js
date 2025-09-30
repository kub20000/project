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
let state = { q: '', category: '전체' }; // 검색 상태 초기화

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
        // state.q가 비어있지 않고, 강의명에 포함되는지 확인하여 필터링
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
                    <span class="badge gray" data-course-id="${course.id}">진도: ${((course.duration_sec / course.total_sec) * 100).toFixed(0) || 0}%</span>
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


// =========================================================================
// **수정 및 통합된 초기 로딩 및 검색어 처리 로직**
// =========================================================================
document.addEventListener('DOMContentLoaded', function() {

    // **1. 초기 검색어 상태 설정:**
    // Thymeleaf가 설정한 q.value (input 요소의 value)를 state.q에 반영합니다.
    if (q.value) {
        state.q = q.value.trim();
    }

    // **2. API 호출 및 이벤트 리스너 설정**
    fetch('/api/courses')
        .then(response => response.json())
        .then(coursesFromApi => {

            // 데이터 로드 후, 현재 state.q (초기 검색어)를 사용하여 강의 목록을 렌더링합니다.
            render(coursesFromApi);

            // 초기 검색이 실행되었을 경우, 사용자 편의를 위해 포커스를 줍니다.
            if (state.q) {
                q.focus();
            }

            // **3. 검색/필터링 이벤트 리스너 설정 (API 데이터 기반)**
            q.addEventListener('input', e => {
                state.q = e.target.value.trim();
                render(coursesFromApi);
            });
            category.addEventListener('change', e => {
                state.category = e.target.value;
                render(coursesFromApi);
            });
            resetBtn.addEventListener('click', () => {
                state = { q: '', category: '전체' };
                q.value = '';
                category.value = '전체';
                render(coursesFromApi);
            });
        })
        .catch(error => {
            console.error('Error fetching courses:', error);
            alert('강의 목록을 불러오는 데 실패했습니다.');
        });

    // --- Video Modal Events (API 데이터와 무관) ---
    closeVideo.addEventListener('click', closeVideoModal);
    videoModal.addEventListener('click', (e)=>{ if(e.target===videoModal) closeVideoModal(); });

    closeQuiz.addEventListener('click', closeQuizModal);
    quizModal.addEventListener('click', (e)=>{ if(e.target===quizModal) closeQuizModal(); });

    document.addEventListener('keydown', (e)=>{
        if(e.key==='Escape'){ closeVideoModal(); closeQuizModal(); }
    });
});


// --- Video ---
function openVideo(course) {
    currentCourse = course;
    videoTitle.textContent = `${course.courses_name}`;
    videoModal.classList.add('open');
    videoModal.removeAttribute('aria-hidden');

    const videoPlayer = `<video id="videoElement" width="100%" height="315" controls controlsList="nodownload nofullscreen"><source src="${course.video_url}" type="video/mp4"></video>`;
    document.getElementById('videoBox').innerHTML = videoPlayer;
    document.getElementById('courseDesc').textContent = course.description;

    const videoElement = document.getElementById('videoElement');

    // 비디오 현재 진도율 설정
    if (course.duration_sec > 0) {
        videoElement.currentTime = course.duration_sec;
    }

    //  진도율 저장: 5초마다 서버에 업데이트
    let lastSavedTime = course.duration_sec;
    const saveProgressInterval = setInterval(() => {
        if (!videoElement.paused && videoElement.currentTime > 0) {
            const currentTime = Math.floor(videoElement.currentTime);
            // 5초 이상 진도 변화가 있을 때만 API 호출
            if (currentTime - lastSavedTime >= 5) {
                updateVideoProgress(currentCourse.id, currentTime);
                lastSavedTime = currentTime;
            }
        }
    }, 1000);

    //  타임라인 앞 건너뛰기 방지 로직 복구
    let lastTime = course.duration_sec;
    const timeUpdateHandler = () => {
        if (videoElement.currentTime > lastTime + 0.5) {
            videoElement.currentTime = lastTime;
        } else {
            lastTime = videoElement.currentTime;
        }
    };
    videoElement.addEventListener('timeupdate', timeUpdateHandler);

    //  비디오가 끝나거나 모달이 닫힐 때 최종 진도율 저장
    const onVideoEndOrClose = () => {
        clearInterval(saveProgressInterval);
        // 마지막 진도율을 서버에 저장
        updateVideoProgress(currentCourse.id, Math.floor(videoElement.currentTime));
        // 모달 닫기
        closeVideoModal();
    };

    videoElement.addEventListener('ended', onVideoEndOrClose);
    closeVideo.addEventListener('click', onVideoEndOrClose); // closeVideo 클릭 시 이벤트 추가
}

// API 엔드포인트와 데이터 키 수정
function updateVideoProgress(courseId, durationSec) {
    fetch(`/api/courses/${courseId}/progress`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ duration_sec: durationSec }),
    })
        .then(response => {
            if (!response.ok) {
                console.error('진도율 저장 실패');
            } else {
                // ⭐ 성공 시, 화면의 진도율을 업데이트
                const totalSec = currentCourse.total_sec;
                const progressPercentage = ((durationSec / totalSec) * 100).toFixed(0) || 0;

                // ⭐ data-course-id 속성을 이용해 해당 강의 카드만 정확히 선택
                const progressSpan = document.querySelector(`.badge.gray[data-course-id="${courseId}"]`);
                if (progressSpan) {
                    progressSpan.textContent = `진도: ${progressPercentage}%`;
                }
            }
        })
        .catch(error => console.error('Error saving progress:', error));
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
        // 강의 재생 버튼
        fetch(`/api/courses/${playId}`)
            .then(response => response.json())
            .then(course => {
                openVideo(course);
            })
            .catch(error => console.error('Error fetching course:', error));
    }

    if (coursesId) {
        // 퀴즈 풀기 버튼
        // 먼저 해당 코스 정보를 가져와서 openQuiz 함수에 전달
        fetch(`/api/courses/${coursesId}`)
            .then(response => response.json())
            .then(course => {
                // 진도율 체크 로직이 포함된 openQuiz 함수 호출
                openQuiz(course);
            })
            .catch(error => console.error('Error fetching course:', error));
    }

});

// --- Quiz ---
function openQuiz(course) {
    // 진도율이 100% 미만일 때 경고창 표시
    if (course.duration_sec < course.total_sec) {
        alert("⚠️ 수강 완료 후 퀴즈를 풀 수 있습니다.");
        return;
    }
    // 진도율이 100% 이상이면 퀴즈 페이지로 바로 이동
    window.location.href = `/quiz/${course.id}`;
}
function closeQuizModal(){ quizModal.classList.remove('open'); }

markCompleteBtn.addEventListener('click', () => {
    if (!currentCourse) return;

    // 현재 진도율 계산
    const totalSec = currentCourse.total_sec;
    const durationSec = currentCourse.duration_sec;
    const progressPercentage = (durationSec / totalSec) * 100;

    if (progressPercentage >= 100) {
        // 진도율이 100% 이상일 때
        alert('🎉 수강을 완료하였습니다.');
        closeVideoModal(); // 창 닫기
    } else {
        // 진도율이 100% 미만일 때
        alert(`⚠️ 현재 수강률은 ${progressPercentage.toFixed(0)}% 입니다. 100%를 채워야 수강이 완료됩니다.`);
        // 창은 닫히지 않음
    }
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