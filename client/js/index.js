const path = window.location.pathname;

window.location.protocol.includes("http:") ? console.info(path) : null;

switch (true) {
  case (path === "/" || path.includes("index")):
    import("./home/index.js").then((module) => module.default());
    break;
  case path.includes("login"):
    import("./login/index.js").then((module) => module.default());
    break;
  case path.includes("/pages/perfil"):
    import("./perfil/index.js").then((module) => module.default());
    break;
  case path.includes("/pages/carrito"):
    import("./cart/index.js").then((module) => module.default());
    break;
  case path.includes("/pages/registro"):
    import("./registro/index.js").then((module) => module.default());
    break;
  case path.includes("/pages/productos"):
    import("./productos/index.js").then((module) => module.default());
    break;
  case path.includes("/pages/forgot-password"):
    import("./forgotPassword/index.js").then((module) => module.default());
    break;
  case path.includes("/pages/admin"):
    import("./admin/index.js").then((module) => module.default());
    break;
  default:
    console.warn("404 - No reconocemos esta ruta.");
}
