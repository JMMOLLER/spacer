import Global from "../globals/index.js";
const module = Global.getInstance();
let eventSubmitIsAdded = false;

export default function init() {
  const btnBack = document.querySelector(".animation_arrow_container");
  addBackLottieAnim();
  addBackEventListener(btnBack);
  module
    .userIsAuth()
    .then((res) => (res ? (window.location.href = "/") : null));
  forceAddEventSubmit();
}

/**
 * @description Función que se encarga de añadir el evento click al botón de regresar.
 * 
 * @param {HTMLElement} el 
 */
function addBackEventListener(el) {
  el.addEventListener("click", (e) => {
    e.preventDefault();
    window.history.back();
  });
}

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

/**
 * @description Función que se encarga de mostrar u ocultar el mensaje de error.
 * 
 * @param {boolean} show 
 */
const showError = (show) => {
  const span = document.querySelector("#error");
  show ? span.classList.remove("hidden") : span.classList.add("hidden");
};

/**
 * @description Función que se encarga de añadir la animación de lottie al botón de regresar.
 */
const addBackLottieAnim = () => {
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

/**
 * @description Función que se encarga de manejar el evento submit del formulario.
 * 
 * @param {InputEvent} e 
 */
const handleSubmit = (e) => {
  e.preventDefault();
  toggleSubmit("Iniciar Sesión");
  const credentials = new FormData(e.target);
  handleLogin(credentials);
};

/**
 * @description Función que se encarga de realizar el login del usuario
 * 
 * @param {FormData} credentials 
 * @returns 
 */
const handleLogin = async (credentials) => {
  showError(false);
  const res = await module.login(Object.fromEntries(credentials))
  if (res) {
    window.location.href = "/";
  } else {
    toggleSubmit("Iniciar Sesión");
    showError(true);
  }
  return res;
};

window.onload = () => handleAddEventSubmit();

/**
 * @description Función que se encarga de añadir el evento submit al formulario.
 */
const handleAddEventSubmit = () => {
  const form = document.querySelector("form");
  form.addEventListener("submit", handleSubmit);

  form.querySelector("input[type='submit']").removeAttribute("disabled");

  eventSubmitIsAdded = true;
};

/**
 * @description Función que se encarga de forzar la adición del evento submit al formulario.
 */
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

export { toggleSubmit, addBackLottieAnim, showError, addBackEventListener };
