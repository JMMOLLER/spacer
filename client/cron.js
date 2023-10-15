const fetch = require("fetch");

const url = "https://spacer-api.onrender.com/api";

setInterval(() => {
  fetch(url);
}, 10 * 60 * 1000); // 10 minutos
