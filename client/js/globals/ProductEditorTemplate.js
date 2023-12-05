import Global from "./index.js";
const module = Global.getInstance();

export default class ProductEditorTemplate {
  /**
   * @type {Promise<Product> | Product}
   */
  #product;

  /**
   * @type {Array<Category>}
   */
  #categories;

  /**
   * @type {Promise<HTMLFormElement>}
   */
  #template;

  /**
   *
   * @param {Promise<Product>} productPromise
   */
  constructor(productPromise) {
    this.#product = productPromise;
    this.#template = this.#generateTemplate();
  }

  async getTemplate() {
    return this.#template;
  }

  #generateTemplate() {
    return new Promise(async (resolve, reject) => {
      try {
        this.#product = await this.#product;

        if (!this.#product) {
          reject(new Error("No se pudo obtener detalles del producto"));
          return;
        }

        await this.#getCategories();

        resolve(this.#generateForm());
      } catch (err) {
        reject(err);
      }
    });
  }

  async #getCategories() {
    const res = await module.fetchAPI("/categoria/all", null, "GET");
    if (res.statusCode !== 200) throw new Error(res.description);
    this.#categories = res.response;
  }

  #generateHeader(id) {
    const header = document.createElement("h2");
    header.textContent = `Editando producto con ID ${id}`;
    return header;
  }

  #generateImageContainer(imageUrl) {
    const container = document.createElement("div");
    container.classList.add("product-img__container");

    const image = document.createElement("img");
    image.classList.add("product__img");
    image.src = imageUrl;
    image.alt = "imagen producto";

    const fileContainer = document.createElement("div");
    fileContainer.classList.add("input-file__container");

    const label = document.createElement("label");
    label.setAttribute("for", "new-image");
    label.textContent = "Cambiar imagen de perfil";

    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.id = "new-image";
    fileInput.name = "img";
    fileInput.accept = "image/*";

    fileContainer.appendChild(label);
    fileContainer.appendChild(fileInput);

    container.appendChild(image);
    container.appendChild(fileContainer);

    return container;
  }

  #generateInputGroup(labelText, inputType, inputId, inputName, inputValue) {
    const groupContainer = document.createElement("div");
    groupContainer.classList.add("input-group");

    const input = document.createElement("input");
    input.type = inputType;
    input.id = inputId;
    input.name = inputName;
    input.value = inputValue;

    if (inputType === "number") input.step = "0.01";

    const label = document.createElement("label");
    label.setAttribute("for", inputId);
    label.textContent = labelText;

    groupContainer.appendChild(input);
    groupContainer.appendChild(label);

    return groupContainer;
  }

  #generateTextareaGroup(textareaValue) {
    const groupContainer = document.createElement("div");
    groupContainer.classList.add("input-group", "flx-g");

    const textarea = document.createElement("textarea");
    textarea.id = "product-description";
    textarea.name = "description";
    textarea.textContent = textareaValue;

    const label = document.createElement("label");
    label.setAttribute("for", "product-description");
    label.textContent = "Descripción";

    groupContainer.appendChild(textarea);
    groupContainer.appendChild(label);

    return groupContainer;
  }

  /**
   *
   * @param {string} buttonId
   * @param {Array<Category>} options
   * @returns
   */
  #generateSelectGroup(buttonId, options) {
    const groupContainer = document.createElement("div");
    groupContainer.classList.add("input-select-group");

    const button = document.createElement("div");
    button.setAttribute("tabindex", "0");
    button.classList.add("select");
    button.name = "categoryId";

    const selectedOption = document.createElement("input");
    selectedOption.id = buttonId;
    selectedOption.name = "categoryId";
    selectedOption.value = "Seleccionar";
    selectedOption.readOnly = true;

    const icon = document.createElement("i");
    icon.classList.add("fa", "fa-down");

    const dropdownContainer = document.createElement("div");
    dropdownContainer.classList.add("content_dropdown");
    dropdownContainer.id = "dropdown_options";

    const handleOptionClick = (e) => {
      const option = e.target;
      selectedOption.value = option.textContent;
      button.blur();
    };

    options.forEach((optionText) => {
      const option = document.createElement("a");
      option.classList.add("option");
      option.setAttribute("data-value", optionText.id);
      option.textContent = optionText.name;
      option.addEventListener("click", handleOptionClick);
      dropdownContainer.appendChild(option);
    });

    button.appendChild(selectedOption);
    button.appendChild(icon);
    button.appendChild(dropdownContainer);

    const label = document.createElement("label");
    label.setAttribute("for", buttonId);
    label.textContent = "Categoria";

    groupContainer.appendChild(button);
    groupContainer.appendChild(label);

    return groupContainer;
  }

  #generateSubmitButton() {
    const groupContainer = document.createElement("div");
    groupContainer.classList.add("inputSubmit-group");

    const submitButton = document.createElement("input");
    submitButton.type = "submit";
    submitButton.value = "Actualizar";

    const loaderSpan = document.createElement("span");
    loaderSpan.classList.add("loader");

    groupContainer.appendChild(submitButton);
    groupContainer.appendChild(loaderSpan);

    return groupContainer;
  }

  #generateCloseButton() {
    const modalClose = document.createElement("span");
    modalClose.className = "modal-close fa fa-close";
    modalClose.id = "modal-close";
    return modalClose;
  }

  /**
   * 
   * @param {Array<Category>} categories
   * @param {Event} e
   */
  #handleFormSubmit(e) {
    e.preventDefault();

    if(!this.#validateForm(e)) return;

    const form = e.target;

    const formData = new FormData(form);

    const data = Object.fromEntries(formData);

    data.categoryId = this.#categories.find((category) => category.name === data.categoryId)?.id;

    if(!data.categoryId) return alert("No se ha especificado una categoría válida");

    console.log(data);
  }

  /**
   * 
   * @param {Event} e 
   */
  #validateForm(e) {
    const form = e.target;
    /**
     * @type {Array<HTMLInputElement>}
     */
    const inputs = Array.from(form.querySelectorAll("input, textarea"));

    const validations = inputs.map((input) => {
      let isValid = true;

      if(input.type === "submit") return isValid;
  
      if (input.type === "text" || input.tagName.toLowerCase() === "textarea") {
        // Validar campos de texto no vacíos
        isValid = input.value.trim() !== "";
      } else if (input.type === "number") {
        // Validar campos de número
        isValid = !isNaN(parseFloat(input.value)) && isFinite(input.value);
  
        if (input.name === "price") {
          // Validar el campo de precio con máximo de dos decimales
          isValid = /^\d+(\.\d{1,2})?$/.test(input.value);
        }if(input.name === "stock"){
          // Validar el campo de stock con máximo de 4 dígitos enteros
          isValid = /^\d{1,4}$/.test(input.value);
        }
      } if (input.readOnly) {
        // Validar campos de solo lectura
        isValid = input.value.trim() !== "Seleccionar";
        input = input.parentElement
      }
  
      // Aplicar estilos de error si la validación falla
      if (!isValid) {
        input.classList.add("error");
      } else {
        input.classList.remove("error");
      }
  
      return isValid;
    });

    return validations.every((validaction) => validaction);
  }

  #generateForm() {
    const form = document.createElement("form");
    form.classList.add("product-editor__form");

    form.appendChild(this.#generateCloseButton());
    form.appendChild(this.#generateHeader(this.#product.id));
    form.appendChild(this.#generateImageContainer(this.#product.urlImg));

    const inputGroupContainer1 = document.createElement("section");
    inputGroupContainer1.classList.add("input-group-container");

    inputGroupContainer1.appendChild(
      this.#generateInputGroup(
        "Marca",
        "text",
        "product-brand",
        "marca",
        this.#product.marca
      )
    );
    inputGroupContainer1.appendChild(
      this.#generateInputGroup(
        "Precio",
        "number",
        "product-price",
        "price",
        this.#product.price
      )
    );
    inputGroupContainer1.appendChild(
      this.#generateInputGroup(
        "Stock",
        "number",
        "product-stock",
        "stock",
        this.#product.stock
      )
    );

    form.appendChild(inputGroupContainer1);

    const textareaGroup = this.#generateTextareaGroup(
      this.#product.description
    );
    form.appendChild(textareaGroup);

    const selectGroup = this.#generateSelectGroup(
      "product-category",
      this.#categories
    );
    form.appendChild(selectGroup);

    const inputGroupContainer2 = document.createElement("section");
    inputGroupContainer2.classList.add("input-group-container");
    inputGroupContainer2.classList.add("flx-w");

    inputGroupContainer2.appendChild(textareaGroup);
    inputGroupContainer2.appendChild(selectGroup);

    form.appendChild(inputGroupContainer2);
    form.appendChild(this.#generateSubmitButton());
    form.addEventListener("submit", this.#handleFormSubmit.bind(this));

    return form;
  }
}
