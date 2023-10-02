import { userIsAuth } from "../globals/index.js";

const preventRedirect = () => {
  // agrega el evento click a todos los elementos del menu y evita que se redirija a otra pagina
  document
    .querySelectorAll("#menuCategorias > li")
    .forEach((el) => el.addEventListener("click", (e) => e.preventDefault()));
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

const handleCheckAuth = () => {
  import("../globals/index.js")
    .then(async(module) => {
      const userIsAuth = await module.userIsAuth();
      if (userIsAuth) {
        document.querySelector("#perfil").href = "/pages/perfil.html";
      }
    })
    .catch((err) => {
      console.error(err);
    });
};

export default function init() {
  userIsAuth().then((res) => res ? document.querySelector("#perfil").href = "/pages/perfil.html": null);
  preventRedirect();
  handleCheckAuth();
  loadHeaderLottieAnimation();
  startCarrouselAnimation();
}

export { preventRedirect, loadHeaderLottieAnimation };