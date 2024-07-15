// background.js

chrome.runtime.onInstalled.addListener(() => {
  console.log('Extension Installed');
});

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.type === 'checkFile') {

    console.log("INSIDE CHECKFILE")

    fetch('http://localhost:8080/api/check', {
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
            title: 'Keyword Detected',
            message: 'The file contains the keyword: secretKeyword',
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
