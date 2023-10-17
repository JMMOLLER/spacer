import Global from "../globals/index.js";
const module = Global.getInstance();

export default function init() {
  import("../home/index.js")
    .then((module) => {
      module.loadHeaderLottieAnimation(); // Se importa el módulo home y se llama a la función loadHeaderLottieAnimation para cargar la animación del header
      module.handleCheckAuth(); // Se importa el módulo home y se llama a la función handleCheckAuth para verificar si el usuario está autenticado
    })
    .then(() => {
      !module
        .userIsAuth()
        .then((isAuth) =>
          !isAuth ? (window.location.href = "/pages/login.html") : null
        ); // Se importa el módulo globals y se llama a la función userIsAuth para verificar si el usuario está autenticado
    });

  document
    .querySelectorAll(".product_quantity")
    .forEach((element) => (element.onclick = handleQuantityClick)); // Seleccionamos todos los elementos con la clase product_quantity y se les agrega el evento onclick

  document
    .querySelectorAll(".delete_product")
    .forEach((element) => addLottieDeleteAnimation(element)); // Seleccionamos todos los elementos con la clase delete_product y se les agrega la animación de Lottie

  const form = document.querySelector("#checkout"); // Seleccionamos el elemento con el id checkout
  form.addEventListener("submit", handleCheckoutSubmit); // Se agrega el evento submit al formulario
  form
    .querySelectorAll("input")
    .forEach((input) => input.addEventListener("input", handleChangeInput)); // Seleccionamos todos los elementos input que estén dentro del formulario y se les agrega el evento input
}

const cardNameRegex = /^[a-zA-Z ]{2,30}$/; // Expresión regular para validar el nombre de la tarjeta de crédito
const cardNumberRegex = /^[0-9]{16}$/; // Expresión regular para validar el número de tarjeta de crédito
const cardExpirationRegex = /^[0-9]{2}\/[0-9]{2}\/[0-9]{2}$/; // Expresión regular para validar la fecha de expiración de la tarjeta de crédito
const cardCvvRegex = /^[0-9]{3}$/; // Expresión regular para validar el cvv de la tarjeta de crédito

/**
 * Esta función se encarga de manejar el evento click de los botones de cantidad de productos.
 *
 * @param {EventTarget} e
 */
function handleQuantityClick(e) {
  e.preventDefault(); // Se detiene el comportamiento por defecto del elemento
  e.stopPropagation(); // Se detiene la propagación del evento
  const parentE = e.currentTarget?.parentNode; // Se obtiene el elemento padre del elemento que disparó el evento
  const quantity = parentE?.querySelector("input"); // Se obtiene el elemento input que esté dentro del elemento padre
  if (quantity?.value > 1 && e.currentTarget?.classList.contains("down")) {
    // Si el valor del input es mayor a 1 y el elemento que disparó el evento tiene la clase down
    quantity.value--; // Se le resta 1 al valor del input
  } else if (
    quantity?.value < 100 &&
    e.currentTarget?.classList.contains("up")
  ) {
    // Si el valor del input es menor a 100 y el elemento que disparó el evento tiene la clase up
    quantity.value++; // Se le suma 1 al valor del input
  }
}

/**
 * Esta función agrega una animación de la librería Lottie a un elemento
 *
 * @param {HTMLElement} element
 */
function addLottieDeleteAnimation(element) {
  const childs = element.childNodes; // Guardamos los hijos del elemento

  const lottieAnimation = lottie.loadAnimation({
    container: element, // El elemento donde se va a cargar la animación
    renderer: "svg", // El tipo de renderizador
    loop: false, // Si la animación se repite
    autoplay: false, // Si la animación se reproduce al cargar
    path: "/assets/lotties/delete.json", // La ruta del archivo .json de la animación
    name: "lottie_delete", // El nombre que se le da a la animación
  }); // Cargamos la animación con su configuración

  lottieAnimation.addEventListener(
    "DOMLoaded",
    () =>
      (lottieAnimation.renderer.svgElement.style.transform =
        "translate3d(0px, -5px, 0px) scale(1.5)")
  ); // Añadimos el evento DOMLoaded para modificar el tamaño de la animación cuano se cargue el DOM

  element.addEventListener("mouseover", () => {
    lottieAnimation.play();
  }); // Añadimos el evento mouseover para reproducir la animación cuando el mouse esté sobre el elemento

  lottieAnimation.addEventListener("complete", () => {
    lottieAnimation.stop();
  }); // Añadimos el evento complete para detener la animación cuando termine

  if (childs.length > 0) {
    childs.forEach((el) => (el.style.display = "none"));
  } // Ocultamos los hijos del elemento
}

/**
 * Esta función se encarga de validar los campos del formulario de checkout
 *
 * @param {Event} e
 */
function handleCheckoutSubmit(e) {
  e.preventDefault(); // Se detiene el comportamiento por defecto del formulario
  e.stopPropagation(); // Se detiene la propagación del evento

  const target = e.currentTarget; // Se obtiene el elemento que disparó el evento
  const formData = new FormData(e.currentTarget); // Se crea un objeto FormData con los datos del formulario
  const stateInputs = []; // Se crea un array para guardar el estado de los inputs

  formData.forEach((value, key) => {
    if (key === "card_number") {
      // Si la key es card_number
      stateInputs.push({
        isValid: validateCardNumber(value).isValid, // Se agrega un objeto al array con el estado de la validación y el input
        input: target.querySelector("#card_number"), // Seleccionamos el elemento con el id card_number
      }); // Se agrega un objeto al array con el estado de la validación y el input
    } else if (key === "card_name") {
      stateInputs.push({
        isValid: validateCardName(value),
        input: target.querySelector("#card_name"),
      });
    } else if (key === "card_expiration") {
      stateInputs.push({
        isValid: validateCardExpiration(value),
        input: target.querySelector("#card_expiration"),
      });
    } else if (key === "card_cvv") {
      stateInputs.push({
        isValid: validateCardCvv(value),
        input: target.querySelector("#card_cvv"),
      });
    }

    console.log(key + ": ", value);
  });

  if (stateInputs.some((input) => !input.isValid)) {
    stateInputs.forEach((input) => {
      handleInputValidation(input.input, input.isValid);
    });
  } else {
    console.log("Formulario válido");
    /*
     * Aquí se debería enviar el formulario al servidor.
     */
  }
}

/**
 * Esta función se encarga de validar los campos del formulario de checkout
 *
 * @param {Event} e
 */
function handleChangeInput(e) {
  e.stopPropagation();
  const target = e.currentTarget;

  if (target.id === "card_name") {
    handleCardName(target); // Se llama a la función handleCardName y se le pasa el input
  } else if (target.id === "card_number") {
    handleCardNumber(target); // Se llama a la función handleCardNumber y se le pasa el input
  } else if (target.id === "card_expiration") {
    handleCardExpiration(target); // Se llama a la función handleCardExpiration y se le pasa el input
  } else if (target.id === "card_cvv") {
    handleCardCvv(target); // Se llama a la función handleCardCvv y se le pasa el input
  }
}

/**
 * Esta función se encarga de validar el número de tarjeta de crédito.
 *
 * @param {String} cardNumber
 * @returns {{isValid: boolean, type: string}} response
 */
const validateCardNumber = (cardNumber) => {
  const regexVisa = /^4[0-9]{12}(?:[0-9]{3})?$/;
  const regexMastercard = /^5[1-5][0-9]{14}$/;
  const regexAmericanExpress = /^3[47][0-9]{13}$/;
  const response = {
    isValid: false,
    type: "",
  }; // Objeto que se retornará

  if (regexVisa.test(cardNumber)) {
    response.isValid = true;
    response.type = "visa";
  } else if (regexMastercard.test(cardNumber)) {
    response.isValid = true;
    response.type = "mastercard";
  } else if (regexAmericanExpress.test(cardNumber)) {
    response.isValid = true;
    response.type = "americanExpress";
  } else {
    response.isValid = cardNumberRegex.test(cardNumber);
    response.type = "otro";
  }

  return response;
};

/**
 * Esta función se encarga de validar el nombre de la tarjeta de crédito.
 *
 * @param {string} cardName
 * @returns boolean
 */
const validateCardName = (cardName) => cardNameRegex.test(cardName); // Método test() de la clase RegExp evalúa un string y devuelve true si cumple con la expresión regular

/**
 * Esta función se encarga de validar la fecha de expiración de la tarjeta de crédito.
 *
 * @param {string} cardExpiration
 * @returns boolean
 */
const validateCardExpiration = (cardExpiration) =>
  cardExpirationRegex.test(cardExpiration);

/**
 * Esta función se encarga de validar el cvv de la tarjeta de crédito.
 *
 * @param {string} cardCvv
 * @returns boolean
 */
const validateCardCvv = (cardCvv) => cardCvvRegex.test(cardCvv);

/**
 * Esta función se encarga de establecer la clase invalid a los inputs que no sean válidos.
 *
 * @param {HTMLElement} input
 * @param {boolean} isValid
 */
const handleInputValidation = (input, isValid) =>
  input.classList[!isValid ? "add" : "remove"]("invalid"); // Si isValid es false se agrega la clase invalid, si es true se elimina la clase invalid

/**
 * Esta función se encarga de validar el campo cardExpiration.
 *
 * @param {HTMLElement} cardExpiration
 */
const handleCardExpiration = (cardExpiration) => {
  /**
   * Método split() de la clase String separa una cadena de string en un array de cadenas
   * string seguún un separador especificado.
   */
  const dia = parseInt(cardExpiration.value.split("/")[0]); // Obtenemos el día
  const mes = parseInt(cardExpiration.value.split("/")[1]); // Obtenemos el mes
  const year = parseInt(cardExpiration.value.split("/")[2]); // Obtenemos el año

  cardExpiration.value = cardExpiration.value.replace(
    /^\/+|[^0-9\/]|\/{2,}/g,
    ""
  ); // Remplazamos los caracteres que no sean números o "/" por una cadena vacía

  /**
   * cardExpiration.value[2] === "/" significa que si el tercer caracter de la cadena es igual
   * a "/" devolverá true.
   */
  if (cardExpiration.value.length === 3 && cardExpiration.value[2] !== "/") {
    cardExpiration.value =
      cardExpiration.value.slice(0, 2) + "/" + cardExpiration.value.slice(2); // Si el tercer caracter no es "/" se agregará
    return; // Se retorna para que no se ejecute el siguiente if
  } else if (
    cardExpiration.value.length === 6 &&
    cardExpiration.value[5] !== "/"
  ) {
    cardExpiration.value =
      cardExpiration.value.slice(0, 5) + "/" + cardExpiration.value.slice(5); // Si el sexto caracter no es "/" se agregará
    return; // Se retorna para que no se ejecute el siguiente if
  }

  if (dia > 31 || cardExpiration.value.slice(0, 2) === "00") {
    cardExpiration.value = cardExpiration.value.slice(0, -1); // Si el día es mayor a 31 o el día es 00 se elimina el último caracter
  } else if (mes > 12 || cardExpiration.value.slice(3, 5) === "00") {
    cardExpiration.value = cardExpiration.value.slice(0, -1); // Si el mes es mayor a 12 o el mes es 00 se elimina el último caracter
  } else if (year > 99 || cardExpiration.value.slice(6) === "0") {
    cardExpiration.value = cardExpiration.value.slice(0, -1); // Si el año es mayor a 99 o el año es 0 se elimina el último caracter
  }

  handleInputValidation(
    cardExpiration,
    validateCardExpiration(cardExpiration.value)
  );
};

/**
 * Esta función se encarga de validar el campo cardCvv.
 *
 * @param {HTMLElement} cardCvv
 */
const handleCardCvv = (cardCvv) => {
  cardCvv.value = cardCvv.value.replace(/[^0-9]/g, ""); // Remplazamos los caracteres que no sean números por una cadena vacía

  if (cardCvv.value.length > 3 || cardCvv.value[0] === "0") {
    // Si la longitud del valor es mayor a 3 o el primer caracter es 0
    cardCvv.value = cardCvv.value.slice(0, -1); // Se elimina el último caracter
  }

  handleInputValidation(cardCvv, validateCardCvv(cardCvv.value)); // Se valida el input
};

/**
 * Esta función se encarga de validar el campo cardNumber.
 *
 * @param {HTMLElement} cardNumber
 */
const handleCardNumber = (cardNumber) => {
  cardNumber.value = cardNumber.value.replace(/[^0-9]/g, "");

  if (cardNumber.value.length > 19) {
    // Si la longitud del valor es mayor a 16 entra al if
  }

  const response = validateCardNumber(cardNumber.value); // Se obtiene la respuesta de la validación

  document
    .querySelector(".card-types") // Seleccionamos el elemento con la clase card-types
    .querySelectorAll("button") // Seleccionamos todos los elementos button que estén dentro del elemento con la clase card-types
    .forEach((el) => {
      if (el.id === response.type) {
        // Si el id del elemento es igual al tipo de tarjeta que se obtuvo en la validación
        el.classList.add("selected"); // Se agrega la clase selected
      } else {
        el.classList.remove("selected"); // Se elimina la clase selected
      }
    });

  handleInputValidation(cardNumber, response.isValid); // Se valida el input
};

/**
 * Esta función se encarga de validar el campo cardName.
 *
 * @param {HTMLElement} cardName
 */
const handleCardName = (cardName) => {
  cardName.value = cardName.value.replace(/[^a-zA-Z ]/g, "");

  handleInputValidation(cardName, validateCardName(cardName.value));
};
