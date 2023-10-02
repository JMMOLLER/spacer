const inputController = () => {
  const form = document.querySelector("form.spacer_form");
  const inputs = form.querySelectorAll("input:not([type='submit'])");
  
  inputs.forEach(input => {
    input.addEventListener("change", (e) => {
      if(e.target.value.length > 0) {
        e.target.classList.add("filled");
      }else{
        e.target.classList.remove("filled");
      }
    })
  });
}

const getUserInfo = async() => {
  const module = await import("../globals/index.js");
  const userInfo = await module.getUserInfo();
  console.log(userInfo);
}

export default async function init() {
  const module = await import("../home/index.js");
  module.preventRedirect();
  module.loadHeaderLottieAnimation();
  inputController();
  getUserInfo()
}