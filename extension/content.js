// Handles interactions within the webpage, such as detecting file uploads, 
// sending content to the background script, 
// and receiving responses to block the upload if necessary.

function processFiles(files, event) {
  if (files.length > 0) {
    const file = files[0];
    const fileReader = new FileReader();
    fileReader.onload = function (e) {
      const fileContent = e.target.result;

      console.log("Sending message");

      chrome.runtime.sendMessage({ type: 'checkFile', content: fileContent }, (response) => {
        console.log("Message sent and came back to content.js");

        if (response && response.shouldBlock) {
          alert('File upload blocked <content>');
          event.preventDefault();  // Prevent the default action
          event.stopPropagation();  // Stop the event from propagating further
          if (event.target.tagName === 'INPUT') {
            event.target.value = '';  // Clear the file input
          }
          chrome.runtime.sendMessage({ type: 'closeTab' });
        }
      });
    };
    fileReader.readAsText(file);
  }
}

// Function to block sending an email, show a notification, and close the tab
function blockEmailAndCloseTab(event) {
  const target = event.target;

  // Check if the clicked button is the Gmail Send button
  if (target.getAttribute('aria-label') === 'Send ‪(Ctrl-Enter)‬' ||
    target.getAttribute('data-tooltip')?.includes('Send')) {
    alert('Sending email blocked.');

    // Prevent the default action (sending the email)
    event.preventDefault();
    event.stopPropagation();

    // Close the tab
    chrome.runtime.sendMessage({ type: 'closeTab' });
  }
}

// Observe the document body for clicks and filter for the Send button
document.body.addEventListener('click', blockEmailAndCloseTab, true);

// Improved MutationObserver to continuously monitor for Gmail Send button
const observer = new MutationObserver(() => {
  // Initial scan for the Gmail Send button and attach event listener
  document.querySelectorAll('div[role="button"][data-tooltip*="Send"]').forEach(button => {
    button.addEventListener('click', blockEmailAndCloseTab, true);
  });
});

observer.observe(document.body, { childList: true, subtree: true });

// Existing file processing logic
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
