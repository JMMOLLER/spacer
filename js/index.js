const path = window.location.pathname;

if(path === "/"){
  import ("./home/index.js").then((module) => module.default());
} else if (path.includes("login")){
  import ("./login/index.js").then((module) => module.default());
}
