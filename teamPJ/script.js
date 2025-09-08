document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();
      //  로그인 백엔드 필요
      alert("Logged in successfully!");
      window.location.href = "index.html";
    });
  }
});

const hero = document.querySelector(".hero");
const images = [
  "images/vegan1.jpg",
  "images/vegan2.jpg",
  "images/vegan3.jpg",
  "images/vegan4.jpg"
];

let currentIndex = 0;

// 초기 배경 세팅
hero.style.backgroundImage = `url(${images[currentIndex]})`;

// 3초마다 배경 변경
setInterval(() => {
  currentIndex = (currentIndex + 1) % images.length;
  hero.style.backgroundImage = `url(${images[currentIndex]})`;
}, 3000);


