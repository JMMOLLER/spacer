import Global from "../globals/index.js";
const module = Global.getInstance();
let eventSubmitIsAdded = false;

/**
 * @description Función que se encarga de habilitar o deshabilitar el botón de submit y aplicar el loader.
 *
 * @param {String} inputValue
 * @param {HTMLButtonElement} button
 */
const toggleSubmit = (inputValue, button) => {
  const btnSubmit = button
    ? button
    : document.querySelector(".inputSubmit-group>input[type='submit']");

  const parentBtn = btnSubmit?.parentElement;

  parentBtn.classList.toggle("submitted");
  btnSubmit.disabled = !btnSubmit.disabled;
  btnSubmit.setAttribute("value", btnSubmit.disabled ? "" : inputValue);
};

const showError = (show) => {
  const span = document.querySelector("#error");
  show ? span.classList.remove("hidden") : span.classList.add("hidden");
};

const loadLottieAnimation = () => {
  const returnElement = document.querySelector(".animation_arrow_container");

  const animation = lottie.loadAnimation({
    container: document.getElementById("animation_arrow"),
    renderer: "svg",
    loop: false,
    autoplay: false,
    path: "../assets/lotties/arrow-left.json",
  });

  returnElement.addEventListener("mouseenter", () => {
    animation.play();
  });
  animation.addEventListener("complete", () => {
    animation.stop();
  });
};

const handleSubmit = (e) => {
  e.preventDefault();
  toggleSubmit("Iniciar Sesión");
  const credentials = new FormData(e.target);
  handleLogin(Object.fromEntries(credentials));
};

const handleLogin = async (credentials) => {
  showError(false);
  const res = await module.fetchAPI("/auth", credentials, "POST");
  if (res?.statusCode === 200) {
    sessionStorage.setItem("token", res?.response?.token);
    window.location.href = "/";
  } else {
    toggleSubmit("Iniciar Sesión");
    showError(true);
  }
  return res ? true : false;
};

window.onload = () => handleAddEventSubmit();

const handleAddEventSubmit = () => {
  const form = document.querySelector("form");
  form.addEventListener("submit", handleSubmit);

  form.querySelector("input[type='submit']").removeAttribute("disabled");

  eventSubmitIsAdded = true;
};

const forceAddEventSubmit = () => {
  const idInterval = setInterval(() => {
    if (eventSubmitIsAdded) {
      clearInterval(idInterval);
      return;
    }
    handleAddEventSubmit();
    console.log("Event submit has been forcibly added to form");
  }, 500);
};

export default function init() {
  loadLottieAnimation();
  module
    .userIsAuth()
    .then((res) => (res ? (window.location.href = "/") : null));
  console.info("login");
  forceAddEventSubmit();
}

export { toggleSubmit };
