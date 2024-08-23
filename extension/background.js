// background.js
// Acts as the background script for the extension, 
// handling messages from the content script and managing interactions
// such as making API calls and closing tabs.


chrome.runtime.onInstalled.addListener(() => {
  console.log('Extension Installed');
});

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.type === 'checkFile') {

    console.log("INSIDE CHECKFILE")

    //? To DlpController.java
    fetch('http://localhost:8080/api/blkupload', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ fileContent: request.content })
    })
      .then(response => response.json())
      .then(data => {
        //! This comes up as NOTIFICATION
        if (data.containsKeyword) {
          chrome.notifications.create({
            type: 'basic',
            iconUrl: 'icons/icon48.png',
            title: 'NinjaShield',
            message: 'The file contains the keyword <background>',
            priority: 2
          });
        }
        sendResponse({ shouldBlock: data.containsKeyword });
      })
      .catch(error => console.error('Error:', error));
    return true;  // Required to keep the message channel open for sendResponse
  } else if (request.type === 'closeTab') {
    chrome.tabs.remove(sender.tab.id);
  }

});
