const API_URL = "https://spacer-api.up.railway.app/api";

/**
 * Este método se encarga de verificar si el usuario tiene
 * un token de sesión válido
 * 
 * @returns {Promise<boolean>}
 */
export function userIsAuth() {
  return new Promise((resolve) => {
    const token = localStorage.getItem("token");
    if (token) {
      doAPIFetch(`${API_URL}/auth/info`, null, "GET")
        .then((res) => {
          if (res.statusCode === 200) {
            resolve(true);
          } else {
            localStorage.removeItem("token");
            resolve(false);
          }
        })
        .catch((err) => {
          localStorage.removeItem("token");
          resolve(false);
        });
    } else {
      resolve(false);
    }
  });
};

/**
 * Este método se encarga de realizar las peticiones a la API
 * 
 * @param {string} path 
 * @param {object | null} credentials 
 * @param {string} method 
 * @returns {Promise<object>}
 */
export async function doAPIFetch(path, credentials, method) {
  try {
    const res = await fetch(`${API_URL}${path}`, {
      method: !method ? "GET" : method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")} `,
      },
      body: method === "POST" ? JSON.stringify(credentials) : null,
    });

    if (!res.ok) {
      throw new Error("Error en la petición");
    }

    const response = await res.json();
    return response;
  } catch (error) {
    console.error(error);
  }
};