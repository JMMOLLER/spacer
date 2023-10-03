const inputController = () => {
  const form = document.querySelector("form.spacer_form");
  const inputs = form.querySelectorAll("input:not([type='submit'])");
  const submitButton = form.querySelector("input[type='submit']");

  inputs.forEach((input) => {
    input.addEventListener("input", () => {
      let allInputsEmpty = true;

      inputs.forEach((input) => {
        if (input.value.trim() !== "") {
          allInputsEmpty = false;
          input.classList.add("filled");
        }else{
          input.classList.remove("filled");
        }
      });

      if (allInputsEmpty) {
        submitButton.disabled = true;
      } else {
        submitButton.disabled = false;
      }
    });
  });
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

  const toggleSubmit = await import("../login/index.js").then(module => module.toggleSubmit)

  const inputSubmitValue = "Actualizar Datos"

  toggleSubmit(inputSubmitValue);

  const module = await import("../globals/index.js");
  const fetchUpdate = module.customFetch;

  const formData = filterFormData(new FormData(e.target));

  // formData.forEach((value, key) => {
  //   console.log(key, value);
  // });

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

  if (updateResponse.statusCode > 400 && updateResponse.statusCode < 500) {
    alert("Tu sesión a expirado, por favor inicia sesión nuevamente.");
    window.location.href = "/pages/login.html";
  }
  console.log(updateResponse);
  const res = confirm("Tu información se ha actualizado correctamente.\n¿Deseas ver los datos actualizados?");
  if (res) {
    window.location.reload();
  }
};

const handleFormSubmit = () => {
  const form = document.querySelector("form.spacer_form");

  form.addEventListener("submit", updateUserInfo);
};

export default async function init() {
  const module = await import("../home/index.js");
  module.preventRedirect();
  module.loadHeaderLottieAnimation();
  inputController();
  fillUserInfo();
  handleFormSubmit();
}
