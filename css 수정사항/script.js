document.addEventListener("DOMContentLoaded", () => {
    console.log("myFridge.js loaded");

    // =========================
    // 공통: hero (있을 때만)
    // =========================
    const hero = document.querySelector(".hero");
    if (hero) {
        const images = ["images/vegan1.jpg","images/vegan2.jpg","images/vegan3.jpg","images/vegan4.jpg"];
        let i = 0;
        hero.style.backgroundImage = `url(${images[i]})`;
        setInterval(() => {
            i = (i + 1) % images.length;
            hero.style.backgroundImage = `url(${images[i]})`;
        }, 3000);
    }

    // =========================
    // 냉장고 페이지 (있을 때만)
    // =========================
    const ingredientListEl = document.querySelector(".ingredient-list");
    const tagContainer = document.getElementById("tagContainer");
    const ingredientSearch = document.getElementById("ingredientSearch");
    const getRecipeBtn = document.getElementById("getRecipeBtn");
    const recipeCard = document.querySelector(".recipeCard");

    if (ingredientListEl && tagContainer && ingredientSearch && getRecipeBtn) {
        const selected = new Set();

        function renderTags() {
            tagContainer.innerHTML = "";
            [...selected].forEach(ing => {
                const tag = document.createElement("div");
                tag.className = "tag";
                tag.innerHTML = `${ing} <span class="remove">&times;</span>`;
                tag.querySelector(".remove").addEventListener("click", () => {
                    selected.delete(ing);
                    renderTags();
                });
                tagContainer.appendChild(tag);
            });
        }

        // 재료 버튼 클릭
        ingredientListEl.addEventListener("click", (e) => {
            if (e.target.tagName === "BUTTON") {
                const ing = e.target.textContent.trim();
                selected.has(ing) ? selected.delete(ing) : selected.add(ing);
                renderTags();
            }
        });

        // 입력창 Enter로 추가
        ingredientSearch.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                const ing = ingredientSearch.value.trim();
                if (ing) {
                    selected.add(ing);
                    ingredientSearch.value = "";
                    renderTags();
                }
            }
        });

        // 레시피 요청
        getRecipeBtn.addEventListener("click", async () => {
            const ingredients = [...selected];
            if (ingredients.length === 0) return alert("재료를 하나 이상 선택하세요!");

            // 로딩 시작: 카드 숨기고 로딩 메시지 표시
            recipeCard.style.display = "none";
            document.getElementById("loadingMessage").style.display = "block";

            document.getElementById("notice").textContent = "";

            getRecipeBtn.disabled = true;

            try {
                const res = await fetch("/api/fridge/getRecipe", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ ingredients })
                });


                if (res.ok) {
                    const data = await res.json();
                    const recipeText = data.recipe || "";

                    let notice = "";
                    let title = "제목 없음";
                    let content = "";

                    const match = recipeText.match(/\[(.*?)\]/); // [제목] 찾기
                    if (match) {
                        // 안내문 = [제목] 나오기 전까지
                        notice = recipeText.substring(0, match.index).trim();

                        // 제목 = [ ] 안
                        title = match[1].trim();

                        // 본문 = [제목] 뒤쪽
                        content = recipeText.substring(match.index + match[0].length).trim();
                    } else {
                        // [ ] 제목이 없으면 그냥 본문 처리
                        content = recipeText.trim();
                    }

                    // 안내문은 카드 밖 영역
                    document.getElementById("notice").textContent = notice;

                    // 제목/본문은 카드 안
                    document.getElementById("recipeTitle").textContent = title;
                    document.getElementById("recipeContent").textContent = content;

                    // 로딩 메시지 숨기고 카드 표시
                    document.getElementById("loadingMessage").style.display = "none";
                    recipeCard.style.display = "block";

                } else {
                    const text = await res.text();
                    document.getElementById("recipeTitle").textContent = "오류";
                    document.getElementById("recipeContent").textContent = `요청 실패: ${text}`;

                    document.getElementById("loadingMessage").style.display = "none";
                    recipeCard.style.display = "block";
                }
            } catch (err) {
                document.getElementById("recipeTitle").textContent = "오류";
                document.getElementById("recipeContent").textContent = "요청 실패: " + err.message;

                document.getElementById("loadingMessage").style.display = "none";
                recipeCard.style.display = "block";
            } finally {
                getRecipeBtn.disabled = false;
            }
        });


    }


    // =========================
    // 게시판 페이지 (있을 때만)
    // =========================
    const table = document.getElementById("boardTable");
    const pagination = document.getElementById("pagination");
    const searchInput = document.getElementById("searchInput");

    if (table && pagination && searchInput) {
        const tbody = table.querySelector("tbody");
        const rowsPerPage = 7;

        let posts = [
            { postId: 10, author: "관리자", postTitle: "최신 공지사항", content: "최신 공지사항 내용입니다.", createdAt: "2025-09-09" },
            { postId: 9, author: "a", postTitle: "공지사항 예시", content: "공지사항 예시 내용입니다.", createdAt: "2025-09-05" },
            { postId: 8, author: "a", postTitle: "테스트 제목", content: "테스트 제목 내용입니다.", createdAt: "2025-09-01" },
            { postId: 7, author: "a", postTitle: "업데이트 알림", content: "업데이트 알림 내용입니다.", createdAt: "2025-08-30" },
            { postId: 6, author: "a", postTitle: "공지사항 제목", content: "공지사항 제목 내용입니다.", createdAt: "2025-08-25" },
            { postId: 5, author: "a", postTitle: "임시 테스트", content: "임시 테스트 내용입니다.", createdAt: "2025-08-20" },
            { postId: 4, author: "a", postTitle: "공지 예시", content: "공지 예시 내용입니다.", createdAt: "2025-08-10" },
            { postId: 3, author: "a", postTitle: "이전 글", content: "이전 글 내용입니다.", createdAt: "2025-08-05" },
            { postId: 2, author: "a", postTitle: "공지사항 테스트", content: "공지사항 테스트 내용입니다.", createdAt: "2025-08-01" },
            { postId: 1, author: "a", postTitle: "첫 글", content: "첫 글 내용입니다.", createdAt: "2025-07-25" }
        ];

        let filteredPosts = [...posts];
        let currentPage = 1;

        function generateRows(list) {
            tbody.innerHTML = "";
            const today = new Date();
            list.forEach(post => {
                const tr = document.createElement("tr");
                const createdDate = new Date(post.createdAt);
                const diffDays = (today - createdDate) / (1000 * 60 * 60 * 24);
                const newLabel = diffDays <= 5 ? `<span class="new-label">NEW</span>` : "";

                tr.innerHTML = `
          <td>${post.postId}</td>
          <td><span class="title-link" data-id="${post.postId}">${post.postTitle}</span> ${newLabel}</td>
          <td>${post.author}</td>
          <td>${post.createdAt}</td>
        `;
                tbody.appendChild(tr);
            });

            if (list.length === 0) {
                const tr = document.createElement("tr");
                const td = document.createElement("td");
                td.colSpan = 4;
                td.textContent = "검색 결과가 없습니다";
                tr.appendChild(td);
                tbody.appendChild(tr);
            }
        }

        function displayPage(page) {
            currentPage = page;
            const start = (page - 1) * rowsPerPage;
            const end = start + rowsPerPage;
            const pagePosts = filteredPosts.slice(start, end);

            generateRows(pagePosts);

            pagination.innerHTML = "";
            const totalPages = Math.ceil(filteredPosts.length / rowsPerPage);
            for (let i = 1; i <= totalPages; i++) {
                const a = document.createElement("a");
                a.textContent = i;
                if (i === page) a.classList.add("active");
                a.addEventListener("click", () => displayPage(i));
                pagination.appendChild(a);
            }
        }

        tbody.addEventListener("click", e => {
            if (e.target.classList.contains("title-link")) {
                const postId = Number(e.target.dataset.id);
                window.location.href = `detailPost.html?postId=${postId}`;
            }
        });

        searchInput.addEventListener("input", function () {
            const query = this.value.replace(/\s+/g, "").toLowerCase();
            filteredPosts = posts.filter(p => p.postTitle.replace(/\s+/g, "").toLowerCase().includes(query));
            displayPage(1);
        });

        displayPage(1);
    }
});


