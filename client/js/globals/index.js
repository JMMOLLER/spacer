const API_URL = "https://spacer-api.onrender.com/api";
const apiResponseModel = {
  statusCode: 0,
  message: "",
  response: {} || null,
};
let username = "";

/**
 * Este método se encarga de verificar si el usuario tiene
 * un token de sesión válido
 *
 * @returns {Promise<boolean>}
 */
export function userIsAuth() {
  return new Promise((resolve) => {
    const token = sessionStorage.getItem("token");
    if (token) {
      fetchAPI("/auth/info", null, "GET")
        .then((res) => {
          if (res.statusCode === 200) {
            username = res.response.username;
            resolve(true);
          } else {
            sessionStorage.removeItem("token");
            resolve(false);
          }
        })
        .catch((err) => {
          sessionStorage.removeItem("token");
          resolve(false);
        });
    } else {
      resolve(false);
    }
  });
}

export function logout() {
  sessionStorage.removeItem("token");
  window.location.href = "/pages/login.html";
}

/**
 * @description Este método se encarga de obtener el nombre de usuario
 * 
 * @returns {String}
 */
export function getUsername() {
  return username;
}

export function getUserImg () {
  return API_URL.replace("api", "cliente") + "/" + username + ".jpg";
}

/**
 * Este método se encarga de obtener la información del usuario
 *
 * @returns {Promise<apiResponseModel>}
 */
export async function getUserInfo() {
  return await fetchAPI("/cliente", null, "GET");
}

/**
 * Este método se encarga de realizar las peticiones a la API
 *
 * @param {string} path
 * @param {object | null} body
 * @param {string} method
 * @returns {Promise<apiResponseModel>}
 */
export async function fetchAPI(path, body, method) {
  try {
    const res = await fetch(`${API_URL}${path}`, {
      method: !method ? "GET" : method,
      headers: {
        "Content-Type": contentTypeHandler(method),
        "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      },
      body: method === "GET" ? null : JSON.stringify(body)
    });

    if (!res.ok) {
      const error = await res.json();
      return error;
    }

    const response = await res.json();
    return response;
  } catch (error) {
    console.error(error);
    return { statusCode: 500, message: "Unknow Error" };
  }
}

/**
 * Este método se encarga de ecoger el tipo de contenido que se
 * va a enviar al servidor.
 * 
 * @param {string} method 
 * @returns 
 */
const contentTypeHandler = (method) => {
  switch (method) {
    case "POST":
      return "application/json";
    case "PUT":
      return "multipart/form-data; boundary=----" + Math.random().toString(16).substring(2);
    default:
      return null;
  }
}

/**
 * 
 * @param {string} path 
 * @param {object} requestOptions 
 * @returns 
 */
export async function customFetch(path, requestOptions){
  try {
    let res = await fetch(`${API_URL}${path}`, requestOptions);
    if(!res.ok){
      const error = await res.json();
      return error;
    }
    return await res.json();
  } catch (error) {
    console.error(error);
    return { statusCode: 500, message: "Unknow Error" };
  }
}
