chrome.runtime.onInstalled.addListener(() => {
    console.log('Extension Installed');
  });
  
  chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    console.log('Message received in background script', request);
    if (request.type === 'checkFile') {
      const fileContent = request.content;
      console.log('Checking file content for keyword');
      if (fileContent.includes('secretKeyword')) {
        console.log('Keyword detected, sending notification');
        chrome.notifications.create({
          type: 'basic',
          iconUrl: 'icons/icon48.png',
          title: 'Keyword Detected',
          message: 'The file contains the keyword: secretKeyword',
          priority: 2
        });
        sendResponse({ shouldBlock: true });
      } else {
        console.log('Keyword not detected');
        sendResponse({ shouldBlock: false });
      }
    }
    return true;  // Required to keep the message channel open for sendResponse
  });
  