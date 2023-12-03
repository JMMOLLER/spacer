import { handleCheckAuth } from "../home/index.js";
import { handleNavClick, updateNavLine } from "../perfil/index.js";
import { toggleLoader, toggleError, renderCardProduct } from "../productos/index.js";
import Global from "../globals/index.js";
const module = Global.getInstance();

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

  // const products = await module.getAllProducts();
  toggleLoader();

  if(!products){
    toggleError();
  }else{
    // products.forEach((product) => {
    //   renderCardProduct(product, { textContent: "Editar producto", controller: sayHello});
    // });
  }
}

/**
 * 
 * @param {InputEvent} e
 */
async function sayHello(e) {
  const btn = e.target;
  console.log("Hello");
  console.log(await getProductById(btn.dataset.id));
}

/**
 * 
 * @param {Product["id"]} id 
 * @returns {Promise<Product>}
 */
async function getProductById(id) {
  try{
    const product = await module.fetchAPI(`/producto/${id}`);
    if(product.statusCode !== 200) throw new Error(product.description);
    return product;
  } catch (err) {
    alert(err.message);
    console.error(err);
  }
}