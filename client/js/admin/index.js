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
  // handleCheckAuth();

  document
    .querySelector("#menu_secciones")
    .childNodes.forEach((node) =>
      node.nodeName === "LI"
        ? node.addEventListener("click", handleNavClick)
        : null
    );

  updateNavLine();

  reloadProducts();
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
  }
}

/**
 *
 * @param {InputEvent} e
 */
async function handleClick(e) {
  const btn = e.target;
  const product = getProductById(btn.dataset.id);
  product.then((product) => console.log(product));
  const form = new ProductEditorTemplate(product, observer);
  modal.open(form.getTemplate());
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
