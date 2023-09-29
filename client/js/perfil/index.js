

export default async function init() {
  const module = await import("../home/index.js");
  module.preventRedirect();
  module.loadHeaderLottieAnimation();
}