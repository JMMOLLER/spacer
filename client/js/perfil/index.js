const inputController = () => {
  const form = document.querySelector("form.spacer_form");
  const inputs = form.querySelectorAll("input:not([type='submit'])");

  inputs.forEach((input) => {
    input.addEventListener("change", (e) => {
      if (e.target.value.length > 0) {
        e.target.classList.add("filled");
        form.querySelector("input[type='submit']").disabled = false; // esto es temporal
      } else {
        e.target.classList.remove("filled");
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

const updateUserInfo = async (e) => {
  e.preventDefault();

  const module = await import("../globals/index.js");
  const fetchUpdate = module.fetchAPI;

  const formData = new FormData(e.target);

  const userInfoUpdated = Object.fromEntries(formData.entries());

  Object.keys(userInfoUpdated).forEach((key) => {
    userInfoUpdated[key] = userInfoUpdated[key] || null;
  });

  console.log(userInfoUpdated);

  const updateResponse = await fetchUpdate("/cliente", userInfoUpdated, "PUT"); // verificar porque no actualiza los datos enviados

  if (updateResponse.statusCode > 400 && updateResponse.statusCode < 500) {
    alert("Tu sesión a expirado, por favor inicia sesión nuevamente.");
    window.location.href = "/pages/login.html";
  }
  console.log(updateResponse);

  return fetchUpdate.response;
};

const handleFormSubmit = () => {
  const form = document.querySelector("form.spacer_form");

  form.addEventListener("submit", updateUserInfo);
}

export default async function init() {
  const module = await import("../home/index.js");
  module.preventRedirect();
  23;
  module.loadHeaderLottieAnimation();
  inputController();
  fillUserInfo();
  handleFormSubmit();
}
