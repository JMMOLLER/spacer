const toggleSubmit = () => {
  const submitGp = document.querySelector(".inputSubmit-group");
  const input = document.querySelector(
    ".inputSubmit-group>input[type='submit']"
  );
  submitGp.classList.toggle("submitted");
  input.disabled = !input.disabled;
  input.setAttribute("value", input.disabled ? "" : "Iniciar Sesión");
};

const loadLottieAnimation = () => {
  const returnElement = document.querySelector(".animation_arrow_container");
  document.querySelector(".spacer_form").addEventListener("submit", (e) => {
    e.preventDefault();
    toggleSubmit();
  });

  const animation = lottie.loadAnimation({
    container: document.getElementById("animation_arrow"), // the dom element that will contain the animation
    renderer: "svg",
    loop: false,
    autoplay: false,
    path: "../assets/lotties/arrow-left.json", // the path to the animation json
  });

  returnElement.addEventListener("mouseenter", () => {
    animation.play();
  });
  animation.addEventListener("complete", () => {
    animation.stop();
  });
};

export default function init() {
  loadLottieAnimation();
}
