// Function to process files
function processFiles(files, event) {
  if (files.length > 0) {
    const file = files[0];
    const fileReader = new FileReader();
    fileReader.onload = function (e) {
      const fileContent = e.target.result;
      chrome.runtime.sendMessage({ type: 'checkFile', content: fileContent }, (response) => {
        if (response.shouldBlock) {
          alert('File upload blocked: contains sensitive keyword');
          event.preventDefault();  // Prevent the default action
          event.stopPropagation();  // Stop the event from propagating further
          if (event.target.tagName === 'INPUT') {
            event.target.value = '';  // Clear the file input
          }
        }
      });
    };
    fileReader.readAsText(file);
  }
}

// Observe for file input changes
const observer = new MutationObserver(mutations => {
  mutations.forEach(mutation => {
    mutation.addedNodes.forEach(node => {
      if (node.tagName === 'INPUT' && node.type === 'file') {
        node.addEventListener('change', (event) => {
          processFiles(event.target.files, event);
        });
      }
    });
  });
});

// Observe the document body for added nodes
observer.observe(document.body, { childList: true, subtree: true });

// Initial scan for existing file inputs
document.querySelectorAll('input[type="file"]').forEach(input => {
  input.addEventListener('change', (event) => {
    processFiles(event.target.files, event);
  });
});

// Drag and drop event listeners
document.addEventListener('dragover', (event) => {
  event.preventDefault();
}, false);

document.addEventListener('drop', (event) => {
  event.preventDefault();
  if (event.dataTransfer && event.dataTransfer.files.length > 0) {
    processFiles(event.dataTransfer.files, event);
  }
});
