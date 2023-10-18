/**
 * @description Función que se encarga de validar el password
 * 
 * @param {String} password 
 * @returns {boolean}
 */
const validatePassword = (password) => {
  const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,}$/;
  return regex.test(password);
};

/**
 * @description Función que se encarga de validar el email
 * 
 * @param {String} email 
 * @returns {boolean}
 */
const validateEmail = (email) => {
  const regex = /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/g;
  return regex.test(email);
};

/**
 * @description Función que se encarga de validar el username
 * 
 * @param {String} username 
 * @returns {boolean}
 */
const validateUsername = (username) => {
  const regex = /^(?!.*\.\.)(?!.*\.$)[^\W][\w.]{0,29}$/igm;
  return regex.test(username);
}

/**
 * @description Función que se encarga de validar los inputs
 * 
 * @param {HTMLInputElement} input 
 */
const inputValidator = (input) => {
  if (input.value.trim() !== "") {
    input.classList.add("filled");
    input.classList.remove("error");
    if (input.id === "username") {
      const res = validateUsername(input.value);
      if (res) {
        input.classList.remove("error");
      } else {
        input.classList.add("error");
      }
    }
  } else {
    input.classList.remove("filled");
    input.classList.add("error");
  }

  if (input.type === "email") {
    const res = validateEmail(input.value);
    if (res) {
      input.classList.remove("error");
    } else {
      input.classList.add("error");
    }
  }

  if (input.type === "password") {
    const res = validatePassword(input.value);
    if (res) {
      input.classList.remove("error");
    } else {
      input.classList.add("error");
    }
  }
};

export { validatePassword, validateEmail, validateUsername, inputValidator }