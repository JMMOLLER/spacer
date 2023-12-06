import Global from "../globals/index.js";
import { Modal } from "../globals/modal.js";
import { handleCheckAuth } from "../home/index.js";
import ProductEditorTemplate from "../globals/ProductEditorTemplate.js";
import { handleNavClick, updateNavLine } from "../perfil/index.js";
import {
  toggleLoader,
  toggleError,
  renderCardProduct,
} from "../productos/index.js";
const module = Global.getInstance();
const modal = new Modal();

/**
 * @summary Objeto indicador si se debe actualizar la lista de productos.
 */
const listener = {
  productUpdated: false,
  categoryUpdated: false,
}

/**
 * @summary Proxy que observa los cambios en el objeto listener.
 * @type {ProxyHandler<typeof listener>}
 */
const observer = new Proxy(listener, {
  set: function (target, prop, value) {
    target[prop] = value;
    if (prop === "productUpdated") {
      console.log("productUpdated");
      document.querySelectorAll(".item").forEach((item) => item.remove());
      toggleLoader();
      reloadProducts();
    } else if (prop === "categoryUpdated") {
      console.log("categoryUpdated");
      reloadCategories()
    }
    return true;
  },
});

export default async function () {
  await validateAuth();


  document
    .querySelector("#menu_secciones")
    .childNodes.forEach((node) =>
      node.nodeName === "LI"
        ? node.addEventListener("click", handleNavClick)
        : null
    );

    reloadCategories()

  updateNavLine();

  reloadProducts();

  document.querySelector("#add-categoria").addEventListener("submit", async(e) => {
    e.preventDefault();
    const form = e.target;
    const data = new FormData(form);
    const body = Object.fromEntries(data.entries());
    const promise = await addCategory(body);
    const res = await modal.waitPromise(promise);
    if (res.statusCode === 201) {
      form.querySelector("input").value = "";
      observer.categoryUpdated = true;
    } else {
      alert(res.description);
      console.error(res)
    }
  });
}

/**
 * @summary Valida si el usuario esta autenticado y es administrador.
 */
async function validateAuth() {
  const isLogged = await handleCheckAuth();
  if (!isLogged) {
    window.location.href = "/pages/login.html";
    return;
  }if(!module.userInfo.isAdmin){
    window.location.href = "/";
    return;
  }
}

/**
 * @summary Recarga la lista de productos.
 */
async function reloadProducts() {
  const products = await module.getAllProducts();
  toggleLoader();

  if (!products) {
    toggleError();
  } else {
    products.forEach((product) => {
      renderCardProduct(product, {
        textContent: "Editar producto",
        controller: handleClick,
      });
    });

    document.querySelectorAll(".item").forEach((item) => {
      const span = document.createElement("span");
      span.classList.add("fa");
      span.classList.add("fa-close");
      span.addEventListener("click", handleDeleteProduct);
      item.appendChild(span);
    });
  }
}

async function reloadCategories() {
  const categories = await getCategories();

  if (!categories) {
    alert("No se han podido cargar las categorías");
  } else {
    const container = document.querySelector(".section_categorias .tbl_categorias");
    container.innerHTML = "";
    const table = await addTable(categories);
    container.appendChild(table);
  }

}

/**
 *
 * @param {InputEvent} e
 */
async function handleClick(e) {
  const btn = e.target;
  /**
   * @type {Promise<Product>}
   */
  let product;
  if(btn.id === "add-product"){
    product = {
      id: null,
      name: null,
      description: null,
      price: null,
      stock: null,
      image: null,
      marca: null,
    }
  } else product = getProductById(btn.dataset.id);
  const form = new ProductEditorTemplate(product, observer);
  modal.open(form.getTemplate());
}

/**
 * 
 * @param {Event} e 
 */
async function handleDeleteProduct(e) {
  const parent = e.target.parentElement;
  const btn = parent.querySelector("button");
  const id = btn.dataset.id;

  
  const confirm = window.confirm("¿Está seguro que desea eliminar el producto?");
  if(!confirm) return;
  
  let promise = module.fetchAPI(`/producto/${id}`, null, "DELETE");
  /**
   * @type {API_RESPONSE}
   */
  const res = await modal.waitPromise(promise);

  if (res.statusCode === 200) {
    observer.productUpdated = true;
  } else {
    alert(res.description);
    console.error(res);
  }
}

/**
 *
 * @param {Product["id"]} id
 * @returns {Promise<Product>}
 */
async function getProductById(id) {
  try {
    const res = await module.fetchAPI(`/producto/${id}`);
    if (res.statusCode !== 200) throw new Error(res.description);
    return res.response;
  } catch (err) {
    alert(err.message);
    console.error(err);
  }
}


/**
 * 
 * @param {string} tagName 
 * @param {string} className 
 * @param {string} textContent 
 * @returns {HTMLElement}
 */
export function createCustomElement(tagName, className, textContent) {
  const elemento = document.createElement(tagName);
  if (className) {
    elemento.className = className;
  }
  if (textContent) {
    elemento.textContent = textContent;
  }
  return elemento;
}

function createTableHeader() {
  const trEncabezado = createCustomElement("tr", "tr");

  trEncabezado.appendChild(createCustomElement("th", null, "ID"));
  trEncabezado.appendChild(createCustomElement("th", null, "Nombre"));
  trEncabezado.appendChild(createCustomElement("th", null, "Eliminar"));

  const thead = createCustomElement("thead", "encabezado");
  thead.appendChild(trEncabezado);

  return thead;
}

/**
 * 
 * @param {Category} category La categoría
 * @returns {HTMLTableSectionElement}
 */
function createTableBody(category) {
  const trCuerpo = createCustomElement("tr", "tr");

  trCuerpo.appendChild(createCustomElement("td", "id-column", `#${category.id}`));
  trCuerpo.appendChild(createCustomElement("td", null, category.name));

  const tdEliminar = createCustomElement("td");
  tdEliminar.appendChild(createDeleteBtn(category.id));

  trCuerpo.appendChild(tdEliminar);

  const tbody = createCustomElement("tbody", "tbod");
  tbody.appendChild(trCuerpo);

  return tbody;
}

/**
 * 
 * @param {number} id 
 * @returns 
 */
function createDeleteBtn(id) {
  const botonEliminar = createCustomElement("button", "bt", "Eliminar");
  botonEliminar.addEventListener("click", async() => {
    const confirm = window.confirm("¿Está seguro que desea eliminar la categoría?");
    if(!confirm) return;
    let res = deleteCategory(id)
    res = await modal.waitPromise(res);
    if (res.statusCode === 200) {
      observer.categoryUpdated = true;
    } else {
      alert(res.description);
      console.error(res);
    }
  });
  return botonEliminar;
}

/**
 * 
 * @param {Category[]} categories 
 * @returns 
 */
async function addTable(categories) {
  const divContainer = createCustomElement("div", "table-container");
  const tabla = createCustomElement("table", "tabla");

  tabla.appendChild(createTableHeader());
  const elements = categories.map(createTableBody);
  elements.forEach((element) => tabla.appendChild(element));

  divContainer.appendChild(tabla);

  return divContainer;
}

/**
 * 
 * @returns {Promise<Category[]>}
 */
async function getCategories() {
  const res = await module.fetchAPI("/categoria/all", null, "GET");
  if (res.statusCode !== 200) throw new Error(res.description);
  return res.response;
}

/**
 * 
 * @param {number} id El `id` de la categoría
 * @returns 
 */
async function deleteCategory(id) {
  const res = await module.fetchAPI(`/categoria/${id}`, null, "DELETE");
  if (res.statusCode !== 200) throw new Error(res.description);
  return res;
}

async function addCategory(body) {
  const res = await module.fetchAPI(`/categoria`, body, "POST");
  if (res.statusCode !== 201) throw new Error(res.description);
  return res;
}