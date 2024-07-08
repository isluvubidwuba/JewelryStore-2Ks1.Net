class UserService {
  constructor() {
    this.token = localStorage.getItem("token");
    this.userId = localStorage.getItem("userId");
    this.userRole = localStorage.getItem("userRole");
    this.apiurl = process.env.API_URL;
  }

  getToken() {
    return this.token;
  }

  getUserId() {
    return this.userId;
  }

  getUserRole() {
    return this.userRole;
  }

  getApiUrl() {
    return this.apiurl;
  }

  setToken(token) {
    this.token = token;
    localStorage.setItem("token", token);
  }

  setUserId(userId) {
    this.userId = userId;
    localStorage.setItem("userId", userId);
  }

  setUserRole(userRole) {
    this.userRole = userRole;
    localStorage.setItem("userRole", userRole);
  }

  parseJwt(token) {
    var base64Url = token.split(".")[1];
    var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    var jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
  }
  determineContentType(data) {
    if (typeof data === "string") {
      return "application/x-www-form-urlencoded; charset=UTF-8";
    } else if (data instanceof FormData) {
      return false; // Let jQuery set the content type to multipart/form-data
    } else if (typeof data === "object") {
      return "application/json; charset=UTF-8";
    }
    return "application/x-www-form-urlencoded; charset=UTF-8";
  }

  async sendAjax(url, type, successCallback, errorCallback, data) {
    try {
      const contentType = this.determineContentType(data);
      const processData = !(data instanceof FormData); // Disable processData for FormData
      return await new Promise((resolve, reject) => {
        $.ajax({
          url: url,
          method: type,
          data:
            contentType === "application/json; charset=UTF-8"
              ? JSON.stringify(data)
              : data,
          contentType: contentType,
          processData: processData,
          xhrFields: {
            withCredentials: true, // Ensures cookies are included for all AJAX calls
          },
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
          success: (response) => {
            if (successCallback) successCallback(response);
            resolve(response);
          },
          error: (error) => {
            if (errorCallback) errorCallback(error);
            reject(error);
          },
        });
      });
    } catch (error) {
      console.error("Error setting up AJAX request:", error);
      if (errorCallback) {
        errorCallback(error);
      }
    }
  }

  async authenticate() {
    const authResponse = await this.sendAjax(
      `http://${this.apiurl}/api/authentication/isAuthenticated`,
      "GET",
      function (response) {
        return response;
      },
      function (error) {
        return error;
      },
      null
    );

    console.log("authResponse:", authResponse); // Log the authResponse

    if (!authResponse) {
      console.error("authResponse is undefined or null");
      return { authenticated: false };
    }

    if (
      authResponse.responseJSON &&
      authResponse.responseJSON.status === "FORBIDDEN"
    ) {
      return { authenticated: false };
    }

    if (authResponse.status === "OK") {
      const authData = authResponse.data;
      if (authData.at === "Valid" && authData.rt === "Valid") {
        return { authenticated: true };
      }

      if (authData.at !== "Valid" && authData.rt === "Valid") {
        const refreshTokenResponse = await this.sendAjax(
          `http://${this.apiurl}/api/authentication/refreshToken`,
          "GET",
          null,
          null,
          null
        );

        console.log("refreshTokenResponse:", refreshTokenResponse); // Log the refreshTokenResponse

        if (refreshTokenResponse && refreshTokenResponse.status === "OK") {
          this.setToken(refreshTokenResponse.data.at);
          return { authenticated: true };
        }
      } else if (authData.at === "Valid") {
        return { authenticated: true };
      }
    }

    return { authenticated: false };
  }

  async sendAjaxWithAuthen(url, type, successCallback, errorCallback, data) {
    try {
      const authResult = await this.authenticate();
      if (!authResult.authenticated) {
        throw new Error("User is not authenticated");
      }
      return this.sendAjax(url, type, successCallback, errorCallback, data);
    } catch (error) {
      console.error("Error in sendAjaxWithAuthen:", error);
      if (errorCallback) {
        errorCallback(error);
      }
    }
  }
  convertToFormData(obj) {
    const formData = new FormData();

    Object.keys(obj).forEach((key) => {
      if (obj[key] !== null && obj[key] !== undefined) {
        formData.append(key, obj[key]);
      }
    });

    return formData;
  }
}
export default UserService;
