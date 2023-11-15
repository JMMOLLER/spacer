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
 * @property {Array<Function>} callbacks
 */
let steps = {
  currentStep: 0,
  maxStep: 3,
  callbacks: [
    handleEmailFormSubmit,
    handleCodeFormSubmit,
    handlePasswordFormSubmit,
  ],
};

/**
 * @type {string | undefined}
 */
let email;

/**
 * @type {string | undefined}
 */
let code;

let stepHandler = {
  set: (obj, prop, value) => handleSetter(obj, prop, value),
};

/**
 * @type {Steps}
 */
let nextStep = new Proxy(steps, stepHandler);

export default function init() {
  const btnBack = document.querySelectorAll(".animation_arrow_container");

  addBackLottieAnim(); // Añade la animación al botón de regresar

  btnBack.forEach((el) => addBackEventListener(el, el.parentElement)); // Añade el evento click al botón de regresar

  document
    .querySelectorAll("form.spacer_form")
    .forEach((form) => form.addEventListener("submit", handleFormSubmit)); // Añade el evento submit a los formularios

  document
    .querySelector("#code")
    .addEventListener(
      "input",
      (e) => {
        e.target.value = e.target.value.toLocaleUpperCase();
        document.querySelector("#code").classList.remove("error")
      }
    ); // Añade el evento input al input del formulario código
}

/**
 * @summary Este método se encarga de manejar el submit del formulario
 *
 * @param {InputEvent} event
 */
function handleFormSubmit(event) {

  event.preventDefault();

  const inputValue = event.target.querySelector("input[type='submit']").value;

  toggleSubmit(inputValue, event.target.querySelector("input[type='submit']"));

  let res = nextStep.callbacks[nextStep.currentStep](event);

  module.customFetch(res.url, res.requestOptions).then((res) => handleFetchResponse(res, inputValue, event.target));
}

/**
 * @summary Este método se encarga de manejar la respuesta de la API
 * 
 * @param {API_RESPONSE} res 
 * @param {string} inputValue 
 * @param {HTMLElement} elem 
 */
function handleFetchResponse(res, inputValue, elem) {
  toggleSubmit(inputValue, elem.querySelector("input[type='submit']"));

  if ([200, 201].includes(res.statusCode)) {
    nextStep.currentStep++;
  } else if (res.statusCode === 400) {
    handleValidationErrors(res);
  } else if (res.statusCode === 401) {
    document.querySelector("#code").classList.add("error");
  } else {
    handleServerError();
  }
}

/**
 * @summary Este método se encarga de manejar los errores de validación
 * 
 * @param {API_RESPONSE} res
 */
function handleValidationErrors(res) {
  const form = document.querySelector("form:not(.hidden)");
  const inputs = form.querySelectorAll("input:not([type='submit'])");

  inputs.forEach((input) => {
    input.classList.add("error");

    const handleInput = () => {
      input.classList.remove("error");
      input.removeEventListener("input", handleInput);
    };

    input.addEventListener("input", handleInput);
  });

  alert("Validación fallida. Por favor, corrija los campos marcados.\n\nError: " + res.description);
}

/**
 * @summary Este método se encarga de manejar el error de servidor
 */
function handleServerError() {
  alert("Hubo un problema con nuestro servidor. Intente más tarde.");
}

/**
 * @summary Este método se encarga de crear los `requestOptions` para la función `customFetch`
 * 
 * @param {string} urlSuffix 
 * @param {RequestOptions["method"]} method 
 * @param {object} bodyData 
 * @returns {{requestOptions: RequestOptions, url: string}}
 */
function createRequestOptions(urlSuffix, method, bodyData = {}) {
  const url = module.API_URL.replace("/api", "/cliente/reset-password"+urlSuffix);

  const requestOptions = {
    headers: { "Content-Type": "application/json" },
    method: method,
    body: JSON.stringify(bodyData),
  };

  return { requestOptions, url };
}

/**
 * @summary Este método se encarga de manejar el submit del formulario de email
 * 
 * @param {InputEvent} event 
 * @returns {{requestOptions: RequestOptions, url: string}} Retorna el resultado de la función `createRequestOptions`
 */
function handleEmailFormSubmit(event) {
  const formData = new FormData(event.target);
  email = formData.get("email");

  return createRequestOptions("", "POST", { email });
}

/**
 * @summary Este método se encarga de manejar el submit del formulario de código
 * 
 * @param {InputEvent} event 
 * @returns {{requestOptions: RequestOptions, url: string}} Retorna el resultado de la función `createRequestOptions`
 */
function handleCodeFormSubmit(event) {
  const formData = new FormData(event.target);
  code = formData.get("code");

  if (!email || !code) {
    alert("Ocurrio un error inesperado. Intente más tarde.");
  }

  return createRequestOptions(`?consultCode=${code}`, "POST", { email });
}

/**
 * @summary Este método se encarga de manejar el submit del formulario de contraseña
 * 
 * @param {InputEvent} event 
 * @returns {{requestOptions: RequestOptions, url: string}} Retorna el resultado de la función `createRequestOptions`
 */
function handlePasswordFormSubmit(event) {
  const formData = Object.fromEntries(new FormData(event.target));
  formData.email = email;

  if (!email || !code) {
    alert("Ocurrio un error inesperado. Intente más tarde.");
  }

  return createRequestOptions(`/${code}`, "PUT", formData);
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
    handleChangeStep(".spacer_form.email.hidden");
  } else if (value === 1) {
    handleChangeStep(".spacer_form.code.hidden");
  } else if (value === 2) {
    handleChangeStep(".spacer_form.password.hidden");
  } else if (value === 3) {
    handleChangeStep(".spacer_form.success.hidden");
  }
  obj[prop] = value;
  return true;
}

/**
 * @summary Este método se encarga de manejar la animación cuando se cambia el valor de `currentStep` del objeto `steps`
 *
 * @param {string} CSSSelector
 */
function handleChangeStep(CSSSelector) {
  const currentForm = document.querySelector(".spacer_form:not(.hidden)");
  const callback = () => {
    const newElement = document.querySelector(CSSSelector);
    newElement.classList.toggle("hidden");
    currentForm.classList.toggle("hidden");
    currentForm.classList.toggle("close");
  };
  addAnimationClose(currentForm, false, callback, currentForm);
  currentForm.click();
}
