import { loadHeaderLottieAnimation } from "../home/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();

export default async function init() {
  loadHeaderLottieAnimation();
  addLoaderAnimation();
  // const products = module.getAllProducts()
  products.forEach((product) => renderCardProduct(product));
}

function addLoaderAnimation(){
  const lot = lottie.loadAnimation({
    container: document.getElementById("loader"), // the dom element that will contain the animation
    renderer: "svg",
    loop: true,
    autoplay: true,
    path: "/assets/lotties/loader-products.json", // the path to the animation json
  });

  lot.addEventListener("DOMLoaded", () => {
    const g = document.querySelector("#loader g");
    console.log(g);
    g.style.tarnsform = "translate(-90px, -90px) scale(1.5)"
    console.log(g.style.tarnsform);
  });
}

function renderCardProduct(product) {
  const { marca, urlImg, description, price, id } = product;
  const template = createItemTemplate(marca, urlImg, description, price, id);
  const contenedorProductos = document.querySelector("#items-container");
  contenedorProductos.appendChild(template);
}

/**
 * Crea un template de elemento.
 *
 * @param {string} titulo - El título del elemento.
 * @param {string} src - La ruta de la imagen.
 * @param {string} info - La información del elemento.
 * @param {string} precio - El precio del elemento.
 * @param {number} id - El ID asociado al elemento.
 * @returns {HTMLElement} - El template de elemento creado.
 */
function createItemTemplate(titulo, src, info, precio, id) {
  const contenedorItems = createElementWithClass("div", "contenedor-items");
  const item = createElementWithClass("div", "item");
  const cabeceraCard = createElementWithClass("div", "cabecera-card");
  const tituloItem = createElementWithClass("span", "titulo-item");
  const imgItem = document.createElement("img");
  const medioCard = createElementWithClass("div", "medio-card");
  const infoItem = createElementWithClass("span", "info-item");
  const precioItem = createElementWithClass("span", "precio-item");
  const botonItem = createElementWithClass("button", "boton-item");

  imgItem.classList.add("img-item");
  imgItem.src = src;
  imgItem.alt = "";

  tituloItem.textContent = titulo;
  infoItem.textContent = info;
  precioItem.textContent = precio;
  botonItem.textContent = "Agregar al Carrito";
  botonItem.dataset.id = id;

  cabeceraCard.appendChild(tituloItem);
  cabeceraCard.appendChild(imgItem);

  medioCard.appendChild(infoItem);
  medioCard.appendChild(precioItem);

  item.appendChild(cabeceraCard);
  item.appendChild(medioCard);
  item.appendChild(botonItem);

  return item;
}

/**
 * Crea un elemento con una clase específica.
 *
 * @param {string} tagName - El nombre de la etiqueta del elemento.
 * @param {string} className - El nombre de la clase a añadir.
 * @returns {HTMLElement} - El elemento creado.
 */
function createElementWithClass(tagName, className) {
  const element = document.createElement(tagName);
  element.classList.add(className);
  return element;
}
