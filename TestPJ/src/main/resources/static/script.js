
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

document.addEventListener("DOMContentLoaded", async() => {
  const table = document.getElementById("boardTable");
  const tbody = table.querySelector("tbody");
  const pagination = document.getElementById("pagination");
  const rowsPerPage = 7;

  // DB에서 데이터를 받아와 posts에 할당
  let posts = await fetchPosts();
  let filteredPosts = [...posts];
  let currentPage = 1;

// 데이터베이스에서 게시글을 가져오는 비동기 함수
  async function fetchPosts() {
    try {
      const response = await fetch('/api/posts'); // 백엔드 API 엔드포인트
      if (!response.ok) {
        throw new Error('네트워크 응답이 올바르지 않습니다.');
      }
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('게시글을 가져오는 중 오류 발생:', error);
      return []; // 오류 발생 시 빈 배열 반환
    }
  }

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

  // 게시글 클릭 시 detailPost.html 이동
  tbody.addEventListener("click", e => {
    if (e.target.classList.contains("title-link")) {
      const postId = Number(e.target.dataset.id);
      window.location.href = `detailPost.html?postId=${postId}`;
    }
  });

  // 검색 함수 수정 (posts 변수가 이미 로드된 상태이므로 그대로 사용 가능)
  document.getElementById("searchInput").addEventListener("input", function () {
    const query = this.value.replace(/\s+/g, "").toLowerCase();
    filteredPosts = posts.filter(p => p.postTitle.replace(/\s+/g, "").toLowerCase().includes(query));
    displayPage(1);
  });

  // 초기 페이지 로딩
  displayPage(1);
});
