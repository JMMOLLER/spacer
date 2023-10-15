const inputController = () => {
  const form = document.querySelector("form.spacer_form");
  const inputs = form.querySelectorAll("input:not([type='submit'])");
  const submitButton = form.querySelector("input[type='submit']");
  const imgInput = document.querySelector("#new-image");

  const inputHandlerEvent = inputHandler.bind(null, inputs, submitButton, imgInput);

  inputs.forEach((input) => {
    input.addEventListener("input", inputHandlerEvent);
    imgInput.addEventListener("change", inputHandlerEvent);
  });
};

const inputHandler = (inputs, submitButton, imgInput) => {
  let allInputsEmpty = true;

  inputs.forEach((input) => {
    if (input.value.trim() !== "") {
      allInputsEmpty = false;
      input.classList.add("filled");
    } else {
      input.classList.remove("filled");
    }
  });
  if(imgInput.value.trim() !== "") {
    allInputsEmpty = false;
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

const updateUserInfo = async (e) => {
  e.preventDefault();

  const toggleSubmit = await import("../login/index.js").then(
    (module) => module.toggleSubmit
  );

  const inputSubmitValue = "Actualizar Datos";

  toggleSubmit(inputSubmitValue);

  const module = await import("../globals/index.js");
  const fetchUpdate = module.customFetch;

  const form = new FormData(e.target);
  form.append("img", document.querySelector("#new-image").files[0] || "");
  const formData = filterFormData(form);

  // formData.forEach((value, key) => {
  //   console.log(key, value);
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

  toggleSubmit(inputSubmitValue);

  console.log(updateResponse);
  if (updateResponse.statusCode === 401) {
    alert("Tu sesión a expirado, por favor inicia sesión nuevamente.");
    window.location.href = "/pages/login.html";
  } else if(updateResponse.statusCode === 200) {
    const res = confirm(
      "Tu información se ha actualizado correctamente.\n¿Deseas ver los datos actualizados?"
    );
    if (res) {
      window.location.reload();
    }
  }else{
    alert(updateResponse.description || "Ha ocurrido un error, intenta nuevamente.");
    return;
  }
};

const handleFormSubmit = () => {
  const form = document.querySelector("form.spacer_form");

  form.addEventListener("submit", updateUserInfo);
};

const handleImageError = (e) => {
  e.target.src = "https://spacer-ecommerce.vercel.app/assets/imgs/user.webp";
  e.target.style.filter = "blur(2px)";
}

export default async function init() {
  const module = await import("../home/index.js");
  const img = document.querySelector(".perfil__img")
  img.onerror = handleImageError
  module.preventRedirect();
  module.loadHeaderLottieAnimation();
  module.handleCheckAuth();
  inputController();
  fillUserInfo();
  handleFormSubmit();
}
