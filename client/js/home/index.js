import Global from "../globals/index.js";
const module = Global.getInstance();

const preventRedirect = () => {
  // agrega el evento click a todos los elementos del menu y evita que se redirija a otra pagina
  document
    .querySelectorAll("#menuCategorias > li a")
    .forEach((el) => el.addEventListener("click", (e) => {
      if(e.target.href === window.location.href){
        e.preventDefault();
      }
    }));
  document.querySelectorAll(".marcas > div > a").forEach((el) =>
    el.addEventListener("click", (e) => {
      e.preventDefault();
      document
        .querySelector(`${e.target.getAttribute("href")}`)
        .scrollIntoView({ behavior: "smooth" });
    })
  );
};

const loadHeaderLottieAnimation = () => {
  const cart = document.querySelector(".container_carrito");

  lottie.loadAnimation({
    container: document.getElementById("animation_wsp"), // the dom element that will contain the animation
    renderer: "svg",
    loop: true,
    autoplay: true,
    path: "/assets/lotties/whatsapp.json", // the path to the animation json
  });

  const lottie_cart = lottie.loadAnimation({
    container: document.getElementById("animation_cart"), // the dom element that will contain the animation
    renderer: "svg",
    loop: false,
    autoplay: false,
    path: "/assets/lotties/bag.json", // the path to the animation json
  });

  cart.addEventListener("mouseover", () => {
    lottie_cart.play();
  });

  lottie_cart.addEventListener("complete", () => {
    lottie_cart.stop();
  });
};

// controlador carrusel hero
function startCarrouselAnimation() {
  let counter = 0;
  let interval = setInterval(checkInput.bind({ counter }), 5000);
  const inputs = document.querySelectorAll("input[type=radio]");

  inputs.forEach((input) => {
    input.addEventListener("click", (e) => {
      counter = Number(e.target.id.split("radio")[1]) - 1;
      clearInterval(interval);
      checkInput.bind({ counter })();
      interval = setInterval(checkInput.bind({ counter }), 5000);
    });
  });
}

function checkInput() {
  this.counter++;
  document.getElementById("radio" + this.counter).checked = true;

  if (this.counter == 1) {
    document.querySelector(".carr.first").style.marginLeft = "0px";
  } else if (this.counter == 2) {
    document.querySelector(".carr.first").style.marginLeft = "-20%";
  } else if (this.counter == 3) {
    document.querySelector(".carr.first").style.marginLeft = "-40%";
  } else {
    document.querySelector(".carr.first").style.marginLeft = "-60%";
    this.counter = 0;
  }
}

/**
 * @description Verifica si el usuario esta logueado y si lo esta, cambia el href del boton perfil
 *
 * @returns {void}
 *
 */
const handleCheckAuth = async() => {
  const userIsAuth = await module.userIsAuth();
  if (userIsAuth) {
    const dropdown = document.querySelectorAll("#dropdown_options .link");
    dropdown[0].href = "/pages/perfil.html";
    dropdown[0].innerText = "Mi cuenta";

    dropdown[1].href = "/logout";
    dropdown[1].innerText = "Cerrar sesión";
    dropdown[1].addEventListener("click", handleClickLogout);
    document.querySelector("#carrito").href = "/pages/carrito.html";
  }
  return userIsAuth;
};

const handleClickLogout = (e) => {
  e.preventDefault();
  module.logout();
};

export default function init() {
  preventRedirect();
  handleCheckAuth();
  loadHeaderLottieAnimation();
  startCarrouselAnimation();
}

export { preventRedirect, loadHeaderLottieAnimation, handleCheckAuth };
