// content.js
function getFileType(fileName) {
  const extension = fileName.toLowerCase().split(".").pop();
  if (extension === "txt") return "text";
  if (extension === "pdf") return "pdf";
  if (extension === "docx") return "docx";
  return "other";
}

// Keep track of files being processed
const processingFiles = new WeakSet();

document.addEventListener(
  "change",
  async function (event) {
    if (event.target.type === "file") {
      const files = event.target.files;
      if (!files.length) return;

      const file = files[0];

      // If we're already processing this file, let the event continue
      if (processingFiles.has(file)) {
        processingFiles.delete(file);
        return;
      }

      // Prevent the default action and stop propagation
      event.preventDefault();
      event.stopPropagation();

      const fileInput = event.target;
      const reader = new FileReader();

      try {
        const arrayBuffer = await new Promise((resolve, reject) => {
          reader.onload = () => resolve(reader.result);
          reader.onerror = reject;
          reader.readAsArrayBuffer(file);
        });

        const fileType = getFileType(file.name);
        let payload;

        if (fileType === "text") {
          payload = {
            fileName: file.name,
            fileContent: new TextDecoder().decode(arrayBuffer),
            fileType: "text",
          };
        } else if (fileType === "pdf" || fileType === "docx") {
          const base64Content = btoa(
            new Uint8Array(arrayBuffer).reduce(
              (data, byte) => data + String.fromCharCode(byte),
              ""
            )
          );
          payload = {
            fileName: file.name,
            fileContent: base64Content,
            fileType: fileType,
          };
        } else {
          const sampleBytes = new Uint8Array(arrayBuffer.slice(0, 1024));
          const possibleText = new TextDecoder().decode(sampleBytes);
          const printableChars = possibleText.replace(
            /[^\x20-\x7E]/g,
            ""
          ).length;

          payload = {
            fileName: file.name,
            fileType: "other",
          };

          if (printableChars / possibleText.length > 0.85) {
            payload.fileContent = new TextDecoder().decode(arrayBuffer);
          }
        }

        console.log("Sending request to backend...");
        const response = await browser.runtime.sendMessage({
          type: "CHECK_FILE",
          payload: {
            ...payload,
            uploadUrl: window.location.href,
          },
        });
        console.log("Received response:", response);

        if (response.allowed) {
          console.log("Upload allowed");
          // Mark the file as being processed to prevent double handling
          processingFiles.add(file);

          // Restore the file and dispatch a new change event
          const dt = new DataTransfer();
          dt.items.add(file);
          fileInput.files = dt.files;

          fileInput.dispatchEvent(
            new Event("change", {
              bubbles: true,
              cancelable: true,
              composed: true,
            })
          );
        } else {
          console.log("Upload blocked");
          fileInput.value = "";
          alert("File upload interrupted");
        }
      } catch (error) {
        console.error("Error during file check:", error);
        fileInput.value = "";
        alert("Error during file check");
      }
    }
  },
  true
);
