document.addEventListener("DOMContentLoaded", () => {
    console.log("JavaScript loaded and DOM fully loaded.");

    document.querySelectorAll('.card-button').forEach(button => {
        console.log("Found button:", button); // Debug log

        button.addEventListener('click', () => {
            const cardId = button.getAttribute('data-card-id');
            console.log("Card ID clicked:", cardId);

            fetch('/Kontermatt/playCard', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ sessionId: '2', cardId: cardId })
            })
            .then(response => {
                // Check Content-Type to decide how to handle the response
                const contentType = response.headers.get('Content-Type');
                if (contentType && contentType.includes('application/json')) {
                    return response.json(); // Parse JSON if Content-Type is application/json
                } else {
                    return response.text(); // Fallback for non-JSON responses
                }
            })
            .then(data => {
                console.log("Server response:", data);

                // Handle JSON or plain text response
                if (typeof data === 'object' && data.success) {
                    button.parentElement.remove(); // Dynamically remove the card
                } else if (typeof data === 'string') {
                    alert("Unexpected response: " + data);
                } else {
                    alert("Error playing card.");
                }
            })
            .catch(error => console.error("AJAX error:", error));
        });
    });
});
