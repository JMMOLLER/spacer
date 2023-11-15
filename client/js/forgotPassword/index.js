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
  addBackLottieAnim();
  btnBack.forEach((el) => addBackEventListener(el, el.parentElement));
  document
    .querySelectorAll("form.spacer_form")
    .forEach((form) => form.addEventListener("submit", handleFormSubmit));

  document
    .querySelector("#code")
    .addEventListener(
      "input",
      (e) => {
        e.target.value = e.target.value.toLocaleUpperCase();
        document.querySelector("#code").classList.remove("error")
      }
    );
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

  module.customFetch(res.url, res.requestOptions).then(async (res) => {
    toggleSubmit(
      inputValue,
      event.target.querySelector("input[type='submit']")
    );
    if ([200, 201, 404].includes(res.statusCode)) {
      nextStep.currentStep++;
    }else if(res.statusCode === 400){
      const form = document.querySelector("form:not(.hidden)");
      const inputs = form.querySelectorAll("input:not([type='submit'])");
      inputs.forEach((input) => {
        input.classList.add("error");
        const handleInput = () => {
          input.classList.remove("error");
          input.removeEventListener("input", handleInput);
        }
        input.addEventListener("input", handleInput);
      });
      alert(res.description);
    } else if(res.statusCode === 401){
      document.querySelector("#code").classList.add("error")
    } else {
      alert("Hubo un problema con nuestro servidor. Intente más tarde");
    }
  });
}

function handleEmailFormSubmit(event) {
  const url = module.API_URL.replace(
    "/api",
    `/cliente/reset-password`
  );

  const formData = new FormData(event.target);
  email = formData.get("email");

  const requestOptions = {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify({email}),
  };

  return { requestOptions, url };
}

function handleCodeFormSubmit(event) {

  const formData = new FormData(event.target);
  code = formData.get("code");

  if(!email && !code){
    alert("Ocurrio un error inesperado. Intente más tarde.");
  }

  const url = module.API_URL.replace(
    "/api",
    `/cliente/reset-password?consultCode=${formData.get("code")}`
  );

  const requestOptions = {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify({email}),
  };

  return { requestOptions, url };
}

function handlePasswordFormSubmit(event) {
  const url = module.API_URL.replace(
    "/api",
    `/cliente/reset-password/${code}`
  );

  const formData = Object.fromEntries(new FormData(event.target));
  formData.email = email;

  const requestOptions = {
    headers: { "Content-Type": "application/json" },
    method: "PUT",
    body: JSON.stringify(formData),
  };

  return { requestOptions, url };
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

export { nextStep };