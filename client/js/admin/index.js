import { handleCheckAuth } from "../home/index.js";
import { handleNavClick, updateNavLine } from "../perfil/index.js";

export default function () {

  handleCheckAuth();

  document
    .querySelector("#menu_secciones")
    .childNodes.forEach((node) =>
      node.nodeName === "LI"
        ? node.addEventListener("click", handleNavClick)
        : null
    );
  
  updateNavLine();
}
