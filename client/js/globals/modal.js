export class Modal {
  #modal;

  constructor() {
    this.#modal = this.#createModal();
    document.body.appendChild(this.#modal);
  }

  #createModalContainer(){
    // Crear el elemento div para el modal
    const modal = document.createElement("div");
    modal.id = "modal";
    modal.className = "modal";
    return modal;
  }

  #createLoader() {
    const loader = document.createElement("span");
    loader.id = "modal-loader";
    return loader;
  }

  /**
   * 
   * @param {HTMLElement} content 
   * @returns 
   */
  #addEventCloseButton(content) {
    // Crear el elemento span para el botón de cerrar
    const modalClose = content.querySelector("#modal-close");

    if(!modalClose) throw new Error("Compruebe que se ha especificado el botón de cerrar con id 'modal-close'");

    modalClose.addEventListener("click", this.close.bind(this));
    return modalClose;
  }

  #createModal() {
    const template = this.#createModalContainer();
    const loader = this.#createLoader();
    template.appendChild(loader);
    return template;
  }

  /**
   * 
   * @param {Promise<HTMLElement>} content 
   */
  open(content) {
    if (!content) throw new Error("No se ha especificado el contenido del modal");

    content.then((html) => {
      document.querySelector("#modal-loader").classList.add("hidden");
      this.#addEventCloseButton(html);
      this.#modal.appendChild(html);
    }).catch((err) => this.close());

    this.#modal.classList.remove("close");
    this.#modal.classList.add("visible");
    document.querySelector("#modal-loader").classList.remove("hidden");
  }

  /**
   * 
   * @param {Promise} promise La promesa a esperar
   * @returns {Promise}
   */
  async waitPromise(promise) {
    if(!promise) throw new Error("No se ha especificado la promesa a esperar");

    this.#modal.classList.remove("close");
    this.#modal.classList.add("visible");
    document.querySelector("#modal-loader").classList.remove("hidden");
    await promise;
    this.close();

    return promise;
  }

  close() {
    this.#modal.classList.add("close");
    const handleClose = () => {
      this.#modal.classList.remove("visible");
      const lastChild = this.#modal.lastElementChild;
      if(lastChild.tagName !== "SPAN") lastChild.remove();
      document.querySelector("#modal-loader").classList.add("hidden");
      this.#modal.removeEventListener("animationend", handleClose);
    }
    this.#modal.addEventListener("animationend", handleClose);
  }
}
