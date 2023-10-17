class Global {
  static #instance;

  #API_URL;
  #apiResponseModel;
  #username;

  constructor() {
    if (!Global.#instance) {
      this.#API_URL = "https://spacer-api.onrender.com/api";
      this.#apiResponseModel = {
        statusCode: 0,
        message: "",
        response: {} || null,
      };
      this.#username = "";
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

  get apiResponseModel() {
    return this.#apiResponseModel;
  }

  set apiResponseModel(value) {
    this.#apiResponseModel = value;
  }

  get username() {
    return this.#username;
  }

  set username(value) {
    this.#username = value;
  }

  /**
   * Este método se encarga de verificar si el usuario tiene
   * un token de sesión válido
   *
   * @returns {Promise<boolean>}
   */
  async userIsAuth() {
    const token = sessionStorage.getItem("token");
    if (token) {
      try {
        const res = await this.fetchAPI("/auth/info", null, "GET");
        if (res.statusCode === 200) {
          this.#username = res.response.username;
          return true;
        } else {
          sessionStorage.removeItem("token");
          return false;
        }
      } catch (error) {
        console.error(error);
        sessionStorage.removeItem("token");
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Este método se encarga de realizar las peticiones a la API
   *
   * @param {string} path
   * @param {object | null} body
   * @param {string} method
   * @returns {Promise<apiResponseModel>}
   */
  async fetchAPI(path, body, method) {
    try {
      const res = await fetch(`${this.#API_URL}${path}`, {
        method: !method ? "GET" : method,
        headers: {
          "Content-Type": this.contentTypeHandler(method),
          Authorization: `Bearer ${sessionStorage.getItem("token")}`,
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
   * @description Este método se encarga de obtener el nombre de usuario
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
      this.#API_URL.replace("api", "cliente") + "/" + this.#username + ".jpg"
    );
  }

  /**
   * Este método se encarga de obtener la información del usuario
   *
   * @returns {Promise<apiResponseModel>}
   */
  async getUserInfo() {
    return await this.fetchAPI("/cliente", null, "GET");
  }

  /**
   *
   * @param {string} path
   * @param {object} requestOptions
   * @returns {Promise<apiResponseModel>}
   */
  async customFetch(path, requestOptions) {
    try {
      let res = await fetch(`${this.#API_URL}${path}`, requestOptions);
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
   * Removes the session token from sessionStorage and redirects the user to the login page.
   */
  logout() {
    sessionStorage.removeItem("token");
    window.location.href = "/pages/login.html";
  }
}

export default Global;
