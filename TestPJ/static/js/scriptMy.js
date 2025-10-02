const tableBody = document.querySelector("#myPostTable tbody");
const searchInput = document.getElementById("searchInput");
const paginationDiv = document.querySelector(".pagination"); // HTML에 pagination div 추가 필요

// 상태 관리 변수
let currentPage = 0;
const pageSize = 10; // 서버의 기본값과 일치시킵니다.
let totalPages = 0;

// API 호출 및 게시글 목록 렌더링 함수
async function fetchAndRenderMyPosts(page = 0, keyword = '') {
    try {
        const url = `/api/mypage/posts?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}`;
        const response = await fetch(url);

        if (response.status === 401) {
            tableBody.innerHTML = '<tr><td colspan="4">로그인이 필요합니다.</td></tr>';
            return;
        }

        if (!response.ok) throw new Error('내 게시글 목록을 불러오는 데 실패했습니다.');

        const data = await response.json(); // PostPageDto를 받습니다.

        totalPages = data.totalPages;

        renderPosts(data.content); // 게시글 목록 렌더링
        renderPagination(data.currentPage, data.totalPages); // 페이지네이션 렌더링

    } catch (error) {
        console.error('오류:', error);
        tableBody.innerHTML = '<tr><td colspan="4">게시글 로딩 중 오류가 발생했습니다.</td></tr>';
    }
}

// 게시글 목록을 HTML로 변환하여 렌더링
function renderPosts(posts) {
    tableBody.innerHTML = '';
    if (posts.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="4">작성된 게시글이 없습니다.</td></tr>';
        return;
    }

    posts.forEach(post => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${post.category === 'NOTICE' ? '공지사항' : '자유'}</td>
            <td>
                <a href="/post/detail/${post.id}" class="post-title-link">${post.title}</a>
            </td>
            <td>${post.created_at.substring(0, 10)}</td> <td>
                <button class="edit-btn" data-id="${post.id}">수정</button>
                <button class="delete-btn" data-id="${post.id}">삭제</button>
            </td>
        `;
        tableBody.appendChild(tr);
    });
}

// 페이지네이션 렌더링 함수
function renderPagination(current, total) {
    if (!paginationDiv) return;

    paginationDiv.innerHTML = '';
    const startPage = Math.max(0, current - 2);
    const endPage = Math.min(total - 1, startPage + 4);

    // 이전 버튼
    if (current > 0) {
        const prev = document.createElement('a');
        prev.textContent = '이전';
        prev.href = '#';
        prev.addEventListener('click', (e) => { e.preventDefault(); fetchAndRenderMyPosts(current - 1, searchInput.value); });
        paginationDiv.appendChild(prev);
    }

    // 페이지 번호
    for (let i = startPage; i <= endPage; i++) {
        const a = document.createElement('a');
        a.textContent = i + 1;
        a.href = '#';
        if (i === current) a.classList.add('active');
        a.addEventListener('click', (e) => { e.preventDefault(); fetchAndRenderMyPosts(i, searchInput.value); });
        paginationDiv.appendChild(a);
    }

    // 다음 버튼
    if (current < total - 1) {
        const next = document.createElement('a');
        next.textContent = '다음';
        next.href = '#';
        next.addEventListener('click', (e) => { e.preventDefault(); fetchAndRenderMyPosts(current + 1, searchInput.value); });
        paginationDiv.appendChild(next);
    }
}

// 검색 이벤트
searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault(); // 폼 제출 방지
        currentPage = 0;
        fetchAndRenderMyPosts(currentPage, searchInput.value);
    }
});

// 관리 버튼 (수정/삭제) 이벤트
tableBody.addEventListener('click', async (e) => {
    const target = e.target;
    const postId = target.getAttribute('data-id');

    if (!postId) return;

    if (target.classList.contains('edit-btn')) {
        // 수정 페이지로 이동
        window.location.href = `/post/edit/${postId}`;
    }

    else if (target.classList.contains('delete-btn')) {
        if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`/api/mypage/posts/${postId}`, {
                    method: 'DELETE',
                });

                if (response.status === 204 || response.ok) {
                    alert('게시글이 성공적으로 삭제되었습니다.');
                    // 삭제 후 현재 페이지를 다시 로드하여 목록 업데이트
                    fetchAndRenderMyPosts(currentPage, searchInput.value);
                } else {
                    const errorData = await response.json();
                    alert(`삭제 실패: ${errorData.error || response.statusText}`);
                }
            } catch (error) {
                console.error('삭제 오류:', error);
                alert('게시글 삭제 중 네트워크 오류가 발생했습니다.');
            }
        }
    }
});


// 초기 로딩 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderMyPosts();
});