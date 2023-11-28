/**
 * @typedef {Object} API_RESPONSE
 * @property {number} statusCode - El código de estado de la respuesta.
 * @property {string} description - El mensaje asociado a la respuesta.
 * @property {Client | null} response - La respuesta de la API (puede ser un objeto o null).
 */

/**
 * @typedef {Object} Client
 * @property {number} id - El ID del cliente.
 * @property {string} firstName - El nombre del cliente.
 * @property {string} lastName - El apellido del cliente.
 * @property {string} urlImg - La URL de la imagen del cliente.
 * @property {boolean} isAdmin - El rol del cliente.
 * @property {string} address - La dirección del cliente.
 * @property {string} email - El email del cliente.
 * @property {string} username - El nombre de usuario del cliente.
 * @property {CardInfo} cardInfo - El número de tarjeta del cliente.
 */

/**
 * @typedef {Object} Product
 * @property {number} id - El ID del producto.
 * @property {string} marca - La marca del producto.
 * @property {string} description - La descripción del producto.
 * @property {number} price - El precio del producto.
 * @property {Object} categoryId - La categoría del producto.
 * @property {number} categoryId.id - El ID de la categoría.
 * @property {string} categoryId.name - El nombre de la categoría.
 * @property {number} stock - La cantidad en stock del producto.
 * @property {string} urlImg - La URL de la imagen del producto.
 */

/**
 * @typedef {Object} Cart
 * @property {number} id - El ID del carrito.
 * @property {Product} productId - Los productos del carrito.
 * @property {number} quantity - La cantidad de producto.
 */

/**
 * @typedef {Object} SearchParam
 * @property {string} key - La clave del parámetro.
 * @property {string} value - El valor del parámetro.
 */

/**
 * @typedef {Object} RequestOptions
 * @property {"GET" | "POST" | "PUT" | "DELETE"} method
 * @property {object} headers
 * @property {object} body
 * @property {string} redirect
 * @property {string} referrerPolicy
 */

/**
 * @typedef {Object} CardInfo
 * @property {number} id - El ID de la tarjeta.
 * @property {string} cardNumber - El número de la tarjeta.
 * @property {string} cardHolder - El nombre del titular de la tarjeta.
 * @property {string} expirationDate - La fecha de expiración de la tarjeta.
 */