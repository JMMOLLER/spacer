import { handleCheckAuth } from "../home/index.js";
import ProductEditorTemplate from "../globals/ProductEditorTemplate.js";
import { Modal } from "../globals/modal.js";
import { handleNavClick, updateNavLine } from "../perfil/index.js";
import {
  toggleLoader,
  toggleError,
  renderCardProduct,
} from "../productos/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();
const modal = new Modal();

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

  const products = await module.getAllProducts();
  toggleLoader();

  if (!products) {
    toggleError();
  } else {
    products.forEach((product) => {
      renderCardProduct(product, {
        textContent: "Editar producto",
        controller: sayHello,
      });
    });
  }
}

/**
 *
 * @param {InputEvent} e
 */
async function sayHello(e) {
  const btn = e.target;
  console.log("Hello");
  const product = getProductById(btn.dataset.id);
  product.then((product) => console.log(product));
  const form = new ProductEditorTemplate(product);
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
