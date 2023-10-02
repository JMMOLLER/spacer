const toggleSubmit = () => {
  const submitGp = document.querySelector(".inputSubmit-group");
  const input = document.querySelector(
    ".inputSubmit-group>input[type='submit']"
  );
  submitGp.classList.toggle("submitted");
  input.disabled = !input.disabled;
  input.setAttribute("value", input.disabled ? "" : "Iniciar Sesión");
};

const showError = (show) => {
  const span = document.querySelector("#error")
  show
    ? span.classList.remove("hidden")
    : span.classList.add("hidden");
}

const loadLottieAnimation = () => {
  const returnElement = document.querySelector(".animation_arrow_container");

  const animation = lottie.loadAnimation({
    container: document.getElementById("animation_arrow"), // the dom element that will contain the animation
    renderer: "svg",
    loop: false,
    autoplay: false,
    path: "../assets/lotties/arrow-left.json", // the path to the animation json
  });

  returnElement.addEventListener("mouseenter", () => {
    animation.play();
  });
  animation.addEventListener("complete", () => {
    animation.stop();
  });
};

const handleSubmit = (e) => {
  e.preventDefault();
  toggleSubmit();
  const credentials = new FormData(e.target);
  handleLogin(Object.fromEntries(credentials));
};

const handleLogin = async (credentials) => {
  showError(false);
  const fetchAPI = (await import("../globals/index.js")).fetchAPI;
  const res = await fetchAPI('/auth', credentials, "POST"); // no puede user esta funcion porque no esta definida
  if (res?.statusCode === 200) {
    sessionStorage.setItem("token", res?.response?.token);
    window.location.href = "/";
  } else {
    toggleSubmit();
    showError(true);
  }
  return res ? true : false;
};

window.onload = () => {
  const form = document.querySelector("form");
  form.addEventListener("submit", handleSubmit);
};

(function init() {
  loadLottieAnimation();
  import ("../globals/index.js").then((module) => {
    module.userIsAuth().then((res) =>
    res
      ? (window.location.href = "/")
      : document
        .querySelector("input[type='submit']")
        .removeAttribute("disabled"));
  })
  console.info("login");
})();
