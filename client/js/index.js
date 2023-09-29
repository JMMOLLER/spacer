const path = window.location.pathname;

console.info(path);

if((path === "/" || path.includes("index"))){
  import ("./home/index.js").then((module) => module.default());
} else if (path.includes("login")){
  import ("./login/index.js").then((module) => module.default());
} else if(path.includes("/pages/perfil.html")) {
  import("./perfil/index.js").then((module) => module.default());
} else {
  console.warn("404");
}
