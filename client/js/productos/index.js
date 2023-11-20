import "../globals/types.js";
import { loadHeaderLottieAnimation, handleCheckAuth } from "../home/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();
let isAuth = false;
/**
 * @summary Almacena los productos
 * @type {Product[] | null}
 */
let products;
/**
 * @summary Almacena los productos filtrados
 * @type {Product[] | null}
 * */
let filtered;

export default async function init() {
  loadHeaderLottieAnimation();
  addFilterCategoryFormListener();
  addFilterPriceFormListener();
  addEventInputChangeListener();
  preventDefaultInNav();
  addPopStateListener();

  isAuth = await handleCheckAuth();
  products = await module.getAllProducts();

  if(!products){
    toggleLoader();
    toggleError();
  }else{
    toggleLoader();
    filterProducts();
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
 * @summary Evita que se redirija a la página de la categoría
 */
function preventDefaultInNav() {
  document.querySelectorAll(".categoria-link").forEach((element) => {
    element.addEventListener("click", redirectHandler);
  });
}

/**
 * 
 * @param {InputEvent} e 
 */
function redirectHandler(e) {
  e.preventDefault();
  const link = e.currentTarget.href;
  window.history.pushState({}, '', link);
  filterProducts();
}

/**
 * @summary Obtiene el parámetro de búsqueda de la URL
 * 
 * @returns {SearchParam[] | null} - El parámetro de búsqueda de la URL
 */
function getSearhParams(){
  const params = [];
  let search = window.location.search.replace("?", "");

  if(search.length === 0) return null;

  search = search.split("&");
  
  search.forEach(
    (param) => params.push({
      key: decodeURIComponent(param.split("=")[0]),
      value: decodeURIComponent(param.split("=")[1])
    })
  );

  return params;
}

/**
 * @summary Filtra los productos por categoría
 */
function filterProducts(){
  cleanProductsRendered();
  const searchParams = getSearhParams();

  filterProductsByCategory(searchParams); // Este método debe ser el primero en ejecutarse
  filterProductsByPriceRange(searchParams);
  filterProductsByBrand(searchParams);

  renderFilteredProducts();
}

/**
 * @summary Renderiza los productos filtrados en el DOM
 */
function renderFilteredProducts(){
  filtered?.forEach((product) => renderCardProduct(product));
}

/**
 * @summary Filtra los productos por categoría
 * 
 * @param {SearchParam[]} searchParams
 */
function filterProductsByCategory(searchParams){
  const categoryParam = searchParams?.find((param) => (param.key).toLowerCase() === "categoria");
  if(categoryParam) {
    filtered = products?.filter(
      (product) => (product.categoryId.name).toUpperCase() === (categoryParam.value).toUpperCase()
    )
  }else{
    filtered = products;
  }
}

/**
 * @summary Filtra los productos por rango de precio
 * 
 * @param {SearchParam[]} searchParams 
 */
function filterProductsByPriceRange(searchParams){
  const minParam = searchParams?.find((param) => (param.key).toLowerCase() === "min");
  const maxParam = searchParams?.find((param) => (param.key).toLowerCase() === "max");

  if(minParam){
    filtered = filtered?.filter((product) => product.price >= Number(minParam.value));
  }
  if(maxParam){
    filtered = filtered?.filter((product) => product.price <= Number(maxParam.value));
  }
}

/**
 * 
 * @param {SearchParam[]} searchParams 
 */
function filterProductsByBrand(searchParams){
  const brandParam = searchParams?.find((param) => (param.key).toLowerCase() === "marca");
  if(brandParam){
    filtered = filtered?.filter((product) => (product.marca).toUpperCase() === (brandParam.value).toUpperCase());
  }
}

/**
 * @summary Limpia los productos renderizados en el DOM
 */
function cleanProductsRendered(){
  document.querySelectorAll(".item")?.forEach((item) => item.remove());
}

/**
 * @summary Agrega un listener al evento popstate del navegador
 */
function addPopStateListener(){
  window.addEventListener("popstate", filterProducts);
}

/**
 * @summary Agrega un listener al evento submit del formulario de filtro de categoría
 */
function addFilterCategoryFormListener(){
  document.querySelector("#filtro-categorias").addEventListener("submit", filterCategoryFormHandler);
}

/**
 * @summary Agrega un listener al evento submit del formulario de filtro de precio
 */
function addFilterPriceFormListener(){
  document.querySelector("#filtro-precio").addEventListener("submit", filterPriceFormHandler);
}

/**
 * @summary Agrega un listener al evento input de los inputs de precio
 */
function addEventInputChangeListener(){
  document.querySelector("#price-min").addEventListener("input", (e) => {
    const label = document.querySelector("#current-min-value");
    const max = Number(document.querySelector("#price-max").value);
    const value = Number(e.target.value);

    if(value < 100) {
      e.target.value = 100;
      label.textContent = "-";
    }else if(max < (value)+200){
      e.target.value = (value)-200;
      label.textContent = "-";
    }else{
      label.textContent = e.target.value;
    }
  });


  document.querySelector("#price-max").addEventListener("input", (e) => {
    const label = document.querySelector("#current-max-value");
    const min = Number(document.querySelector("#price-min").value);
    const value = Number(e.target.value);

    if(value > 6600) {
      e.target.value = 6599;
      label.textContent = "-";
    }else if(min > (value)-200){
      e.target.value = (value)+200;
      label.textContent = "-";
    }else{
      label.textContent = value;
    }
  });
}

/**
 * 
 * @param {SubmitEvent} e 
 */
function filterCategoryFormHandler(e){
  e.preventDefault();
  const selected = e.target.querySelector("input:checked");
  let url = window.location;
  url = url.origin + url.pathname + `?${selected?.value || ""}`;
  window.history.pushState({}, '', url);
  filterProducts();
}

/**
 * @summary Filtra los productos por precio
 * 
 * @param {SubmitEvent} e 
 */
function filterPriceFormHandler(e){
  e.preventDefault();
  const ranges = e.target.querySelectorAll("span");
  const minRange = ranges[0];
  const maxRange = ranges[1];

  let url = new URL(window.location);

  let search = url.search;
  let bindParam = "?";

  if(search.includes("?")){
    bindParam = "&";
  }
  if(search.includes("min") || search.includes("max")){
    url.search = search.replace(/&?min=\d+&?/g, "").replace(/&?max=\d+&?/g, "");
  }

  
  if(minRange.textContent === "-"){
    url.search += `${bindParam}max=${maxRange.textContent}`;
  }else if(maxRange.textContent === "-"){
    url.search += `${bindParam}min=${minRange.textContent}`;
  }else{
    url.search += `${bindParam}min=${minRange.textContent}&max=${maxRange.textContent}`;
  }
  
  if(minRange.textContent === "-" && maxRange.textContent === "-") url = window.location.origin + window.location.pathname;

  window.history.pushState({}, '', url);
  filterProducts();
}

/**
 * @summary Renderiza un producto en el DOM
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
 * @summary Crea un template de elemento.
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
 * @summary Crea un elemento con una clase específica.
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
 * @summary Este método se encarga de agregar un producto al carrito
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
      const cartCant = document.querySelector("#cart_cant").innerHTML;
      document.querySelector("#cart_cant").innerHTML = Number(cartCant) + 1;
    } else {
      alert("No se pudo agregar el producto al carrito");
    }
    toggleLoadingSection();
  }
}
