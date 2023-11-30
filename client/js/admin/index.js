import { handleNavClick, updateNavLine } from "../perfil/index.js";

export default function () {
  document
    .querySelector("#menu_secciones")
    .childNodes.forEach((node) =>
      node.nodeName === "LI"
        ? node.addEventListener("click", handleNavClick)
        : null
    );
  
  updateNavLine();
}
