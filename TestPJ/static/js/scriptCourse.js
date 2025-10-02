// UI refsr
const grid = document.getElementById('grid');
const q = document.getElementById('q');
const category = document.getElementById('category');
const resetBtn = document.getElementById('reset');
const chips = document.getElementById('chips');

const videoModal = document.getElementById('videoModal');
const videoTitle = document.getElementById('videoTitle');
const closeVideo = document.getElementById('closeVideo'); // 닫기 버튼 요소
const markCompleteBtn = document.getElementById('markComplete');

const quizModal = document.getElementById('quizModal');
const quizTitle = document.getElementById('quizTitle');
const closeQuiz = document.getElementById('closeQuiz');

let currentCourse = null; // 현재 재생 중인 강의 DTO 저장
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

    grid.innerHTML = filteredData.map(course=> {
        // userDurationSec를 기반으로 progress를 계산하여 UI에 표시
        const displayProgress = (course.userDurationSec && course.total_sec > 0)
            ? Math.min(100, Math.floor((course.userDurationSec / course.total_sec) * 100))
            : 0;


        return `
            <article class="card">
                <div class="thumb">
                    <img src="${course.thumbnail_url}" alt="${course.courses_name} 썸네일">
                </div>
                <div class="card-body">
                    <div class="title">${course.courses_name}</div>
                    <div class="meta">
                        <span class="badge">${course.courses_category}</span>
                         <span class="badge gray" data-course-id="${course.id}">진도: ${displayProgress}%</span>
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
        `;
    }).join('');

    renderChips();
}


// =========================================================================
// **초기 로딩 및 이벤트 리스너 설정**
// =========================================================================
document.addEventListener('DOMContentLoaded', function() {

    // **1. 초기 검색어 상태 설정:**
    if (q.value) {
        state.q = q.value.trim();
    }

    // **2. API 호출 및 이벤트 리스너 설정**
    fetch('/api/courses')
        .then(response => response.json())
        .then(coursesFromApi => {

            render(coursesFromApi);

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
    // ⭐️ [수정 1] closeVideo 리스너 삭제: openVideo에서 동적으로 등록 및 해제하여 중복 등록 방지
    // closeVideo.addEventListener('click', closeVideoModal);

    // 모달 배경 클릭 시 닫기 (이 로직은 유지)
    videoModal.addEventListener('click', (e)=>{
        if(e.target === videoModal) {
            // 배경 클릭 시에도 진도율 저장 로직을 타도록 closeVideoModal 대신 closeVideoHandler의 로직을 사용
            // 하지만 closeVideoHandler는 openVideo 내부에 동적으로 생성되므로,
            // 여기서는 단순히 closeVideoModal만 호출하고, 사용자에게 닫기 버튼을 사용하도록 유도합니다.
            closeVideoModal();
        }
    });

    closeQuiz.addEventListener('click', closeQuizModal);
    quizModal.addEventListener('click', (e)=>{ if(e.target===quizModal) closeQuizModal(); });

    document.addEventListener('keydown', (e)=>{
        if(e.key==='Escape'){
            if (videoModal.classList.contains('open')) {
                // ESC 키를 누르면, 닫기 버튼을 누른 것과 동일하게 처리
                closeVideo.click();
            }
            closeQuizModal();
        }
    });
});


// --- Video ---
/**
 * 비디오 재생 창을 열고, 유저의 마지막 시청 시간부터 재생을 시작하며,
 * 진도율 저장 로직을 초기화합니다.
 * @param {object} course - CourseDetailDto 객체 (userDurationSec 포함)
 */
function openVideo(course) {
    currentCourse = course;
    videoTitle.textContent = `${course.courses_name}`;
    videoModal.classList.add('open');
    videoModal.removeAttribute('aria-hidden');

    // 비디오 플레이어 생성 (controlsList="nodownload nofullscreen" 유지)
    const videoPlayer = `<video id="videoElement" width="100%" height="315" controls controlsList="nodownload nofullscreen"><source src="${course.video_url}" type="video/mp4"></video>`;

    document.getElementById('videoBox').innerHTML = videoPlayer;
    document.getElementById('courseDesc').textContent = course.description;

    const videoElement = document.getElementById('videoElement');

    if (!videoElement) {
        console.error("비디오 요소(id='videoElement')를 찾을 수 없습니다.");
        return;
    }

    // ⭐️ [수정 2] DTO 필드명 사용: userDurationSec
    const userDurationSec = currentCourse.userDurationSec || 0;
    const totalSec = currentCourse.total_sec;

    if (userDurationSec > 0) {
        videoElement.currentTime = userDurationSec;
    }

    // -----------------------------------------------------------
    // 이벤트 리스너 정의 (중복 방지 및 핸들러 정의)
    // -----------------------------------------------------------

    // A. 타임라인 건너뛰기 방지 핸들러
    let lastTime = userDurationSec;
    const timeUpdateHandler = () => {
        if (videoElement.currentTime > lastTime + 0.5) {
            videoElement.currentTime = lastTime;
        } else {
            lastTime = videoElement.currentTime;
        }
    };

    // B. 비디오 종료 또는 닫기 시 최종 진도율 저장 및 정리 핸들러
    let lastSavedTime = userDurationSec;

    const onVideoEndOrClose = () => {
        // 인터벌 클리어
        if (videoElement.progressInterval) {
            clearInterval(videoElement.progressInterval);
            videoElement.progressInterval = null;
        }

        // 최종 시청 시간 계산
        const finalDuration = Math.min(Math.floor(videoElement.currentTime), totalSec);

        // 최종 진도율 저장 (마지막 저장 시간보다 길거나, 영상 끝에 도달했을 경우)
        if (finalDuration > lastSavedTime || finalDuration === totalSec) {
            updateVideoProgress(currentCourse.id, finalDuration, totalSec);
        }

        // 모든 이벤트 리스너 제거 (중요! 메모리 및 중복 방지)
        videoElement.removeEventListener('timeupdate', timeUpdateHandler);
        videoElement.removeEventListener('ended', closeVideoEndHandler);
        closeVideo.removeEventListener('click', closeVideoHandler);
    };

    // C. 닫기 버튼 클릭 핸들러
    const closeVideoHandler = () => {
        onVideoEndOrClose();
        closeVideoModal();
    };

    // D. 비디오 재생 종료 핸들러
    const closeVideoEndHandler = () => {
        onVideoEndOrClose();
        closeVideoModal();
    };

    // -----------------------------------------------------------
    // 이벤트 리스너 등록
    // -----------------------------------------------------------

    // 닫기 버튼 클릭 시 동적 핸들러 등록
    closeVideo.addEventListener('click', closeVideoHandler);

    // 비디오 이벤트 리스너 등록
    videoElement.addEventListener('timeupdate', timeUpdateHandler);
    videoElement.addEventListener('ended', closeVideoEndHandler);

    // -----------------------------------------------------------
    // 1초마다 진도율 저장 인터벌
    // -----------------------------------------------------------

    if (videoElement.progressInterval) {
        clearInterval(videoElement.progressInterval); // 이전 인터벌 클리어
    }

    videoElement.progressInterval = setInterval(() => {
        if (!videoElement.paused && videoElement.currentTime > 0) {
            const currentTime = Math.floor(videoElement.currentTime);

            // 5초마다 또는 영상 끝에 도달했을 때 저장
            if (currentTime >= lastSavedTime + 5 || (currentTime >= totalSec && totalSec > 0)) {
                updateVideoProgress(currentCourse.id, currentTime, totalSec);
                lastSavedTime = currentTime;
            }
        }
    }, 1000); // 1초마다 체크
}


function closeVideoModal(){
    videoModal.classList.remove('open');
    videoModal.setAttribute('aria-hidden', 'true');
    // 비디오 요소 자체 제거 (메모리 해제 및 다음 재생 준비)
    document.getElementById('videoBox').innerHTML = '';
}


function updateVideoProgress(courseId, durationSec, totalSec) {
    // ️ UI 업데이트를 위해 백분율을 미리 계산해 둡니다.
    const progressPercentage = Math.min(100, Math.round(((durationSec / totalSec) * 100))) || 0;

    // 1. 기존: 유저-강의 진도율 (userDurationSec) 업데이트 API 호출
    fetch(`/api/courses/${courseId}/progress`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        // 서버에는 시청 시간(초)만 보냅니다.
        body: JSON.stringify({ duration_sec: durationSec }),
    })
        .then(response => {
            if (response.status === 401) {
                console.error('로그인 후 진도율 저장이 가능합니다.');
                return;
            }
            if (!response.ok) {
                console.error('진도율 저장 실패');
            } else {
                // 성공 시, 화면의 진도율을 업데이트
                const progressSpan = document.querySelector(`.badge.gray[data-course-id="${courseId}"]`);
                if (progressSpan) {
                    progressSpan.textContent = `진도: ${progressPercentage}%`;
                }
                // currentCourse의 진도율도 업데이트하여 다음 openVideo 호출 시 최신 상태를 반영
                if (currentCourse && currentCourse.id === courseId) {
                    currentCourse.userDurationSec = durationSec;
                }
                //  2. video_history (최근 본 강의) 테이블 업데이트 API 호출
                saveVideoHistory(courseId, progressPercentage);
            }
        })
        .catch(error => console.error('Error saving progress:', error));
}
    // 서버에 video_history 테이블 기록을 위한 API를 호출합니다
function saveVideoHistory(courseId, progressRate) {
    fetch(`/api/video-history`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            // 서버에 비디오 ID와 현재 진도율을 전송
            videoId: courseId,
            progressRate: progressRate
        }),
    })
        .then(response => {
            if (!response.ok) {
                console.error('비디오 히스토리 저장 실패');
            } else {
                console.log('최근 본 강의 기록 성공:', courseId);
                // 성공 시, 별도의 UI 변화는 필요 없습니다.
            }
        })
        .catch(error => console.error('Error saving video history:', error));
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
        fetch(`/api/courses/${coursesId}`)
            .then(response => response.json())
            .then(course => {
                openQuiz(course);
            })
            .catch(error => console.error('Error fetching course:', error));
    }

});

// --- Quiz ---
function openQuiz(course) {
    // DTO에서 받은 userDurationSec 사용
    const userDurationSec = course.userDurationSec || 0;
    const totalSec = course.total_sec;

    // 정수 진도율 계산
    const progressPercentage = Math.min(100, Math.round(((userDurationSec / totalSec) * 100))) || 0;

    // 진도율이 100% 미만일 때 경고창 표시
    if (progressPercentage < 100) {
        // alert() 대신 사용자 정의 메시지 박스 사용 권장
        alert("⚠️ 수강 완료 후 퀴즈를 풀 수 있습니다.");
        return;
    }
    // 진도율이 100% 이상이면 퀴즈 페이지로 바로 이동
    window.location.href = `/quiz/${course.id}`;
}

function closeQuizModal(){ quizModal.classList.remove('open'); }

markCompleteBtn.addEventListener('click', () => {
    if (!currentCourse) return;

    const totalSec = currentCourse.total_sec;
    // ⭐️ [수정 3] DTO 필드명 사용: userDurationSec
    const durationSec = currentCourse.userDurationSec;

    // 현재 진도율 계산
    const progressPercentage = totalSec > 0 ? Math.floor((durationSec / totalSec) * 100) : 0;

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
