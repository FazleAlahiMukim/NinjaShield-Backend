browser.runtime.onMessage.addListener(async (message, sender) => {
  if (message.type === 'CHECK_FILE') {
    try {
      const response = await fetch('http://localhost:8080/api/webUpload', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(message.payload)
      });
      
      const result = await response.text();
      return { allowed: result === 'OK' };
    } catch (error) {
      console.error('Error checking file:', error);
      return { allowed: true };
    }
  }
});