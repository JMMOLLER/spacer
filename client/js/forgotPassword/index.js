import {
  addBackLottieAnim,
  addBackEventListener,
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
  set: function (obj, prop, value) {
    if((value > 3) || (value < 0)) {
      console.log("No se puede ir a esa página");
      return false;
    }
    else if(value === 0) {
      console.log("Mostrando formulario email");
      return false;
    }
    else if(value === 1) {
      console.log("Ir a ingresar código");
    }
    else if(value === 2) {
      console.log("Ir a cambiar contraseña");
    }
    else if(value === 3) {
      console.log("Ir a contraseña cambiada");
    }
    obj[prop] = value;
    return true;
  },
};

/**
 * @type {Steps}
 */
let numeroProxy = new Proxy(steps, stepHandler);


export default function init() {
  const btnBack = document.querySelector(".animation_arrow_container");
  addBackLottieAnim();
  addBackEventListener(btnBack);
  document
    .querySelector("form.spacer_form")
    .addEventListener("submit", handleFormSubmit);
}

function handleFormSubmit(event) {
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
    if ([200, 404].includes(res.statusCode)) {
      numeroProxy.currentStep++;
    } else {
      alert("Hubo un problema con nuestro servidor. Intente más tarde");
    }
  });
}
