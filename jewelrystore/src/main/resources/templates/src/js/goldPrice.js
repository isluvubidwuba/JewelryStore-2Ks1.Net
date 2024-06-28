const apiurl = process.env.API_URL;
document.addEventListener("DOMContentLoaded", fetchGoldPrices);

function fetchGoldPrices() {
  const token = localStorage.getItem("token");

  fetch(`http://${apiurl}/proxy`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.text();
    })
    .then((data) => {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(data, "application/xml");
      const items = xmlDoc.querySelectorAll("item");
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
