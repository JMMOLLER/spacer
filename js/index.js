const cart = document.querySelector(".container_carrito");
lottie.loadAnimation({
  container: document.getElementById("animation_wsp"), // the dom element that will contain the animation
  renderer: "svg",
  loop: true,
  autoplay: true,
  path: "./assets/lotties/whatsapp.json", // the path to the animation json
});
const lottie_cart = lottie.loadAnimation({
  container: document.getElementById("animation_cart"), // the dom element that will contain the animation
  renderer: "svg",
  loop: false,
  autoplay: false,
  path: "./assets/lotties/bag.json", // the path to the animation json
});

cart.addEventListener("mouseover", () => {
  lottie_cart.play();
  lottie_cart.addEventListener("complete", () => {
    lottie_cart.stop();
  });
});
document.querySelector("#popup_cart").addEventListener("click", (e) => e.stopPropagation());
cart.addEventListener("click", () => document.querySelector("#popup_cart").classList.toggle("active"));

// agrega el evento click a todos los elementos del menu y evita que se redirija a otra pagina
document.querySelectorAll("#menuCategorias > li").forEach((el) => el.addEventListener("click", (e) => e.preventDefault()))