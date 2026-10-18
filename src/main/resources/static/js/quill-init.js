document.addEventListener("DOMContentLoaded", () => {

    const editors = [];

    document.querySelectorAll("textarea").forEach((textarea, index) => {

        textarea.style.display = "none";

        const wrapper = document.createElement("div");
        wrapper.className = "quill-wrapper";

        const editor = document.createElement("div");
        editor.id = `quill-editor-${index}`;

        const counter = document.createElement("div");
        counter.style.marginTop = "5px";
        counter.style.fontSize = "12px";
        counter.style.color = "#666";
        counter.textContent = "0 caractères";

        textarea.parentNode.insertBefore(wrapper, textarea);
        wrapper.appendChild(editor);
        wrapper.appendChild(counter);

        const quill = new Quill(editor, {
            theme: "snow",
            modules: {
                toolbar: [
                    [{ header: [1, 2, 3, false] }],
                    ["bold", "italic", "underline"],
                    [{ list: "ordered" }, { list: "bullet" }],
                    [{ indent: "-1" }, { indent: "+1" }],
                ]
            }
        });

        quill.root.innerHTML = textarea.value || "";

        const updateCounter = () => {
            const text = quill.getText().trim();
            const length = text.length;
            counter.textContent = `${length} caractère${length > 1 ? "s" : ""}`;
        };

        updateCounter();

        quill.on("text-change", updateCounter);

        editors.push({ textarea, quill });
    });

    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", () => {
            editors.forEach(editor => {
                editor.textarea.value = editor.quill.root.innerHTML;
            });
        });
    });
});