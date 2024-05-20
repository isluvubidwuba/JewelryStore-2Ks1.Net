document.addEventListener("DOMContentLoaded", function () {
  loadComponent("sidebar-placeholder", "components/sidebar.html");
  loadComponent("header-placeholder", "components/header.html");
});

function loadComponent(elementId, url) {
  const element = document.getElementById(elementId);
  fetch(url)
    .then((response) => response.text())
    .then((data) => {
      element.innerHTML = data;
    })
    .catch((error) => console.error("Error loading the component:", error));
}
