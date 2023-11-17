import "./types.js"

class Global {
  static #instance;

  #API_URL;
  #username;
  #token;

  constructor() {
    if (!Global.#instance) {
      this.#API_URL = "https://spacer-api.onrender.com/api";
      this.#username = "";
      this.#token = this.#checkSessionCookie();
      Global.#instance = this;
      console.log("Global instance created");
    }
    return Global.#instance;
  }

  /**
   * Obtiene la instancia única de la clase Global.
   * @returns {Global} La instancia única de la clase Global.
   */
  static getInstance() {
    if (!Global.#instance) {
      Global.#instance = new Global();
    }
    return Global.#instance;
  }

  get API_URL() {
    return this.#API_URL;
  }

  set API_URL(value) {
    this.#API_URL = value;
  }

  get username() {
    return this.#username;
  }

  set username(value) {
    this.#username = value;
  }

  get token() {
    return this.#token;
  }

  set token(value) {
    this.#token = value;
  }

  /**
   * @summary Este método se encarga de verificar si el usuario tiene una cookie de sesión válida
   * 
   * @returns {string | null}
   */
  #checkSessionCookie() {
    const cookies = document.cookie.split("; ").map((cookie) => {
      const [key, value] = cookie.split("=");
      return { key, value };
    });
    const token = cookies.find((cookie) => cookie.key === "SESSIONID")?.value;
    
    return token ?? null;
  }

  /**
   * @summary Este método se encarga de limpiar la cookie de sesión
   */
  #cleanSessionCookie() {
    document.cookie = "SESSIONID=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    this.#token = null;
  }

  /**
   * @summary Este método se encarga de verificar si el usuario tiene un token de sesión válido
   *
   * @returns {Promise<boolean>}
   */
  async userIsAuth() {
    if (this.#token) {
      try {
        const res = await this.fetchAPI("/auth/info", null, "GET");
        if (res.statusCode === 200) {
          this.#username = res.response.username;
          return true;
        } else {
          this.#cleanSessionCookie();
          return false;
        }
      } catch (error) {
        console.error(error);
        this.#cleanSessionCookie();
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * @summary Este método se encarga de realizar las peticiones a la API
   *
   * @param {string} path
   * @param {object | null} body
   * @param {string} method
   * @returns {Promise<API_RESPONSE>}
   */
  async fetchAPI(path, body, method) {
    try {
      const res = await fetch(`${this.#API_URL}${path}`, {
        method: !method ? "GET" : method,
        headers: {
          "Content-Type": this.contentTypeHandler(method),
          Authorization: `Bearer ${this.#token ?? ""}`,
        },
        body: method === "GET" ? null : JSON.stringify(body),
      });

      if (!res.ok) {
        const error = await res.json();
        return error;
      }

      const response = await res.json();
      return response;
    } catch (error) {
      console.error(error);
      return { statusCode: 500, message: "Unknow Error", response: null };
    }
  }

  /**
   * @summary Este método se encarga de ecoger el tipo de contenido que se va a enviar al servidor.
   *
   * @param {string} method
   * @returns
   */
  contentTypeHandler(method) {
    switch (method) {
      case "POST":
        return "application/json";
      case "PUT":
        return (
          "multipart/form-data; boundary=----" +
          Math.random().toString(16).substring(2)
        );
      default:
        return null;
    }
  }

  /**
   * @summary Este método se encarga de obtener el nombre de usuario
   *
   * @returns {String}
   */
  getUsername() {
    return this.#username;
  }

  /**
   * @returns {String}
   */
  getUserImg() {
    return (
      this.#API_URL.replace(/\/api/, "/cliente") + "/" + this.#username + ".jpg"
    );
  }

  /**
   * Este método se encarga de obtener la información del usuario
   *
   * @returns {Promise<API_RESPONSE>}
   */
  async getUserInfo() {
    return await this.fetchAPI("/cliente", null, "GET");
  }

  /**
   *
   * @param {string} url
   * @param {RequestOptions} requestOptions
   * @returns {Promise<API_RESPONSE>}
   */
  async customFetch(url, requestOptions) {
    try {
      let res = await fetch(url, requestOptions);
      if (!res.ok) {
        const error = await res.json();
        return error;
      }
      return await res.json();
    } catch (error) {
      console.error(error);
      return { statusCode: 500, message: "Unknow Error" };
    }
  }

  /**
   *
   * @returns {Promise<Product[]>}
   */
  async getAllProducts() {
    const products = await this.fetchAPI("/producto/all", null, "GET");
    return products.response;
  }

  /**
   * @summary Este método se encarga de obtener los productos del carrito
   * 
   * @param {number} id 
   * @returns {Promise<boolean>}
   */
  async addProductToCart(id) {
    const data = {
      productId: id,
      quantity: 1,
    };
    const res = await this.fetchAPI("/cliente/carrito", data, "POST");
    return res.statusCode === 201 ? true : false;
  }

  /**
   * @summary Este método se encarga de aumentar la cantidad de un producto en el carrito
   * 
   * @param {number} id 
   * @returns {Promise<boolean>}
   */
  async decreaseProductQuantity(id) {
    const res = await this.fetchAPI(`/cliente/carrito/decrease/${id}`, null, "POST");
    return res.statusCode === 200 ? true : false;
  }

  /**
   * @summary Este método se encarga de remover un producto del carrito
   * 
   * @param {number} id
   * @returns {Promise<boolean>}
   */
  async removeProductFromCart(id) {
    const res = await this.fetchAPI(`/cliente/carrito/${id}`, null, "DELETE");
    return res.response;
  }

  /**
   * @summary Este método se encarga de obtener los productos del carrito del usuario
   * 
   * @returns {Promise<Cart[]>}
   */
  async getCartProducts() {
    const res = await this.fetchAPI("/cliente", null, "GET");
    return res.response.cart;
  }

  /**
   * @summary Este método se encarga de realizar el login del usuario
   *
   * @param {FormData} credentials
   * @returns {Promise<boolean>}
   */
  async login(credentials) {
    try {
      const formData = new FormData();
      formData.append("username", credentials.username);
      formData.append("password", credentials.password);
      const res = await this.fetchAPI(
        "/auth",
        Object.fromEntries(formData),
        "POST"
      );
      if (res?.statusCode === 200) {
        const expDate = new Date(res?.response?.payload?.exp * 1000);
        const cookieExp = expDate.toUTCString();
        document.cookie = `SESSIONID=${res?.response?.token}; SameSite=None; Secure; path=/; expires=${cookieExp}`;
      }
      return res.statusCode === 200 ? true : false;
    } catch (error) {
      console.error(error);
      return false;
    }
  }

  /**
   * @summary Este método se encarga de realiazar el logout del usuario
   */
  logout() {
    this.#cleanSessionCookie();
    window.location.href = "/pages/login.html";
  }
}

export default Global;
