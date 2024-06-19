// load components
function loadComponents() {
  const components = [
    { id: "sidebar-placeholder", url: "components/sidebar.html" },
    { id: "header-placeholder", url: "components/header.html" },
  ];

  components.forEach((component) => {
    $("#" + component.id).load(component.url, function (response, status, xhr) {
      if (status === "error") {
        console.error(
          "Error loading the component:",
          xhr.status,
          xhr.statusText
        );
      }
    });
  });
}
$(document).ready(function () {
  loadComponents();
});
