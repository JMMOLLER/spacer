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
  productUpdated: false
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

    document.querySelector("#add-product").addEventListener("click", handleClick);

  updateNavLine();

  reloadProducts();
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
