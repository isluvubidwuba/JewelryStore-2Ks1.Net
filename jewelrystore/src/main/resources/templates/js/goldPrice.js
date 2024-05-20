document.addEventListener("DOMContentLoaded", () => {
  fetchGoldPrices();
});

function fetchGoldPrices() {
  fetch("http://localhost:8080/proxy")
    .then((response) => response.text())
    .then((data) => {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(data, "application/xml");
      const items = xmlDoc.querySelectorAll("item");
      // Lấy giá trị của thuộc tính "updated"
      const updated = xmlDoc.querySelector("ratelist").getAttribute("updated");

      const container = document.getElementById("tables");
      let tableHTML = "<table><tr><th>Type</th><th>Buy</th><th>Sell</th></tr>";

      items.forEach((item) => {
        const type = item.getAttribute("type");
        const buy = item.getAttribute("buy");
        const sell = item.getAttribute("sell");
        tableHTML += `<tr><td>${type}</td><td>${buy}</td><td>${sell}</td></tr>`;
      });

      tableHTML += "</table>";
      const updatedInfo = `<h1>Updated: ${updated}</h1>`;
      container.innerHTML = updatedInfo + tableHTML;
    })
    .catch((error) => console.error("Error fetching gold prices:", error));
}
