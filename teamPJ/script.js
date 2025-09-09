document.addEventListener("DOMContentLoaded", () => {
  // hero 코드 (null 체크)
  const hero = document.querySelector(".hero");
  if (hero) {
    const images = [
      "images/vegan1.jpg",
      "images/vegan2.jpg",
      "images/vegan3.jpg",
      "images/vegan4.jpg"
    ];
    let currentIndex = 0;
    hero.style.backgroundImage = `url(${images[currentIndex]})`;
    setInterval(() => {
      currentIndex = (currentIndex + 1) % images.length;
      hero.style.backgroundImage = `url(${images[currentIndex]})`;
    }, 3000);
  }


  // 냉장고 페이지
  // 냉장고 버튼 코드
  const ingredientButtons = document.querySelectorAll(".ingredient-list button");
  const tagContainer = document.getElementById("tagContainer");
  const ingredientSearch = document.getElementById("ingredientSearch");
  const getRecipeBtn = document.getElementById("getRecipeBtn");

  let selectedIngredients = [];

  function updateTags() {
    tagContainer.innerHTML = "";
    selectedIngredients.forEach(ing => {
      const tag = document.createElement("div");
      tag.classList.add("tag");
      tag.innerHTML = `${ing} <span class="remove">&times;</span>`;
      tag.querySelector(".remove").addEventListener("click", () => {
        selectedIngredients = selectedIngredients.filter(i => i !== ing);
        updateTags();
      });
      tagContainer.appendChild(tag);
    });
  }

  ingredientButtons.forEach(button => {
    button.addEventListener("click", () => {
      const ingredient = button.textContent.trim();
      if (!selectedIngredients.includes(ingredient)) {
        selectedIngredients.push(ingredient);
        updateTags();
      }
    });
  });

  ingredientSearch.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      const ingredient = ingredientSearch.value.trim();
      if (ingredient && !selectedIngredients.includes(ingredient)) {
        selectedIngredients.push(ingredient);
        updateTags();
        ingredientSearch.value = "";
      }
    }
  });

  getRecipeBtn.addEventListener("click", () => {
    if (selectedIngredients.length < 1) {
      alert("재료를 선택 해 주세요");
      return;
    }
  });
});



document.addEventListener("DOMContentLoaded", () => {
  const table = document.getElementById("boardTable");
  const tbody = table.querySelector("tbody");
  const pagination = document.getElementById("pagination");
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

  let comments = [];
  let filteredPosts = [...posts]; // 검색 결과 저장
  let currentPage = 1;

  // 게시판 출력 (NEW 라벨 포함)
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
      td.setAttribute("colspan", 4);
      td.textContent = "검색 결과가 없습니다";
      tr.appendChild(td);
      tbody.appendChild(tr);
    }
  }

  // 페이지네이션
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
      a.classList.toggle("active", i === page);
      a.addEventListener("click", () => displayPage(i));
      pagination.appendChild(a);
    }
  }

  // 모달 열기/닫기
  function openModal(id) {
    document.getElementById(id).classList.add("open");
  }
  function closeModal(id) {
    document.getElementById(id).classList.remove("open");
  }

  // 게시글 클릭 시 모달 열기
  tbody.addEventListener("click", e => {
    if (e.target.classList.contains("title-link")) {
      const postId = Number(e.target.dataset.id);
      const post = posts.find(p => p.postId === postId);
      if (!post) return;

      document.getElementById("modalTitle").textContent = post.postTitle;
      document.getElementById("modalAuthor").textContent = post.author;
      document.getElementById("modalCreatedAt").textContent = post.createdAt;
      document.getElementById("modalContent").textContent = post.content;

      renderComments(postId);
      openModal("postModal");

      document.getElementById("addCommentBtn").onclick = () => {
        const input = document.getElementById("commentInput");
        const text = input.value.trim();
        if (!text) return;

        comments.push({
          comId: comments.length + 1,
          postId,
          writer: "익명",
          comContent: text,
          createAt: new Date().toISOString().split("T")[0]
        });

        input.value = "";
        renderComments(postId);
      };
    }
  });

  // 댓글 렌더링
  function renderComments(postId) {
    const container = document.getElementById("modalComments");
    container.innerHTML = "";
    const postComments = comments.filter(c => c.postId === postId);

    if (postComments.length === 0) {
      container.innerHTML = "<p>댓글이 없습니다.</p>";
    } else {
      postComments.forEach(c => {
        const div = document.createElement("div");
        div.classList.add("comment");
        div.innerHTML = `<p><strong>${c.writer}</strong> (${c.createAt})<br>${c.comContent}</p>`;
        container.appendChild(div);
      });
    }
  }

  // 글쓰기 모달
  document.getElementById("openWriteModal").addEventListener("click", () => openModal("writeModal"));
  document.querySelectorAll(".closeModal").forEach(btn => {
    btn.addEventListener("click", () => closeModal(btn.dataset.target));
  });

  // 글 등록
  document.getElementById("addPostBtn").addEventListener("click", () => {
    const title = document.getElementById("newPostTitle").value.trim();
    const content = document.getElementById("newPostContent").value.trim();
    const author = document.getElementById("newPostAuthor").value.trim();
    if (!title || !content || !author) return alert("모든 항목을 입력해주세요!");

    const createdAt = new Date().toISOString().split("T")[0];
    const newPost = { postId: posts.length + 1, author, postTitle: title, content, createdAt };
    posts.unshift(newPost);
    filteredPosts = [...posts]; // 검색 초기화
    displayPage(1);

    document.getElementById("newPostTitle").value = "";
    document.getElementById("newPostContent").value = "";
    document.getElementById("newPostAuthor").value = "";

    closeModal("writeModal");
  });

  // 검색
  document.getElementById("searchInput").addEventListener("input", function () {
    const query = this.value.replace(/\s+/g, "").toLowerCase();
    filteredPosts = posts.filter(p => p.postTitle.replace(/\s+/g, "").toLowerCase().includes(query));
    displayPage(1);
  });

  // 초기 실행
  displayPage(1);
});
