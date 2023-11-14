import {
  addBackLottieAnim,
  addBackEventListener,
  addAnimationClose,
  toggleSubmit,
} from "../login/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();

/**
 * @typedef {Object} Steps
 * @property {number} currentStep
 * @property {number} maxStep
 */
let steps = { currentStep: 0, maxStep: 3 };

let stepHandler = {
  set: (obj, prop, value) => handleSetter(obj, prop, value),
};

/**
 * @type {Steps}
 */
let nextStep = new Proxy(steps, stepHandler);

export default function init() {
  const btnBack = document.querySelectorAll(".animation_arrow_container");
  addBackLottieAnim();
  btnBack.forEach((el) => addBackEventListener(el, el.parentElement));
  document
    .querySelectorAll("form.spacer_form")
    .forEach((form) => form.addEventListener("submit", handleFormSubmit));
}

/**
 * @summary Este método se encarga de manejar el submit del formulario
 * 
 * @param {InputEvent} event 
 */
function handleFormSubmit(event) {

  /**
   * @description Falta crear funciones que manejen los distintos tipos
   * de comportamiento que tiene cada formulario y crear una funcion separada
   * que solo haga la peticion al servidor y realice el cambio de formulario
   */

  event.preventDefault();
  const inputValue = event.target.querySelector("input[type='submit']").value;
  toggleSubmit(inputValue, event.target.querySelector("input[type='submit']"));
  const form = event.target;
  const formData = new FormData(form);
  const data = Object.fromEntries(formData);
  const url = module.API_URL.replace("/api", "/cliente/reset-password");
  console.log(url);
  const requestOptions = {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify(data),
  };

  module.customFetch(url, requestOptions).then(async (res) => {
    toggleSubmit(
      inputValue,
      event.target.querySelector("input[type='submit']")
    );
    if ([200, 201, 404].includes(res.statusCode)) {
      nextStep.currentStep++;
    } else {
      alert("Hubo un problema con nuestro servidor. Intente más tarde");
    }
  });
}

/**
 * @summary Este método se encarga de manejar el setter del proxy
 *
 * @param {Steps} obj
 * @param {string} prop
 * @param {Number} value
 * @returns
 */
function handleSetter(obj, prop, value) {
  if (value > 3 || value < 0) value = 0;
  if (value === 0) {
    console.log("Mostrando formulario email");
    handleNextStep(".spacer_form.success", ".spacer_form.email.hidden");
  } else if (value === 1) {
    console.log("Ir a ingresar código");
    handleNextStep(".spacer_form.email", ".spacer_form.code.hidden");
  } else if (value === 2) {
    console.log("Ir a cambiar contraseña");
    handleNextStep(".spacer_form.code", ".spacer_form.password.hidden");
  } else if (value === 3) {
    console.log("Ir a contraseña cambiada");
    handleNextStep(".spacer_form.password", ".spacer_form.success.hidden");
  }
  obj[prop] = value;
  return true;
}

/**
 * @summary Este método se encarga de manejar la animación de el siguiente paso
 *
 * @param {string} currentCSSSelector
 * @param {string} nextCSSSelector
 */
function handleNextStep(currentCSSSelector, nextCSSSelector) {
  const element = document.querySelector(currentCSSSelector);
  const callback = () => {
    const newElement = document.querySelector(`${nextCSSSelector}.hidden`);
    newElement.classList.toggle("hidden");
    element.classList.toggle("hidden");
    element.classList.toggle("close");
  };
  addAnimationClose(element, false, callback, element);
  element.click();
}

export { nextStep };