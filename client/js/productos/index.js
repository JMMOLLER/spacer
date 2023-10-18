import "../globals/types.js";
import { loadHeaderLottieAnimation, handleCheckAuth } from "../home/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();
let isAuth = false;

export default async function init() {
  loadHeaderLottieAnimation();
  isAuth = handleCheckAuth();
  const products = await module.getAllProducts()
  if(!products){
    toggleLoader();
    toggleError();
  }else{
    toggleLoader();
    products.forEach((product) => renderCardProduct(product));
  }

}

function toggleLoader() {
  document.querySelector("#loader").classList.toggle("hidden");
}

function toggleError() {
  document.querySelector("#error").classList.toggle("hidden");
}

function toggleLoadingSection() {
  document.querySelector("section.productos").classList.toggle("loading");
}

/**
 * 
 * @param {Product} product 
 */
function renderCardProduct(product) {
  const { marca, urlImg, description, price, id, categoryId } = product;
  const template = createItemTemplate(marca, urlImg, description, price, id, categoryId.name);
  const contenedorProductos = document.querySelector("#items-container");
  contenedorProductos.appendChild(template);
}

/**
 * Crea un template de elemento.
 *
 * @param {string} titulo - El título del elemento.
 * @param {string} src - La ruta de la imagen.
 * @param {string} info - La información del elemento.
 * @param {number} precio - El precio del elemento.
 * @param {number} id - El ID asociado al elemento.
 * @param {string} category - La categoría del elemento.
 * @returns {HTMLElement} - El template de elemento creado.
 */
function createItemTemplate(titulo, src, info, precio, id, category) {
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
  imgItem.addEventListener("error", () => {
    imgItem.src = "/assets/imgs/no-available-image.png"
  });

  tituloItem.textContent = titulo;
  infoItem.textContent = info;
  precioItem.textContent = "S/"+precio.toFixed(2);
  botonItem.textContent = "Agregar al Carrito";
  botonItem.dataset.id = id;
  botonItem.addEventListener("click", handleAddProduct.bind(null, id));

  cabeceraCard.appendChild(tituloItem);
  cabeceraCard.appendChild(imgItem);

  medioCard.appendChild(infoItem);
  medioCard.appendChild(precioItem);

  item.dataset.category = category;
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

/**
 * @description Este método se encarga de agregar un producto al carrito
 * 
 * @param {number} id 
 */
async function handleAddProduct(id){
  if(!isAuth){
    alert("Debes iniciar sesión para agregar productos al carrito");
  }else{
    toggleLoadingSection();
    const res = await module.addProductToCart(id);
    if(res){
      console.log("Agregado al carrito producto con ID: " + id);
    } else {
      alert("No se pudo agregar el producto al carrito");
    }
    toggleLoadingSection();
  }
}
