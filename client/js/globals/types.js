/**
 * @typedef {Object} API_RESPONSE
 * @property {number} statusCode - El código de estado de la respuesta.
 * @property {string} description - El mensaje asociado a la respuesta.
 * @property {any} response - La respuesta de la API (puede ser un objeto o null).
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
 * @typedef {Object} requestOptions
 * @property {string} method
 * @property {object} headers
 * @property {object} body
 * @property {string} redirect
 * @property {string} referrerPolicy
 */
