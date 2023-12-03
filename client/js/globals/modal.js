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

  #createModalContent() {
    // Crear el elemento div para el contenido del modal
    const modalContent = document.createElement("div");
    modalContent.id = "modal-content";
    modalContent.className = "modal-content";
    return modalContent;
  }

  #createModalClose() {
    // Crear el elemento span para el botón de cerrar
    const modalClose = document.createElement("span");
    modalClose.id = "modal-close";
    modalClose.className = "modal-close";
    modalClose.innerHTML = "&times;";
    modalClose.addEventListener("click", this.close.bind(this));
    return modalClose;
  }

  #createModalHeader() {
    // Crear el elemento div para el encabezado del modal
    const modalHeader = document.createElement("div");
    modalHeader.id = "modal-header";
    modalHeader.className = "modal-header";
    return modalHeader;
  }

  #createModalTitle() {
    // Crear el elemento h2 para el título del modal
    const modalTitle = document.createElement("h2");
    modalTitle.id = "modal-title";
    modalTitle.className = "modal-title";
    modalTitle.textContent = "Modal Title";
    return modalTitle;
  }

  #createModalBody() {
    // Crear el elemento div para el cuerpo del modal
    const modalBody = document.createElement("div");
    modalBody.id = "modal-body";
    modalBody.className = "modal-body";
    return modalBody;
  }

  #createModal() {
    const modal = this.#createModalContainer();

    const modalContent = this.#createModalContent();

    const modalClose = this.#createModalClose();
    
    const modalHeader = this.#createModalHeader();

    const modalTitle = this.#createModalTitle();

    const modalBody = this.#createModalBody();

    // Crear los elementos p para el texto del cuerpo del modal
    const text1 = document.createElement("p");
    text1.textContent = "Some text in the Modal Body";

    const text2 = document.createElement("p");
    text2.textContent = "Some other text...";

    // Crear el elemento div para el pie del modal
    const modalFooter = document.createElement("div");
    modalFooter.id = "modal-footer";
    modalFooter.className = "modal-footer";

    // Crear el elemento h3 para el contenido del pie del modal
    const modalFooterText = document.createElement("h3");
    modalFooterText.textContent = "Modal Footer";

    // Adjuntar todos los elementos al modal
    modalHeader.appendChild(modalTitle);
    modalBody.appendChild(text1);
    modalBody.appendChild(text2);
    modalFooter.appendChild(modalFooterText);
    modalContent.appendChild(modalClose);
    modalContent.appendChild(modalHeader);
    modalContent.appendChild(modalBody);
    modalContent.appendChild(modalFooter);
    modal.appendChild(modalContent);

    return modal;
  }

  open(title, body, footer) {
    this.#modal.classList.remove("close");
    this.#modal.classList.add("visible");
  }

  close() {
    this.#modal.classList.add("close");
    const handleClose = () => {
      this.#modal.classList.remove("visible");
      this.#modal.removeEventListener("animationend", handleClose);
    }
    this.#modal.addEventListener("animationend", handleClose);
  }
}
