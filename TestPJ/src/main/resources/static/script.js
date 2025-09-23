//홈 화면
window.addEventListener("scroll", () => {
    document.querySelectorAll(".reveal").forEach((el) => {
        const windowHeight = window.innerHeight;
        const revealTop = el.getBoundingClientRect().top;
        if (revealTop < windowHeight - 100) {
            el.classList.add("active");
        }
    });
});

document.addEventListener("DOMContentLoaded", async () => {
    // 1. 기존의 첫 번째 DOMContentLoaded 리스너의 내용
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



    //
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