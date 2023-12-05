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
    return this.#createModalContainer();
  }

  /**
   * 
   * @param {Promise<HTMLElement>} content 
   */
  open(content) {
    if (!content) throw new Error("No se ha especificado el contenido del modal");

    content.then((html) => {
      this.#addEventCloseButton(html);
      this.#modal.appendChild(html);
    }).catch((err) => this.close());

    this.#modal.classList.remove("close");
    this.#modal.classList.add("visible");
  }

  close() {
    this.#modal.classList.add("close");
    const handleClose = () => {
      this.#modal.classList.remove("visible");
      this.#modal.innerHTML = "";
      this.#modal.removeEventListener("animationend", handleClose);
    }
    this.#modal.addEventListener("animationend", handleClose);
  }
}
