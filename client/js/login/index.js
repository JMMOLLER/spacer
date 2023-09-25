const toggleSubmit = () => {
  const submitGp = document.querySelector(".inputSubmit-group");
  const input = document.querySelector(
    ".inputSubmit-group>input[type='submit']"
  );
  submitGp.classList.toggle("submitted");
  input.disabled = !input.disabled;
  input.setAttribute("value", input.disabled ? "" : "Iniciar Sesión");
};

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
}

const checkAuth = () => {
  const token = localStorage.getItem("token");
  if (!token && token?.startsWith("ey")) {
    window.location.href = "/home";
  }
};

const handleLogin = async(credentials) => {
  const url = "http://localhost:8080/api/auth";

  const response = await doFetch(url, credentials, "POST" );
  if(response?.token) {
    localStorage.setItem("token", response.token);
    window.location.href = "/home";
  } else {
    toggleSubmit();
  }
  return response ? true : false;
}

const doFetch = async (url, credentials, method) => {
  try {
    const consult = await fetch(url, {
      method: !method ? "GET" : method ,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    });

    if(!consult.ok) {
      throw new Error("Error en la petición");
    }

    const response = await consult.json();
    return response.response;
  } catch (error) {
    console.error(error);
  }
}

window.onload = () => {
  const form = document.querySelector("form");
  form.addEventListener("submit", handleSubmit);
};

export default function init() {
  loadLottieAnimation();
  checkAuth();
  console.info("login");
}
