const inputController = () => {
  const form = document.querySelectorAll("form.spacer_form");
  const inputsFormDatos = form[0].querySelectorAll(
    "input:not([type='submit'])"
  );
  const inputsFormSeguridad = form[1].querySelectorAll(
    "input[type='password']"
  );
  const btnSubmitDatos = form[0].querySelector("input[type='submit']");
  const btnSubmitSeguridad = form[1].querySelector("input[type='submit']");
  const imgInput = document.querySelector("#new-image");

  const inputHandlerDatos = inputHandler.bind(
    null,
    inputsFormDatos,
    btnSubmitDatos,
    imgInput
  );

  const inputHandlerSeguridad = inputHandler.bind(
    null,
    inputsFormSeguridad,
    btnSubmitSeguridad,
    null
  );

  inputsFormDatos.forEach((input) => {
    input.addEventListener("input", inputHandlerDatos);
    imgInput.addEventListener("change", inputHandlerDatos);
  });

  inputsFormSeguridad.forEach((input) => {
    input.addEventListener("input", inputHandlerSeguridad);
  });
};

/**
 * @description Función que se encarga de habilitar o deshabilitar el botón de submit
 *
 * @param {NodeListOf} inputs
 * @param {HTMLButtonElement} submitButton
 * @param {HTMLInputElement} imgInput
 */
const inputHandler = (inputs, submitButton, imgInput) => {
  let allInputsEmpty = true;

  const inputsPassword = [];
  inputs.forEach((input) => {
    if (input.type === "password") {
      inputsPassword.push(input);
    }
  });

  if (inputsPassword.length > 0) {
    inputsPassword.forEach((input) => {
      if (input.value.trim() !== "") {
        input.classList.add("filled");
      } else {
        input.classList.remove("filled");
      }
    });

    if (
      inputsPassword[0].value.trim().length > 6 &&
      inputsPassword[1].value.trim().length > 6
    ) {
      allInputsEmpty = false;
    }
  } else {
    inputs.forEach((input) => {
      if (input.value.trim() !== "") {
        allInputsEmpty = false;
        input.classList.add("filled");
      } else {
        input.classList.remove("filled");
      }
    });

    if (imgInput != null && imgInput.value.trim() !== "") {
      allInputsEmpty = false;
    }
  }

  if (allInputsEmpty) {
    submitButton.disabled = true;
  } else {
    submitButton.disabled = false;
  }
};

const fillUserInfo = async () => {
  const img_profile = document.querySelector(".perfil__img");
  const fullName = document.querySelector(
    ".content__datos--nombre > .content__datos--content"
  );
  const email = document.querySelector(
    ".content__datos--correo > .content__datos--content"
  );
  const address = document.querySelector(
    ".content__datos--direccion > .content__datos--content"
  );
  const username = document.querySelector(
    ".content__datos--usuario > .content__datos--content"
  );

  const userInfo = await getUserInfo();

  img_profile.src = userInfo.url_img;
  fullName.textContent = `${userInfo.firstName} ${userInfo.lastName}`;
  email.textContent = userInfo.email;
  address.textContent = userInfo.address || "Sin dirección";
  username.textContent = userInfo.username;
};

const getUserInfo = async () => {
  const module = await import("../globals/index.js");
  const userInfo = await module.getUserInfo();

  if (userInfo.statusCode > 400 && userInfo.statusCode < 500) {
    alert("Tu sesión a expirado, por favor inicia sesión nuevamente.");
    window.location.href = "/pages/login.html";
  }
  // console.log(userInfo);

  return userInfo.response;
};

const filterFormData = (formData) => {
  const filteredEntries = [...formData.entries()].filter(
    ([key, value]) => value !== ""
  );

  const filteredFormData = new FormData();

  filteredEntries.forEach(([key, value]) => {
    filteredFormData.append(key, value);
  });

  return filteredFormData;
};

/**
 * @description Función que se encarga de enviar los datos del formulario al servidor
 * 
 * @param {SubmitEvent} e 
 * @returns {void}
 */
const handleFormSubmit = async (e) => {
  e.preventDefault();

  const toggleSubmit = await import("../login/index.js").then(
    (module) => module.toggleSubmit
  );

  const inputSubmitValue = "Actualizar Datos";
  const btnSubmit = e.target.querySelector("input[type='submit']");

  toggleSubmit(inputSubmitValue, btnSubmit);

  const module = await import("../globals/index.js");
  const fetchUpdate = module.customFetch;

  const form = new FormData(e.target);

  if(form.has("address")){
    form.append("img", document.querySelector("#new-image").files[0] || "");
  }else{
    if(form.get("password") !== form.get("new-password")){
      alert("Las contraseñas no coinciden.");
      toggleSubmit(inputSubmitValue, btnSubmit);
      e.target.querySelector("input[type='submit']").disabled = true;
      return;
    }
  }

  const formData = filterFormData(form);

  // formData.forEach((value, key) => {
  //   console.log(key + ": ", value);
  // });
  // return;

  const myHeaders = new Headers();
  myHeaders.append(
    "Authorization",
    `Bearer ${sessionStorage.getItem("token")}`
  );

  const requestOptions = {
    method: "PUT",
    headers: myHeaders,
    body: formData,
    redirect: "follow",
  };

  const updateResponse = await fetchUpdate("/cliente", requestOptions);

  toggleSubmit(inputSubmitValue, btnSubmit);

  console.log(updateResponse);
  if (updateResponse.statusCode === 401) {
    alert("Tu sesión a expirado, por favor inicia sesión nuevamente.");
    window.location.href = "/pages/login.html";
  } else if (updateResponse.statusCode === 200) {
    const res = confirm(
      "Tu información se ha actualizado correctamente.\n¿Deseas ver los datos actualizados?"
    );
    if (res) {
      window.location.reload();
    }
  } else {
    alert(
      updateResponse.description || "Ha ocurrido un error, intenta nuevamente."
    );
    return;
  }
};

const setHandlersOnForms = () => {
  const form = document.querySelectorAll("form.spacer_form");

  form.forEach((f) => {
    f.addEventListener("submit", handleFormSubmit);
  });
};

const handleImageError = (e) => {
  e.target.src = "https://spacer-ecommerce.vercel.app/assets/imgs/user.webp";
  e.target.style.filter = "blur(2px)";
};

const handleNavClick = (e) => {
  const main = document.querySelector(".perfil__main");
  main.dataset.tab = e.target.id;

  updateNavLine();
};

const updateNavLine = () => {
  const id = document.querySelector(`.perfil__main`).dataset.tab;
  const line = document.querySelector("#selected_section");
  const tab = document.querySelector(`#${id}`);
  line.style.left = `${tab.offsetLeft}px`;
  line.style.width = `${tab.offsetWidth}px`;
};

window.addEventListener("resize", updateNavLine);

export default async function init() {
  const module = await import("../home/index.js");
  const img = document.querySelector(".perfil__img");
  img.onerror = handleImageError;
  module.preventRedirect();
  module.loadHeaderLottieAnimation();
  module.handleCheckAuth();
  inputController();
  fillUserInfo();
  setHandlersOnForms();

  document
    .querySelector("#menu_secciones")
    .childNodes.forEach((node) =>
      node.nodeName === "LI"
        ? node.addEventListener("click", handleNavClick)
        : null
    );

  updateNavLine();
}
