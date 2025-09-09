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



//게시판

//공지사항
 
// 최신 게시물에 자동으로 NEW 표시 추가
    document.addEventListener("DOMContentLoaded", () => {
      const table = document.getElementById("noticeTable");
      const firstRow = table.querySelector("tbody tr");

      if (firstRow) {
        const firstCell = firstRow.querySelector("td");
        if (firstCell) {
          firstCell.innerHTML = '<span class="new-label">new</span>';
        }
      }
    });

    // 게시글 상세보기 예시
    function viewPost() {
      window.location.href = "notice_view.html";
    }

    // 페이징 처리
    const rowsPerPage = 7;
    const table = document.getElementById("noticeTable");
    const tbody = table.querySelector("tbody");
    const rows = Array.from(tbody.querySelectorAll("tr"));
    const pagination = document.getElementById("pagination");

    function displayPage(page) {
      tbody.innerHTML = "";
      const start = (page - 1) * rowsPerPage;
      const end = start + rowsPerPage;
      const pageRows = rows.slice(start, end);
      pageRows.forEach(r => tbody.appendChild(r));
      updatePagination(page);
    }

    function updatePagination(currentPage) {
      pagination.innerHTML = "";
      const totalPages = Math.ceil(rows.length / rowsPerPage);

      for (let i = 1; i <= totalPages; i++) {
        const a = document.createElement("a");
        a.textContent = i;
        a.classList.toggle("active", i === currentPage);
        a.onclick = () => displayPage(i);
        pagination.appendChild(a);
      }
    }

    // 검색 기능
document.getElementById("searchInput").addEventListener("input", function () {
  const query = this.value.replace(/\s+/g, "").toLowerCase(); // 띄어쓰기 제거 후 검색

  if (query === "") {
    // 검색창이 비어 있으면 원래 페이지네이션 복귀
    displayPage(1);
    return;
  }

  // 검색어가 있을 때만 필터링
  const filteredRows = rows.filter(row => {
    const title = row.querySelector("td:nth-child(2)").innerText.replace(/\s+/g, "").toLowerCase();
    return title.includes(query);
  });

  tbody.innerHTML = "";
  filteredRows.forEach(r => tbody.appendChild(r));

  // 검색 결과에는 페이지네이션 숨김 
  pagination.innerHTML = "";
});

    // 초기 표시
    displayPage(1);