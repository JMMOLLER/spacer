const path = window.location.pathname;

// console.log(path);

if(path === "/" || path.includes("index")){
  // console.log("home");
  import ("./home/index.js").then((module) => module.default());
} else if (path.includes("login")){
  // console.log("login");
  import ("./login/index.js").then((module) => module.default());
} else {
  // console.log("404");
}
