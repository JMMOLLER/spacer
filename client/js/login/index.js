import Global from "../globals/index.js";
const module = Global.getInstance();
let eventSubmitIsAdded = false;

export default function init() {
  const btnBack = document.querySelector(".animation_arrow_container");
  const linkElements = document.querySelectorAll(".opciones_login > a");
  addBackLottieAnim();
  addBackEventListener(btnBack);
  linkElements.forEach((el) => addAnimationClose(el, true));
  module
    .userIsAuth()
    .then((res) => (res ? (window.location.href = "/") : null));
  forceAddEventSubmit();
}

/**
 * @description Función que se encarga de añadir el evento click al botón de regresar.
 *
 * @param {HTMLElement} el
 * @param {HTMLElement | null} customAnimEl
 */
function addBackEventListener(el, customAnimEl) {
  const callback = () => {
    if(window.location.href.includes("login")) window.location.href = "/";
    else if(window.history.length > 1) window.history.back();
    else window.location.href = "/";
  };
  addAnimationClose(el, false, callback, customAnimEl);
}

/**
 * @summary Función que se encarga de añadir una animación cuando cambias entre formularios.
 * 
 * @description La función esta principalmente orientada para que el parametro firstEl sea un elemento de tipo <a> por lo que existe el parametro continueDefault que se encarga de continuar con el comportamiento por defecto y el customAnimEl que se encarga de añadir la animación a un elemento personalizado ya que por defecto se añade la animación al elemento con la clase .spacer_form. Por último tenermos el parametro callback que se encarga de ejecutar una función personalizada cuando la animación ha terminado.
 *
 * @param {HTMLElement} firstEl
 * @param {boolean} continueDefault
 * @param {CallableFunction | null} callback
 * @param {HTMLElement | null} customAnimEl
 */
function addAnimationClose(firstEl, continueDefault, callback, customAnimEl) {
  const handleClick = (e) => {
    e.preventDefault();

    customAnimEl = 
      customAnimEl 
        ? customAnimEl
        : document.querySelector("form.spacer_form");

    customAnimEl.classList.toggle("close");

    const handleAnimationEnd = () => {
      if (continueDefault) {
        firstEl.removeEventListener("click", handleClick);
        firstEl.click();
      }
      if (typeof callback === "function") {
        callback();
      }
      e.target.removeEventListener("animationend", handleAnimationEnd);
    };

    customAnimEl.addEventListener("animationend", handleAnimationEnd);
    e.target.removeEventListener("click", handleClick);
  };
  firstEl.addEventListener("click", handleClick);
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
  const returnElement = document.querySelectorAll(".animation_arrow_container");

  returnElement.forEach((el) => {
    const animation = lottie.loadAnimation({
      container: el.children[0],
      renderer: "svg",
      loop: false,
      autoplay: false,
      path: "../assets/lotties/arrow-left.json",
    });
  
    el.addEventListener("mouseenter", () => {
      animation.play();
    });
    animation.addEventListener("complete", () => {
      animation.stop();
    });
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
  const res = await module.login(Object.fromEntries(credentials));
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

export {
  toggleSubmit,
  addBackLottieAnim,
  showError,
  addBackEventListener,
  addAnimationClose,
};
