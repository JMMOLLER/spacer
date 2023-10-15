let eventSubmitIsAdded = false;

const toggleSubmit = (inputValue) => {
  const submitGp = document.querySelector(".inputSubmit-group");
  const input = document.querySelector(
    ".inputSubmit-group>input[type='submit']"
  );
  submitGp.classList.toggle("submitted");
  input.disabled = !input.disabled;
  input.setAttribute("value", input.disabled ? "" : inputValue);
};

const showError = (show) => {
  const span = document.querySelector("#error");
  show ? span.classList.remove("hidden") : span.classList.add("hidden");
};

const loadLottieAnimation = () => {
  const returnElement = document.querySelector(".animation_arrow_container");

  const animation = lottie.loadAnimation({
    container: document.getElementById("animation_arrow"),
    renderer: "svg",
    loop: false,
    autoplay: false,
    path: "../assets/lotties/arrow-left.json",
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
  toggleSubmit("Iniciar Sesión");
  const credentials = new FormData(e.target);
  handleLogin(Object.fromEntries(credentials));
};

const handleLogin = async (credentials) => {
  showError(false);
  const fetchAPI = (await import("../globals/index.js")).fetchAPI;
  const res = await fetchAPI("/auth", credentials, "POST");
  if (res?.statusCode === 200) {
    sessionStorage.setItem("token", res?.response?.token);
    window.location.href = "/";
  } else {
    toggleSubmit("Iniciar Sesión");
    showError(true);
  }
  return res ? true : false;
};

window.onload = () => handleAddEventSubmit();

const handleAddEventSubmit = () => {
  const form = document.querySelector("form");
  form.addEventListener("submit", handleSubmit);

  form.querySelector("input[type='submit']").removeAttribute("disabled");

  eventSubmitIsAdded = true;
};

const forceAddEventSubmit = () => {
  const idInterval = setInterval(() => {
    if (eventSubmitIsAdded) {
      clearInterval(idInterval);
      return;
    }
    handleAddEventSubmit();
    console.log("Event submit has been forcibly added to form");
  }, 500);
}

export default function init() {
  loadLottieAnimation();
  import("../globals/index.js").then((module) => {
    module
      .userIsAuth()
      .then((res) => (res ? (window.location.href = "/") : null));
  });
  console.info("login");
  forceAddEventSubmit();
}

export { toggleSubmit };
