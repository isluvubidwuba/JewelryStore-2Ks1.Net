// Call the function to fetch gold prices when the page loads
document.addEventListener("DOMContentLoaded", fetchGoldPrices);

function fetchGoldPrices() {
  fetch("http://localhost:8080/proxy")
    .then((response) => response.text())
    .then((data) => {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(data, "application/xml");
      const items = xmlDoc.querySelectorAll("item");
      // Lấy giá trị của thuộc tính "updated"
      const updated = xmlDoc.querySelector("ratelist").getAttribute("updated");

      const tbody = document.getElementById("tbody-table-price");
      const updatedInfo = document.getElementById("day-update");

      // Clear existing rows
      tbody.innerHTML = "";

      items.forEach((item) => {
        const type = item.getAttribute("type");
        const buy = item.getAttribute("buy");
        const sell = item.getAttribute("sell");
        const row = `
          <tr>
            <td class="w-1/3 text-left py-3 px-4">${type}</td>
            <td class="w-1/3 text-left py-3 px-4">${buy}</td>
            <td class="text-left py-3 px-4">${sell}</td>
          </tr>
        `;
        tbody.innerHTML += row;
      });

      updatedInfo.innerHTML = updated;
    })
    .catch((error) => console.error("Error fetching gold prices:", error));
}
