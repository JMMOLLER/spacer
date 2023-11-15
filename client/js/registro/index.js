import { inputValidator } from "../utils/index.js";
import {
  addBackLottieAnim,
  toggleSubmit,
  showError,
  addBackEventListener,
  addAnimationClose,
} from "../login/index.js";
import { inputHandler } from "../perfil/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();

export default function init() {
  const form = document.querySelector("form.spacer_form");
  const btnBack = document.querySelector(".animation_arrow_container");
  const link = document.querySelector(".link-login");
  form.addEventListener("submit", handleFormSubmit);
  addBackEventListener(btnBack);
  addAnimationClose(link, true);
  addBackLottieAnim();
  inputController();
  module
    .userIsAuth()
    .then((res) => (res ? (window.location.href = "/") : null));
}

const handleFormSubmit = (e) => {
  e.preventDefault();
  const form = new FormData(e.target);
  // form.forEach((value, key) => console.log(key + ": ", value));
  toggleSubmit("Registrando...");
  handleRegister(Object.fromEntries(form));
};

const handleRegister = async (credentials) => {
  showError(false);
  const res = await module.fetchAPI("/cliente", credentials, "POST");
  if (res?.statusCode === 201) {
    if (await module.login(credentials)) {
      window.location.href = "/";
    } else {
      alert(
        "Ha ocurrido un error al iniciar sesión, por favor inicia sesión manualmente."
      );
      toggleSubmit("Registrarse");
      showError(true);
    }
  } else {
    showError(true);
    toggleSubmit("Registrarse");
  }
  return res.statusCode === 201 ? true : false;
};

const inputController = () => {
  const form = document.querySelector("form.spacer_form");
  const inputsFormDatos = form.querySelectorAll("input:not([type='submit'])");
  const btnSubmitDatos = form.querySelector("input[type='submit']");

  const inputHandlerDatos = inputHandler.bind(
    null,
    inputsFormDatos,
    btnSubmitDatos,
    null
  );

  inputsFormDatos.forEach((input) => {
    input.addEventListener("input", (e) => {
      inputValidator(e.target);
      inputHandlerDatos(e);
    });
  });
};
